package cs3822;

import java.util.Objects;

/**
 * Represents an uncollidable square on the grid.
 *
 * @author Daniil Kuznetsov
 */
class BrickNode extends Node {

  /** Default constructor */
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
    super(node);
  }

  /** Copy constructor. */
  public BrickNode(Node node) {
    super(node);
  }

  /** Return type of node. */
  @Override
  public NodeType getType() {
    return NodeType.BRICK;
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
  public void setValue(int value) throws NoValueException {
    throw new NoValueException();
  }

  /** 
   * Return boolean flag. 
   *
   * @return Boolean value of node move flag
   * @throws NoMergeFlagException Node is not contain the move flag
   */
  @Override
  public boolean getMergeFlag() throws NoMergeFlagException {
    throw new NoMergeFlagException();
  }
  
  /** Set boolean flag. */
  @Override
  public void onMergeFlag() throws NoMergeFlagException {
    throw new NoMergeFlagException();
  }

  /** Unset boolean flag. */
  @Override
  public void offMergeFlag() throws NoMergeFlagException {
    throw new NoMergeFlagException();
  }

  /** Return the move flag. */
  @Override
  public boolean getMoveFlag() throws NoMoveFlagException {
    throw new NoMoveFlagException();
  }

  /** Set boolean flag. */
  @Override
  public void onMoveFlag() throws NoMoveFlagException {
    throw new NoMoveFlagException(); 
  }

  /** Unset boolean flag. */
  @Override
  public void offMoveFlag() throws NoMoveFlagException {
    throw new NoMoveFlagException();  
  }

  /** Return hash of node. */
  @Override 
  public int hashCode() {
    return Objects.hash(pos, NodeType.BRICK);
  }

  /** Return node as a string. */
  @Override
  public String toString() {
    return "[" + pos + " Brick]";
  }
  
  @Override
  public void setOldPos(Position pos) throws CantMoveException {
    throw new CantMoveException();
  }

  @Override
  public Position getOldPos() throws CantMoveException {
    throw new CantMoveException();
  }

  @Override
  public void moveTo(Position pos) throws CantMoveException {
    this.pos = pos;
  }

  @Override
  public boolean canMove(Grid grid) throws CantMoveException {
    throw new CantMoveException();
  }
  
  @Override
  public boolean canMoveUp(Grid grid) throws CantMoveException {
    throw new CantMoveException();
  }
  
  @Override
  public boolean canMoveRight(Grid grid) throws CantMoveException {
    throw new CantMoveException();
  }

  @Override
  public boolean canMoveDown(Grid grid) throws CantMoveException {
    throw new CantMoveException();
  }

  @Override
  public boolean canMoveLeft(Grid grid) throws CantMoveException {
    throw new CantMoveException();
  }

} 


