package cs3822;

import java.util.List;

/**
 * Interface representing the algorithm used by the game.
 *
 * @author Daniil Kuznetsov
 */
public interface Algorithm {

  /**
   * Calculate the steps needed based on the algorithm used.
   *
   * @param instance Current grid state
   * @return List of Actions to execute
   */
  public List<Action> move(Grid instance);
}
