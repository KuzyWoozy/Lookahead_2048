package cs3822;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;


class TreeNodeMDP {

  private static Actions[] actions = new Actions[]{Actions.SWIPE_UP, Actions.SWIPE_RIGHT, Actions.SWIPE_DOWN, Actions.SWIPE_LEFT};

  Grid state;
  float prob;
  float reward;

  public TreeNodeMDP(Grid grid, HashMap<Integer, Actions> map) throws NoValueException, MovingOutOfBoundsException, UnknownNodeTypeException, NoMoveFlagException {
    if (grid.won()) {
      reward = 1;
    } else if (grid.lost()) {
      reward = 0;
    } else {
      // Signals reward not yet found
      reward = -1;
      /*
      rewards[0] = genPosibUp(grid, map);
      rewards[1] = genPosibRight(grid, map);
      rewards[2] = genPosibDown(grid, map);
      rewards[3] = genPosibLeft(grid, map);
      
      List<Float> rewardsList = Arrays.asList(rewards);
      int rewardMaxIndex = rewardsList.indexOf(Collections.max(rewardsList));
      reward = rewards[rewardMaxIndex];
      // Store the optimal action in the map
      map.put(grid.hashCode(), actions[rewardMaxIndex]); 
      */
    }
  }

  public TreeNodeMDP(Grid grid, float prob, HashMap<Integer, Actions> map) throws NoValueException, MovingOutOfBoundsException, UnknownNodeTypeException, NoMoveFlagException {
    this(grid, map);
    this.prob = prob;
  }

  private void genPosibUp(Grid grid, HashMap<Integer, Actions> map) throws NoValueException, MovingOutOfBoundsException, UnknownNodeTypeException, NoMoveFlagException {
    grid.slideUp(false);

    List<EmptyNode> emptyList = grid.getEmptyNodes();

    float reward = 0;
    for (EmptyNode node : emptyList) {
      grid.setValueNode(node.getPos(), 2);
      reward += getExpectedReward();
      grid.setEmptyNode(node.getPos());
    }
    grid.undo();

  }

  private float genPosibRight(Grid grid, HashMap<Integer, Actions> map) throws NoValueException, MovingOutOfBoundsException, UnknownNodeTypeException, NoMoveFlagException {
    grid.slideRight(false);

    List<EmptyNode> emptyList = grid.getEmptyNodes();

    float reward = 0;
    for (EmptyNode node : emptyList) {
      grid.setValueNode(node.getPos(), 2);
      reward += (new TreeNodeMDP(grid, prob * (1 / emptyList.size()), map)).getExpectedReward();
      grid.setEmptyNode(node.getPos());
    }
    grid.undo();

    return reward;
  }

  private float genPosibDown(Grid grid, HashMap<Integer, Actions> map) throws NoValueException, MovingOutOfBoundsException, UnknownNodeTypeException, NoMoveFlagException {
    grid.slideDown(false);

    List<EmptyNode> emptyList = grid.getEmptyNodes();

    float reward = 0;
    for (EmptyNode node : emptyList) {
      grid.setValueNode(node.getPos(), 2);
      reward += (new TreeNodeMDP(grid, prob * (1 / emptyList.size()), map)).getExpectedReward();
      grid.setEmptyNode(node.getPos());
    }
    grid.undo();

    return reward;
  }

  private float genPosibLeft(Grid grid, HashMap<Integer, Actions> map) throws NoValueException, MovingOutOfBoundsException, UnknownNodeTypeException, NoMoveFlagException {
    grid.slideLeft(false);

    List<EmptyNode> emptyList = grid.getEmptyNodes();

    float reward = 0;
    for (EmptyNode node : emptyList) {
      grid.setValueNode(node.getPos(), 2);
      reward += (new TreeNodeMDP(grid, prob * (1 / emptyList.size()), map)).getExpectedReward();
      grid.setEmptyNode(node.getPos());
    }
    grid.undo();

    return reward;
  }

  public float getExpectedReward() {
    return prob * reward;
  }
 
}
