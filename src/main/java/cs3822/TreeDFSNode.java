package cs3822;

import java.util.List;
import java.util.LinkedList;


class TreeDFSNode {
  // Per possibility attr
  private Action bestAction;
  private float bestReward;
  private Action action;
  
  // Per collection of possibility attr
  private float expSumReward = 0;
  private LinkedList<ValueNode> posi = new LinkedList<ValueNode>();
  private int posiNum = 0;

  private float twoProb;
  private float fourProb;


  public TreeDFSNode(float twoProb) {
    this.bestReward = 0;
    this.action = Action.SWIPE_UP;
    this.bestAction = Action.SWIPE_UP;

    this.twoProb = twoProb;
    this.fourProb = 1 - twoProb;
  }

  public TreeDFSNode(Grid grid, float twoProb) throws UnknownNodeTypeException, NoValueException {
    this(twoProb);

    List<EmptyNode> emptyNodes = grid.getEmptyNodesCopy();
    
    // Update grid with the given possibility
    if (emptyNodes.size() > 0) {
      for (EmptyNode node : emptyNodes) { 
        this.posi.add(new ValueNode(node, 2));
        this.posi.add(new ValueNode(node, 4));
      }

      this.posiNum = this.posi.size();
      grid.setValueNode(this.posi.get(0), true);
    }
  }

  public void setMaxReward(float reward) {
    if (bestReward < reward) {
      bestReward = reward;
      bestAction = action;
    }
  }

  public void addExpSumOfReward(float reward) {
    expSumReward += reward;
  }

  public void setNextPosi(Grid grid) throws UnknownNodeTypeException, NoValueException, InvalidValueException { 

    if (posi.size() > 0) {
      grid.undo();

      int val = posi.remove(0).getValue();
      if (val == 2) {
        this.expSumReward += this.twoProb * this.bestReward; 
      } else if (val == 4) {
        this.expSumReward += this.fourProb * this.bestReward; 
      } else {
        throw new InvalidValueException();
      }

      if (posi.size() > 0) {
        // Init for next possibility
        this.bestReward = 0;
        this.action = Action.SWIPE_UP;
        this.bestAction = Action.SWIPE_UP;

        grid.setValueNode(posi.get(0), true);
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

  public float getExpSumReward() {
    return expSumReward;
  }

  public float getExpectedReward() throws EarlyExpReturnException {
    if (posi.size() == 0) {
      return (1f / this.posiNum) * expSumReward;
    } else {
      throw new EarlyExpReturnException();
    }
  }

  public boolean isPosiEmpty() {
    return posi.isEmpty();
  }

} 
