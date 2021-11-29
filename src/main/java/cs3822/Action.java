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
  NONE;
  
  /**
   * Convert a character into an Action enum.
   *
   * @param chr Character to be converted into an Action
   * @returns Action corresponding to the provided character
   * @throws UnknownNodeTypeException Character has no translation into an Action
   */
  public static Action getAction(char chr) throws UnknownNodeTypeException {
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
      default:
        throw new UnknownNodeTypeException();
    }
  }

}
