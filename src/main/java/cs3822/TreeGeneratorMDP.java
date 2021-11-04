package cs3822;

import java.util.HashMap;
import java.util.Stack;
import java.util.List;
import java.util.LinkedList;

class TreeGeneratorMDP {
  private HashMap<Integer, Actions> map;

  public TreeGeneratorMDP(Grid grid) throws NoValueException, MovingOutOfBoundsException, UnknownNodeTypeException, NoMoveFlagException, InvalidActionException {
    this.map = new HashMap<Integer, Actions>();
    
    // Stack to manage which action we have taken
    Stack<Actions> slideHistory = new Stack<Actions>();
    // Stack to manage possible states at each layer
    Stack<List<EmptyNode>> possibleStates = new Stack<List<EmptyNode>>();
    possibleStates.push(new LinkedList<EmptyNode>());
    // Stack to manage the original number of states at each layer
    Stack<Integer> possibleStatesNum = new Stack<Integer>();
    possibleStatesNum.push(1);

    // Stack to manage the best reward from each action
    Stack<Float> bestReward = new Stack<Float>();
    // Initialize
    bestReward.push(0f);
    // Stack to manage the sum of reward at each layer
    Stack<Float> sumOfRewards = new Stack<Float>();
    // Initialize
    sumOfRewards.push(0f);

    int startGrid_hash = grid.hashCode(); 
    
    // Perform the initial dive
    dive(grid, possibleStates, slideHistory, possibleStatesNum, sumOfRewards, bestReward);
    updateRewards(sumOfRewards.pop(), grid, bestReward, slideHistory, sumOfRewards, map);
    // Keep going until we traversed the entire tree
    while (grid.hashCode() != startGrid_hash) {
      loop:
        while(true) {
           
          // pop off next possible state, if it exists
          Actions action = slideHistory.pop();
          
          switch(action) {
            // If the previous action was UP, do a right
            case SWIPE_UP:
              
              slideHistory.push(Actions.SWIPE_RIGHT); 
              
              if (!grid.canMoveRight()) {
                break;
              }
              
              grid.slideRight(false);

              if (grid.won()) {
                grid.undo();
                updateRewards(1f, grid, bestReward, slideHistory, sumOfRewards, map);
                break;
              } else if (grid.lost()) {
                grid.undo();
                break;
              }

              addPossibilities(grid, possibleStates, possibleStatesNum);
              sumOfRewards.push(0f);
              bestReward.push(0f);

              dive(grid, possibleStates, slideHistory, possibleStatesNum, sumOfRewards, bestReward);
              updateRewards(sumOfRewards.pop(), grid, bestReward, slideHistory, sumOfRewards, map);
              continue;

            case SWIPE_RIGHT:

              slideHistory.push(Actions.SWIPE_DOWN);
 
              if (!grid.canMoveDown()) {
                break; 
              }

              grid.slideDown(false);

              if (grid.won()) {
                grid.undo();
                updateRewards(1f, grid, bestReward, slideHistory, sumOfRewards, map);
                break;
              } else if (grid.lost()) {
                grid.undo();
                break;
              }

              addPossibilities(grid, possibleStates, possibleStatesNum); 
              sumOfRewards.push(0f);
              bestReward.push(0f);

              dive(grid, possibleStates, slideHistory, possibleStatesNum, sumOfRewards, bestReward);
              updateRewards(sumOfRewards.pop(), grid, bestReward, slideHistory, sumOfRewards, map);
              continue;

            case SWIPE_DOWN:
                            
              slideHistory.push(Actions.SWIPE_LEFT);
 
              if (!grid.canMoveLeft()) {
                break;
              }

              grid.slideLeft(false);

              if (grid.won()) {
                grid.undo();
                updateRewards(1f, grid, bestReward, slideHistory, sumOfRewards, map);
                break;
              } else if (grid.lost()) {
                grid.undo();
                break;
              }
              addPossibilities(grid, possibleStates, possibleStatesNum);
              sumOfRewards.push(0f);
              bestReward.push(0f);

              dive(grid, possibleStates, slideHistory, possibleStatesNum, sumOfRewards, bestReward);
              updateRewards(sumOfRewards.pop(), grid, bestReward, slideHistory, sumOfRewards, map);
              continue;
             

            case SWIPE_LEFT:
              bestReward.pop();
              break loop;
            
            default:
              throw new InvalidActionException();
        }
      }
      
      grid.undo();

      if (possibleStates.peek().isEmpty()) {
        // time to go up a level in the tree
        grid.undo();
        possibleStates.pop();
        updateRewards((1 / possibleStatesNum.pop()) * sumOfRewards.pop(), grid, bestReward, slideHistory, sumOfRewards, map);
        continue;
      }
      
      EmptyNode nextEmptyNode = possibleStates.peek().remove(0);
      grid.setValueNode(nextEmptyNode.getPos(), 2, true); 
      bestReward.push(0f);
 
      dive(grid, possibleStates, slideHistory, possibleStatesNum, sumOfRewards, bestReward);
      updateRewards(sumOfRewards.pop(), grid, bestReward, slideHistory, sumOfRewards, map);
    }
  }

  private void dive(Grid grid, Stack<List<EmptyNode>> possibleStates, Stack<Actions> slideHistory, Stack<Integer> possibleStatesNum, Stack<Float> sumOfRewards, Stack<Float> bestReward) throws NoValueException, MovingOutOfBoundsException, UnknownNodeTypeException, NoMoveFlagException {
    while(true) {
       
      slideHistory.push(Actions.SWIPE_UP); 
 
      if (!grid.canMoveUp()) {
        sumOfRewards.push(0f);
        return;
      } 

      grid.slideUp(false);
      
      if (grid.won()) {
        sumOfRewards.push(1f);
        grid.undo();
        return;
      } else if (grid.lost()) {
        sumOfRewards.push(0f);
        grid.undo();
        return;
      }

      addPossibilities(grid, possibleStates, possibleStatesNum);
      sumOfRewards.push(0f);
      bestReward.push(0f);
    }
  }

  private void updateRewards(float newReward, Grid grid, Stack<Float> bestReward, Stack<Actions> slideHistory, Stack<Float> sumOfRewards, HashMap<Integer, Actions> map) {
    //System.out.println("");
    //System.out.println(sumOfRewards.size());
    //System.out.println(bestReward.size());
    float rewMax = bestReward.pop(); 

    float temp = sumOfRewards.pop();
    sumOfRewards.push(newReward + temp);

    if (newReward > rewMax) {
      map.put(grid.hashCode(), slideHistory.peek());
      bestReward.push(newReward);
    } else {
      bestReward.push(rewMax);
    }
  }

  private void addPossibilities(Grid grid, Stack<List<EmptyNode>> possibleStates, Stack<Integer> possibleStatesNum) throws NoValueException, UnknownNodeTypeException {

    List<EmptyNode> possibilities = grid.getEmptyNodesCopy();
    if (possibilities.isEmpty()) {
      possibleStatesNum.push(1);
      possibleStates.push(possibilities);
    } else {
      possibleStatesNum.push(possibilities.size());
      grid.setValueNode(possibilities.remove(0).getPos(), 2, true);
      possibleStates.push(possibilities);
    }
  }

  public HashMap<Integer, Actions> getMapRef() {
    return map;
  }

}

