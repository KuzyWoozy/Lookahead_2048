package cs3822;

import java.util.HashMap;
import java.util.Stack;
import java.util.LinkedList;
import java.util.List;
import java.lang.Math;


class Lookahead implements Algorithm {
  private long instancesProcessed = 0;
  private long depth = 0;
  
  final private long depth_max;
  final private float reward_max;

  private Stack<TreeDFSNode> history;

  final private ModelStorage db;

 
  private float rewardFunc(Grid grid) {
    return grid.getScore();
  }

  public Lookahead(float reward_max, long depth_max) {
    this.depth_max = depth_max;
    this.reward_max = reward_max;
    // Initialize the Tree DFS stack
    history = new Stack<TreeDFSNode>();
    this.db = new MapStorage(new HashMap<Integer, Pair<Float, Action>>());
  }

  public Lookahead(ModelStorage db, float reward_max, long depth_max) {
    this.depth_max = depth_max;
    this.reward_max = reward_max;
    // Initialize the Tree DFS stack
    history = new Stack<TreeDFSNode>();
    this.db = db;
  }
 
  private void move_pure(Grid grid) {

    GridManager manager = new GridManager(grid);

    depth = 0;
    instancesProcessed = 0;

    history.push(new TreeDFSNode());
    // Initial dive
    dive(manager, history, db);
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

                if (manager.show().won()) {
                  TreeDFSNode node = new TreeDFSNode(peek, rewardFunc(manager.show()), Action.SWIPE_RIGHT);
        history.push(new TreeDFSNode(node, Action.SWIPE_LEFT));
                  
                  manager.undo();
                  continue;
                } 

                depth++;
                if (depth == depth_max) {
                  history.push(new TreeDFSNode(peek, rewardFunc(manager.show()), Action.SWIPE_RIGHT));
                  manager.undo();
                  depth--;
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

                if (manager.show().won()) {
                  TreeDFSNode node = new TreeDFSNode(peek, rewardFunc(manager.show()), Action.SWIPE_DOWN);
                  history.push(new TreeDFSNode(node, Action.SWIPE_LEFT));
                  
                  manager.undo();
                  continue;
                } 

                depth++;
                if (depth == depth_max) {
                  history.push(new TreeDFSNode(peek, rewardFunc(manager.show()), Action.SWIPE_DOWN));
                  manager.undo();
                  depth--;
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

                if (manager.show().won()) {
                  TreeDFSNode node = new TreeDFSNode(peek, rewardFunc(manager.show()), Action.SWIPE_LEFT);
                  // For consistencies sake
                  history.push(new TreeDFSNode(node, Action.SWIPE_LEFT));
                  manager.undo();
                  continue;
                } 

                depth++;
                if (depth == depth_max) {
                  history.push(new TreeDFSNode(peek, rewardFunc(manager.show()), Action.SWIPE_LEFT));
                  manager.undo();
                  depth--;
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
          manager.insertValue(node.getNextPossibility());


          if (manager.show().lost()) {
            history.push(new TreeDFSNode(node, Action.NONE));
            continue;
          }

          history.push(node);
          dive(manager, history, db);
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
          
          manager.undo();
          depth--;

          // Commit latest rewards
          if (node.getNextPossibility().getValue() == 2) {
            node = new TreeDFSNode(node, grid.getTwoProb(), node.getRestPossibilities());
          } else {
            node = new TreeDFSNode(node, 1 - grid.getTwoProb(), node.getRestPossibilities());
          }
          float expectedReward = node.getExpectedReward();

          TreeDFSNode nodeAbove = history.pop();
          
          /*
          if (expectedReward >= reward_max) {
            // Early cutting
            TreeDFSNode temp = new TreeDFSNode(nodeAbove, expectedReward, nodeAbove.getAction());
            history.push(new TreeDFSNode(temp, Action.SWIPE_LEFT));
          } else {
            history.push(new TreeDFSNode(nodeAbove, expectedReward, nodeAbove.getAction()));
          }
          */

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
            dive(manager, history, db);
          }
        }
      };
    } catch(InvalidActionException e) {
      e.printStackTrace();
      System.exit(1);
    }

  }

  @Override
  public List<Action> move(Grid grid) {
    db.clear();
    
    move_pure(grid);

    LinkedList<Action> list = new LinkedList<Action>();
    list.add(db.fetch(grid.hashCode()).getSecond()); 
    return list;
  }

  public float move_reward(Grid grid) {
    move_pure(grid);
    
    return db.fetch(grid.hashCode()).getFirst(); 
  }
  
  /**
   * Performs shifts upwards until a terminal is reached, 
   * a loss, win or no more possible moves upwards. 
   * All of the processing information is recoded in 
   * the specified Stack and Hash table.
   *
   * @param grid Start point for the dive
   * @param history Stack manager for processing in a DFS manner
   * @param db Table of optimal solutions
   */
  private void dive(GridManager manager, Stack<TreeDFSNode> history, ModelStorage db) {

    while(true) {
      TreeDFSNode peek = history.pop();
      
      if (peek.getAction() != Action.NONE) {
        Pair<Float, Action> item = db.fetch(manager.show().hashCode());
        if (item != null) {
          peek = new TreeDFSNode(peek, item.getFirst(), Action.NONE);
        }
      }
       
      List<Grid> frames = manager.slideUp(false);
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

      
      depth++;
      if (depth == depth_max) {
        history.push(new TreeDFSNode(peek, rewardFunc(manager.show()), Action.SWIPE_UP));
        manager.undo();
        depth--;
        return;
      }

      history.push(peek);

      TreeDFSNode node = new TreeDFSNode(manager.show());
      manager.insertValue(node.getNextPossibility());

      if (manager.show().lost()) {
        history.push(new TreeDFSNode(node, Action.NONE));
        return;
      }
      
      history.push(node);
    }
  }

  public long getDepth() {
    return depth;
  }

  public long getInstancesProcessed() {
    return instancesProcessed;
  }

}

