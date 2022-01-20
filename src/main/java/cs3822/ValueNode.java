package cs3822;

import java.util.Objects;

/**
 * Represents an node with a value on the grid.
 *
 * @author Daniil Kuznetsov
 */
class ValueNode extends Node {
  
  private int value;
  private int oldValue;
  private boolean mergeFlag = false;
  private boolean moveFlag = false;
  private Position oldPos = null;

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
    this.oldPos = new Position(pos);
  }

  /** Copy constructor. */
  public ValueNode(ValueNode node) {
    super(node.pos);
    this.oldValue = node.oldValue;
    this.value = node.value;
    this.mergeFlag = node.mergeFlag;
    this.moveFlag = node.moveFlag;
    this.oldPos = new Position(node.oldPos);
  }

  /**
   * Create a corresponding value node.
   *
   * @param node An Empty node to replace
   * @param value Value to assign to the node
   */
  public ValueNode(EmptyNode node, int value) {
    super(node.getPos());
    this.oldPos = new Position(node.getPos());
    this.value = value;
    this.oldValue = value;
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
    this.oldValue = value;
    this.oldPos = new Position(pos);
  }
 
  /** Return true of the specified Node can move up in the grid. */
  private boolean canMoveUp(Grid grid) throws UnknownNodeTypeException {
    // check boundary
    if (pos.canMoveUp()) {
      Node node = grid.getNodes().get(grid.indexUp(pos));
      switch (node.getType()) {
        case EMPTY:
          return true;

        case BRICK:
          break;

        case VALUE:
          try {
            if (node.getValue() == this.getValue()) {
              return true;
            }
          } catch (NoValueException e) {
            e.printStackTrace();
            System.exit(1);
          }
          break;
        
        default:
          throw new UnknownNodeTypeException();
      }
    }
    return false;
  }
  /** Return true of the specified Node can move right in the grid. */
  private boolean canMoveRight(Grid grid) throws UnknownNodeTypeException {
    if (pos.canMoveRight()) {
      Node node = grid.getNodes().get(grid.indexRight(pos));
      switch (node.getType()) {
        case EMPTY:
          return true;

        case BRICK:
          break;

        case VALUE:
          try {
            if (node.getValue() == this.getValue()) {
              return true;
            }
          } catch(NoValueException e) {
            e.printStackTrace();
            System.exit(1);
          }
          break;
        
        default:
          throw new UnknownNodeTypeException();
      }
    }
    return false;
  }

/** Return true of the specified Node can move down in the grid. */
  private boolean canMoveDown(Grid grid) throws UnknownNodeTypeException {
    if (pos.canMoveDown()) {
      Node node = grid.getNodes().get(grid.indexDown(pos));
      switch (node.getType()) {
        case EMPTY:
          return true;

        case BRICK:
          break;

        case VALUE:
          try {
            if (node.getValue() == this.getValue()) {
              return true;
            }
          } catch(NoValueException e) {
            e.printStackTrace();
            System.exit(1);
          }
          break;
        
        default:
          throw new UnknownNodeTypeException();
      }
    }
    return false;
  }

/** Return true of the specified Node can move left in the grid. */
  private boolean canMoveLeft(Grid grid) throws UnknownNodeTypeException {
    if (pos.canMoveLeft()) {
      Node node = grid.getNodes().get(grid.indexLeft(pos));
      switch (node.getType()) {
        case EMPTY:
          return true;

        case BRICK:
          break;

        case VALUE:
          try { 
            if (node.getValue() == this.getValue()) {
              return true;
            }
          } catch(NoValueException e) {
            e.printStackTrace();
            System.exit(1);
          }
          break;
        
        default:
          throw new UnknownNodeTypeException();
      }
    }
    return false;
  }


  /** Return the type of the node. */
  public NodeType getType() {
    return NodeType.VALUE;
  }
 
  @Override
  public int getOldValue() {
    return oldValue;
  }

  /** Return the value of the node. */
  @Override
  public int getValue() {
    return value;
  }
  
  /** Set the value for the node. */
  @Override
  public void setValue(int value) {
    this.oldValue = this.value;
    this.value = value;
  }
  
  @Override
  public void setOldPos(Position pos) {
    this.oldPos = pos;
  }

  @Override
  public Position getOldPos() {
    return oldPos;
  }

  /** Return the merge flag. */
  @Override
  public boolean getMergeFlag() {
    return mergeFlag;
  }
  
  /** Set boolean flag. */
  @Override
  public void onMergeFlag() {
    mergeFlag = true;
  }
  
  /** Unset boolean flag. */
  @Override
  public void offMergeFlag() {
    mergeFlag = false;
  }

  /** Return the merge flag. */
  @Override
  public boolean getMoveFlag() {
    return moveFlag;
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
  
  /** Set position for the node. */
  @Override
  public void moveTo(Position pos) {
    if (!this.pos.equals(pos)) {
      moveFlag = true;  
      this.oldPos = this.pos;
      this.pos = new Position(pos);
    } else {
      moveFlag = false;
      this.oldPos = this.pos;
    }
  }
  
  /** Return true if the node can move within the specified grid. */
  @Override
  public boolean canMove(Grid grid) {
    try { 
      if (canMoveUp(grid)) {
        return true;
      }

      if (canMoveRight(grid)) {
        return true;
      }

      if (canMoveDown(grid)) {
        return true;
      }

      if (canMoveLeft(grid)) {
        return true;
      }
    } catch(UnknownNodeTypeException e) {
      e.printStackTrace();
      System.exit(1);
    }

    return false;
  }
  
} 


