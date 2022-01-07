package cs3822;

import java.util.List;


class PlayerAlgo implements Algorithm {
  
  private View view;

  public PlayerAlgo(View view) {
    this.view = view;
  }

  public List<Action> move(Grid instance) {
    return view.getInput();
  } 
}
