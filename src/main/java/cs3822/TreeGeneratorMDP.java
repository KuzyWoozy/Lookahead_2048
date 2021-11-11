package cs3822;

import java.util.HashMap;
import java.util.Stack;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;


class TreeGeneratorMDP {
  private HashMap<Integer, SolTableItem> map;

  public TreeGeneratorMDP(Grid grid) throws NoValueException, MovingOutOfBoundsException, UnknownNodeTypeException, NoMoveFlagException, InvalidActionException {
    this.map = new HashMap<Integer, SolTableItem>();
   
    // Initialize the Tree DFS stack
    Stack<TreeDFSNode> history = new Stack<TreeDFSNode>();
    history.push(new TreeDFSNode());
    
    // Obtain hash of the start node
    int startGrid_hash = grid.hashCode();  
   
    // Initial dive
    dive(grid, history, map);
    // Keep going until we traversed the entire tree
    while(true) {
      loop:
        while(true) {

          // Statement to view progress, can be safely discarded
          if (grid.hashCode() == startGrid_hash) {
            try {
              System.out.println("Checkpoint: " + history.peek().getAction().toString());
            } catch(Exception e) {}
          }
              
          switch(history.peek().getAction()) {
            // If the previous action was UP, do a right
            case SWIPE_UP:
              
              history.peek().setAction(Action.SWIPE_RIGHT); 
              
              if (!grid.canMoveRight()) {
                history.peek().setMaxReward(0f);
                continue;
              }
              
              grid.slideRight(false);
              
              if (grid.won()) {
                history.peek().setMaxReward(1f);
                grid.undo();
                history.peek().setAction(Action.SWIPE_LEFT);
                continue;
              } else if (grid.lost()) {
                history.peek().setMaxReward(0f);
                grid.undo();
                continue;
              }

              history.push(new TreeDFSNode(grid));
              break;

            case SWIPE_RIGHT:
              
              history.peek().setAction(Action.SWIPE_DOWN);

              if (!grid.canMoveDown()) {
                history.peek().setMaxReward(0f);
                continue; 
              }

              grid.slideDown(false);

              if (grid.won()) {
                history.peek().setMaxReward(1f);
                grid.undo();
                history.peek().setAction(Action.SWIPE_LEFT);
                continue;
              } else if (grid.lost()) {
                history.peek().setMaxReward(0f);
                grid.undo();
                continue;
              }

              history.push(new TreeDFSNode(grid));
              break;

            case SWIPE_DOWN:
                            
              history.peek().setAction(Action.SWIPE_LEFT);
 
              if (!grid.canMoveLeft()) { 
                history.peek().setMaxReward(0f);
                continue;
              }

              grid.slideLeft(false);

              if (grid.won()) {
                history.peek().setMaxReward(1f);
                grid.undo();
                history.peek().setAction(Action.SWIPE_LEFT);
                continue;
              } else if (grid.lost()) {
                history.peek().setMaxReward(0f);
                grid.undo();
                continue;
              }
              
              history.push(new TreeDFSNode(grid));
              break;
             
            case SWIPE_LEFT:
              
              TreeDFSNode node = history.peek();
              map.put(grid.hashCode(), new SolTableItem(node.getBestAction(), node.getBestReward()));

              history.peek().setNextPosi(grid);
              break loop;

            // Part of caching optimization
            case NONE:
              history.peek().setNextPosi(grid);
              break loop;
            
            default:
              throw new InvalidActionException();
        }
        
        dive(grid, history, map);
      }

      
      if (history.peek().isPosiEmpty()) {
        TreeDFSNode node = history.pop();
        if (history.isEmpty()) {
          break;
        }
        // Time to go up a level in the tree
        grid.undo();

        history.peek().setMaxReward((1f / node.getPosiNum()) * node.getSumReward());
      }
            
    };
  }

  private void dive(Grid grid, Stack<TreeDFSNode> history, HashMap<Integer, SolTableItem> map) throws UnknownNodeTypeException, NoValueException, MovingOutOfBoundsException, NoMoveFlagException {
    while(true) {
      
      int hash = grid.hashCode();
      if (map.containsKey(hash)) {
        SolTableItem item = map.get(hash);
        TreeDFSNode node = history.peek();

        node.addSumOfReward(item.getReward());
        node.setAction(Action.NONE);
        return;
      }

      if (!grid.canMoveUp()) {
        history.peek().setMaxReward(0f);
        return;
      } 

      grid.slideUp(false);
      
      if (grid.won()) {
        history.peek().setMaxReward(1f);
        grid.undo();
        // Can skip other alternatives if we won
        history.peek().setAction(Action.SWIPE_LEFT);
        return;
      } else if (grid.lost()) {
        history.peek().setMaxReward(0f);
        grid.undo();
        return;
      }

      history.push(new TreeDFSNode(grid));
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

