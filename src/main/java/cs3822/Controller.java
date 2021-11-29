package cs3822;

import java.util.List;

/**
 * Controller part of the MVC model.
 *
 * @author Daniil Kuznetsov
 */
public class Controller {

  private View view;
  private Grid grid;

  private String map;
  float twoProb;
  int winCondition;

  /**
   * Controller constructor.
   *
   * @param view View representation from the MVC model
   * @param map The game map to be generated specified as a string
   * @param win_condition The target value to be reached to win the game
   * @param twoProb Probability of generating a value node containing a 2
   */
  public Controller(View view, String map, int win_condition, float twoProb) throws InvalidMapSizeException, InvalidMapSymbolException, MaxPosNotInitializedException, UnknownNodeTypeException, NoValueException {
    this.view = view;
    this.map = map;
    this.twoProb = twoProb;
    this.winCondition = win_condition;
    // Create the grid representation from the string map
    grid = new Grid(map, win_condition, twoProb);
  }

  /** Return grid instance */
  public Grid getGrid() {
    return grid;
  }

  /** 
   * Iterate over the sequence of actions, calling each corresponding function in turn.
   * @param actions Sequence of actions to process
   * @throws InvalidActionException Action does not exist
   * */
  public void process(List<Action> actions) throws NoValueException, MovingOutOfBoundsException, InvalidActionException, InvalidMapSizeException, InvalidMapSymbolException, MaxPosNotInitializedException, UnknownNodeTypeException, NoMoveFlagException {
    for (Action action : actions) {
      switch(action) {
        case SWIPE_UP:
          grid.slideUp();
          break;
        case SWIPE_RIGHT:
          grid.slideRight();
          break;
        case SWIPE_DOWN:
          grid.slideDown();
          break;
        case SWIPE_LEFT:
          grid.slideLeft();
          break;
        case UNDO:
          grid.undo();
          break;
        case REDO:
          grid.redo();
          break;
        case RESET:
          reset();
          break;
        default:
          throw new InvalidActionException();
      }
    }
  }

  /** Reset the grid instance. */
  public void reset() throws InvalidMapSizeException, InvalidMapSymbolException, MaxPosNotInitializedException, UnknownNodeTypeException, NoValueException {
    grid = new Grid(map, winCondition, twoProb);
  }
  
  /** Execute the game logic. */
  public void play() throws MaxPosNotInitializedException, UnknownNodeTypeException, NoValueException, MovingOutOfBoundsException, InvalidActionException, InvalidMapSizeException, InvalidMapSymbolException, NoMoveFlagException {
    while(!grid.lost()) {
      view.display(grid);
      process(view.getInput()); 
    }
    view.display(grid);
  }
  
}
