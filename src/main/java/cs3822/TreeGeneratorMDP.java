package cs3822;


class TreeGeneratorMDP {

  public TreeNodeMDP initial_node;

  public TreeGeneratorMDP(Grid grid) throws UnknownNodeTypeException, NoValueException, MovingOutOfBoundsException, NoMoveFlagException, UnknownStateException {
    initial_node = new TreeNodeMDP(grid);
  }

  public TreeNodeMDP getNode(Grid grid) throws NoValueException {
    return initial_node.getNode(grid);
  }

  public int getInProgress() throws UnknownStateException {
    return initial_node.getNumInProgress();
  }

  public int getWon() throws UnknownStateException {
    return initial_node.getNumWon();
  }

  public int getLost() throws UnknownStateException {
    return initial_node.getNumLost();
  }

  public int getTotal() throws UnknownStateException {
    return initial_node.getTotalNodes();
  }

  public Action getOptimalMove(Grid grid) throws TreeNodeNotFoundException, NoValueException {
    return initial_node.optimalMove(grid);
  }

 
}

