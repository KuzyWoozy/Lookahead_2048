package cs3822;

import java.util.ArrayList;


/**
 * Representation of actions an agent can take.
 *
 * @author Daniil Kuznetsov
 */
enum Action {
  SWIPE_UP,
  SWIPE_RIGHT,
  SWIPE_DOWN,
  SWIPE_LEFT,
  UNDO,
  REDO,
  RESET,
  NONE,
  EXIT;
  
  /**
   * Convert a character into an Action enum.
   *
   * @param chr Character to be converted into an Action
   * @return Action corresponding to the input character
   * @throws InvalidActionException Character has no translation into an Action
   */
  public static Action convertCharToAction(char chr) throws InvalidActionException {
    switch(chr) {
      case 'w': 
        return SWIPE_UP;
      case 'd':
        return SWIPE_RIGHT;
      case 's':
        return SWIPE_DOWN;
      case 'a':
        return SWIPE_LEFT;
      case 'u':
        return UNDO;
      case 'r':
        return REDO;
      case 'x':
        return RESET;
      case '~':
        return NONE;
      case 'q':
        return EXIT;
      default:
        throw new InvalidActionException();
    }
  }
  
  /**
   * Convert Action enum into an integer.
   *
   * @param action Action to be converted into an integer
   * @return Integer corresponding to the input Action
   * @throws InvalidActionException Character has no translation into an integer
   */
  public static int convertActionToInt(Action action) throws InvalidActionException { 
    switch(action) {
      case SWIPE_UP:
        return 0;
      case SWIPE_RIGHT:
        return 1;
      case SWIPE_DOWN:
        return 2;
      case SWIPE_LEFT:
        return 3;
      case UNDO:
        return 4;
      case REDO:
        return 5;
      case RESET:
        return 6;
      case NONE:
        return 7;
      case EXIT:
        return 8;
      default:
        throw new InvalidActionException();
    }
  }
  
  /**
   * Convert integer into an Action enum.
   *
   * @param val Integer to be converted into an Action
   * @return Integer to be converted into a corresponding Action
   * @throws InvalidActionException Character has no translation into an integer
   */
  public static Action convertIntToAction(int val) throws InvalidActionException {
    switch(val) {
      case 0:
        return SWIPE_UP;
      case 1:
        return SWIPE_RIGHT;
      case 2:
        return SWIPE_DOWN;
      case 3:
        return SWIPE_LEFT;
      case 4:
        return UNDO;
      case 5:
        return REDO;
      case 6:
        return RESET;
      case 7:
        return NONE;
      case 8:
        return EXIT;
      default:
        throw new InvalidActionException();
    }
  }

  /** Return all possible movement actions. */
  public static ArrayList<Action> getMoveActions() {
    ArrayList<Action> list = new ArrayList<Action>();
    
    list.add(SWIPE_UP);
    list.add(SWIPE_RIGHT);
    list.add(SWIPE_DOWN);
    list.add(SWIPE_LEFT);

    return list;
  }
  
  /**
   * Return the next action around the clock.
   *
   * @param action Current action
   * @return Next action with respect to input
   * @throws InvalidActionException Movement action not specified
   */
  public static Action nextSwipe(Action action) throws InvalidActionException {
    switch(action) {
      case SWIPE_UP:
        return SWIPE_RIGHT;
      case SWIPE_RIGHT:
        return SWIPE_DOWN;
      case SWIPE_DOWN:
        return SWIPE_LEFT;
      case SWIPE_LEFT:
        return SWIPE_UP;
      default:
        throw new InvalidActionException();
    }
  }

}
