package cs3822;


enum Actions {
  SWIPE_UP,
  SWIPE_RIGHT,
  SWIPE_DOWN,
  SWIPE_LEFT,
  UNDO,
  REDO,
  RESET,
  NONE;

  public static Actions getAction(char chr) throws UnknownNodeTypeException {
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
