package cs3822;

import java.util.List;
import java.lang.Thread;


interface Algorithm {
  public static void pause() {
    try {
      Thread.sleep(1000);
    } catch(InterruptedException e) {
      e.printStackTrace();
    }
  }

  public List<Action> move(Grid instance);
}
