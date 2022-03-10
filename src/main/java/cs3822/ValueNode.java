package cs3822;

import java.util.Objects;

/**
 * Represents an node with a value on the grid.
 *
 * @author Daniil Kuznetsov
 */
final class ValueNode extends Node {
  
  final private int value;
  final private int oldValue;
  final private boolean mergeFlag;
  final private boolean moveFlag;
  final private Position oldPos;

  /**
   * Constructor for value node.
   *
   * @param pos Position of the node
   * @param value Value of the node
   * @param mergeFlag Merge flag
   * @param oldValue Node's old value
   * @param oldPos Node's old position
   * @param moveFlag Move flag
   */
  private ValueNode(Position pos, int value, boolean mergeFlag, int oldValue, Position oldPos, boolean moveFlag) {
    super(pos);
    this.value = value;
    this.oldValue = oldValue;
    this.mergeFlag = mergeFlag;
    this.oldPos = new Position(oldPos);
    this.moveFlag = moveFlag;
  }

  /** Copy constructor. */
  public ValueNode(ValueNode node) {
    super(node.pos);
    this.oldValue = node.oldValue;
    this.value = node.value;
    this.mergeFlag = false;
    this.moveFlag = false;
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
    this.mergeFlag = false;
    this.moveFlag = false;
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
    this.oldPos = new Position(pos);
    this.value = value;
    this.mergeFlag = false;
    this.moveFlag = false;
    this.oldValue = value;
  }
 
  /** Return true of the specified Node can move up in the grid. */
  private boolean canMoveUp(Grid grid) throws UnknownNodeTypeException {
    // check boundary
    if (pos.getY() > 0) {
      Node node = grid.fetchUp(grid.getNodes(), pos);
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
  /** Return true of the specified Node can move right in the grid. */
  private boolean canMoveRight(Grid grid) throws UnknownNodeTypeException {
    if (pos.getX() < grid.getCols() - 1) {
      Node node = grid.fetchRight(grid.getNodes(), pos);
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
    if (pos.getY() < grid.getRows() - 1) {
      Node node = grid.fetchDown(grid.getNodes(), pos);
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
    if (pos.getY() > 0) {
      Node node = grid.fetchLeft(grid.getNodes(), pos);
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
  

  @Override
  public Triplet<Node, Node, Integer> merge(Node node) throws UnknownNodeTypeException {
    switch(node.getType()) {
      case BRICK:
        return new Triplet<Node, Node, Integer>(new ValueNode(this.pos, this.value, this.mergeFlag, this.value, this.pos, false), node, 0);

      case EMPTY:
        return new Triplet<Node, Node, Integer>(new EmptyNode(this.pos), new ValueNode(node.getPos(), this.value, this.mergeFlag, this.value, this.pos, true), 0);
      case VALUE:
        try {
          if (!mergeFlag && !node.hasMerged() && value == node.getValue()) {
            return new Triplet<Node, Node, Integer>(new EmptyNode(this.pos), new ValueNode(node.pos, this.value * 2, true, this.value, this.pos, true), this.value * 2);
          } else {
            return new Triplet<Node, Node, Integer>(new ValueNode(this.pos, this.value, this.mergeFlag, this.value, this.pos, false), node, 0);
          }
        } catch(NoValueException | NoMergeFlagException e) {
          e.printStackTrace();
          System.exit(1);
        }
      default:
        throw new UnknownNodeTypeException(); 
    }
  }

  @Override
  public boolean hasMerged() {
    return mergeFlag;
  }
  
  
  @Override
  public boolean hasMoved() {
    return moveFlag;
  }

  
  @Override
  public NodeType getType() {
    return NodeType.VALUE;
  }

  
  @Override
  public String toString() {
    return "[" + pos + " " + String.valueOf(value) + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(pos, NodeType.VALUE, value);
  }

  @Override
  public int getOldValue() {
    return oldValue;
  }

  
  @Override
  public int getValue() {
    return value;
  }

  @Override
  public Position getOldPos() {
    return oldPos;
  }

  @Override
  public boolean canMove(Grid grid) {
    try { 
      if (canMoveUp(grid) || canMoveRight(grid) || canMoveDown(grid) || canMoveLeft(grid)) {
        return true;
      }
    } catch(UnknownNodeTypeException e) {
      e.printStackTrace();
      System.exit(1);
    }

    return false;
  }

} 


