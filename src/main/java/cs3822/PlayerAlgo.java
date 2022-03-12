package cs3822;

import java.util.List;

/**
 * Handles human player input actions.
 *
 * @author Daniil Kuznetsov
 */
class PlayerAlgo implements Algorithm {
  
  private View view;

  /** View constructor. */
  public PlayerAlgo(View view) {
    this.view = view;
  }

  @Override
  public List<Action> move(Grid instance) {
    return view.getInput();
  } 
}
