package cs3822;


/**
 * Enum to represent types of lookahead heuristic functions.
 *
 * @author Daniil Kuznetsov
 */
enum Heuristic {
  EMPTY_NODES,
  HIGHSCORE,
  HIGHSCORE_ORDER;

  /** Return heuristic corresponding to specified string. */
  public static Heuristic convertStringToHeuristic(String name) throws UnknownHeuristicException {
    switch(name) {
      case "empty":
        return EMPTY_NODES;
      case "highscore":
        return HIGHSCORE;
      case "order":
        return HIGHSCORE_ORDER;
      default:
        throw new UnknownHeuristicException();
    }
  }

}
