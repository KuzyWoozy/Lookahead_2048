package cs3822;


enum Action {
  SWIPE_UP,
  SWIPE_RIGHT,
  SWIPE_DOWN,
  SWIPE_LEFT,
  UNDO,
  REDO,
  RESET;

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
      default:
        throw new UnknownNodeTypeException();
    }
  }

}