package cs3822;

import java.util.Objects;


class ValueNode extends Node {
  
  private int value;
  private boolean moveFlag = false;

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

  public ValueNode(Node node) throws NoValueException {
    super(node);
    this.value = node.getValue();
  }

  public ValueNode(EmptyNode node, int value) {
    super(node.getPos());
    this.value = value;
  }

  public ValueNode(Position pos, int value) {
    super(pos);
    this.value = value;
  }

  public NodeType getType() {
    return NodeType.VALUE;
  }

  @Override
  public int getValue() {
    return value;
  }

  @Override
  public void setValue(int value) {
    this.value = value;
  }

  @Override
  public boolean getMoveFlag() {
    return moveFlag;
  }

  @Override
  public void onMoveFlag() {
    moveFlag = true;
  }

  @Override
  public void offMoveFlag() {
    moveFlag = false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(pos, NodeType.VALUE, value);
  }

  @Override
  public String toString() {
    return "[" + pos + " " + String.valueOf(value) + "]";
  }

} 


