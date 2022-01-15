package cs3822;

/**
 * Abstract class for the Node object.
 *
 * @author Daniil Kuznetsov
 */
abstract class Node {
  
  protected Position pos;

  /** 
   * Returns a copy of the given node. 
   * 
   * @param node Node to copy
   * @return Copied node
   * @throws UnknownNodeTypeException A node with an unknown type was discovered
   */
  public static Node copyNode(Node node) throws UnknownNodeTypeException {
    switch(node.getType()) {
      case BRICK:
        return new BrickNode(node);
      case EMPTY:
        return new EmptyNode(node);
      case VALUE:
        return new ValueNode(node);
      default:
        throw new UnknownNodeTypeException();
    } 
  }

  /** Node constructor. */
  public Node() {
    try {
      this.pos = new Position(0, 0);
    } catch(MaxPosNotInitializedException e) {
      e.printStackTrace();
    }
  }
  
  /** Constructs the Node with the specified position. */
  public Node(Position pos) {
    this.pos = pos;
  }

  /** Copy constructor. */
  public Node(Node node) {
    this.pos = new Position(node.pos);
  }

  public abstract NodeType getType();
  public abstract int getOldValue() throws NoValueException;
  public abstract int getValue() throws NoValueException;
  public abstract void setValue(int value) throws NoValueException;
  public abstract String toString();
  public abstract boolean getMergeFlag() throws NoMergeFlagException;
  public abstract void onMergeFlag() throws NoMergeFlagException;
  public abstract void offMergeFlag() throws NoMergeFlagException;
  
  public abstract boolean getMoveFlag() throws NoMoveFlagException;
  public abstract void onMoveFlag() throws NoMoveFlagException;
  public abstract void offMoveFlag() throws NoMoveFlagException;

  public abstract int hashCode();

  /** Return position of the node. */
  public Position getPos() {
    return pos;
  }
  
  /** Return true if the two nodes are equal. */
  public boolean equals(Node node) {
    boolean x = false;
    if (pos.equals(node.getPos()) && getType() == node.getType()) {
      if (getType() == NodeType.VALUE) {
        try {
          if (getValue() == node.getValue()) {
            x = true;
          }
        } catch(NoValueException e) {
          e.printStackTrace();
          System.exit(1);
        }
      } else {
        x = true;
      }
    }
    return x;
  }

  /** Return the string representation of the node. */
  public String stringify() throws UnknownNodeTypeException {
    switch(this.getType()) {
      case BRICK:
        return "XXXX";
      case EMPTY:
        return "    ";
      case VALUE:
        try {
          return String.format("%4d", this.getValue());
        } catch(NoValueException e) {
          e.printStackTrace();
          System.exit(1);
        }
      default:
        throw new UnknownNodeTypeException(); 
    }
  }
  
  public abstract void setOldPos(Position pos) throws CantMoveException;
  public abstract Position getOldPos() throws CantMoveException;

  public abstract void moveTo(Position pos) throws CantMoveException;

  public abstract boolean canMove(Grid grid) throws CantMoveException;
  
  public abstract boolean canMoveUp(Grid grid) throws CantMoveException, UnknownNodeTypeException;
  
  public abstract boolean canMoveRight(Grid grid) throws CantMoveException, UnknownNodeTypeException;

  public abstract boolean canMoveDown(Grid grid) throws CantMoveException, UnknownNodeTypeException;

  public abstract boolean canMoveLeft(Grid grid) throws CantMoveException, UnknownNodeTypeException;

} 


