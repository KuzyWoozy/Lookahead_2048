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
  
  private long instancesProcessed = 0;
  private long depth = 0;
  
  private Stack<TreeDFSNode> history;

  final private ModelStorage db;

  /** 
   * Standard constructor.
   *
   * @param grid Initial start state
   * @param db Solution storage 
   */
  public TreeGeneratorMDP(Grid grid, ModelStorage db) throws InvalidActionException {
    this.history = new Stack<TreeDFSNode>();
    this.db = db;

    move(grid);

    System.out.println("-----------------\nUnique nodes in DAG: " + String.valueOf(db.getElemCount()) + "\nInitial state:\n" + grid.stringify() + "\nDepth: " + String.valueOf(depth) + "\nStates processed: " + String.valueOf(instancesProcessed) + "\nExpected win rate (%): " + String.valueOf(db.fetch(grid.hashCode()).getFirst() * 100));
  
  }
  
  @Override
  public List<Action> move(Grid grid) {
    db.clear();
    
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

                history.push(new TreeDFSNode(peek, Action.SWIPE_RIGHT));
                break;

              case SWIPE_RIGHT:
                frames = manager.slideDown(false);

                instancesProcessed++;
                
                if (!GridManager.hasMoved(frames)) {
                  history.push(new TreeDFSNode(peek, Action.SWIPE_DOWN));
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

                history.push(new TreeDFSNode(peek, Action.SWIPE_LEFT));
                break;

              case SWIPE_LEFT:
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
         
          if (Math.abs(expectedReward - 1f) < 0.00001) {
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
            dive(manager);
          }
        }
      };
    } catch(InvalidActionException e) {
      e.printStackTrace();
      System.exit(1);
    }

    LinkedList<Action> list = new LinkedList<Action>();
    list.add(db.fetch(grid.hashCode()).getSecond()); 
    return list;
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
          TreeDFSNode node = new TreeDFSNode(peek, 1f, Action.SWIPE_RIGHT);
          history.push(new TreeDFSNode(node, Action.SWIPE_LEFT));
          manager.undo();
          return;

        }
        manager.undo();
      }

      frames = manager.slideDown(false);
      if (GridManager.hasMoved(frames)) {
        if (manager.show().won()) {
          TreeDFSNode node = new TreeDFSNode(peek, 1f, Action.SWIPE_DOWN);
          history.push(new TreeDFSNode(node, Action.SWIPE_LEFT));
          manager.undo();
          return;

        }
        manager.undo();
      }

      frames = manager.slideLeft(false);
      if (GridManager.hasMoved(frames)) {
        if (manager.show().won()) {
          TreeDFSNode node = new TreeDFSNode(peek, 1f, Action.SWIPE_LEFT);
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
        TreeDFSNode node = new TreeDFSNode(peek, 1f, Action.SWIPE_UP);
        history.push(new TreeDFSNode(node, Action.SWIPE_LEFT));

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
  
}

