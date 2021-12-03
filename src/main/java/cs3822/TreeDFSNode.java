package cs3822;

import java.util.List;
import java.util.LinkedList;

/**
 * A node in the abstract DAG of instances.
 *
 * @author Daniil Kuznetsov
 */
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


  /** Constructs a node with the given grid and probability of generating a 2.*/
  public TreeDFSNode(Grid grid, float twoProb) throws UnknownNodeTypeException, NoValueException {
    
    this.bestReward = 0;
    this.action = Action.SWIPE_UP;
    this.bestAction = Action.SWIPE_UP;

    this.twoProb = twoProb;
    this.fourProb = 1 - twoProb;


    List<EmptyNode> emptyNodes = grid.getEmptyNodesCopy();
    
    // Update grid with the given possibility
    if (emptyNodes.size() > 0) {
      for (EmptyNode node : emptyNodes) { 
        this.posi.add(new ValueNode(node, 2));
        this.posi.add(new ValueNode(node, 4));
      }
      
      this.posiNum = this.posi.size() / 2;
      grid.setValueNode(this.posi.get(0), true);
    }
  }

  /** Set the maximal reward obtained. */
  public void setMaxReward(float reward) {
    bestReward = reward;
  }

  /** Updates maximal reward obtained. */
  public void updateMaxReward(float reward) {
    if (bestReward < reward) {
      bestReward = reward;
      bestAction = action;
    }
  }
  
  /** Process the next possibility, updating the grid accordingly. */
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
  
  /** Return current set action. */
  public Action getAction() {
    return action;
  }

  /** Return best found so far action. */
  public Action getBestAction() {
    return bestAction;
  }
 
  /** Return best found so far reward. */
  public float getBestReward() {
    return bestReward;
  }

  /** Set action. */
  public void setAction(Action action) {
    this.action = action;
  }

  /** Return expected reward from this node. */
  public float getExpectedReward() {
    return (1f / this.posiNum) * expSumReward;
  }

  /** Return true if every possibility has been processed. */
  public boolean isPosiEmpty() {
    return posi.isEmpty();
  }

} 
