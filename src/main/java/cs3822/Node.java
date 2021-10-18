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
  public abstract boolean getMoveFlag() throws NoMoveFlagException;
  public abstract void onMoveFlag();
  public abstract void offMoveFlag();

  public Position getPos() {
    return pos;
  }

  public void setPos(Position pos) {
    this.pos = pos;
  }

  public boolean canMove(Grid grid) throws NoValueException, UnknownNodeTypeException {
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

  

} 


