package cs3822;

import java.util.Objects;

/**
 * Represents an empty on the grid.
 *
 * @author Daniil Kuznetsov
 */
class EmptyNode extends Node {

  /** Default constructor */
  public EmptyNode() {
    super();
  }

  /**
   Instantiates the node with the specified position.

   @param pos Location of the node on the grid
   */
  public EmptyNode(Position pos) {
    super(pos);
  }

  /** Copy constructor. */
  public EmptyNode(EmptyNode node) {
    super(node);
  }

  /** Copy constructor. */
  public EmptyNode(Node node) {
    super(node);
  }

  /** Return type of node. */
  @Override
  public NodeType getType() {
    return NodeType.EMPTY;
  }

  /** 
   * Return the value of the node.
   *
   * @return value of node
   * @throws NoValueException Node has no value
   */
  @Override
  public int getValue() throws NoValueException {
    throw new NoValueException();
  }

  /** Set node value. */
  @Override
  public void setValue(int value) {
    
  }

  /** 
   * Return boolean flag. 
   *
   * @return Boolean value of node move flag
   * @throws NoMoveFlagException Node is not contain the move flag
   */
  @Override
  public boolean getMoveFlag() throws NoMoveFlagException {
    throw new NoMoveFlagException();
  }

  /** Set boolean flag. */
  @Override
  public void onMoveFlag() {

  }

  /** Unset boolean flag. */
  @Override
  public void offMoveFlag() {

  }

  /** Return hash of node. */
  @Override
  public int hashCode() {
    return Objects.hash(pos, NodeType.EMPTY);
  }

  /** Return node as a string. */
  @Override
  public String toString() {
    return "[" + pos + " Empty]";
  }

} 


