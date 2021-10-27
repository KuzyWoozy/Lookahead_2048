package cs3822;


class EmptyNode extends Node {

  public EmptyNode() {
    super();
  }

  public EmptyNode(Position pos) {
    super(pos);
  }

  public EmptyNode(EmptyNode node) {
    super(node);
  }

  public EmptyNode(Node node) {
    super(node);
  }

  @Override
  public NodeType getType() {
    return NodeType.EMPTY;
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
    return "[" + pos + " Empty]";
  }

} 


