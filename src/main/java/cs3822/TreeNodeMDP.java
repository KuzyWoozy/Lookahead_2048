package cs3822;

import java.util.List;
import java.util.LinkedList;
import java.util.Random;

class TreeNodeMDP {
  Grid state;
  float prob;
  Terminal status;

  int totalNodesNum;
  int inProgressNum;
  int wonNodesNum;
  int lostNodesNum;

  List<TreeNodeMDP> up = new LinkedList<TreeNodeMDP>();
  List<TreeNodeMDP> right = new LinkedList<TreeNodeMDP>();
  List<TreeNodeMDP> down = new LinkedList<TreeNodeMDP>();
  List<TreeNodeMDP> left = new LinkedList<TreeNodeMDP>();


  public TreeNodeMDP(Grid grid) throws UnknownNodeTypeException, NoValueException, MovingOutOfBoundsException, NoMoveFlagException, UnknownStateException {
    prob = 1;

    state = new Grid(grid); 

    if (state.lost()) {
      status = Terminal.LOST;
    } else if (state.won()) {
      status = Terminal.WON;
    } else {
      status = Terminal.IN_PROGRESS;
      genPosibUp();
      genPosibRight();
      genPosibDown();
      genPosibLeft();
    }
  
    totalNodesNum = findTotalNodes();
    inProgressNum = findNumInProgress();
    wonNodesNum = findNumWon();
    lostNodesNum = findNumLost();
  }

  public TreeNodeMDP(Grid grid, float prob) throws UnknownNodeTypeException, NoValueException, MovingOutOfBoundsException, NoMoveFlagException, UnknownStateException {
    this(grid);
    this.prob = prob;
  }


  private void genPosibUp() throws UnknownNodeTypeException, NoValueException, MovingOutOfBoundsException, NoMoveFlagException, UnknownStateException {
    Grid grid = new Grid(state);
    grid.slideUp(false);

    Grid copy;
    List<EmptyNode> emptyList = grid.getEmptyNodes();
    for (EmptyNode node : emptyList) {
      copy = new Grid(grid);
      copy.setValueNode(node.getPos(), 2);
      up.add(new TreeNodeMDP(copy, 1 / emptyList.size()));
    }
  }

  private void genPosibRight() throws UnknownNodeTypeException, NoValueException, MovingOutOfBoundsException, NoMoveFlagException, UnknownStateException {
    Grid grid = new Grid(state);
    grid.slideRight(false);

    Grid copy;
    List<EmptyNode> emptyList = grid.getEmptyNodes();
    for (EmptyNode node : emptyList) {
      copy = new Grid(grid);
      copy.setValueNode(node.getPos(), 2);
      right.add(new TreeNodeMDP(copy, 1 / emptyList.size()));
    }
  }

  private void genPosibDown() throws UnknownNodeTypeException, NoValueException, MovingOutOfBoundsException, NoMoveFlagException, UnknownStateException {
    Grid grid = new Grid(state);
    grid.slideDown(false);

    Grid copy;
    List<EmptyNode> emptyList = grid.getEmptyNodes();
    for (EmptyNode node : emptyList) {
      copy = new Grid(grid);
      copy.setValueNode(node.getPos(), 2);
      down.add(new TreeNodeMDP(copy, 1 / emptyList.size()));
    }
  }

  private void genPosibLeft() throws UnknownNodeTypeException, NoValueException, MovingOutOfBoundsException, NoMoveFlagException, UnknownStateException {
    Grid grid = new Grid(state);
    grid.slideLeft(false);

    Grid copy;
    List<EmptyNode> emptyList = grid.getEmptyNodes();
    for (EmptyNode node : emptyList) {
      copy = new Grid(grid);
      copy.setValueNode(node.getPos(), 2);
      left.add(new TreeNodeMDP(copy, 1 / emptyList.size()));
    }
  }

    private int findNumInProgress() throws UnknownStateException {
    if (status == Terminal.IN_PROGRESS) {
      int sum = 1;
      for (TreeNodeMDP node : up) {
        sum += node.findNumInProgress();
      }
      for (TreeNodeMDP node : right) {
        sum += node.findNumInProgress();
      }
      for (TreeNodeMDP node : down) {
        sum += node.findNumInProgress();
      }
      for (TreeNodeMDP node : left) {
        sum += node.findNumInProgress();
      }
      return sum;

    } else if (status == Terminal.WON) {
      return 0;
    } else if (status == Terminal.LOST) {
      return 0;
    } else {
      throw new UnknownStateException();
    }
  }

  private int findNumWon() throws UnknownStateException {
    if (status == Terminal.IN_PROGRESS) {
      int sum = 0;
      for (TreeNodeMDP node : up) {
        sum += node.findNumWon();
      }
      for (TreeNodeMDP node : right) {
        sum += node.findNumWon();
      }
      for (TreeNodeMDP node : down) {
        sum += node.findNumWon();
      }
      for (TreeNodeMDP node : left) {
        sum += node.findNumWon();
      }
      return sum;

    } else if (status == Terminal.WON) {
      return 1;
    } else if (status == Terminal.LOST) {
      return 0;
    } else {
      throw new UnknownStateException();
    }
  }
  
  private int findNumLost() throws UnknownStateException {
    if (status == Terminal.IN_PROGRESS) {
      int sum = 0;
      for (TreeNodeMDP node : up) {
        sum += node.findNumLost();
      }
      for (TreeNodeMDP node : right) {
        sum += node.findNumLost();
      }
      for (TreeNodeMDP node : down) {
        sum += node.findNumLost();
      }
      for (TreeNodeMDP node : left) {
        sum += node.findNumLost();
      }
      return sum;

    } else if (status == Terminal.LOST) {
      return 1;
    } else if (status == Terminal.WON) {
      return 0;
    } else {
      throw new UnknownStateException();
    }
  }

  private int findTotalNodes() {
    if (status == Terminal.WON || status == Terminal.LOST) {
      return 1;
    }
    int sum = 1;
    for (TreeNodeMDP node : up) {
      sum += node.findTotalNodes();
    }
    for (TreeNodeMDP node : right) {
      sum += node.findTotalNodes();
    }
    for (TreeNodeMDP node : down) {
      sum += node.findTotalNodes();
    }
    for (TreeNodeMDP node : left) {
      sum += node.findTotalNodes();
    }
    return sum;
  }

  public int getNumInProgress() {
    return inProgressNum;
  }

  public int getNumWon() {
    return wonNodesNum;
  }

  public int getNumLost() {
    return lostNodesNum;
  }

  public int getTotalNodes() {
    return totalNodesNum;
  }

  public TreeNodeMDP getNode(Grid grid) throws NoValueException {
    if (state.equals(grid)) {
      return this;
    }
    if (status == Terminal.LOST || status == Terminal.WON) {
      return null;
    }

    TreeNodeMDP found;
    for (TreeNodeMDP node : up) {
      found = node.getNode(grid);
      if (found != null) {
        return found;
      }
    }
    for (TreeNodeMDP node : right) {
      found = node.getNode(grid);
      if (found != null) {
        return found;
      }
    }
    for (TreeNodeMDP node : down) {
      found = node.getNode(grid);
      if (found != null) {
        return found;
      }
    }
    for (TreeNodeMDP node : left) {
      found = node.getNode(grid);
      if (found != null) {
        return found;
      }
    }
    return null;
  }

  public Action optimalMove(Grid grid) throws TreeNodeNotFoundException, NoValueException {
    TreeNodeMDP node = getNode(grid); 
    if (node == null) {
      throw new TreeNodeNotFoundException();
    }

    float upWon = 0;
    float upLost = 0;
    for (TreeNodeMDP nodeUp : up) {
      upWon += nodeUp.getNumWon();
      upLost += nodeUp.getNumLost();
    }
    float upRatio = upWon / upLost;
  
    float rightWon = 0;
    float rightLost = 0;
    for (TreeNodeMDP nodeRight : right) {
      rightWon += nodeRight.getNumWon();
      rightLost += nodeRight.getNumLost();
    }
    float rightRatio = rightWon / rightLost;

    float downWon = 0;
    float downLost = 0;
    for (TreeNodeMDP nodeDown : down) {
      downWon += nodeDown.getNumWon();
      downLost += nodeDown.getNumLost();
    }
    float downRatio = downWon / downLost;

    float leftWon = 0;
    float leftLost = 0;
    for (TreeNodeMDP nodeLeft : left) {
      leftWon += nodeLeft.getNumWon();
      leftLost += nodeLeft.getNumLost();
    }
    float leftRatio = leftWon / leftLost;
    

    if (Float.compare(upRatio, rightRatio) > 0 && Float.compare(upRatio, downRatio) > 0 && Float.compare(upRatio, leftRatio)> 0) {
      return Action.SWIPE_UP;

    } else if (Float.compare(rightRatio, upRatio) > 0 && Float.compare(rightRatio, downRatio) > 0 && Float.compare(rightRatio, leftRatio) > 0) {
      return Action.SWIPE_RIGHT;

    } else if (Float.compare(downRatio, upRatio) > 0 && Float.compare(downRatio, rightRatio) > 0 && Float.compare(downRatio, leftRatio) > 0) {
      return Action.SWIPE_DOWN;

    } else if (Float.compare(leftRatio, upRatio) > 0 && Float.compare(leftRatio, rightRatio) > 0 && Float.compare(leftRatio, downRatio) > 0) {
      return Action.SWIPE_LEFT;

    } else {
      // If we got to this case, then all the ratios are equal
      // Hence choose a random one
      int choice = new Random().nextInt(4);
      if (choice == 0) {
        return Action.SWIPE_UP;
      } else if (choice == 1) {
        return Action.SWIPE_RIGHT;
      } else if (choice == 2) {
        return Action.SWIPE_DOWN;
      } else {
        return Action.SWIPE_LEFT;
      }
    }
    
  }

}

