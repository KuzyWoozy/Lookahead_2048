package cs3822;

import java.util.Stack;
import java.util.LinkedList;
import java.util.List;
import java.lang.Math;


/**
 * Responsible for processing the DAG of instances
 * generated from the initial node and creating 
 * the optimal model to play the game.
 *
 * @author Daniil Kuznetsov
 */
class TreeGeneratorMDP implements Algorithm {
  
  private Stack<TreeDFSNode> history;
  private long depth;
  private long instancesProcessed;
  
  final private ModelStorage db;

  /** Initialize class with initial node and probability of generating a 2. */
  public TreeGeneratorMDP(Grid grid, ModelStorage db) throws InvalidActionException {
    this.history = new Stack<TreeDFSNode>();
    this.db = db;

    move_pure(grid);

    System.out.println("-----------------\nUnique nodes in DAG: " + String.valueOf(db.getElemCount()) + "\nInitial state:\n" + grid.stringify() + "\nDepth: " + String.valueOf(depth) + "\nStates processed: " + String.valueOf(instancesProcessed) + "\nExpected win rate (%): " + String.valueOf(db.fetch(grid.hashCode()).getFirst() * 100));
  
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
                  TreeDFSNode node = new TreeDFSNode(peek, 1f, Action.SWIPE_RIGHT);
        history.push(new TreeDFSNode(node, Action.SWIPE_LEFT));
                  
                  manager.undo();
                  continue;
                } 
                
                depth++;
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
                  TreeDFSNode node = new TreeDFSNode(peek, 1f, Action.SWIPE_DOWN);
                  history.push(new TreeDFSNode(node, Action.SWIPE_LEFT));
                  
                  manager.undo();
                  continue;
                } 

                depth++;
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
                  TreeDFSNode node = new TreeDFSNode(peek, 1f, Action.SWIPE_LEFT);
                  // For consistencies sake
                  history.push(new TreeDFSNode(node, Action.SWIPE_LEFT));
                  manager.undo();
                  continue;
                } 
                
                depth++;
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
          depth++;


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
          
          if (Math.abs(expectedReward - 1f) >= 0.00001) {
            // Early cutting
            TreeDFSNode temp = new TreeDFSNode(nodeAbove, expectedReward, nodeAbove.getAction());
            history.push(new TreeDFSNode(temp, Action.SWIPE_LEFT));
          } else {
            history.push(new TreeDFSNode(nodeAbove, expectedReward, nodeAbove.getAction()));
          }

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
        TreeDFSNode node = new TreeDFSNode(peek, 1f, Action.SWIPE_UP);
        history.push(new TreeDFSNode(node, Action.SWIPE_LEFT));

        manager.undo();
        return;
      } 
      
      depth++;
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


  @Override 
  public List<Action> move(Grid grid) {
    LinkedList<Action> list = new LinkedList<Action>();
    list.add(db.fetch(grid.hashCode()).getSecond());
    return list;
  }
  
}

