package cs3822;

import java.util.Stack;
import java.util.LinkedList;
import java.util.List;
import java.lang.Math;


class Lookahead implements Algorithm {
  private long instancesProcessed = 0;
  private long depth = 0;
  private float twoProb;
  
  private long depth_max;
  private float reward_max;

  private Stack<TreeDFSNode> history;

  private ModelStorage db = new HashMapStorage();


  private float rewardFunc(Grid grid) {
    return grid.getEmptyNodesCopy().size() + 1;
  }

  public Lookahead(float reward_max, long depth_max, float twoProb) {
    this.twoProb = twoProb;
    this.depth_max = depth_max;
    this.reward_max = reward_max;
    // Initialize the Tree DFS stack
    history = new Stack<TreeDFSNode>();
  }

  public List<Action> move(Grid grid) {
    db.clear();
    depth = 0;
    instancesProcessed = 0;

    depth++;
    history.push(new TreeDFSNode(twoProb));
    // Initial dive
    dive(grid, history, db);
    try {
      while(true) {
        loop:  
          while(true)  {
            
            if (db.contains(grid.hashCode())) {
              TreeDFSNode node = history.peek();
              node.setMaxReward(db.fetchReward(grid.hashCode()));
              node.setAction(Action.NONE);
            }
            
            switch(history.peek().getAction()) {
              // If the previous action was UP, do a right
              case SWIPE_UP:
                history.peek().setAction(Action.SWIPE_RIGHT); 

                grid.slideRight(false);

                if (!grid.getMove()) {
                  history.peek().updateMaxReward(0f);
                  continue;
                }
                break;

              case SWIPE_RIGHT:
                history.peek().setAction(Action.SWIPE_DOWN);

                grid.slideDown(false);

                if (!grid.getMove()) {
                  history.peek().updateMaxReward(0f);
                  continue;
                }
                break;

              case SWIPE_DOWN:
                history.peek().setAction(Action.SWIPE_LEFT);
                grid.slideLeft(false);
                if (!grid.getMove()) {
                  history.peek().updateMaxReward(0f);
                  continue;
                }
                break;
               
              case SWIPE_LEFT:
                // Debug info
                instancesProcessed++;

                TreeDFSNode node = history.peek(); 
                db.insert(grid.hashCode(), node.getBestAction(), node.getBestReward());

                break loop;

              // Part of caching optimization
              case NONE:
                // Debug info
                instancesProcessed++;
                break loop;
              
              default:
                throw new InvalidActionException();
          }
          
          if (grid.won()) {
            history.peek().updateMaxReward(reward_max);
            grid.undo();
            history.peek().setAction(Action.SWIPE_LEFT);
            continue;
          } 

          depth++;
          if (depth == depth_max) {
            TreeDFSNode node = history.peek();
            node.setMaxReward(rewardFunc(grid));
            grid.undo();
            node.setAction(Action.NONE);
            depth--;
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
    LinkedList<Action> list = new LinkedList<Action>();
    list.add(db.fetchAction(grid.hashCode())); 

    return list;
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
      if (db.contains(grid.hashCode())) {
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
        history.peek().updateMaxReward(reward_max);
        grid.undo();
        // Can skip other alternatives if we won
        history.peek().setAction(Action.SWIPE_LEFT);
        return;
      }
      
      depth++;
      if (depth == depth_max) {
        TreeDFSNode node = history.peek();
        node.setMaxReward(rewardFunc(grid));
        grid.undo();
        node.setAction(Action.NONE);
        depth--;
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
}

