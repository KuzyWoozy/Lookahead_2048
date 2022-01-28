package cs3822;

import java.util.HashMap;
import java.util.Stack;
import java.util.LinkedList;
import java.util.List;
import java.lang.Math;


class Lookahead implements Algorithm {
  private long instancesProcessed = 0;
  private long depth = 0;
  
  private long depth_max;
  private float reward_max;

  private Stack<TreeDFSNode> history;

  private ModelStorage db;

  
  private float rewardFunc(Grid grid) {
    return grid.getEmptyNodesCopy().size() + 1;
  }
  
  /*
  private float rewardFunc(Grid grid) {
    return grid.getScore();
  }
  */

  public Lookahead(float reward_max, long depth_max) {
    this.depth_max = depth_max;
    this.reward_max = reward_max;
    // Initialize the Tree DFS stack
    history = new Stack<TreeDFSNode>();
    this.db = new MapStorage(new HashMap<Integer, SolTableItem>());
  }

  public Lookahead(ModelStorage db, float reward_max, long depth_max) {
    this.depth_max = depth_max;
    this.reward_max = reward_max;
    // Initialize the Tree DFS stack
    history = new Stack<TreeDFSNode>();
    this.db = db;
  }
 
  private void move_pure(Grid grid) {
    depth = 0;
    instancesProcessed = 0;

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

                db.insert(grid.hashCode(), peek.getBestAction(), peek.getBestReward());

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
            peek.updateMaxReward(reward_max);
            grid.undo();
            peek.setAction(Action.SWIPE_LEFT);
            continue;
          } 

          depth++;
          if (depth == depth_max) {
            peek.setMaxReward(rewardFunc(grid));
            grid.undo();
            peek.setAction(Action.NONE);
            depth--;
            continue;
          } 
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

  }

  @Override
  public List<Action> move(Grid grid) {
    db.clear();
    
    move_pure(grid);

    LinkedList<Action> list = new LinkedList<Action>();
    list.add(db.fetch(grid.hashCode()).getAction()); 
    return list;
  }

  public float move_reward(Grid grid) {
    move_pure(grid);
    
    return db.fetch(grid.hashCode()).getReward(); 
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
      history.push(new TreeDFSNode(grid));

      // Need to check if we lost AFTER instantiating
      if (grid.lost()) {
        history.peek().updateMaxReward(0f);
        history.peek().setAction(Action.NONE);
        return;
      }
    }
  }
}

