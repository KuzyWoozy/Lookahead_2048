package cs3822;

import java.util.Objects;

/**
 * Represents an node with a value on the grid.
 *
 * @author Daniil Kuznetsov
 */
class ValueNode extends Node {
  
  private int value;
  private boolean moveFlag = false;

  /** Default constructor */
  public ValueNode() {
    super();
  }

  /**
   * Instantiates the node with the specified position.
   *
   * @param pos Location of the node on the grid
   */
  public ValueNode(Position pos) {
    super(pos);
  }

  /** Copy constructor. */
  public ValueNode(ValueNode node) {
    super(node);
    this.value = node.getValue();
  }

  /** Copy constructor. */
  public ValueNode(Node node) {
    super(node);
    try {
      this.value = node.getValue();
    } catch(NoValueException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Create a corresponding value node.
   *
   * @param node An Empty node to replace
   * @param value Value to assign to the node
   */
  public ValueNode(EmptyNode node, int value) {
    super(node.getPos());
    this.value = value;
  }
  
  /**
   * Create a corresponding value node.
   *
   * @param node Position to create the node at
   * @param value Value to assign to the node
   */
  public ValueNode(Position pos, int value) {
    super(pos);
    this.value = value;
  }

  /** Return the type of the node. */
  public NodeType getType() {
    return NodeType.VALUE;
  }
  
  /** Return the value of the node. */
  @Override
  public int getValue() {
    return value;
  }
  
  /** Set the value for the node. */
  @Override
  public void setValue(int value) {
    this.value = value;
  }
  
  /** Return the movement flag. */
  @Override
  public boolean getMoveFlag() {
    return moveFlag;
  }

  /** Set boolean flag. */
  @Override
  public void onMoveFlag() {
    moveFlag = true;
  }

  /** Unset boolean flag. */
  @Override
  public void offMoveFlag() {
    moveFlag = false;
  }

  /** Return hash of node. */
  @Override
  public int hashCode() {
    return Objects.hash(pos, NodeType.VALUE, value);
  }

  /** Return node as a string. */
  @Override
  public String toString() {
    return "[" + pos + " " + String.valueOf(value) + "]";
  }

} 


