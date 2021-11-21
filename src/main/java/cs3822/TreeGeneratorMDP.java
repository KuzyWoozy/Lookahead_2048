package cs3822;

import java.util.HashMap;
import java.util.Stack;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;


class TreeGeneratorMDP {
  private HashMap<Integer, SolTableItem> map;
  private float twoProb;

  public TreeGeneratorMDP(Grid grid, float twoProb) throws NoValueException, MovingOutOfBoundsException, UnknownNodeTypeException, NoMoveFlagException, InvalidActionException, InvalidValueException, EarlyExpReturnException {
    this.map = new HashMap<Integer, SolTableItem>();
    this.twoProb = twoProb;
   
    // Initialize the Tree DFS stack
    Stack<TreeDFSNode> history = new Stack<TreeDFSNode>();
    history.push(new TreeDFSNode(twoProb));
    // Initial dive
    dive(grid, history, map);
    while(true) {
      loop:  
        while(true)  { 

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
              if (map.size() % 10000 == 0) {
                System.out.println(map.size());
              } 
              
              
              map.put(grid.hashCode(), new SolTableItem(node.getBestAction(), node.getBestReward()));

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

        dive(grid, history, map);
      }
  

      history.peek().setNextPosi(grid);
      if (history.peek().isPosiEmpty()) {
        TreeDFSNode node = history.pop();
        if (history.isEmpty()) {
          break;
        }
        // Time to go up a level in the tree
        grid.undo();

        history.peek().updateMaxReward(node.getExpectedReward());
      } else {
        if (grid.lost()) { 
          history.peek().updateMaxReward(0f);
          history.peek().setAction(Action.NONE);
        } else {
          // Need to dive if we have a new possibility
          dive(grid, history, map);
        }
      }
            
    };
  }

  private void dive(Grid grid, Stack<TreeDFSNode> history, HashMap<Integer, SolTableItem> map) throws UnknownNodeTypeException, NoValueException, MovingOutOfBoundsException, NoMoveFlagException {

    while(true) {
      int hash = grid.hashCode(); 
      
      if (map.containsKey(hash)) {
        TreeDFSNode node = history.peek();

        node.setMaxReward(map.get(hash).getReward());
        node.setAction(Action.NONE);
        return;
      }
      
      if (!grid.canMoveUp()) {
        history.peek().updateMaxReward(0f);
        return;
      }

      grid.slideUp(false);
      
      if (grid.won()) {
        history.peek().updateMaxReward(1f);
        grid.undo();
        // Can skip other alternatives if we won
        history.peek().setAction(Action.SWIPE_LEFT);
        return;
      } 

      history.push(new TreeDFSNode(grid, twoProb));
      // Need to check if we lost AFTER instantiating
      if (grid.lost()) {
        history.peek().updateMaxReward(0f);
        history.peek().setAction(Action.NONE);
        return;
      }
    }
  }

  public HashMap<Integer, SolTableItem> getMapRef() {
    return map;
  }

  public void save(String fileName) throws IOException {
    FileOutputStream file = null;
    ObjectOutputStream out = null;
    try {
    
      file = new FileOutputStream(fileName);
      out = new ObjectOutputStream(file);
  
      out.writeObject(map);

    } finally {
      if (file != null) {
        file.close();
      }
      if (out != null) {
        out.close();
      }
    }
  }

}

