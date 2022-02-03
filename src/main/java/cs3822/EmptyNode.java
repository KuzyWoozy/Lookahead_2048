package cs3822;

import java.util.Objects;

/**
 * Represents an empty on the grid.
 *
 * @author Daniil Kuznetsov
 */
final class EmptyNode extends Node {

  public EmptyNode() {
    super();
  }

  /**
   * Instantiates the node with the specified position.
   *
   * @param pos Location of the node on the grid
   */
  public EmptyNode(Position pos) {
    super(pos);
  }

  /** Copy constructor. */
  public EmptyNode(EmptyNode node) {
    super(node.pos);
  }

  @Override
  public Triplet<Node, Node, Integer> merge(Node node) throws UnknownNodeTypeException {
    return new Triplet<Node, Node, Integer>(this, node, 0); 
  }

  /** 
   * Return boolean flag. 
   *
   * @return Boolean value of node move flag
   * @throws NoMergeFlagException Node is not contain the move flag
   */
  @Override
  public boolean hasMerged() throws NoMergeFlagException {
    throw new NoMergeFlagException();
  }

  /** Return the move flag. */
  @Override
  public boolean hasMoved() throws NoMoveFlagException {
    throw new NoMoveFlagException();
  }

  /** Return type of node. */
  @Override
  public NodeType getType() {
    return NodeType.EMPTY;
  }

  /** Return node as a string. */
  @Override
  public String toString() {
    return "[" + pos + " Empty]";
  }
  
  /** Return hash of node. */
  @Override
  public int hashCode() {
    return Objects.hash(pos, NodeType.EMPTY);
  }

  @Override
  public int getOldValue() throws NoValueException {
    throw new NoValueException();
  }

  @Override
  public int getValue() throws NoValueException {
    throw new NoValueException();
  }

  @Override
  public Position getOldPos() throws CantMoveException {
    throw new CantMoveException();
  }

  @Override
  public boolean canMove(Grid grid) throws CantMoveException {
    throw new CantMoveException();
  }
  
} 


