package cs3822;

import java.util.Stack;
import java.util.List;
import java.util.LinkedList;

/**
 * History of a Grid instance, going back to its initial state
 * 
 * @author Daniil Kuznetsov
 */
class GridManager {
  private Stack<Grid> instances;
  private Stack<Grid> temp;

  public static boolean hasMoved(List<Grid> frames) {
    if (frames.size() == 1) {
      return false;
    }
    return true;
  } 

  /** Constructs from specified initial state of nodes. */ 
  public GridManager(Grid grid) {
    this.instances = new Stack<Grid>();
    this.instances.push(grid);
    this.temp = new Stack<Grid>();
  }

  /** Copy constructor. */
  public GridManager(GridManager history) {
    this.instances = new Stack<Grid>();
    for (Grid instance : history.instances) {
      this.instances.push(instance);
    }

    this.temp = new Stack<Grid>();
    for (Grid instance : history.temp) {
      this.temp.push(instance);
    }
  }

  public boolean isHistoryEmpty() {
    return instances.isEmpty();
  }
  
  /** Return list of nodes representing the new Grid after an undo operation. */
  public Grid undo() {
    // Do not take initial state into account for instances size
    if (instances.size() > 1) {
      temp.push(instances.pop());
      return instances.peek();
    }
    return instances.get(0);
  }

  /** 
   * Perform a redo operation on the Grid instance.
   *
   * @return list of nodes representing the new Grid
   * @throws NoFutureGridException 
   */
  public Grid redo() throws NoFutureGridException {
    if (temp.empty()) {
      throw new NoFutureGridException();
    }
    Grid instance = temp.pop();
    instances.push(instance);
    return instance;
  }

  /** Add a new state to the history and overwrite the current future. */
  public void save(Grid instance) {
    instances.push(instance);
    // Delete the current future
    temp.clear();
  }

  public Grid initial() {
    return instances.get(0);
  }

  public Grid reset() {
    Grid grid = instances.get(0);
    instances.clear();
    temp.clear();
    instances.push(grid);
    return grid;
  }

  public Grid restart() {
    Grid first = instances.get(0);
    Grid grid = null;
    try {
      grid = new Grid(first.getMapString(), first.getWinCondition(), first.getTwoProb(), true);
    } catch(InvalidMapSizeException e) {
      e.printStackTrace();
      System.exit(1);
    }
    instances.clear();
    temp.clear();
    instances.push(grid);
    return grid;
  }

  public Grid show() {
    return instances.peek();
  }


  public List<Grid> process(Action action) throws InvalidActionException {
    List<Grid> frames;
    switch(action) {
      case SWIPE_UP:
        return slideUp();
      case SWIPE_RIGHT:
        return slideRight(); 
      case SWIPE_DOWN:
        return slideDown();
      case SWIPE_LEFT:
        return slideLeft();
      case UNDO:
        frames = new LinkedList<Grid>();
        frames.add(undo());
        return frames;
      case REDO:
        try {
          frames = new LinkedList<Grid>();
          frames.add(redo());
          return frames;
        } catch(NoFutureGridException e) {
          e.printStackTrace();
          System.exit(1);
        }
        break;
      case RESET:
        frames = new LinkedList<Grid>();
        frames.add(reset());
        return frames;
      case NONE:
        frames = new LinkedList<Grid>();
        frames.add(show());
        return frames;
      case EXIT:
        System.exit(0);
      default:
        throw new InvalidActionException();
    }

    return null;
  }

  public List<Grid> process(List<Action> actions) {
    List<Grid> frames = new LinkedList<Grid>();
    try {
      for (Action action : actions) {
        frames.addAll(process(action));
      }
    } catch(InvalidActionException e) {
      e.printStackTrace();
      System.exit(1);
    }
    return frames;
  }

  public List<Grid> slideUp(boolean generate) {
    List<Grid> frames = instances.peek().slideUp(generate);
    if (GridManager.hasMoved(frames)) {
      save(frames.get(frames.size() - 1));
    }
    return frames;
  }

  public List<Grid> slideUp() {
    return slideUp(true);
  }

  public List<Grid> slideRight(boolean generate) {
    List<Grid> frames = instances.peek().slideRight(generate);
    if (GridManager.hasMoved(frames)) {
      save(frames.get(frames.size() - 1));
    }
    return frames;
  }

  public List<Grid> slideRight() {
    return slideRight(true);
  }

  public List<Grid> slideDown(boolean generate) {
    List<Grid> frames = instances.peek().slideDown(generate);
    if (GridManager.hasMoved(frames)) {
      save(frames.get(frames.size() - 1));
    }
    return frames;
  }

  public List<Grid> slideDown() {
    return slideDown(true);
  }

  public List<Grid> slideLeft(boolean generate) {
    List<Grid> frames = instances.peek().slideLeft(generate);
    if (GridManager.hasMoved(frames)) {
      save(frames.get(frames.size() - 1));
    }
    return frames;
  }

  public List<Grid> slideLeft() {
    return slideLeft(true);
  }
  
  public Grid insertValue(ValueNode node) {
    Grid grid = instances.peek().setValueNode(node);
    save(grid);
    return grid;
  }

}
