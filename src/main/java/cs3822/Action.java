package cs3822;


/**
 * Representation of actions a player can take.
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
   * @return Action corresponding to the provided character
   * @throws InvalidActionException Character has no translation into an Action
   */
  public static Action getAction(char chr) throws InvalidActionException {
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

  public static int convertToInt(Action action) throws InvalidActionException { 
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

  public static Action convertToAction(int val) throws InvalidActionException {
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

}
