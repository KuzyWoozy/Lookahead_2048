package cs3822;


class ValueNode extends Node {
  
  private int value;

  public ValueNode() {
    super();
  }

  public ValueNode(Position pos) {
    super(pos);
  }

  public ValueNode(ValueNode node) {
    super(node);
    this.value = node.getValue();
  }

  public ValueNode(Position pos, int value) {
    super(pos);
    this.value = value;
  }

  public NodeType getType() {
    return NodeType.VALUE;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "[" + pos + " " + String.valueOf(value) + "]";
  }

} 


