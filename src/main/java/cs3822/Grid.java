package cs3822;

import java.util.Random;
import java.util.List;
import java.util.LinkedList;
import java.util.Arrays;


class Grid {

  private List<Node> nodes;
  private GridHistory history;

  private float twoProb;
  private int winCondition;

  private int columnSize;
  private int rowSize;

  private Random rand = new Random();

  

  public Grid(String map, int win_condition, float twoProb) throws InvalidMapSizeException, InvalidMapSymbolException, MaxPosNotInitializedException, UnknownNodeTypeException, NoValueException  {
    /*
    # = empty
    ~ = brick
    2 = two
    4 = four
    */

    this.twoProb = twoProb;
    this.winCondition = win_condition;
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
    
    generateNewNode();
    generateNewNode();
    
    this.history = new GridHistory(cloneNodes());
  }


  public Grid(Grid grid) throws UnknownNodeTypeException, NoValueException {

    this.twoProb = grid.twoProb;
    this.winCondition = grid.winCondition;
    this.history = new GridHistory(grid.history);
    this.nodes = Arrays.asList(new Node[grid.rowSize * grid.columnSize]);
    for (int i = 0; i < grid.nodes.size(); i++) {
      this.nodes.set(i, Node.copyNode(grid.nodes.get(i)));
    }
    this.rowSize = grid.rowSize;
    this.columnSize = grid.columnSize;
    this.rand = new Random();
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

  public void generateNewNode() {
    LinkedList<Position> availablePos = new LinkedList<Position>();

    for (Node node : nodes) {
      if (node.getType() == NodeType.EMPTY) {
        availablePos.add(node.getPos()); 
      }
    }

    if (availablePos.size() == 0) {
      return;
    }

    Node newNode = new ValueNode(availablePos.get(rand.nextInt(availablePos.size())), rand.nextFloat() <= twoProb ? 2 : 4);

    nodes.set(index(newNode.getPos()), newNode);
  }

  private void swap(Node node1, Node node2) {
    Position tempPos = new Position(node1.getPos());
    node1.setPos(node2.getPos());
    node2.setPos(tempPos);

    nodes.set(index(node1.getPos()), node1);
    nodes.set(index(node2.getPos()), node2);
  }

  private void clearMoveFlags() {
    for (Node node : nodes) {
      if (node.getType() == NodeType.VALUE) {
        node.offMoveFlag();
      }
    }
  }

  public void slideUp() throws NoValueException, MovingOutOfBoundsException, UnknownNodeTypeException, NoMoveFlagException {
    slideUp(true);
  }

  public void slideUp(boolean genNewNode) throws NoValueException, MovingOutOfBoundsException, UnknownNodeTypeException, NoMoveFlagException {
    List<Node> nodesCopy = cloneNodes();

    for (int y = 0; y < rowSize; y++) {
      for (int x = 0; x < columnSize; x++) {
        Node node = nodesCopy.get((y * rowSize) + x);
        
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
            if (node.getValue() == nodes.get(indexUp(pos)).getValue() && !nodes.get(indexUp(pos)).getMoveFlag()) {
              
              pos.moveUp();
              Node replacementNode = nodes.get(index(pos));
              replacementNode.onMoveFlag();
             
              replacementNode.setValue(node.getValue() + nodes.get(index(pos)).getValue());

              nodes.set(index(node.getPos()), new EmptyNode(new Position(node.getPos())));

            } else {
              swap(nodes.get(index(node.getPos())), nodes.get(index(pos)));
            }
          }
        }
      }
    }
    
    clearMoveFlags();
    if (genNewNode) {
      generateNewNode();
    }
    // Make a back-up 
    history.add(cloneNodes());
  }

  public void slideRight() throws NoValueException, MovingOutOfBoundsException, UnknownNodeTypeException, NoMoveFlagException {
    slideRight(true);
  }

  public void slideRight(boolean genNewNode) throws NoValueException, MovingOutOfBoundsException, UnknownNodeTypeException, NoMoveFlagException {
    List<Node> nodesCopy = cloneNodes();
    
    for (int y = 0; y < rowSize; y++) {
      for (int x = (columnSize-1); x >= 0; x--) {
        Node node = nodesCopy.get((y * rowSize) + x);

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
            if (node.getValue() == nodes.get(indexRight(pos)).getValue() && !nodes.get(indexRight(pos)).getMoveFlag()) {
              // Merge 
              pos.moveRight();
              
              Node replacementNode = nodes.get(index(pos));
              replacementNode.onMoveFlag();
             
              replacementNode.setValue(node.getValue() + nodes.get(index(pos)).getValue());

              nodes.set(index(node.getPos()), new EmptyNode(new Position(node.getPos())));
            } else {
              swap(nodes.get(index(node.getPos())), nodes.get(index(pos)));
            }
          }
        }
      }
    }

    clearMoveFlags();
    if (genNewNode) {
      generateNewNode();
    }
    // Make a back-up 
    history.add(cloneNodes());
  }

  public void slideDown() throws NoValueException, MovingOutOfBoundsException, UnknownNodeTypeException, NoMoveFlagException {
    slideDown(true);
  }

  public void slideDown(boolean genNewNode) throws NoValueException, MovingOutOfBoundsException, UnknownNodeTypeException, NoMoveFlagException {
    List<Node> nodesCopy = cloneNodes();

    
    for (int y = (rowSize-1); y >= 0; y--) {
      for (int  x = 0; x < columnSize; x ++) {
        Node node = nodesCopy.get((y * rowSize) + x);


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
            if (node.getValue() == nodes.get(indexDown(pos)).getValue() && !nodes.get(indexDown(pos)).getMoveFlag()) {
              // Merge 
              pos.moveDown();
              
              Node replacementNode = nodes.get(index(pos));
              replacementNode.onMoveFlag();
             
              replacementNode.setValue(node.getValue() + nodes.get(index(pos)).getValue());

              nodes.set(index(node.getPos()), new EmptyNode(new Position(node.getPos())));

            } else {
              swap(nodes.get(index(node.getPos())), nodes.get(index(pos)));
            }
          }
        }
      }
    }

    clearMoveFlags();
    if (genNewNode) {
      generateNewNode();
    }
    // Make a back-up 
    history.add(cloneNodes());
  }

  public void slideLeft() throws NoValueException, MovingOutOfBoundsException, UnknownNodeTypeException, NoMoveFlagException {
    slideLeft(true);
  }

  public void slideLeft(boolean genNewNode) throws NoValueException, MovingOutOfBoundsException, UnknownNodeTypeException, NoMoveFlagException {
    List<Node> nodesCopy = cloneNodes();

    for (int y = 0; y < rowSize; y++) {
      for (int x = 0; x < columnSize; x++) {
        Node node = nodesCopy.get((y * rowSize) + x);


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
            if (node.getValue() == nodes.get(indexLeft(pos)).getValue() && !nodes.get(indexLeft(pos)).getMoveFlag()) { 
              // Merge 
              pos.moveLeft();
              Node replacementNode = nodes.get(index(pos));
              replacementNode.onMoveFlag();
             
              replacementNode.setValue(node.getValue() + nodes.get(index(pos)).getValue());

              nodes.set(index(node.getPos()), new EmptyNode(new Position(node.getPos()))); 
   
            } else {
              swap(nodes.get(index(node.getPos())), nodes.get(index(pos)));
            }
          }
        }
      }
    }
    
    clearMoveFlags();
    if (genNewNode) {
      generateNewNode();
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

  public int index(Position pos) {
    return (pos.getY() * rowSize) + pos.getX();
  }

  public int indexUp(Position pos) {
    return ((pos.getY()-1) * rowSize) + pos.getX();
  }

  public int indexRight(Position pos) {
    return (pos.getY() * rowSize) + (pos.getX()+1);
  }

  public int indexDown(Position pos) {
    return ((pos.getY()+1) * rowSize) + pos.getX();
  }

  public int indexLeft(Position pos) {
    return (pos.getY() * rowSize) + (pos.getX()-1);
  }
 

  public List<Node> getNodes() {
    return nodes;
  }

  public GridHistory getHistory() {
    return history;
  }

  public int getRows() {
    return rowSize;
  }

  public int getCols() {
    return columnSize;
  }

  public boolean lost() throws NoValueException, UnknownNodeTypeException {
    for (Node node : nodes) {
      if (node.getType() == NodeType.EMPTY) {
        return false;
      } else if (node.getType() == NodeType.VALUE) {
        if (node.canMove(this)) {
          return false;
        }
      }
    } 
    return true;
  }

  public boolean canMoveUp() throws UnknownNodeTypeException, NoValueException {
    for (Node node : nodes) {
      if (node.getType() == NodeType.EMPTY) {
        return true;
      } else if (node.getType() == NodeType.VALUE) {
        if (node.canMoveUp(this)) {
          return true;
        }
      }
    } 
    return false;
  }

  public boolean canMoveRight() throws UnknownNodeTypeException, NoValueException {
    for (Node node : nodes) {
      if (node.getType() == NodeType.EMPTY) {
        return true;
      } else if (node.getType() == NodeType.VALUE) {
        if (node.canMoveRight(this)) {
          return true;
        }
      }
    } 
    return false;
  }

  public boolean canMoveDown() throws UnknownNodeTypeException, NoValueException {
    for (Node node : nodes) {
      if (node.getType() == NodeType.EMPTY) {
        return true;
      } else if (node.getType() == NodeType.VALUE) {
        if (node.canMoveDown(this)) {
          return true;
        }
      }
    } 
    return false;
  }

  public boolean canMoveLeft() throws UnknownNodeTypeException, NoValueException {
    for (Node node : nodes) {
      if (node.getType() == NodeType.EMPTY) {
        return true;
      } else if (node.getType() == NodeType.VALUE) {
        if (node.canMoveRight(this)) {
          return true;
        }
      }
    } 
    return false;
  }

  public boolean won() throws NoValueException {
    for (Node node : nodes) {
      if (node.getType() == NodeType.VALUE) {
        if (node.getValue() >= winCondition) {
          return true;
        }
      }
    }
    return false;
  }
  
  
  public boolean equals(Grid grid) throws NoValueException {

    if (this.rowSize != grid.rowSize || this.columnSize != grid.columnSize) {
      return false;
    }

    for (int i = 0; i < grid.nodes.size(); i++) {
      if (!this.nodes.get(i).equals(grid.nodes.get(i))) {
        return false;
      }
    }
    return true;
  }

  public List<EmptyNode> getEmptyNodesCopy() {
    LinkedList<EmptyNode> list = new LinkedList<EmptyNode>();
    for (Node node : nodes) {
      if (node.getType() == NodeType.EMPTY) {
        list.add(new EmptyNode(node));
      }
    }
    return list;
  }

  public void setValueNode(Position pos, int value) {
    nodes.set(index(pos), new ValueNode(pos, value));
  }

  public void setValueNode(Position pos, int value, boolean flag) throws UnknownNodeTypeException, NoValueException {
    if (flag) {
      nodes.set(index(pos), new ValueNode(pos, value));
      history.add(cloneNodes());
    } else {
      nodes.set(index(pos), new ValueNode(pos, value));
    }
  }

  public void setEmptyNode(Position pos) {
    nodes.set(index(pos), new EmptyNode(pos));
  }

  public String stringify() throws UnknownNodeTypeException, MaxPosNotInitializedException, NoValueException {

    LinkedList<String> output = new LinkedList<String>();
    LinkedList<String> innerStringBuffer = new LinkedList<String>();
    String str;

    for (int y = 0; y < this.getRows()-1; y++) {
      innerStringBuffer.clear();
      for (int x = 0; x < this.getCols(); x++) {
        innerStringBuffer.add(this.getNodes().get(this.index(new Position(x, y))).stringify());
      }

      str = String.join("|", innerStringBuffer);
      output.add(str);
      innerStringBuffer.clear();

      for (int i = 0; i < str.length(); i++) {
        innerStringBuffer.add("-");
      }
      output.add(String.join("", innerStringBuffer));
    }

    innerStringBuffer.clear();
    for (int x = 0; x < this.getCols(); x++) {
      innerStringBuffer.add(this.getNodes().get(this.index(new Position(x, this.getRows()-1))).stringify());
    }

    str = String.join("|", innerStringBuffer);
    output.add(str);
    
    return String.join("\n", output);
  }
  
  @Override
  public int hashCode() {
    return nodes.hashCode();
  }


}



