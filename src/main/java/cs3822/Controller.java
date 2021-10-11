package cs3822;

import java.util.List;


public class Controller {

  private Grid grid;

  private String map;
  float fourProb;
  float twoProb;
  
  public Controller(String map, float fourProb, float twoProb) throws InvalidMapSizeException, InvalidMapSymbolException, MaxPosNotInitializedException, UnknownNodeTypeException, NoValueException {
    this.map = map;
    this.fourProb = fourProb;
    this.twoProb = twoProb;

    grid = new Grid(map, fourProb, twoProb);
  }

  //private View view;

  //public Controller(View view) {
  //  this.view = view;
  //}

  //public void runAlgorithm(Algorithm algo) {

  //}

  public Grid getGrid() {
    return grid;
  }

  public void process(List<Actions> actions) throws NoValueException, MovingOutOfBoundsException, InvalidActionException, InvalidMapSizeException, InvalidMapSymbolException, MaxPosNotInitializedException, UnknownNodeTypeException {
    for (Actions action : actions) {
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
    grid = new Grid(map, fourProb, twoProb);
  }

  /*
  public void play() {
    String input;
    while(!grid.lost()) {
      process(view.getInput());
      view.display(grid);
    }
  }
  */

}
