package cs3822;

import java.util.List;


public class Controller {

  private View view;
  private Grid grid;

  private String map;
  float twoProb;
  int winCondition;

  
  public Controller(View view, String map, int win_condition, float twoProb) throws InvalidMapSizeException, InvalidMapSymbolException, MaxPosNotInitializedException, UnknownNodeTypeException, NoValueException {
    this.view = view;
    this.map = map;
    this.twoProb = twoProb;
    this.winCondition = win_condition;

    grid = new Grid(map, win_condition, twoProb);
  }


  //public void runAlgorithm(Algorithm algo) {

  //}

  public Grid getGrid() {
    return grid;
  }

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

  public void reset() throws InvalidMapSizeException, InvalidMapSymbolException, MaxPosNotInitializedException, UnknownNodeTypeException, NoValueException {
    grid = new Grid(map, winCondition, twoProb);
  }
  
  public void play() throws MaxPosNotInitializedException, UnknownNodeTypeException, NoValueException, MovingOutOfBoundsException, InvalidActionException, InvalidMapSizeException, InvalidMapSymbolException, NoMoveFlagException {
    while(!grid.lost()) {

      view.display(grid);
      process(view.getInput()); 
    }
    view.display(grid);
  }
  

}
