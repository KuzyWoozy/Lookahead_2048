package cs3822;

import java.util.Objects;

/**
 * Represents an node with a value on the grid.
 *
 * @author Daniil Kuznetsov
 */
class ValueNode extends Node {
  
  private int value;
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
    super(node);
    this.value = node.getValue();
    this.mergeFlag = node.mergeFlag;
    this.moveFlag = node.moveFlag;
    this.oldPos = new Position(node.oldPos);
  }

  /** Copy constructor. */
  public ValueNode(Node node) {
    super(node);
    try {
      this.value = node.getValue();
      this.mergeFlag = node.getMergeFlag();
      this.moveFlag = node.getMoveFlag();
      this.oldPos = node.getOldPos();
    } catch(NoValueException | NoMergeFlagException | NoMoveFlagException | CantMoveException e) {
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
    this.oldPos = new Position(node.getPos());
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
    this.oldPos = new Position(pos);
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

  @Override
  public Position getOldPos() {
    return oldPos;
  }

  /** Set position for the node. */
  @Override
  public void moveTo(Position pos) {
    if (!this.pos.equals(pos)) {
      onMoveFlag(); 
      this.oldPos = this.pos;
      this.pos = new Position(pos);
    }
  }
  
  @Override
  public void setOldPos(Position pos) {
    this.oldPos = pos;
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
  
  /** Return true of the specified Node can move up in the grid. */
  public boolean canMoveUp(Grid grid) throws UnknownNodeTypeException {
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
  public boolean canMoveRight(Grid grid) throws UnknownNodeTypeException {
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
  public boolean canMoveDown(Grid grid) throws UnknownNodeTypeException {
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
  public boolean canMoveLeft(Grid grid) throws UnknownNodeTypeException {
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


} 


