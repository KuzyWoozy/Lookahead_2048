package cs3822;

import java.util.List;


/**
 * Interface for user interfaces (no pun intended).
 *
 * @author Daniil Kuznetsov
 */
interface View {
  /** Obtain the input of Actions from the user. */
  public List<Action> getInput();
 
  /** Update the interface with the new grid state. */
  public void display(Grid grid);
}
