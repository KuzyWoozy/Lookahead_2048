package cs3822;

import java.util.Stack;
import java.util.LinkedList;
import java.util.List;
import java.lang.Math;


/**
 * Responsible for processing the DAG of instances
 * generated from the initial node and creating 
 * the optimal model to play the game.
 *
 * @author Daniil Kuznetsov
 */
class TreeGeneratorMDP implements Algorithm {
  
  final private ModelStorage db;

  /** Initialize class with initial node and probability of generating a 2. */
  public TreeGeneratorMDP(Grid grid, ModelStorage db) throws InvalidActionException {

    this.db = db;

    Lookahead optimal = new Lookahead(db, 1f, Long.MAX_VALUE); 
    optimal.move(grid); 

    System.out.println("-----------------\nUnique nodes in DAG: " + String.valueOf(db.getElemCount()) + "\nInitial state:\n" + grid.stringify() + "\nDepth: " + String.valueOf(optimal.getDepth()) + "\nStates processed: " + String.valueOf(optimal.getInstancesProcessed()) + "\nExpected win rate (%): " + String.valueOf(db.fetch(grid.hashCode()).getFirst() * 100));
  
  }

  
  public List<Action> move(Grid grid) {
    LinkedList<Action> list = new LinkedList<Action>();
    list.add(db.fetch(grid.hashCode()).getSecond());
    return list;
  }
  
}

