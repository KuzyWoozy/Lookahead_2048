package cs3822;

/**
 * Abstract class for the node object.
 *
 * @author Daniil Kuznetsov
 */
public abstract class Node {
  
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

  /** 
   * Compute a pretty print of the grid.
   *
   * @return String representation of the node 
   * @throws UnknownNodeTypeException Unknown node discovered in the grid
   */
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
  
  /**
   * Merge two nodes together.
   *
   * @param node Node to merge with
   * @return Collection of the two merged nodes, and the reward from the merge 
   * @throws UnknownNodeTypeException When unknown node type is encountered
   */
  public abstract Triplet<Node, Node, Integer> merge(Node node) throws UnknownNodeTypeException;

  /** Return the merge flag. */
  public abstract boolean hasMerged() throws NoMergeFlagException; 
  /** Return the merge flag. */
  public abstract boolean hasMoved() throws NoMoveFlagException;
  
  /** Return the type of the node. */
  public abstract NodeType getType();
  
  /** Return node as a string. */
  public abstract String toString();

  /** Return hash of node. */
  public abstract int hashCode();
  
  /** Return the old value of the node. */
  public abstract int getOldValue() throws NoValueException;
  
  /** Return the value of the node. */
  public abstract int getValue() throws NoValueException;
  
  /** Return the old position of the node. */
  public abstract Position getOldPos() throws CantMoveException;
  
  /** Return true if the node can move within the specified grid. */
  public abstract boolean canMove(Grid grid) throws CantMoveException;
} 


