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
  private float expSumReward;
  private LinkedList<ValueNode> posi;
  private int posiNum;

  private float twoProb;
  private float fourProb;

  private int currentNodeValue;

  /** Initial state constructor. */
  public TreeDFSNode(float twoProb) {
    this.bestAction = Action.SWIPE_UP;
    this.bestReward = 0;
    this.action = Action.SWIPE_UP;

    this.expSumReward = 0;
    
    this.posi = new LinkedList<ValueNode>();
    this.posiNum = 0;

    this.twoProb = twoProb;
    this.fourProb = 1 - twoProb;
  }


  /** Constructs a node with the given grid and probability of generating a 2.*/
  public TreeDFSNode(Grid grid, float twoProb) {
    this(twoProb);

    List<EmptyNode> emptyNodes = grid.getEmptyNodesCopy();
    this.posiNum = emptyNodes.size();
    // Update grid with the given possibility
    for (EmptyNode node : emptyNodes) { 
      this.posi.add(new ValueNode(node, 2));
      this.posi.add(new ValueNode(node, 4));
    }
    
    ValueNode node = posi.remove(0);
    try {
      this.currentNodeValue = node.getValue();
      grid.setValueNode(node, true);
    } catch(UnknownNodeTypeException | NoValueException e) {
      e.printStackTrace();
      System.exit(1);
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
 
  public void commitReward(Grid grid) throws InvalidValueException {
    // Remove the previous possibility insertion
    grid.undo();

    // Now that we processed the previous node, we can delete it
    if (currentNodeValue == 2) {
      expSumReward += twoProb * bestReward; 
    } else if (currentNodeValue == 4) {
      expSumReward += fourProb * bestReward;
    } else {
      throw new InvalidValueException();
    }
  }

  /** Process the next possibility, updating the grid accordingly. */
  public void setNextPosi(Grid grid) {
  
    commitReward(grid);

    // Init for next possibility
    bestReward = 0;
    action = Action.SWIPE_UP;
    bestAction = Action.SWIPE_UP;

    ValueNode node = posi.remove(0);
    currentNodeValue = node.getValue();
    grid.setValueNode(node, true); 
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
