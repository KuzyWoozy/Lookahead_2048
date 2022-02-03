package cs3822;

/**
 * Abstract class for the Node object.
 *
 * @author Daniil Kuznetsov
 */
abstract class Node {
  
  final protected Position pos;

  
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
        return new BrickNode((BrickNode) node);
      case EMPTY:
        return new EmptyNode((EmptyNode) node);
      case VALUE:
        return new ValueNode((ValueNode) node);
      default:
        throw new UnknownNodeTypeException();
    } 
  }


  /** Node constructor. */
  public Node() {
    this.pos = null;
  }
  
  /** Constructs the Node with the specified position. */
  public Node(Position pos) {
    this.pos = new Position(pos);
  }

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

  public abstract Triplet<Node, Node, Integer> merge(Node node) throws UnknownNodeTypeException;
  public abstract boolean hasMerged() throws NoMergeFlagException; 
  public abstract boolean hasMoved() throws NoMoveFlagException;
  
  public abstract NodeType getType();
  public abstract String toString();
  public abstract int hashCode();
  
  public abstract int getOldValue() throws NoValueException;
  public abstract int getValue() throws NoValueException;
  public abstract Position getOldPos() throws CantMoveException;
  public abstract boolean canMove(Grid grid) throws CantMoveException;
} 


