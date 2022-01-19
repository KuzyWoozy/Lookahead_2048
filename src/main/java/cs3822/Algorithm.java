package cs3822;

import java.util.List;
import java.lang.Thread;

/**
 * Interface representing the algorithm used by the game.
 *
 * @author Daniil Kuznetsov
 */
interface Algorithm {

  /* Put the thread to sleep for a moment. */
  public static void pause() {
    try {
      Thread.sleep(500);
    } catch(InterruptedException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Calculate the steps needed based on the algorithm used.
   *
   * @param instance Current grid state
   * @return List of Actions to execute
   */
  public List<Action> move(Grid instance);
}
