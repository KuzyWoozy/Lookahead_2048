package cs3822;


class BrickNode extends Node {

  public BrickNode() {
    super();
  }

  public BrickNode(Position pos) {
    super(pos);
  }

  public BrickNode(BrickNode node) {
    super(node);
  }

  @Override
  public NodeType getType() {
    return NodeType.BRICK;
  }

  @Override
  public int getValue() throws NoValueException {
    throw new NoValueException();
  }

  @Override
  public void setValue(int value) {
    
  }


  @Override
  public boolean getMoveFlag() throws NoMoveFlagException {
    throw new NoMoveFlagException();
  }

  @Override
  public void onMoveFlag() {

  }

  @Override
  public void offMoveFlag() {

  }

  @Override
  public String toString() {
    return "[" + pos + " Brick]";
  }

} 


