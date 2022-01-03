package cs3822;

import java.util.Random;
import java.util.List;
import java.util.LinkedList;

class UniformRandomPlay implements Algorithm {
  
  private Random random;

  public UniformRandomPlay() {
    this.random = new Random();
  }

  public List<Action> move(Grid instance) {
    LinkedList<Action> list = new LinkedList<Action>();
    switch(random.nextInt(4)) {
      case 0:
        list.add(Action.SWIPE_UP);
        break;
      case 1:
        list.add(Action.SWIPE_RIGHT);
        break;
      case 2:
        list.add(Action.SWIPE_DOWN);
        break;
      case 3:
        list.add(Action.SWIPE_LEFT);
        break;
    }
    return list;
  }

}
