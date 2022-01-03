package cs3822;

import java.util.List;


interface Algorithm {
  public List<Action> move(Grid instance) throws UnknownNodeTypeException;
}
