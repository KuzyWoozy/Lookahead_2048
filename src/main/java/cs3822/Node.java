package cs3822;


abstract class Node {
  

  public static Node copyNode(Node node) throws UnknownNodeTypeException, NoValueException {
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
  public abstract boolean getMoveFlag() throws NoMoveFlagException;
  public abstract void onMoveFlag();
  public abstract void offMoveFlag();
  public abstract int hashCode();

  public Position getPos() {
    return pos;
  }

  public void setPos(Position pos) {
    this.pos = pos;
  }

  public boolean canMove(Grid grid) throws NoValueException, UnknownNodeTypeException {
  
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
    
   
    return false;
  }

  public boolean canMoveUp(Grid grid) throws NoValueException, UnknownNodeTypeException {
    // check boundary
    if (pos.canMoveUp()) {
      Node node = grid.getNodes().get(grid.indexUp(pos));
      switch (node.getType()) {
        case EMPTY:
          return true;

        case BRICK:
          break;

        case VALUE:
          if (node.getValue() == this.getValue()) {
            return true;
          }
          break;
        
        default:
          throw new UnknownNodeTypeException();
      }
    }
    return false;
  }

  public boolean canMoveRight(Grid grid) throws NoValueException, UnknownNodeTypeException {
    if (pos.canMoveRight()) {
      Node node = grid.getNodes().get(grid.indexRight(pos));
      switch (node.getType()) {
        case EMPTY:
          return true;

        case BRICK:
          break;

        case VALUE:
          if (node.getValue() == this.getValue()) {
            return true;
          }
          break;
        
        default:
          throw new UnknownNodeTypeException();
      }
    }
    return false;
  }

  public boolean canMoveDown(Grid grid) throws NoValueException, UnknownNodeTypeException {
    if (pos.canMoveDown()) {
      Node node = grid.getNodes().get(grid.indexDown(pos));
      switch (node.getType()) {
        case EMPTY:
          return true;

        case BRICK:
          break;

        case VALUE:
          if (node.getValue() == this.getValue()) {
            return true;
          }
          break;
        
        default:
          throw new UnknownNodeTypeException();
      }
    }
    return false;
  }

  public boolean canMoveLeft(Grid grid) throws NoValueException, UnknownNodeTypeException {
    if (pos.canMoveLeft()) {
      Node node = grid.getNodes().get(grid.indexLeft(pos));
      switch (node.getType()) {
        case EMPTY:
          return true;

        case BRICK:
          break;

        case VALUE:
          if (node.getValue() == this.getValue()) {
            return true;
          }
          break;
        
        default:
          throw new UnknownNodeTypeException();
      }
    }
    return false;
  }

  public boolean equals(Node node) throws NoValueException {
    return pos.equals(node.getPos()) && getType() == node.getType() && getValue() == node.getValue();
  }

  public String stringify() throws UnknownNodeTypeException, NoValueException {
    switch(this.getType()) {
      case BRICK:
        return "XXXX";
      case EMPTY:
        return "    ";
      case VALUE:
        return String.format("%4d", this.getValue());
      default:
        throw new UnknownNodeTypeException(); 
    }
  }

} 


