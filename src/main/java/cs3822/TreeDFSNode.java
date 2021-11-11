package cs3822;

import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;


class TreeDFSNode {
  // Per possibility attr
  private Action bestAction;
  private float bestReward;
  private Action action;
  
  // Per collection of possibility attr
  private float sumOfReward = 0;
  private List<EmptyNode> posi = null;
  private int posiNum;


  public TreeDFSNode() {
    bestReward = 0;
    action = Action.SWIPE_UP;
    bestAction = Action.SWIPE_UP;
    
    this.posi = new LinkedList<EmptyNode>();
    this.posiNum = 0;
  }

  public TreeDFSNode(Grid grid) throws UnknownNodeTypeException, NoValueException {
    this.posi = grid.getEmptyNodesCopy();
    // We can do this because we have not yet 'generated' the new node on the grid after an action
    this.posiNum = posi.size();

    // Update grid with the given possibility
    grid.setValueNode(posi.get(0).getPos(), 2, true);

    bestReward = 0;
    bestAction = null;
    action = Action.SWIPE_UP;
  }

  public void setMaxReward(float reward) {
    sumOfReward -= bestReward;
    if (bestReward < reward) {
      bestReward = reward;
      bestAction = action;
    }
    sumOfReward += bestReward;
  }

  public void addSumOfReward(float reward) {
    sumOfReward += reward;
  }

  public void setNextPosi(Grid grid) throws UnknownNodeTypeException, NoValueException {
    
    // Init for next possibility
    bestReward = 0;
    bestAction = null;
    action = Action.SWIPE_UP;
    // Update grid with the given possibility 
    if (posi.size() > 0) {
      grid.undo();
      posi.remove(0);
      if (posi.size() > 0) {
        grid.setValueNode(posi.get(0).getPos(), 2, true);
      }
    } 
  }

  public Action getAction() {
    return action;
  }

  public Action getBestAction() {
    return bestAction;
  }
  
  public float getBestReward() {
    return bestReward;
  }

  public void setAction(Action action) {
    this.action = action;
  }

  public int getPosiNum() {
    return posiNum;
  }
  
  public float getSumReward() {
    return sumOfReward;
  }

  public boolean isPosiEmpty() {
    return posi.isEmpty();
  }

  public List<EmptyNode> getPosi() {
    return posi;
  }

} 
