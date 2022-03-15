package cs3822;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;

/**
 * Threaded lookahead.
 *
 * @author Daniil Kuznetsov
 */
class ThreadedLookahead implements Algorithm {

  private final long depth_max;

  private List<Callable<Object>> jobs = new LinkedList<Callable<Object>>();
  private List<List<MutableFloat>> rewards = new LinkedList<List<MutableFloat>>();

  private ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

  private ModelStorage db;

  /** Standard constructor with max depth of the lookahead. */
  public ThreadedLookahead(long depth_max) {
    this.db = new MapStorage(new ConcurrentHashMap<Integer, Pair<Float, Action>>());
    this.depth_max = depth_max;
  }
  
  /** Use the threading pool to process all the jobs. */
  private void processJobs() {
    try {
      // Begin processing the necessary jobs
      List<Future<Object>> futures = pool.invokeAll(jobs);
      // Wait for the jobs to terminate
      for (Future<Object> future : futures) {
        future.get();
      }
    } catch(InterruptedException | ExecutionException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
  
  /** Compute rewards for each jobs. */
  private Action processRewards(float twoProb) {
    ArrayList<Action> actions = Action.getMoveActions();
    ArrayList<Float> expRewards = new ArrayList<Float>();
    
    for (List<MutableFloat> localRewards : rewards) {
      if (localRewards.isEmpty()) {
        expRewards.add(0f);
      } else {
        float sum = 0;
        for (int i = 0; i < localRewards.size(); i+=2) {
          sum += twoProb * localRewards.get(i).get();
          sum += (1 - twoProb) * localRewards.get(i+1).get();
        }
        expRewards.add((1f / (localRewards.size() / 2)) * sum);
      }
    }

    // Find best action based on maximal reward
    Action bestAction = null;
    float maxReward = 0;
    for (int i = 0; i < actions.size(); i++) {
      if (expRewards.get(i) >= maxReward) {
        bestAction = actions.get(i);
        maxReward = expRewards.get(i);
      }
    }
    return bestAction;
  }
  
  /** Create jobs for the thread pool to process. */
  private void fillUpJobs(GridManager manager) {
    jobs.clear();
    rewards.clear();
    List<Grid> frames;

    frames = manager.slideUp(false);
    if (manager.hasMoved(frames)) {
      addJobs(manager.show()); 
      manager.undo();
    } else {
      rewards.add(new LinkedList<MutableFloat>());
    }
    
    frames = manager.slideRight(false);
    if (manager.hasMoved(frames)) {
      addJobs(manager.show()); 
      manager.undo();
    } else {
      rewards.add(new LinkedList<MutableFloat>());
    }

    frames = manager.slideDown(false);
    if (manager.hasMoved(frames)) {
      addJobs(manager.show()); 
      manager.undo();
    } else {
      rewards.add(new LinkedList<MutableFloat>());
    }

    frames = manager.slideLeft(false);
    if (manager.hasMoved(frames)) {
      addJobs(manager.show()); 
      manager.undo();
    } else {
      rewards.add(new LinkedList<MutableFloat>());
    }
  }
  
  /** Add possibilities as each independent job to process. */
  private void addJobs(Grid grid) {
    LinkedList<MutableFloat> localRewards = new LinkedList<MutableFloat>();
    for (EmptyNode node : grid.getEmptyNodesCopy()) {
      // Create a job with generated 2
      MutableFloat reward1 = new MutableFloat();
      Grid gridCopy1 = grid.setValueNode(new ValueNode(node, 2));
      
      LookaheadStrand thread1 = new LookaheadStrand(gridCopy1, db, reward1, depth_max - 1);
      jobs.add(Executors.callable(thread1));
      localRewards.add(reward1);

      // Create job with generated 4
      MutableFloat reward2 = new MutableFloat();
      Grid gridCopy2 = grid.setValueNode(new ValueNode(node, 4));
      
      LookaheadStrand thread2 = new LookaheadStrand(gridCopy2, db, reward2, depth_max - 1);
      jobs.add(Executors.callable(thread2));
      localRewards.add(reward2);
    }
    rewards.add(localRewards);
  }

  @Override
  public List<Action> move(Grid grid) {

    GridManager manager = new GridManager(grid);

    db.clear();
    
    fillUpJobs(manager);

    processJobs();
    
    Action bestAction = processRewards(grid.getTwoProb());
    List<Action> list = new LinkedList<Action>();
    list.add(bestAction);
    
    return list;
  }

}
