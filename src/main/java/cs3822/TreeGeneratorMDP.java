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

  private float twoProb;
  private ModelStorage db;

  /** Initialize class with initial node and probability of generating a 2. */
  public TreeGeneratorMDP(Grid grid, ModelStorage db, float twoProb) throws InvalidActionException {
    this.twoProb = twoProb;
    this.db = db;
   
    // Initialize the Tree DFS stack
    Stack<TreeDFSNode> history = new Stack<TreeDFSNode>();
    history.push(new TreeDFSNode(twoProb));
    depth++;
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


              grid.slideRight(false);

              if (!grid.moved()) {
                history.peek().updateMaxReward(0f);
                continue;
              }
              break;

            case SWIPE_RIGHT:
              history.peek().setAction(Action.SWIPE_DOWN);

              
              grid.slideDown(false);

              if (!grid.moved()) {
                history.peek().updateMaxReward(0f);
                continue;
              }
              break;

            case SWIPE_DOWN:

              history.peek().setAction(Action.SWIPE_LEFT);
 
            
              grid.slideLeft(false);

              if (!grid.moved()) {
                history.peek().updateMaxReward(0f);
                continue;
              }
              break;
             
            case SWIPE_LEFT:
              // Debug info
              instancesProcessed++;
              if (instancesProcessed % 100000 == 0) {
                System.out.println("[DEBUG] Unique states in DAG: " + String.valueOf(db.getElemCount()) + "\n        States processed: " + String.valueOf(instancesProcessed) + "\n        Depth: " + String.valueOf(depth));
              } 


              TreeDFSNode node = history.peek();
              
              db.insert(grid.hashCode(), node.getBestAction(), node.getBestReward());
              break loop;

            // Part of caching optimization
            case NONE:
              // Debug info
              instancesProcessed++;
              if (instancesProcessed % 100000 == 0) {
                System.out.println("[DEBUG] Unique states in DAG: " + String.valueOf(db.getElemCount()) + "\n        States processed: " + String.valueOf(instancesProcessed) + "\n        Depth: " + String.valueOf(depth));
              } 

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
        depth++;
        
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
    
    System.out.println("-----------------\nUnique nodes in DAG: " + String.valueOf(db.getElemCount()) + "\nInitial state:\n" + grid.stringify() + "\nExpected win rate (%): " + String.valueOf(db.fetchReward(grid.hashCode()) * 100));

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
      int hash = grid.hashCode();
      if (db.contains(hash)) {
        TreeDFSNode node = history.peek();

        node.setMaxReward(db.fetchReward(hash));
        node.setAction(Action.NONE);
      	return;
      }

      // Move the state upwards
      grid.slideUp(false);

      // Check if the state is stuck  
      if (!grid.moved()) {
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
      history.push(new TreeDFSNode(grid, twoProb));
      depth++;

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

