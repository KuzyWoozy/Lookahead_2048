package cs3822;

import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;


class TreeDFSNode {
  // Per possibility attr
  private Action bestAction;
  private float bestReward;
  private float sumOfRewardLocal;
  private Action action;
  
  // Per collection of possibility attr
  private float sumOfReward = 0;
  private List<EmptyNode> posi = null;
  private int posiNum;


  public TreeDFSNode() {
    bestReward = 0;
    sumOfRewardLocal = 0;
    bestAction = null;
    action = Action.SWIPE_UP;

    this.posi = new LinkedList<EmptyNode>();
    this.posiNum = 0;
  }

  public TreeDFSNode(Grid grid) throws UnknownNodeTypeException, NoValueException {
    this.posi = grid.getEmptyNodesCopy();
    this.posiNum = posi.size();
    
    bestReward = 0;
    sumOfRewardLocal = 0;
    bestAction = null;
    action = Action.SWIPE_UP;
    // Update grid with the given possibility
    if (posiNum > 0) {
      grid.setValueNode(posi.get(0).getPos(), 2, true);
    }
  }

  public void addReward(float reward) {
    sumOfReward += reward;
    sumOfRewardLocal += reward;
    if (bestReward <= reward) {
      bestReward = reward;
      bestAction = action;
    }
  }

  public void addSumOfReward(float reward) {
    sumOfReward += reward;
  }

  public void setNextPosi(Grid grid) throws UnknownNodeTypeException, NoValueException {
    
    // Init for next possibility
    bestReward = 0;
    sumOfRewardLocal = 0;
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

  public void setAction(Action action) {
    this.action = action;
  }

  public int getPosiNum() {
    return posiNum;
  }
  
  public float getSumReward() {
    return sumOfReward;
  }

  public float getSumRewardLocal() {
    return sumOfRewardLocal;
  }

  public boolean isPosiEmpty() {
    return posi.isEmpty();
  }

  public List<EmptyNode> getPosi() {
    return posi;
  }

} 
