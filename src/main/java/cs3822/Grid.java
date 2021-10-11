package cs3822;

import java.util.List;
import java.util.Arrays;


class Grid {

  private List<Node> nodes;
  private GridHistory history;

  float fourProb;
  float twoProb;

  int columnSize;
  int rowSize;

  public Grid(String map, float fourProb, float twoProb) throws InvalidMapSizeException, InvalidMapSymbolException, MaxPosNotInitializedException, UnknownNodeTypeException, NoValueException  {
    /*
    # = empty
    ~ = brick
    2 = two
    4 = four
    */

    this.history = new GridHistory();

    this.fourProb = fourProb;
    this.twoProb = twoProb;

    // DON'T FORGET TO HANDLE ALL ERRORS
    List<String> rows = Arrays.asList(map.split("\n"));

    this.columnSize = rows.get(0).length();
    this.rowSize = rows.size();
    // Set the boundaries for Position
    Position.setMax(columnSize, rowSize);

    for (String row : rows.subList(1, rowSize)) {
      if (row.length() != columnSize) throw new InvalidMapSizeException();
    }

    nodes = Arrays.asList(new Node[rowSize * columnSize]);
    Position pos;
    // Initialize Node list
    for (int y = 0; y < rowSize; y++) {
      for (int x = 0; x < columnSize; x++) {
        pos = new Position(x, y);
        nodes.set(index(pos), createNode(rows.get(y).charAt(x), pos)); 
      } 
    } 

    // Make a back-up 
    history.add(cloneNodes());
  }

  private int index(Position pos) {
    return (pos.getY() * rowSize) + pos.getX();
  }

  private int indexUp(Position pos) {
    return ((pos.getY()-1) * rowSize) + pos.getX();
  }

  private int indexRight(Position pos) {
    return (pos.getY() * rowSize) + (pos.getX()+1);
  }

  private int indexDown(Position pos) {
    return ((pos.getY()+1) * rowSize) + pos.getX();
  }

  private int indexLeft(Position pos) {
    return (pos.getY() * rowSize) + (pos.getX()-1);
  }
 
  private Node createNode(char symbol, Position pos) throws InvalidMapSymbolException {
    switch (symbol) {
      case '#': 
        return new EmptyNode(pos);
  
      case '~':
        return new BrickNode(pos);

      case '2':
        return new ValueNode(pos, 2);

      case '4':
        return new ValueNode(pos, 4);

      default:
        throw new InvalidMapSymbolException();
    }
  }

  private List<Node> cloneNodes() throws UnknownNodeTypeException, UnknownNodeTypeException, NoValueException {
    List<Node> nodeCopy = Arrays.asList(new Node[nodes.size()]);
    for (int i = 0; i < nodes.size(); i++) {
      switch(nodes.get(i).getType()) {
        case EMPTY:
          nodeCopy.set(i, new EmptyNode(nodes.get(i).getPos()));
          break;
        case BRICK:
          nodeCopy.set(i, new BrickNode(nodes.get(i).getPos()));
          break;
        case VALUE:
          nodeCopy.set(i, new ValueNode(nodes.get(i).getPos(), nodes.get(i).getValue()));
          break;
   
        default:
          throw new UnknownNodeTypeException();
      }
    }
    return nodeCopy;
  }

  private void generateNewNode() {
    
  }

  private void swap(Node node1, Node node2) {
    Position tempPos = new Position(node1.getPos());
    node1.setPos(node2.getPos());
    node2.setPos(tempPos);

    nodes.set(index(node1.getPos()), node1);
    nodes.set(index(node2.getPos()), node2);
  }

  public void slideUp() throws NoValueException, MovingOutOfBoundsException, UnknownNodeTypeException {
    List<Node> nodesCopy = cloneNodes();

    for (final Node node : nodesCopy) {
      if (node.getType() == NodeType.VALUE) {
  
        Position pos = new Position(node.getPos());

        // Check if we have not reached the edge and that the
        // square we are traversing into is empty
        while (pos.canMoveUp()) {
          if (nodes.get(indexUp(pos)).getType() == NodeType.EMPTY) {
            pos.moveUp();
          } else {
            break;
          }
        }
        
        if (!pos.canMoveUp()) {
          swap(nodes.get(index(node.getPos())), nodes.get(index(pos)));
        } else if (nodes.get(indexUp(pos)).getType() == NodeType.BRICK) {
          swap(nodes.get(index(node.getPos())), nodes.get(index(pos)));
        } else {
          if (node.getValue() == nodes.get(indexUp(pos)).getValue()) {
            Node replacementNode = nodes.get(index(node.getPos()));
            // Merge 
            pos.moveUp();
            replacementNode.setValue(node.getValue() + nodes.get(index(pos)).getValue());
            nodes.set(index(pos), new EmptyNode(new Position(pos))); 
            swap(replacementNode, nodes.get(index(pos)));
          } else {
            swap(nodes.get(index(node.getPos())), nodes.get(index(pos)));
          }
        }
      }
    }

    // Make a back-up 
    history.add(cloneNodes());
  }

  public void slideRight() throws NoValueException, MovingOutOfBoundsException, UnknownNodeTypeException {
    List<Node> nodesCopy = cloneNodes();
    
    for (final Node node : nodesCopy) {

      if (node.getType() == NodeType.VALUE) {
  
        Position pos = new Position(node.getPos());

        // Check if we have not reached the edge and that the
        // square we are traversing into is empty
        while (pos.canMoveRight()) {
          if (nodes.get(indexRight(pos)).getType() == NodeType.EMPTY) {
            pos.moveRight();
          } else {
            break;
          }
        }
        
        if (!pos.canMoveRight()) {
          swap(nodes.get(index(node.getPos())), nodes.get(index(pos)));
        } else if (nodes.get(indexRight(pos)).getType() == NodeType.BRICK) {
          swap(nodes.get(index(node.getPos())), nodes.get(index(pos)));
        } else {
          if (node.getValue() == nodes.get(indexRight(pos)).getValue()) {
            // Merge 
            pos.moveRight();
            nodes.get(index(node.getPos())).setValue(node.getValue() + nodes.get(index(pos)).getValue());
            nodes.set(index(pos), new EmptyNode(new Position(pos))); 
            swap(nodes.get(index(node.getPos())), nodes.get(index(pos)));
          } else {
            swap(nodes.get(index(node.getPos())), nodes.get(index(pos)));
          }
        }
      }
    }
    
    // Make a back-up 
    history.add(cloneNodes());
  }



  public void slideDown() throws NoValueException, MovingOutOfBoundsException, UnknownNodeTypeException {
    List<Node> nodesCopy = cloneNodes();

    for (final Node node : nodesCopy) {
      if (node.getType() == NodeType.VALUE) {
  
        Position pos = new Position(node.getPos());

        // Check if we have not reached the edge and that the
        // square we are traversing into is empty
        while (pos.canMoveDown()) {
          if (nodes.get(indexDown(pos)).getType() == NodeType.EMPTY) {
            pos.moveDown();
          } else {
            break;
          }
        }
        
        if (!pos.canMoveDown()) {
          swap(nodes.get(index(node.getPos())), nodes.get(index(pos)));
        } else if (nodes.get(indexDown(pos)).getType() == NodeType.BRICK) {
          swap(nodes.get(index(node.getPos())), nodes.get(index(pos)));
        } else {
          if (node.getValue() == nodes.get(indexDown(pos)).getValue()) {
            Node replacementNode = nodes.get(index(node.getPos()));
            // Merge 
            pos.moveDown();
            replacementNode.setValue(node.getValue() + nodes.get(index(pos)).getValue());
            nodes.set(index(pos), new EmptyNode(new Position(pos))); 
            swap(replacementNode, nodes.get(index(pos)));
          } else {
            swap(nodes.get(index(node.getPos())), nodes.get(index(pos)));
          }
        }
      }
    }

    // Make a back-up 
    history.add(cloneNodes());
  }

  public void slideLeft() throws NoValueException, MovingOutOfBoundsException, UnknownNodeTypeException {
    List<Node> nodesCopy = cloneNodes();

    for (final Node node : nodesCopy) {
      if (node.getType() == NodeType.VALUE) {
  
        Position pos = new Position(node.getPos());

        // Check if we have not reached the edge and that the
        // square we are traversing into is empty
        while (pos.canMoveLeft()) {
          if (nodes.get(indexLeft(pos)).getType() == NodeType.EMPTY) {
            pos.moveLeft();
          } else {
            break;
          }
        }
        
        if (!pos.canMoveLeft()) {
          swap(nodes.get(index(node.getPos())), nodes.get(index(pos)));
        } else if (nodes.get(indexLeft(pos)).getType() == NodeType.BRICK) {
          swap(nodes.get(index(node.getPos())), nodes.get(index(pos)));
        } else {
          if (node.getValue() == nodes.get(indexLeft(pos)).getValue()) {
            Node replacementNode = nodes.get(index(node.getPos()));
            // Merge 
            pos.moveLeft();
            replacementNode.setValue(node.getValue() + nodes.get(index(pos)).getValue());
            nodes.set(index(pos), new EmptyNode(new Position(pos))); 
            swap(replacementNode, nodes.get(index(pos)));
          } else {
            swap(nodes.get(index(node.getPos())), nodes.get(index(pos)));
          }
        }
      }
    }

    // Make a back-up 
    history.add(cloneNodes());
  } 

  public void undo() {
    try {
      this.nodes = history.undo();
    } catch(EmptyGridHistoryException e) {
      // Do nothing if we have no history
    }
  }

  public void redo() {
    try {
      this.nodes = history.redo();
    } catch(NoFutureGridException e) {
      // Do nothing if we have no future
    }
  }

  public List<Node> getNodes() {
    return nodes;
  }

  public GridHistory getHistory() {
    return history;
  }

  public boolean lost() {
    // TODO
    // Check if there are any possible moves
    return false;
  }

}



