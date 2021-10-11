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

  public NodeType getType() {
    return NodeType.EMPTY;
  }

  public int getValue() throws NoValueException {
    throw new NoValueException();
  }

  public void setValue(int value) {
    
  }

  @Override
  public String toString() {
    return "[" + pos + " Empty]";
  }

} 


