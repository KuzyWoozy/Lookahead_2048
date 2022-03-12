package cs3822;

import java.util.Random;
import java.util.List;
import java.util.LinkedList;
import java.util.Arrays;

import org.ejml.simple.SimpleMatrix;

/**
 * Representation of the 2048 grid, in which nodes reside.
 *
 * @author Daniil Kuznetsov
 */
class Grid {
  final private String map;

  final private List<Node> nodes;

  final private int score;

  final private float twoProb;
  final private int winCondition;

  final private int columnSize;
  final private int rowSize;

  final private Node generatedNode;
  
  final private Random rand = new Random();
 
  /**
   * Constructor for the grid object.
   *
   * @param map String representation of the grid to construct
   * @param winCondition Target value for the game to achieve
   * @param twoProb Probability of generating a 2, the rest is probability of generating a 4
   * @param generate True if the two starting nodes should be generated
   */
  public Grid(String map, int winCondition, float twoProb, boolean generate) throws InvalidMapSizeException {
    this.map = map;
    this.twoProb = twoProb;
    this.winCondition = winCondition;
    this.score = 0;

    List<String> rows = Arrays.asList(map.strip().split("\\|"));
    this.columnSize = rows.get(0).length();
    this.rowSize = rows.size();
    
    // Quick verification for shape of map correctness
    for (String row : rows.subList(1, rowSize)) {
      if (row.length() != columnSize) throw new InvalidMapSizeException();
    }

    this.nodes = Arrays.asList(new Node[columnSize * rowSize]);
    Position pos = null;
    // Initialize Node list
    for (int y = 0; y < rowSize; y++) {
      for (int x = 0; x < columnSize; x++) {
        try {
          pos = new Position(x, y);
          this.nodes.set(index(pos), createNode(rows.get(y).charAt(x), pos)); 
        } catch(InvalidMapSymbolException e) {
          this.nodes.set(index(pos), new EmptyNode(pos));
        }
      } 
    } 
    // Generate the initial nodes
    if (generate) {
      generateNewNode(this.nodes);
      generateNewNode(this.nodes);
    }
    this.generatedNode = null; 
  }
  
  /**
   * Alternate grid copy constructor.
   *
   * @param nodes Array of Node objects
   * @param columnSize Width of the grid
   * @param rowSize Height of the grid
   * @param winCondition Target value for the game to achieve
   * @param twoProb Probability of generating a 2, the rest is probability of generating a 4
   */
  public Grid(List<Node> nodes, int columnSize, int rowSize, int winCondition, float twoProb) {
    this.map = "";

    this.twoProb = twoProb;
    this.winCondition = winCondition;
    this.score = 0;

    this.columnSize = columnSize;
    this.rowSize = rowSize;
    
    this.nodes = nodes;

    this.generatedNode = null;
  }

  /** Grid copy constructor. */
  public Grid(Grid grid) {
    this.map = new String(grid.map);
    this.twoProb = grid.twoProb;
    this.winCondition = grid.winCondition;
    this.score = grid.score;
    this.nodes = grid.cloneNodes(grid.nodes); 
    this.rowSize = grid.rowSize;
    this.columnSize = grid.columnSize;
    
    this.generatedNode = null;
  }


  /** Grid copy constructor, with new score. */
  private Grid(Grid grid, List<Node> nodes, int score) {
    this.map = new String(grid.map);
    this.twoProb = grid.twoProb;
    this.winCondition = grid.winCondition;

    this.nodes = cloneNodes(nodes);
    this.score = score;
    this.rowSize = grid.rowSize;
    this.columnSize = grid.columnSize;

    this.generatedNode = null;
  }

  /** Grid copy constructor, with new score and generated node. */
  private Grid(Grid grid, List<Node> nodes, int score, Node generated) {
    this.map = new String(grid.map);
    this.twoProb = grid.twoProb;
    this.winCondition = grid.winCondition;

    this.nodes = cloneNodes(nodes);
    
    this.score = score;
    this.rowSize = grid.rowSize;
    this.columnSize = grid.columnSize;
    this.generatedNode = generated;
  }

  /** Reset the nodes to the original initial state. */
  private List<Node> resetNodes() {
    List<Node> copyNode = Arrays.asList(new Node[nodes.size()]);
    try {
      for (int i = 0; i < nodes.size(); i++) {
        copyNode.set(i, Node.copyNode(nodes.get(i)));
      }
    } catch(UnknownNodeTypeException e) {
      e.printStackTrace();
      System.exit(1);
    }
  
    return copyNode;
  }
  
  /** Clone grid's Node objects. */
  private List<Node> cloneNodes(List<Node> nodes) {
    List<Node> copyNodes = Arrays.asList(new Node[nodes.size()]);
    for (int i = 0; i < nodes.size(); i++) {
      copyNodes.set(i, nodes.get(i));
    }
    return copyNodes;
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
  
      case 'x':
        return new BrickNode(pos);

      case 'X':
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
  
  /** Return true if any of the nodes have moved. */
  private boolean moved(List<Node> nodes) {
    for (Node node : nodes) {
      if (node.getType() == NodeType.VALUE) {
        try {
          if (node.hasMoved()) {
            return true;
          }
        } catch(NoMoveFlagException e) {
          e.printStackTrace();
          System.exit(1);
        }
      }
    }
    return false;
  } 

  /** Generates a new node within the grid. */
  private Node generateNewNode(List<Node> nodes) {
    LinkedList<Position> availablePos = new LinkedList<Position>();
    // Find the available slots in the grid
    for (Node node : nodes) {
      if (node.getType() == NodeType.EMPTY) {
        availablePos.add(node.getPos()); 
      }
    }
    
    // Abort if there is no available space
    if (availablePos.size() == 0) {
      return null;
    }

    Node newNode = new ValueNode(availablePos.get(rand.nextInt(availablePos.size())), rand.nextFloat() <= twoProb ? 2 : 4);
    
    nodes.set(index(newNode.getPos()), newNode);

    return newNode;
  }
  
  /** Shift the nodes one step upwards. */
  private int slideUpIteration(List<Node> nodes, int score) {

    for (int y = 0; y < rowSize; y++) {
      for (int x = 0; x < columnSize; x++) {
        Position nodePos = new Position(x, y);

        Node nodeUp = fetchUp(nodes, nodePos);
        Node node = fetch(nodes, nodePos);
        
        Triplet<Node, Node, Integer> newNodes = null;
        try {
          newNodes = node.merge(nodeUp);
        } catch(UnknownNodeTypeException e) {
          e.printStackTrace();
          System.exit(1);
        }
        
        if (newNodes.getSecond().getPos() != null) {
          nodes.set(index(newNodes.getSecond().getPos()), newNodes.getSecond());
        }
        nodes.set(index(newNodes.getFirst().getPos()), newNodes.getFirst());
        
        score += newNodes.getThird();
      }
    }

    return score;
  }
  
  /** Shift the nodes upwards. */
  public List<Grid> slideUp(boolean generate) {
    List<Node> cloned = resetNodes(); 
    List<Grid> frames = new LinkedList<Grid>();
    frames.add(new Grid(this));
    
    int scoreCumSum = slideUpIteration(cloned, this.score);
    // Make sure a node was shifted to constitue a move 
    if (moved(cloned)) {
      do {
        frames.add(new Grid(this, cloned, scoreCumSum));
        scoreCumSum = slideUpIteration(cloned, scoreCumSum);
      } while(moved(cloned));

      if (generate) {
        Node generated = generateNewNode(cloned);
        frames.add(new Grid(this, cloned, scoreCumSum, generated));
      }
    } 
    
    return frames;
  }

  /** Shift every node upwards. */
  public List<Grid> slideUp() {
    return slideUp(true);
  }
  
  /** Shift the nodes one step rightwards. */
  private int slideRightIteration(List<Node> nodes, int score) {

    for (int y = 0; y < rowSize; y++) {
      for (int x = columnSize - 1; x >= 0; x--) {
        Position nodePos = new Position(x, y);

        Node nodeRight = fetchRight(nodes, nodePos);
        Node node = fetch(nodes, nodePos);
        
        Triplet<Node, Node, Integer> newNodes = null;
        try {
          newNodes = node.merge(nodeRight);
        } catch(UnknownNodeTypeException e) {
          e.printStackTrace();
          System.exit(1);
        }
        
        if (newNodes.getSecond().getPos() != null) {
          nodes.set(index(newNodes.getSecond().getPos()), newNodes.getSecond());
        }
        nodes.set(index(newNodes.getFirst().getPos()), newNodes.getFirst());
        
        score += newNodes.getThird();
      }
    }

    return score;
  }
  
  /** Shift the nodes rightwards. */
  public List<Grid> slideRight(boolean generate) {
    List<Node> cloned = resetNodes(); 
    List<Grid> frames = new LinkedList<Grid>();
    frames.add(new Grid(this));

    int scoreCumSum = slideRightIteration(cloned, this.score);
    if (moved(cloned)) {
      do {
        frames.add(new Grid(this, cloned, scoreCumSum));
        scoreCumSum = slideRightIteration(cloned, scoreCumSum);
      } while(moved(cloned));

      if (generate) {
        Node generated = generateNewNode(cloned);
        frames.add(new Grid(this, cloned, scoreCumSum, generated));
      }
    }
    
    return frames;
  }

  /** Slide every node rightwards. */
  public List<Grid> slideRight() {
    return slideRight(true);
  }
  
  /** Shift the nodes one step downwards. */
  private int slideDownIteration(List<Node> nodes, int score) {
    for (int y = rowSize - 1; y >= 0; y--) {
      for (int x = 0; x < columnSize; x++) {
        Position nodePos = new Position(x, y);

        Node nodeDown = fetchDown(nodes, nodePos);
        Node node = fetch(nodes, nodePos);
        
        Triplet<Node, Node, Integer> newNodes = null;
        try {
          newNodes = node.merge(nodeDown);
        } catch(UnknownNodeTypeException e) {
          e.printStackTrace();
          System.exit(1);
        }
        
        if (newNodes.getSecond().getPos() != null) {
          nodes.set(index(newNodes.getSecond().getPos()), newNodes.getSecond());
        }
        nodes.set(index(newNodes.getFirst().getPos()), newNodes.getFirst());
        
        score += newNodes.getThird();
      }
    }

    return score;
  }
  
  /** Shift the nodes downwards. */
  public List<Grid> slideDown(boolean generate) {
    List<Node> cloned = resetNodes(); 
    List<Grid> frames = new LinkedList<Grid>();
    frames.add(new Grid(this));
    
    int scoreCumSum = slideDownIteration(cloned, this.score);
    if (moved(cloned)) {
      do {
        frames.add(new Grid(this, cloned, scoreCumSum));
        scoreCumSum = slideDownIteration(cloned, scoreCumSum);
      } while(moved(cloned));

      if (generate) {
        Node generated = generateNewNode(cloned);
        frames.add(new Grid(this, cloned, scoreCumSum, generated));
      }
    } 
    
    return frames;
  }

  /** Shift the nodes downwards. */
  public List<Grid> slideDown() {
    return slideDown(true);
  }
  
  /** Shift the nodes one step leftwards. */
  private int slideLeftIteration(List<Node> nodes, int score) {
    for (int y = 0; y < rowSize; y++) {
      for (int x = 0; x < columnSize; x++) {
        Position nodePos = new Position(x, y);
        Node nodeLeft = fetchLeft(nodes, nodePos);
        Node node = fetch(nodes, nodePos);
        
        Triplet<Node, Node, Integer> newNodes = null;
        try {
          newNodes = node.merge(nodeLeft);
        } catch(UnknownNodeTypeException e) {
          e.printStackTrace();
          System.exit(1);
        }
        
        if (newNodes.getSecond().getPos() != null) {
          nodes.set(index(newNodes.getSecond().getPos()), newNodes.getSecond());
        }
        nodes.set(index(newNodes.getFirst().getPos()), newNodes.getFirst());
        
        score += newNodes.getThird();
      }
    }

    return score;
  }
  
  /** Shift the nodes leftwards. */
  public List<Grid> slideLeft(boolean generate) {
    List<Node> cloned = resetNodes(); 
    List<Grid> frames = new LinkedList<Grid>();
    frames.add(new Grid(this));
    
    int scoreCumSum = slideLeftIteration(cloned, this.score);
    if (moved(cloned)) {
      do {
        frames.add(new Grid(this, cloned, scoreCumSum));
        scoreCumSum = slideLeftIteration(cloned, scoreCumSum);
      } while(moved(cloned));

      if (generate) {
        Node generated = generateNewNode(cloned);
        frames.add(new Grid(this, cloned, scoreCumSum, generated));
      }
    }
    
    return frames;
  }

  /** Slide every node leftwards. */
  public List<Grid> slideLeft() {
    return slideLeft(true);
  }
  
  /** Return the latest generated node. */
  public Node getGeneratedNode() {
    return generatedNode;
  }
  
  /** Convert position into nodes array index. */
  public int index(Position pos) {
    return pos.getY() * columnSize + pos.getX();
  }

  /** Fetch the node at the specified positon. */
  public Node fetch(Position pos) {
    return nodes.get(pos.getY() * columnSize + pos.getX());
  }

  /** Return array fetch of position. */
  public Node fetch(List<Node> nodes, Position pos) {
    return nodes.get(pos.getY() * columnSize + pos.getX());
  }

  /** Return node, one up above the specified position. */
  public Node fetchUp(Position pos) {
    if (pos.getY() - 1 < 0) {
      return new BrickNode();
    } else {
      return nodes.get((pos.getY() - 1) * columnSize + pos.getX());
    }
  }

  /** Return array fetch of position, above the specified position. */
  public Node fetchUp(List<Node> nodes, Position pos) {
    if (pos.getY() - 1 < 0) {
      return new BrickNode();
    } else {
      return nodes.get((pos.getY() - 1) * columnSize + pos.getX());
    }
  }
  
  /** Return array fetch of position, to the right of the specified position. */
  public Node fetchRight(Position pos) {
    if (pos.getX() + 1 >= columnSize) {
      return new BrickNode();
    } else {
      return nodes.get(pos.getY() * columnSize + (pos.getX() + 1));
    }
  }

  /** Return array fetch of position, to the right of the specified position. */
  public Node fetchRight(List<Node> nodes, Position pos) {
    if (pos.getX() + 1 >= columnSize) {
      return new BrickNode();
    } else {
      return nodes.get(pos.getY() * columnSize + (pos.getX() + 1));
    }
  }

  /** Return array fetch of position, below the specified position. */
  public Node fetchDown(Position pos) {
    if (pos.getY() + 1 >= rowSize) {
      return new BrickNode();
    } else {
      return nodes.get((pos.getY() + 1) * columnSize + pos.getX());
    }
  }


  /** Return array fetch of position, below the specified position. */
  public Node fetchDown(List<Node> nodes, Position pos) {
    if (pos.getY() + 1 >= rowSize) {
      return new BrickNode();
    } else {
      return nodes.get((pos.getY() + 1) * columnSize + pos.getX());
    }
  }

  /** Return array fetch of position, to the left of the specified position. */
  public Node fetchLeft(Position pos) {
    if (pos.getX() - 1 < 0) {
      return new BrickNode();
    } else {
      return nodes.get(pos.getY() * columnSize + (pos.getX() - 1));
    }
  }

  /** Return array fetch of position, to the left of the specified position. */
  public Node fetchLeft(List<Node> nodes, Position pos) {
    if (pos.getX() - 1 < 0) {
      return new BrickNode();
    } else {
      return nodes.get(pos.getY() * columnSize + (pos.getX() - 1));
    }
  }

  /** Return grid array state. */
  public List<Node> getNodes() {
    return nodes;
  }

  /** Return number of rows in the grid. */
  public int getRows() {
    return rowSize;
  }

  /** Return number of columns in the grid. */
  public int getCols() {
    return columnSize;
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
    for (Node node : nodes) {
      if (node.getType() == NodeType.VALUE) {
        try {
          if (node.getValue() >= winCondition) {
            return true;
          }
        } catch(NoValueException e) {
          e.printStackTrace();
          System.exit(1);
        }
      }
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

  /** Return probability of generating a 2. */
  public float getTwoProb() {
    return twoProb;
  }

  /** Return how many empty nodes are in the grid. */
  public int getEmptyNodesCount() {
    int count = 0;
    for (Node node : nodes) {
      if (node.getType() == NodeType.EMPTY) {
        count++;
      }
    }
    return count;
  }

  /** Return list of empty nodes within the grid. */
  public List<EmptyNode> getEmptyNodesCopy() {
    LinkedList<EmptyNode> list = new LinkedList<EmptyNode>();
    for (Node node : nodes) {
      if (node.getType() == NodeType.EMPTY) {
        list.add(new EmptyNode((EmptyNode) node));
      }
    }
    return list;
  }

  /** Return biggest node value in the grid. */
  public int getMaxValue() {
    int max = Integer.MIN_VALUE;
    int val = 0;

    for (Node node : nodes) {
      if (node.getType() == NodeType.VALUE) {
        try {
          val = node.getValue();
        } catch(NoValueException e) {
          e.printStackTrace();
          System.exit(1);
        }
        if (val > max) {
          max = val;
        }
      }
    }

    return max;
  }

  /** 
   * Create a value node within the grid.
   *
   * @param pos Position of the node
   * @param value Value for the node to have
   */
  public Grid setValueNode(ValueNode node) {
    List<Node> cloned = cloneNodes(this.nodes); 
    cloned.set(index(node.getPos()), new ValueNode(node));
    return new Grid(this, cloned, this.score);
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
          innerStringBuffer.add(this.fetch(new Position(x, y)).stringify());
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
        innerStringBuffer.add(this.fetch(new Position(x, this.getRows()-1)).stringify());
      }

      str = String.join("|", innerStringBuffer);
      output.add(str);

    } catch(UnknownNodeTypeException e) {
      e.printStackTrace();
      System.exit(1);
    }
    
    return String.join("\n", output);
  }
  
  @Override
  public int hashCode() {
    return nodes.hashCode();
  }

  /** Return string map. */
  public String getMapString() {
    return map;
  }

  /** Return win target value. */
  public int getWinCondition() {
    return winCondition;
  }

  /** Return score. */
  public int getScore() {
    return score;
  }

  /** Return vector representation of the grid. */
  public SimpleMatrix toVector() {
    
    SimpleMatrix vector = new SimpleMatrix(nodes.size(), 1);
    for (int i = 0; i < nodes.size(); i++) {
      if (nodes.get(i).getType() == NodeType.VALUE) {
        try {
          vector.set(i, nodes.get(i).getValue());
        } catch(NoValueException e) {
          e.printStackTrace();
          System.exit(1);
        }
      } else {
        // Assuming brick nodes do not exist for now
        vector.set(i, 0);
      }
    }
    return vector;
  }

  /** Return weighted matrix element sum. */
  public float getImmediateWeightedScore() {
    SimpleMatrix weights = new SimpleMatrix(rowSize, columnSize);
    weights.fill(1d);
    
    
    weights.set(0, 0, 5);
    weights.set(0, 1, 4);
    weights.set(0, 2, 3);
    weights.set(0, 3, 2);
    weights.set(1, 0, 3);
    weights.set(1, 1, 2);
    weights.set(1, 2, 1);
    
    
    /*
    weights.set(0, 0, 5);
    weights.set(0, 1, 4);
    weights.set(0, 2, 3);
    weights.set(0, 3, 2);
    */


    weights.reshape(rowSize * columnSize, 1);
    return (float)(toVector().transpose().mult(weights).get(0));
  }

}



