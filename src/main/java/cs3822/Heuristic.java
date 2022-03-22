package cs3822;

enum Heuristic {
  EMPTY_NODES,
  HIGHSCORE,
  HIGHSCORE_ORDER;

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
