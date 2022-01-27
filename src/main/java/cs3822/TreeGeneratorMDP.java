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
  private ModelStorage db;

  /** Initialize class with initial node and probability of generating a 2. */
  public TreeGeneratorMDP(Grid grid, ModelStorage db) throws InvalidActionException {
    // Initialize the Tree DFS stack
    history = new Stack<TreeDFSNode>();
    this.db = db;
    
    depth++;
    history.push(new TreeDFSNode(grid.getTwoProb()));
    // Initial dive
    dive(grid, history, db);
    try {
      while(true) {
        loop:  
          while(true)  {
            TreeDFSNode peek = history.peek();
            
            if (peek.getAction() != Action.NONE) {
              SolTableItem item = db.fetch(grid.hashCode());
              if (item != null) { 
                peek.setMaxReward(item.getReward());
                peek.setAction(Action.NONE);
              }
            }

            switch(peek.getAction()) {
              // If the previous action was UP, do a right
              case SWIPE_UP:
                peek.setAction(Action.SWIPE_RIGHT); 

                grid.slideRight(false);

                if (!grid.getMove()) {
                  peek.updateMaxReward(0f);
                  continue;
                }
                break;

              case SWIPE_RIGHT:
                peek.setAction(Action.SWIPE_DOWN);

                grid.slideDown(false);

                if (!grid.getMove()) {
                  peek.updateMaxReward(0f);
                  continue;
                }
                break;

              case SWIPE_DOWN:
                peek.setAction(Action.SWIPE_LEFT);
                grid.slideLeft(false);
                if (!grid.getMove()) {
                  peek.updateMaxReward(0f);
                  continue;
                }
                break;
               
              case SWIPE_LEFT:
                // Debug info
                instancesProcessed++;

                if (instancesProcessed % 100000 == 0) {
                  System.out.println("[DEBUG] Unique states in DAG: " + String.valueOf(db.getElemCount()) + "\n        States processed: " + String.valueOf(instancesProcessed) + "\n        Depth: " + String.valueOf(depth));
                } 
                db.insert(grid.hashCode(), peek.getBestAction(), peek.getBestReward());

                break loop;

              // Part of caching optimization
              case NONE:
                // Debug info
                if (instancesProcessed % 100000 == 0) {
                  System.out.println("[DEBUG] Unique states in DAG: " + String.valueOf(db.getElemCount()) + "\n        States processed: " + String.valueOf(instancesProcessed) + "\n        Depth: " + String.valueOf(depth));
                } 
                instancesProcessed++;
                break loop;
              
              default:
                throw new InvalidActionException();
          }
          
          if (grid.won()) {
            peek.updateMaxReward(1f);
            grid.undo();
            peek.setAction(Action.SWIPE_LEFT);
            continue;
          } 
          
          depth++;
          history.push(new TreeDFSNode(grid));      
          
          if (grid.lost()) {
            history.peek().updateMaxReward(0f);
            history.peek().setAction(Action.NONE);
            continue;
          }
          
          dive(grid, history, db);
        }
       
        TreeDFSNode node = history.peek();
        if (node.isPosiEmpty()) {
          history.pop();
          depth--;
          if (history.isEmpty()) {
            break;
          }
          try {
            node.commitReward(grid);
          } catch (InvalidValueException e) {
            e.printStackTrace();
            System.exit(1);
          }

          // One to go up a level in the tree
          grid.undo();
          
          float expectedReward = node.getExpectedReward();
          
          history.peek().updateMaxReward(expectedReward);
        } else {
          node.setNextPosi(grid);
          if (grid.lost()) { 
            node.updateMaxReward(0f);
            node.setAction(Action.NONE);
          } else {
            // Need to dive if we have a new possibility
            dive(grid, history, db);
          }
        }
      };
    } catch(InvalidActionException e) {
      e.printStackTrace();
      System.exit(1);
    }

    System.out.println("-----------------\nUnique nodes in DAG: " + String.valueOf(db.getElemCount()) + "\nInitial state:\n" + grid.stringify() + "\nDepth: " + String.valueOf(depth) + "\nStates processed: " + String.valueOf(instancesProcessed) + "\nExpected win rate (%): " + String.valueOf(db.fetch(grid.hashCode()).getReward() * 100));

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
  private void dive(Grid grid, Stack<TreeDFSNode> history, ModelStorage db) {

    while(true) {
      // Hash caching
      SolTableItem item = db.fetch(grid.hashCode());
      if (item != null) {
        TreeDFSNode node = history.peek();
        node.setMaxReward(item.getReward());
        node.setAction(Action.NONE);
        return;
      }

      // Move the state upwards
      grid.slideUp(false);

      // Check if the state is stuck  
      if (!grid.getMove()) {
        history.peek().updateMaxReward(0f);
        return;
      }
      
      // Check if the game is beat
      if (grid.won()) {
        history.peek().updateMaxReward(1f);
        grid.undo();
        // Can skip other alternatives if we won
        history.peek().setAction(Action.SWIPE_LEFT);
        return;
      }
      
      // Create a new node in the DAG 
      depth++;
      history.push(new TreeDFSNode(grid));

      // Need to check if we lost AFTER instantiating
      if (grid.lost()) {
        history.peek().updateMaxReward(0f);
        history.peek().setAction(Action.NONE);
        return;
      }
    }
  }
  
  public List<Action> move(Grid grid) {
    LinkedList<Action> list = new LinkedList<Action>();
    list.add(db.fetch(grid.hashCode()).getAction());
    return list;
  }
  
}

