package cs3822;

import java.util.HashMap;
import java.util.Stack;
import java.util.LinkedList;
import java.util.List;


/**
 * Lookahead 2048 algorithm.
 *
 * @author Daniil Kuznetsov
 */
class Lookahead implements Algorithm {
  private long instancesProcessed = 0;
  private long depth = 0;
  
  final private long depth_max;
  
  private Stack<TreeDFSNode> history;
  final private ModelStorage db;

  
  /*
  private float rewardFunc(Grid grid) {
    return grid.getScore();
  }
  */
  
  /** Return heuristic reward of given grid. */
  
  private float rewardFunc(Grid grid) {
    return grid.getScore() + grid.getImmediateWeightedScore();
  }
  
  
  /** Standard constructor with lookahead depth. */
  public Lookahead(long depth_max) {
    this.depth_max = depth_max;
    // Initialize the Tree DFS stack
    history = new Stack<TreeDFSNode>();
    this.db = new MapStorage(new HashMap<Integer, Pair<Float, Action>>());
  }

  /** 
   * Standard constructor.
   *
   * @param db Solution storage 
   * @param depth_max Max lookahead depth
   */
  public Lookahead(ModelStorage db, long depth_max) {
    this.depth_max = depth_max;
    // Initialize the Tree DFS stack
    history = new Stack<TreeDFSNode>();
    this.db = db;
  }
  

  public float move_reward(Grid grid, boolean clear) {
    move_pure(grid, clear);
    
    return db.fetch(grid.hashCode()).getFirst();
  }

  @Override
  public List<Action> move(Grid grid) {
    move_pure(grid);

    LinkedList<Action> list = new LinkedList<Action>();
    list.add(db.fetch(grid.hashCode()).getSecond()); 
    return list;
  }

  public void move_pure(Grid grid) {
    move_pure(grid, true);
  }

  public void move_pure(Grid grid, boolean clear) {
    if (clear) {
      db.clear();
    }
    
    GridManager manager = new GridManager(grid);

    depth = 0;
    instancesProcessed = 0;

    history.push(new TreeDFSNode());
    // Initial dive
    dive(manager);
    try {
      while(true) {
        loop:  
          while(true)  {
            
            TreeDFSNode peek = history.pop();
            
            if (peek.getAction() != Action.NONE) {
              Pair<Float, Action> item = db.fetch(manager.show().hashCode());
              if (item != null) {
                peek = new TreeDFSNode(peek, item.getFirst(), Action.NONE);
              }
            }
            
            List<Grid> frames;
            switch(peek.getAction()) {
              // If the previous action was UP, do a right
              case SWIPE_UP:
                frames = manager.slideRight(false);
                instancesProcessed++;
                
                if (!GridManager.hasMoved(frames)) {
                  history.push(new TreeDFSNode(peek, Action.SWIPE_RIGHT));
                  continue;
                }

                if ((depth + 1) == depth_max) {
                  history.push(new TreeDFSNode(peek, rewardFunc(manager.show()), Action.SWIPE_RIGHT));
                  manager.undo();
                  continue;
                }
                history.push(new TreeDFSNode(peek, Action.SWIPE_RIGHT));
                break;

              case SWIPE_RIGHT:
                frames = manager.slideDown(false);

                instancesProcessed++;
                
                if (!GridManager.hasMoved(frames)) {
                  history.push(new TreeDFSNode(peek, Action.SWIPE_DOWN));
                  continue;
                }

                if ((depth + 1) == depth_max) {
                  history.push(new TreeDFSNode(peek, rewardFunc(manager.show()), Action.SWIPE_DOWN));
                  manager.undo();
                  continue;
                }

                history.push(new TreeDFSNode(peek, Action.SWIPE_DOWN));
                break;

              case SWIPE_DOWN:
                frames = manager.slideLeft(false);
                instancesProcessed++;

                if (!GridManager.hasMoved(frames)) {
                  history.push(new TreeDFSNode(peek, Action.SWIPE_LEFT));
                  continue;
                }

                if ((depth + 1) == depth_max) {
                  history.push(new TreeDFSNode(peek, rewardFunc(manager.show()), Action.SWIPE_LEFT));
                  manager.undo();
                  continue;
                }

                history.push(new TreeDFSNode(peek, Action.SWIPE_LEFT));
                break;

              case SWIPE_LEFT:
                // Debug info
                db.insert(manager.show().hashCode(), peek.getBestAction(), peek.getBestReward());
                
                history.push(peek);
                break loop;

              // Part of caching optimization
              case NONE:
                // Debug info
                
                history.push(peek);
                break loop;
              
              default:
                throw new InvalidActionException();
          }
          
          TreeDFSNode node = new TreeDFSNode(manager.show());
          depth++;
          manager.insertValue(node.getNextPossibility());


          if (manager.show().lost()) {
            history.push(new TreeDFSNode(node, Action.NONE));
            continue;
          }

          history.push(node);
          dive(manager);
        }
        
        // TIME TO PROCESS NEXT POSSIBILITY
        
        // Undo the previous insert
        manager.undo();

        TreeDFSNode node = history.pop();
        if (history.isEmpty()) {
          instancesProcessed++;
          break;
        }
        
        if (node.noMorePossibilities()) { 
          // Go up a level in the tree
          
          depth--;
          manager.undo();

          // Commit latest rewards
          if (node.getNextPossibility().getValue() == 2) {
            node = new TreeDFSNode(node, grid.getTwoProb(), node.getRestPossibilities());
          } else {
            node = new TreeDFSNode(node, 1 - grid.getTwoProb(), node.getRestPossibilities());
          }
          
          float expectedReward = node.getExpectedReward();
          
          TreeDFSNode nodeAbove = history.pop();
          
          history.push(new TreeDFSNode(nodeAbove, expectedReward, nodeAbove.getAction()));
          
        } else {
          // Update next possibility
          
          if (node.getNextPossibility().getValue() == 2) {
            node = new TreeDFSNode(node, grid.getTwoProb(), node.getRestPossibilities());
          } else {
            node = new TreeDFSNode(node, 1 - grid.getTwoProb(), node.getRestPossibilities());
          }

          manager.insertValue(node.getNextPossibility());
          
          if (manager.show().lost()) { 
            history.push(new TreeDFSNode(node, Action.NONE));
          } else {
            // Need to dive if we have a new possibility
            history.push(node);
            dive(manager);
          }
        }
      };
    } catch(InvalidActionException e) {
      e.printStackTrace();
      System.exit(1);
    } 
  }

  /**
   * Performs shifts upwards until a terminal is reached, 
   * a loss, win or no more possible moves upwards. 
   *
   * @param manager Starts the dive at latest item in manager history
   */
  private void dive(GridManager manager) {
    List<Grid> frames;
    while(true) {
      TreeDFSNode peek = history.pop();
      
      if (peek.getAction() != Action.NONE) {
        Pair<Float, Action> item = db.fetch(manager.show().hashCode());
        if (item != null) {
          peek = new TreeDFSNode(peek, item.getFirst(), Action.NONE);
        }
      }

      // Check if we are about to win optimization
      frames = manager.slideRight(false);
      if (GridManager.hasMoved(frames)) {
        if (manager.show().won()) {
          TreeDFSNode node = new TreeDFSNode(peek, rewardFunc(manager.show()), Action.SWIPE_RIGHT);
          history.push(new TreeDFSNode(node, Action.SWIPE_LEFT));
          manager.undo();
          return;

        }
        manager.undo();
      }

      frames = manager.slideDown(false);
      if (GridManager.hasMoved(frames)) {
        if (manager.show().won()) {
          TreeDFSNode node = new TreeDFSNode(peek, rewardFunc(manager.show()), Action.SWIPE_DOWN);
          history.push(new TreeDFSNode(node, Action.SWIPE_LEFT));
          manager.undo();
          return;

        }
        manager.undo();
      }

      frames = manager.slideLeft(false);
      if (GridManager.hasMoved(frames)) {
        if (manager.show().won()) {
          TreeDFSNode node = new TreeDFSNode(peek, rewardFunc(manager.show()), Action.SWIPE_LEFT);
          history.push(new TreeDFSNode(node, Action.SWIPE_LEFT));
          manager.undo();
          return;

        }
        manager.undo();
      }

       
      frames = manager.slideUp(false);
      instancesProcessed++;

      if (!GridManager.hasMoved(frames)) {
        history.push(new TreeDFSNode(peek, Action.SWIPE_UP));
        return;
      }

      if (manager.show().won()) {
        // I cant tell if im enlightened or fucking stupid
        TreeDFSNode node = new TreeDFSNode(peek, rewardFunc(manager.show()), Action.SWIPE_UP);
        history.push(new TreeDFSNode(node, Action.SWIPE_LEFT));

        manager.undo();
        return;
      } 

      
      if ((depth + 1) == depth_max) {
        history.push(new TreeDFSNode(peek, rewardFunc(manager.show()), Action.SWIPE_UP));
        manager.undo();
        return;
      }

      history.push(peek);

      TreeDFSNode node = new TreeDFSNode(manager.show());
      depth++;
      manager.insertValue(node.getNextPossibility());

      if (manager.show().lost()) {
        history.push(new TreeDFSNode(node, Action.NONE));
        return;
      }
      
      history.push(node);
    }
  }
  
  /** Return current depth. */
  public long getDepth() {
    return depth;
  }

  /** Return number of instances processed. */
  public long getInstancesProcessed() {
    return instancesProcessed;
  }

}

