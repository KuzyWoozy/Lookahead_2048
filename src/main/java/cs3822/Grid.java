package cs3822;

import java.util.Random;
import java.util.List;
import java.util.LinkedList;
import java.util.Arrays;


/**
 * Representation of the 2048 grid, in which nodes reside.
 *
 * @author Daniil Kuznetsov
 */
class Grid {
  private String map;

  private List<Node> nodes;
  private GridHistory history;
  private List<List<Node>> frames;

  private float twoProb;
  private int winCondition;

  private int columnSize;
  private int rowSize;

  private int hash;

  private boolean move = false;

  private Random rand = new Random();

  private Position genNodePos = null;
  /**
   * Grid constructor.
   *
   * @param map Grid map string representation
   * @param win_condition Target value to be reached to win the game
   * @param twoProb Probability of generating a node with the value 2
   */
  public Grid(String map, int winCondition, float twoProb) throws InvalidMapSizeException {

    this.map = map;

    this.twoProb = twoProb;
    this.winCondition = winCondition;


    List<String> rows = Arrays.asList(map.split("\n"));
    this.columnSize = rows.get(0).length();
    this.rowSize = rows.size();
    // Set the boundaries for Position
    Position.setMax(columnSize, rowSize);
    
    // Quick verification for shape of map correctness
    for (String row : rows.subList(1, rowSize)) {
      if (row.length() != columnSize) throw new InvalidMapSizeException();
    }

    nodes = Arrays.asList(new Node[rowSize * columnSize]);
    Position pos;
    // Initialize Node list
    for (int y = 0; y < rowSize; y++) {
      for (int x = 0; x < columnSize; x++) {
        try {
          pos = new Position(x, y);
          nodes.set(index(pos), createNode(rows.get(y).charAt(x), pos)); 
        } catch(MaxPosNotInitializedException | InvalidMapSymbolException e) {
          e.printStackTrace();
          System.exit(1);
        }
      } 
    } 
    // Generate the initial nodes
    //generateNewNode();
    //generateNewNode();
   

    // Start keeping track of history
    try {
      this.frames = new LinkedList<List<Node>>();
      this.frames.add(cloneNodes());
      this.history = new GridHistory(cloneNodes());
    } catch(UnknownNodeTypeException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  /** Grid copy constructor. */
  public Grid(Grid grid) {

    this.twoProb = grid.twoProb;
    this.winCondition = grid.winCondition;
    this.history = new GridHistory(grid.history);
    this.nodes = Arrays.asList(new Node[grid.rowSize * grid.columnSize]);
    try {
      for (int i = 0; i < grid.nodes.size(); i++) {
        this.nodes.set(i, Node.copyNode(grid.nodes.get(i)));
      }
    } catch(UnknownNodeTypeException e) {
      e.printStackTrace();
      System.exit(1);
    }
    this.rowSize = grid.rowSize;
    this.columnSize = grid.columnSize;
    this.rand = new Random();
  }
  
  /** 
   * Computes the node represented by the specified symbol and position
   *
   * @param symbol Type of node to be created
   * @param pos Position of the node within the grid
   * @return Node created with the specified symbol and position
   * @throws InvalidMapSymbolException char symbol has no known convertion into a node.
   */
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

      case '8':
        return new ValueNode(pos, 8);

      default:
        throw new InvalidMapSymbolException();
    }
  }

  /** 
   * Copies and returns the Grid representation.
   *
   * @return Copy of the grid state
   * @throws UnknownNodeTypeException Discovered node with no lnown corresponding type
   */
  private List<Node> cloneNodes() throws UnknownNodeTypeException {
    List<Node> nodeCopy = Arrays.asList(new Node[nodes.size()]);
    for (int i = 0; i < nodes.size(); i++) {
      nodeCopy.set(i, Node.copyNode(nodes.get(i)));
    }
    return nodeCopy;
  }

  /** Generates a new node within the grid. */
  public void generateNewNode() {
    LinkedList<Position> availablePos = new LinkedList<Position>();
    // Find the available slots in the grid
    for (Node node : nodes) {
      if (node.getType() == NodeType.EMPTY) {
        availablePos.add(node.getPos()); 
      }
    }
    
    // Abort if there is no available space
    if (availablePos.size() == 0) {
      return;
    }

    Node newNode = new ValueNode(availablePos.get(rand.nextInt(availablePos.size())), rand.nextFloat() <= twoProb ? 2 : 4);
    // Insert the new node into the grid
    nodes.set(index(newNode.getPos()), newNode);

    genNodePos = new Position(newNode.getPos());
  }

  private void swap(Position pos1, Position pos2) throws CantMoveException {
    Node temp = null;
    try {
      temp = Node.copyNode(nodes.get(index(pos1)));
    } catch(UnknownNodeTypeException e) {
      e.printStackTrace();
      System.exit(1);
    } 
    temp.moveTo(pos2);


    nodes.set(index(pos1), nodes.get(index(pos2)));
    nodes.get(index(pos1)).moveTo(pos1);
   

    nodes.set(index(pos2), temp);
  }
 
  private boolean moved() {
    try {
      for (Node node : nodes) {
        if (node.getType() == NodeType.VALUE && node.getMoveFlag()) {
          return true;
        }
      }
    } catch(NoMoveFlagException e) {
      e.printStackTrace();
      System.exit(1);
    }
    return false;
  }

  /** Resets the movement flags for each approxpriate node. */
  private void clearMoveFlags() {
    try {
      for (Node node : nodes) {
        if (node.getType() == NodeType.VALUE) {
          node.offMoveFlag();
        }
      }
    } catch(NoMoveFlagException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  /** Resets the merge flags for each approxpriate node. */
  private void clearMergeFlags() {
    try {
      for (Node node : nodes) {
        if (node.getType() == NodeType.VALUE) {
          node.offMergeFlag();
        }
      }
    } catch(NoMergeFlagException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  /** Slide every node upwards. */
  public void slideUp() {
    slideUp(true);
  }

  private void slideUpIteration() {
    clearMoveFlags();

    for (int y = 0; y < rowSize; y++) {
      for (int x = 0; x < columnSize; x++) {
        Node node = nodes.get((y * rowSize) + x);

        if (node.getType() == NodeType.VALUE) {
          Position pos = new Position(node.getPos());
          if (pos.canMoveUp()) {
            NodeType aboveType = nodes.get(indexUp(pos)).getType();
            try {
              if (aboveType == NodeType.BRICK) {
                continue;
              } else if (aboveType == NodeType.EMPTY) {
                pos.moveUp();    
                swap(pos, node.getPos());
              } else if (aboveType == NodeType.VALUE && node.getValue() == nodes.get(indexUp(pos)).getValue() && !node.getMergeFlag() && !nodes.get(indexUp(pos)).getMergeFlag()) {
                // Merge 
                pos.moveUp();

                node.setValue(node.getValue() * 2);

                nodes.set(index(pos), new EmptyNode(new Position(pos)));
                node.onMergeFlag();

                swap(pos, node.getPos());
              }
            } catch (NoValueException | CantMoveException | MovingOutOfBoundsException | NoMergeFlagException e) {
                e.printStackTrace();
                System.exit(1);
            }
          }
        }
      }
    }
    try {
      this.frames.add(cloneNodes());
    } catch(UnknownNodeTypeException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  /** Slide every node upwards, specifying whenever to generate a new node. */
  public void slideUp(boolean genNewNode) {
    // Clear flags before movement
    clearMergeFlags();
    this.frames.clear();
    genNodePos = null;
  
    move = false;
    slideUpIteration();
    if (moved()) {
      move = true;
      do {
        slideUpIteration(); 
      } while (moved());
      if (genNewNode) {
        generateNewNode();
        try {
          this.frames.add(cloneNodes());
        } catch(UnknownNodeTypeException e) {
          e.printStackTrace();
          System.exit(1);
        }
        
      }
      // Make a back-up 
      try {
        history.add(cloneNodes());
      } catch(UnknownNodeTypeException e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
  }


  /** Slide every node rightwards. */
  public void slideRight() {
    slideRight(true);
  }

  /** Slide every node rightwards, specifying if a new node should be generated. */
  private void slideRightIteration() {
    clearMoveFlags();
    for (int y = 0; y < rowSize; y++) {
      for (int x = (columnSize-1); x >= 0; x--) {
        Node node = nodes.get((y * rowSize) + x);

        if (node.getType() == NodeType.VALUE) {
          Position pos = new Position(node.getPos());
          if (pos.canMoveRight()) {
            NodeType rightType = nodes.get(indexRight(pos)).getType();
            try {
              if (rightType == NodeType.BRICK) {
                continue;
              } else if (rightType == NodeType.EMPTY) {
                pos.moveRight();    
                swap(pos, node.getPos());
              } else if (rightType == NodeType.VALUE && node.getValue() == nodes.get(indexRight(pos)).getValue() && !nodes.get(indexRight(pos)).getMergeFlag()) {
                  // Merge 
                  pos.moveRight();

                  node.setValue(node.getValue() * 2);

                  nodes.set(index(pos), new EmptyNode(new Position(pos)));
                  node.onMergeFlag();

                  swap(pos, node.getPos());
              }
            } catch (NoValueException | CantMoveException | MovingOutOfBoundsException | NoMergeFlagException e) {
              e.printStackTrace();
              System.exit(1);
            }
          }
        }
      }
    }
    try {
      this.frames.add(cloneNodes());
    } catch(UnknownNodeTypeException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  /** Slide every node rightwards, specifying whenever to generate a new node. */
  public void slideRight(boolean genNewNode) {
    // Clear flags before movement
    clearMergeFlags();
    this.frames.clear();
    genNodePos = null;
  
    slideRightIteration();
    move = false;
    if (moved()) {
      move = true;
      do {
        slideRightIteration(); 
      } while (moved());
      if (genNewNode) {
        generateNewNode();
        try {
          this.frames.add(cloneNodes());
        } catch(UnknownNodeTypeException e) {
          e.printStackTrace();
          System.exit(1);
        }
      }
      // Make a back-up 
      try {
        history.add(cloneNodes());
      } catch(UnknownNodeTypeException e) {
        e.printStackTrace();
        System.exit(1);
      }
    } 
  }

  /** Slide every node downwards. */
  public void slideDown() {
    slideDown(true);
  }

  /** Slide every node downwards, specifying if a new node should be generated. */
  private void slideDownIteration() {
    clearMoveFlags();

    for (int y = (rowSize-1); y >= 0; y--) {
      for (int x = 0; x < columnSize; x++) {
        Node node = nodes.get((y * rowSize) + x);

        if (node.getType() == NodeType.VALUE) {
          Position pos = new Position(node.getPos());
          if (pos.canMoveDown()) {
            NodeType belowType = nodes.get(indexDown(pos)).getType();
            try {
              if (belowType == NodeType.BRICK) {
                continue;
              } else if (belowType == NodeType.EMPTY) {
                pos.moveDown();    
                swap(pos, node.getPos());
              } else if (belowType == NodeType.VALUE && node.getValue() == nodes.get(indexDown(pos)).getValue() && !nodes.get(indexDown(pos)).getMergeFlag()) {
                  // Merge 
                  pos.moveDown();

                  node.setValue(node.getValue() * 2);

                  nodes.set(index(pos), new EmptyNode(new Position(pos)));
                  node.onMergeFlag();

                  swap(pos, node.getPos());
              }
            } catch (NoValueException | CantMoveException | MovingOutOfBoundsException | NoMergeFlagException e) {
              e.printStackTrace();
              System.exit(1);
            }
          }
        }
      }
    }
    try {
      this.frames.add(cloneNodes());
    } catch(UnknownNodeTypeException e) {
      e.printStackTrace();
    }
  }

  /** Slide every node downwards, specifying whenever to generate a new node. */
  public void slideDown(boolean genNewNode) {
    // Clear flags before movement
    clearMergeFlags();
    this.frames.clear();
    genNodePos = null;

    move = false; 
    slideDownIteration();
    if (moved()) {
      move = true;
      do {
        slideDownIteration(); 
      } while (moved());
      if (genNewNode) {
        generateNewNode();
        try {
          this.frames.add(cloneNodes());
        } catch(UnknownNodeTypeException e) {
          e.printStackTrace();
          System.exit(1);
        }
      }
      try {
        history.add(cloneNodes());
      } catch(UnknownNodeTypeException e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
  }

  /** Slide every node leftwards. */
  public void slideLeft() {
    slideLeft(true);
  }
  

  /** Slide every node leftwards, specifying if a new node should be generated. */
  private void slideLeftIteration() {
    clearMoveFlags();

    for (int y = 0; y < rowSize; y++) {
      for (int x = 0; x < columnSize; x++) {
        Node node = nodes.get((y * rowSize) + x);

        if (node.getType() == NodeType.VALUE) {
          Position pos = new Position(node.getPos());
          if (pos.canMoveLeft()) {
            NodeType leftType = nodes.get(indexLeft(pos)).getType();
            try {
              if (leftType == NodeType.BRICK) {
                continue;
              } else if (leftType == NodeType.EMPTY) {
                pos.moveLeft();    
                swap(pos, node.getPos());
                } else if (leftType == NodeType.VALUE && node.getValue() == nodes.get(indexLeft(pos)).getValue() && !nodes.get(indexLeft(pos)).getMergeFlag()) {
                    // Merge 
                    pos.moveLeft();

                    node.setValue(node.getValue() * 2);

                    nodes.set(index(pos), new EmptyNode(new Position(pos)));
                    node.onMergeFlag();

                    swap(pos, node.getPos());
                }
              } catch (NoValueException | CantMoveException | MovingOutOfBoundsException | NoMergeFlagException e) {
                e.printStackTrace();
                System.exit(1);
              }
            }
        }
      }
    }
    try {
      this.frames.add(cloneNodes());
    } catch(UnknownNodeTypeException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  /** Slide every node leftwards, specifying whenever to generate a new node. */
  public void slideLeft(boolean genNewNode) {
    // Clear flags before movement
    clearMergeFlags();
    this.frames.clear();
    genNodePos = null;
  
    slideLeftIteration();
    move = false;
    if (moved()) {
      move = true;
      do {
        slideLeftIteration(); 
      } while (moved());
      if (genNewNode) {
        generateNewNode();
        try {
          this.frames.add(cloneNodes());
        } catch(UnknownNodeTypeException e) {
          e.printStackTrace();
          System.exit(1);
        }
      }
      try {
        history.add(cloneNodes());
      } catch(UnknownNodeTypeException e) {
        e.printStackTrace();
        System.exit(1);
      }
    } 
  }
  
  public boolean getMove() {
    return move;
  }

  /** Undo the most recent action. */
  public void undo() {
    this.nodes = history.undo();
  }

  /** Redo the last undone action. */
  public void redo() { 
    List<Node> list = null;
    try {
      list = history.redo();
    } catch(NoFutureGridException e) {
      // Do nothing if we have no future
    }
    if (list != null) {
      this.nodes = list;
    }
  }
  
  /** Return array index of position. */
  public int index(Position pos) {
    return (pos.getY() * rowSize) + pos.getX();
  }

  /** Return array index of position, above the specified position. */
  public int indexUp(Position pos) {
    return ((pos.getY()-1) * rowSize) + pos.getX();
  }

  /** Return array index of position, to the right of the specified position. */
  public int indexRight(Position pos) {
    return (pos.getY() * rowSize) + (pos.getX()+1);
  }

  /** Return array index of position, below the specified position. */
  public int indexDown(Position pos) {
    return ((pos.getY()+1) * rowSize) + pos.getX();
  }

  /** Return array index of position, to the left of the specified position. */
  public int indexLeft(Position pos) {
    return (pos.getY() * rowSize) + (pos.getX()-1);
  }
 
  /** Return grid state. */
  public List<Node> getNodes() {
    return nodes;
  }

  /** Return history object. */
  public GridHistory getHistory() {
    return history;
  }
  
  /** Return number of rows in the grid. */
  public int getRows() {
    return rowSize;
  }

  /** Return number of columns in the grid. */
  public int getCols() {
    return columnSize;
  }

  public List<List<Node>> getFrames() {
    return frames;
  }

  public void setDefaultFrame() {
    this.frames.clear();
    clearMoveFlags();
    try { 
      this.frames.add(cloneNodes());
    } catch(UnknownNodeTypeException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public void clearFrames() {
    frames.clear();
  }

  /** Return true if the game is lost. */
  public boolean lost() {
    for (Node node : nodes) {
      if (node.getType() == NodeType.EMPTY) {
        return false;
      } else if (node.getType() == NodeType.VALUE) {
        try {
          if (node.canMove(this)) {
            return false;
          }
        } catch(CantMoveException e) {
          e.printStackTrace();
          System.exit(1);
        }
      }
    } 
    return true;
  }
  
  /** Return true if the win condition is satisfied. */
  public boolean won() {
    try {
      for (Node node : nodes) {
        if (node.getType() == NodeType.VALUE) {
          if (node.getValue() >= winCondition) {
            return true;
          }
        }
      }
    } catch(NoValueException e) {
      e.printStackTrace();
      System.exit(1);
    }
    return false;
  }
  
  /** Return true if grids are equal. */
  public boolean equals(Grid grid) {

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

  /** Return list of empty nodes within the grid. */
  public List<EmptyNode> getEmptyNodesCopy() {
    LinkedList<EmptyNode> list = new LinkedList<EmptyNode>();
    for (Node node : nodes) {
      if (node.getType() == NodeType.EMPTY) {
        list.add(new EmptyNode(node));
      }
    }
    return list;
  }

  /** 
   * Create a value node within the grid.
   *
   * @param pos Position of the node
   * @param value Value for the node to have
   */
  public void setValueNode(Position pos, int value) {
    nodes.set(index(pos), new ValueNode(pos, value));
  }

  /** Set a value node within the grid. */
  public void setValueNode(ValueNode node) {
    nodes.set(index(node.getPos()), new ValueNode(node));
  }

  /** 
   * Create a value node within the grid.
   *
   * @param pos Position of the node
   * @param value Value for the node to have
   * @param flag Will add this change to history if true
   */
  public void setValueNode(Position pos, int value, boolean flag) {
    if (flag) {
      nodes.set(index(pos), new ValueNode(pos, value));
      try {
        history.add(cloneNodes());
      } catch(UnknownNodeTypeException e) {
        e.printStackTrace();
        System.exit(1);
      }
    } else {
      nodes.set(index(pos), new ValueNode(pos, value));
    }
  }
  
  /** 
   * Create a value node within the grid.
   *
   * @param node Value node to set
   * @param flag Will add this change to history if true
   */
  public void setValueNode(ValueNode node, boolean flag) {
    if (flag) {
      nodes.set(index(node.getPos()), new ValueNode(node));
      try {
        history.add(cloneNodes());
      } catch(UnknownNodeTypeException e) {
        e.printStackTrace();
        System.exit(1);
      }
    } else {
      nodes.set(index(node.getPos()), new ValueNode(node));
    }
  }
  
  /** Transform the node at the speicified position into an empty one. */
  public void setEmptyNode(Position pos) {
    nodes.set(index(pos), new EmptyNode(pos));
  }

  /** Return the string representation of the grid. */
  public String stringify() {

    LinkedList<String> output = new LinkedList<String>();
    LinkedList<String> innerStringBuffer = new LinkedList<String>();
    String str;
    try {
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

    } catch(MaxPosNotInitializedException | UnknownNodeTypeException e) {
      e.printStackTrace();
      System.exit(1);
    }
    
    return String.join("\n", output);
  }
  
  /** Return hash code for the grid. */
  @Override
  public int hashCode() {
    return nodes.hashCode();
  }

  public void reset() {
    nodes = history.initialInstanceCopy();
    try {
      this.history = new GridHistory(cloneNodes());
    } catch(UnknownNodeTypeException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public void restart() {
    List<String> rows = Arrays.asList(map.split("\n"));
    
    nodes = Arrays.asList(new Node[rowSize * columnSize]);
    Position pos;
    // Initialize Node list
    try {
      for (int y = 0; y < rowSize; y++) {
        for (int x = 0; x < columnSize; x++) {
          pos = new Position(x, y);
          nodes.set(index(pos), createNode(rows.get(y).charAt(x), pos)); 
        } 
      } 
    } catch(MaxPosNotInitializedException | InvalidMapSymbolException e) {
      e.printStackTrace();
      System.exit(1);
    }
    // Generate the initial nodes
    generateNewNode();
    generateNewNode();
    
    // Start keeping track of history
    try {
      this.history = new GridHistory(cloneNodes());
    } catch(UnknownNodeTypeException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public Position getGenPos() {
    return genNodePos;
  }

  public void process(Action action) throws InvalidActionException {
    switch(action) {
      case SWIPE_UP:
        slideUp();
        break;
      case SWIPE_RIGHT:
        slideRight();
        break;
      case SWIPE_DOWN:
        slideDown();
        break;
      case SWIPE_LEFT:
        slideLeft();
        break;
      case UNDO:
        undo();
        break;
      case REDO:
        redo();
        break;
      case RESET:
        reset();
        break;
      case NONE:
        break;
      case EXIT:
        System.exit(0);
      default:
        throw new InvalidActionException();
    }
  }

  /** 
   * Iterate over the sequence of actions, calling each corresponding function in turn.
   * @param actions Sequence of actions to process
   * @throws InvalidActionException Action does not exist
   * */
  public void process(List<Action> actions) throws InvalidActionException {
    for (Action action : actions) {
      process(action);      
    }
  }


}



