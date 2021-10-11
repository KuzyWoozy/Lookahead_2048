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


  public NodeType getType() {
    return NodeType.BRICK;
  }

  public int getValue() throws NoValueException {
    throw new NoValueException();
  }

  public void setValue(int value) {
    
  }

  @Override
  public String toString() {
    return "[" + pos + " Brick]";
  }

} 


