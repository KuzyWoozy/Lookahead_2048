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

  private Algorithm algo;

  /**
   * Controller constructor.
   *
   * @param view View representation from the MVC model
   * @param map The game map to be generated specified as a string
   * @param win_condition The target value to be reached to win the game
   * @param twoProb Probability of generating a value node containing a 2
   */
  public Controller(Grid grid, View view, Algorithm algo) {
    this.view = view;
    this.map = map;
    this.algo = algo;
    // Create the grid representation from the string map
    this.grid = grid; 
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
  public void process(List<Action> actions) throws InvalidActionException {
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
  public void reset() {
    grid.reset();
  }

  /** Restart the grid instance. */
  public void restart() {
    grid.restart();
  }

  /** Execute the game logic. */
  public GameStats play() {
    GameStats stat = new GameStats();
    try {
      while (true) {
        if (grid.won()) {
          stat.won();
          break;
        } else if (grid.lost()) {
          stat.lost();
          break;
        }
        view.display(grid);
        process(algo.move(grid)); 
      }
    } catch(InvalidActionException e) {
      e.printStackTrace();
      System.exit(1);
    }
    view.display(grid);

    return stat;
  }

  public GameStats play(int iterations) throws InvalidNumberOfGamesException {
    if (iterations < 1) {
      throw new InvalidNumberOfGamesException();
    }
    GameStats stats = new GameStats();
    for (int i = 0; i < iterations; i++) {
      stats.merge(play()); 
      restart();
    }
    return stats;
  }

  public GameStats play(int iterations, boolean reset) throws InvalidNumberOfGamesException {
    if (iterations < 1) {
      throw new InvalidNumberOfGamesException();
    }
    GameStats stats = new GameStats();
    for (int i = 0; i < iterations; i++) {
      stats.merge(play()); 
      if (reset) {
        reset();
      } else {
        restart();
      }
    }
    return stats;
  }

}
