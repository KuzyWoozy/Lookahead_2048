package cs3822;

import java.util.Objects;


/**
 * Represents a brick node on the grid.
 *
 * @author Daniil Kuznetsov
 */
public final class BrickNode extends Node {
  
  /** Default constructor. */
  public BrickNode() {
    super();
  }

  /**
   * Instantiates the node at the specified position.
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

  @Override
  public boolean hasMerged() throws NoMergeFlagException {
    throw new NoMergeFlagException();
  }

  @Override
  public boolean hasMoved() throws NoMoveFlagException {
    throw new NoMoveFlagException();
  }

  @Override
  public NodeType getType() {
    return NodeType.BRICK;
  }

  @Override
  public String toString() {
    return "[" + pos + " Brick]";
  }
  
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


