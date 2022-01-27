package cs3822;

import java.util.concurrent.ConcurrentHashMap;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.lang.InterruptedException;


class ThreadedLookahead implements Algorithm {

  private final float reward_max;
  private final long depth_max;

  private List<GridThread> jobs = new LinkedList<GridThread>();
  private List<List<Reward>> rewards = new LinkedList<List<Reward>>();

  private ModelStorage db;

  public ThreadedLookahead(float reward_max, long depth_max) {
    this.db = new MapStorage(new ConcurrentHashMap<Integer, SolTableItem>());
    this.reward_max = reward_max;
    this.depth_max = depth_max;
  }

  private void processJobs() {
    
    for (GridThread thread : jobs) {
      thread.start();
    }
    
    try {
      for (GridThread thread : jobs) {
        thread.join();
      }
    } catch(InterruptedException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  private Action processRewards(float twoProb) {
    ArrayList<Action> actions = Action.getMoveActions();
    ArrayList<Float> expRewards = new ArrayList<Float>();
    
    for (List<Reward> localRewards : rewards) {
      float sum = 0;
      for (int i = 0; i < localRewards.size(); i+=2) {
        sum += twoProb * localRewards.get(i).getReward();
        sum += (1 - twoProb) * localRewards.get(i+1).getReward();
      }
      expRewards.add((1f / (localRewards.size() / 2)) * sum);
    }

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

  private void fillUpJobs(Grid grid) {

    jobs.clear();
    rewards.clear();

    grid.slideUp(false);
    addJobs(grid); 
    grid.undo();
    
    grid.slideRight(false);
    addJobs(grid);
    grid.undo();

    grid.slideDown(false);
    addJobs(grid); 
    grid.undo();

    grid.slideLeft(false);
    addJobs(grid);
    grid.undo();
  }

  private void addJobs(Grid grid) {
    LinkedList<Reward> localRewards = new LinkedList<Reward>();
    for (EmptyNode node : grid.getEmptyNodesCopy()) {
      Reward reward1 = new Reward();
      Grid gridCopy1 = new Grid(grid);
      gridCopy1.setValueNode(new ValueNode(node, 2));
      
      GridThread thread1 = new GridThread(gridCopy1, db, reward1, reward_max, depth_max - 1);
      jobs.add(thread1);
      localRewards.add(reward1);


      Reward reward2 = new Reward();
      Grid gridCopy2 = new Grid(grid);
      gridCopy2.setValueNode(new ValueNode(node, 4));
      
      GridThread thread2 = new GridThread(gridCopy2, db, reward2, reward_max, depth_max - 1);
      jobs.add(thread2);
      localRewards.add(reward2);
    }
    rewards.add(localRewards);
  }

  @Override
  public List<Action> move(Grid grid) {
    db.clear();
    
    fillUpJobs(grid);

    processJobs();
    
    Action bestAction = processRewards(grid.getTwoProb());
    List<Action> list = new LinkedList<Action>();
    list.add(bestAction);
    return list;
  }

}
