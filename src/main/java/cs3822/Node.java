package cs3822;


abstract class Node {
  
  protected Position pos;

  public Node() {
    try {
      this.pos = new Position(0, 0);
    } catch(MaxPosNotInitializedException e) {
    }
  }

  public Node(Position pos) {
    this.pos = new Position(pos);
  }

  public Node(Node node) {
    this.pos = new Position(node.pos);
  }


  public abstract NodeType getType();
  public abstract int getValue() throws NoValueException;
  public abstract void setValue(int value);
  public abstract String toString(); 

  public Position getPos() {
    return pos;
  }

  public void setPos(Position pos) {
    this.pos = pos;
  }

} 


