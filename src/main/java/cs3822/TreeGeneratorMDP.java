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
  private float twoProb;
  private ModelStorage db;

  /** Initialize class with initial node and probability of generating a 2. */
  public TreeGeneratorMDP(Grid grid, ModelStorage db, float twoProb) throws NoValueException, MovingOutOfBoundsException, UnknownNodeTypeException, NoMoveFlagException, InvalidActionException, InvalidValueException, MaxPosNotInitializedException {
    this.twoProb = twoProb;
    this.db = db;
   
    // Initialize the Tree DFS stack
    Stack<TreeDFSNode> history = new Stack<TreeDFSNode>();
    history.push(new TreeDFSNode(twoProb));
    // Initial dive
    dive(grid, history, db);
    while(true) {
      loop:  
        while(true)  { 
          int hash = grid.hashCode();
          if (db.contains(hash)) {
      	    TreeDFSNode node = history.peek();

            node.setMaxReward(db.fetchReward(hash));
            node.setAction(Action.NONE);
	  }
	  switch(history.peek().getAction()) {
            // If the previous action was UP, do a right
	    case SWIPE_UP:
              history.peek().setAction(Action.SWIPE_RIGHT); 
              
              if (!grid.canMoveRight()) {
                history.peek().updateMaxReward(0f);
                continue;
              }
              
              grid.slideRight(false);
              
              break;

            case SWIPE_RIGHT:
              history.peek().setAction(Action.SWIPE_DOWN);

              if (!grid.canMoveDown()) {
                history.peek().updateMaxReward(0f);
                continue; 
              }

              grid.slideDown(false);

              break;

            case SWIPE_DOWN:

              history.peek().setAction(Action.SWIPE_LEFT);
 
              if (!grid.canMoveLeft()) { 
                history.peek().updateMaxReward(0f);
                continue;
              }

              grid.slideLeft(false);

              break;
             
            case SWIPE_LEFT: 

              TreeDFSNode node = history.peek();

              // Debug info
              if (db.getElemCount() % 10000 == 0) {
                System.out.println("[DEBUG] Unique states in DAG: " + String.valueOf(db.getElemCount()));
              } 
              
              db.insert(grid.hashCode(), node.getBestAction(), node.getBestReward());
              break loop;

            // Part of caching optimization
            case NONE:
              break loop;
            
            default:
              throw new InvalidActionException();
        }
        
        if (grid.won()) {
          history.peek().updateMaxReward(1f);
          grid.undo();
          history.peek().setAction(Action.SWIPE_LEFT);
          continue;
        } 

        history.push(new TreeDFSNode(grid, twoProb));
              
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
        if (history.isEmpty()) {
          break;
        }

        node.commitReward(grid);

        // One to go up a level in the tree
        grid.undo();
        
        float expectedReward = node.getExpectedReward();
        
        history.peek().updateMaxReward(expectedReward);
        // Early branch cutting optimizations, has to be after above line!!
        if (Math.abs(1f - expectedReward) <= 0.0001) {
          history.peek().setAction(Action.SWIPE_LEFT);
        }
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
    
    System.out.println("-----------------\nUnique nodes in DAG " + String.valueOf(db.getElemCount()) + "\nInitial state:\n" + grid.stringify() + "\nExpected win rate (%): " + String.valueOf(db.fetchReward(grid.hashCode()) * 100));

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
  private void dive(Grid grid, Stack<TreeDFSNode> history, ModelStorage db) throws UnknownNodeTypeException, NoValueException, MovingOutOfBoundsException, NoMoveFlagException {

    while(true) { 

      // Hash caching
      int hash = grid.hashCode();
      if (db.contains(hash)) {
        TreeDFSNode node = history.peek();

        node.setMaxReward(db.fetchReward(hash));
        node.setAction(Action.NONE);
      	return;
      }
      
      // Check if the state is stuck  
      if (!grid.canMoveUp()) {
        history.peek().updateMaxReward(0f);
        return;
      }

      // Move the state upwards
      grid.slideUp(false);
      
      // Check if the game is beat
      if (grid.won()) {
        history.peek().updateMaxReward(1f);
        grid.undo();
        // Can skip other alternatives if we won
        history.peek().setAction(Action.SWIPE_LEFT);
        return;
      }

      // Create a new node in the DAG 
      history.push(new TreeDFSNode(grid, twoProb));
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
    list.add(db.fetchAction(grid.hashCode()));
    return list;
  }
  
}

