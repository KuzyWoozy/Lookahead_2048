package cs3822;

import java.util.List;
import java.util.LinkedList;
import java.util.Arrays;

/**
 * A node in the abstract DAG of instances.
 *
 * @author Daniil Kuznetsov
 */
class TreeDFSNode {
  final private Action action;

  // Per possibility attr
  final private float bestReward;
  final private Action bestAction;

  final private float expSum;
  
  final private List<ValueNode> posi;
  final private int posiNum;


  private List<ValueNode> cloneNodes(List<ValueNode> nodes) {
    List<ValueNode> nodeCopy = Arrays.asList(new ValueNode[nodes.size()]);
    for (int i = 0; i < nodes.size(); i++) {
      nodeCopy.set(i, nodes.get(i));
    }
    return nodeCopy;
  }
  
  public TreeDFSNode() {
    this.bestReward = 0;
    this.bestAction = Action.SWIPE_UP;

    this.action = Action.SWIPE_UP;

    this.expSum = 0;

    this.posi = new LinkedList<ValueNode>();
    this.posiNum = 0;
  }


  /** Constructs a node with the given grid and probability of generating a 2.*/
  public TreeDFSNode(Grid grid) {
    this.bestReward = 0;
    this.bestAction = Action.SWIPE_UP;

    this.action = Action.SWIPE_UP;

    this.expSum = 0;
    
    List<EmptyNode> emptyNodes = grid.getEmptyNodesCopy();
    this.posiNum = emptyNodes.size();

    this.posi = new LinkedList<ValueNode>();
    // Update grid with the given possibility
    for (EmptyNode node : emptyNodes) { 
      this.posi.add(new ValueNode(node, 2));
      this.posi.add(new ValueNode(node, 4));
    }
  }

  public TreeDFSNode(TreeDFSNode node, Action action) {
    this.bestReward = node.bestReward;
    this.bestAction = node.bestAction;

    this.action = action;

    this.expSum = node.expSum;
   
    this.posi = cloneNodes(node.posi);
    this.posiNum = node.posiNum;
  }


  public TreeDFSNode(TreeDFSNode node, float reward, Action action) {
    if (reward > node.bestReward) {
      this.bestReward = reward;
      this.bestAction = action;
    } else {
      this.bestReward = node.bestReward;
      this.bestAction = node.bestAction;
    }

    this.action = action;

    this.expSum = node.expSum;
   
    this.posi = cloneNodes(node.posi);
    this.posiNum = node.posiNum;
  }

  public TreeDFSNode(TreeDFSNode node, float prob, List<ValueNode> rest) {
    this.bestReward = 0;
    this.bestAction = Action.SWIPE_UP;

    this.action = Action.SWIPE_UP;

    this.expSum = node.expSum + prob * node.bestReward;
    
    this.posi = cloneNodes(rest); 
    
    this.posiNum = node.posiNum;
  }


  public Action getBestAction() {
    return bestAction;
  }

  public Action getAction() {
    return action;
  }

  public float getBestReward() {
    return bestReward;
  }

  public List<ValueNode> getPossibilities() {
    return posi;
  }

  public ValueNode getNextPossibility() {
    return posi.get(0); 
  }

  public List<ValueNode> getRestPossibilities() {
    return posi.subList(1, posi.size());
  }

  public boolean noMorePossibilities() {
    // We always assume we processed first possibility
    if (this.posi.size() == 1) {
      return true;
    }
    return false;
  }

  public float getExpectedReward() {
    return (1f / this.posiNum) * expSum;
  }

} 
