package cs3822;

import java.util.HashMap;
import java.util.Stack;
import java.util.List;


class TreeGeneratorMDP {
  private HashMap<Integer, Actions> map;

  public TreeGeneratorMDP(Grid grid) throws NoValueException, MovingOutOfBoundsException, UnknownNodeTypeException, NoMoveFlagException, InvalidActionException {
    this.map = new HashMap<Integer, Actions>();
     
    Stack<Actions> slideHistory = new Stack<Actions>();
    Stack<List<EmptyNode>> possibleStates = new Stack<List<EmptyNode>>();
    Stack<Integer> possibleStatesNum = new Stack<Integer>();
    Stack<Float> bestReward = new Stack<Float>();
    Stack<Float> sumOfRewards = new Stack<Float>();

    
    int startGrid_hash = grid.hashCode(); 
    float rew;
    
    // Perform the initial dive
    dive(grid, possibleStates, slideHistory, possibleStatesNum, sumOfRewards, bestReward);
    
    while (grid.hashCode() != startGrid_hash) {
      loop:
        while(true) {
          grid.undo();
          // pop off next possible state, if it exists
          Actions action = slideHistory.pop();
          
          switch(action) {
            // If the previous action was UP, do a right
            case SWIPE_UP:
              slideHistory.push(Actions.SWIPE_RIGHT);
              if (!grid.canMoveRight()) {
                sumOfRewards.push(0f);
                break loop;
              }
              grid.slideRight(false);

              if (grid.won()) {
                sumOfRewards.push(1f);
                break loop;
              } else if (grid.lost()) {
                sumOfRewards.push(0f);
                break loop;
              }

              addPossibilities(grid, possibleStates, possibleStatesNum);
              
              break;

            case SWIPE_RIGHT:
              slideHistory.push(Actions.SWIPE_DOWN);
              if (!grid.canMoveDown()) {
                sumOfRewards.push(0f);
                break loop;
              }
              grid.slideDown(false);

              if (grid.won()) {
                sumOfRewards.push(1f);
                break loop;
              } else if (grid.lost()) {
                sumOfRewards.push(0f);
                break loop;
              }

              addPossibilities(grid, possibleStates, possibleStatesNum);
              
              break;

            case SWIPE_DOWN:
              slideHistory.push(Actions.SWIPE_LEFT);
              if (!grid.canMoveLeft()) {
                sumOfRewards.push(0f);
                break loop;
              }
              grid.slideLeft(false);

              if (grid.won()) {
                sumOfRewards.push(1f);
                break loop;
              } else if (grid.lost()) {
                sumOfRewards.push(0f);
                break loop;
              }

              addPossibilities(grid, possibleStates, possibleStatesNum);
              break;
              
            case SWIPE_LEFT:

              bestReward.pop();
              break loop;
            
            default:
              throw new InvalidActionException();
        }
        System.out.println(slideHistory.peek().toString());
        try {
          System.out.println(grid.stringify());
        } catch(Exception e) {
        }
         
        rew = (1 / possibleStatesNum.peek()) *  sumOfRewards.pop();
        updateBetterReward(rew, grid, bestReward, slideHistory, sumOfRewards, map);
 
        dive(grid, possibleStates, slideHistory, possibleStatesNum, sumOfRewards, bestReward);
      }

      if (possibleStates.peek().isEmpty()) {
        // time to go up a level in the tree
        possibleStatesNum.pop();
        possibleStates.pop();
        updateBetterReward(sumOfRewards.pop(), grid, bestReward, slideHistory, sumOfRewards, map);
        continue;
      }
      
      EmptyNode nextEmptyNode = possibleStates.peek().remove(0);
      grid.undo();
      grid.setValueNode(nextEmptyNode.getPos(), 2, true); 

      dive(grid, possibleStates, slideHistory, possibleStatesNum, sumOfRewards, bestReward); 
    }
  }

  private void dive(Grid grid, Stack<List<EmptyNode>> possibleStates, Stack<Actions> slideHistory, Stack<Integer> possibleStatesNum, Stack<Float> sumOfRewards, Stack<Float> bestReward) throws NoValueException, MovingOutOfBoundsException, UnknownNodeTypeException, NoMoveFlagException {
    while(grid.canMoveUp()) {
      grid.slideUp(false);
      slideHistory.push(Actions.SWIPE_UP); 

      try {
        System.out.println("");
        System.out.println(grid.stringify());
      } catch(Exception e) {

      }


      if (grid.won()) {
        sumOfRewards.push(1f);
        return;
      } else if (grid.lost()) {
        sumOfRewards.push(0f);
        return;
      }

      addPossibilities(grid, possibleStates, possibleStatesNum);
      sumOfRewards.push(0f);
      bestReward.push(0f);

    }
    sumOfRewards.push(0f);
  }

  private void updateBetterReward(float newReward, Grid grid, Stack<Float> bestReward, Stack<Actions> slideHistory, Stack<Float> sumOfRewards, HashMap<Integer, Actions> map) {
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

