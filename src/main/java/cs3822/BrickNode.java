package cs3822;

import java.util.Objects;

/**
 * Represents an empty on the grid.
 *
 * @author Daniil Kuznetsov
 */
final class BrickNode extends Node {

  public BrickNode() {
    super();
  }

  /**
   * Instantiates the node with the specified position.
   *
   * @param pos Location of the node on the grid
   */
  public BrickNode(Position pos) {
    super(pos);
  }

  /** Copy constructor. */
  public BrickNode(BrickNode node) {
    super(node.pos);
  }

  @Override
  public Triplet<Node, Node, Integer> merge(Node node) {
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
    return NodeType.BRICK;
  }

  /** Return node as a string. */
  @Override
  public String toString() {
    return "[" + pos + " Brick]";
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


