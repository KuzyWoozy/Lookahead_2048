package cs3822;

import java.io.IOException;

/** 
 * Requirements of the model storage objects.
 *
 * @author Daniil Kuznetsov
 */
interface ModelStorage {
  
  /**
   * Insert grid state into model.
   * 
   * @param hash Hash of grid state
   * @param action Best action
   * @param reward Reward of best action
   */
  public void insert(int hash, Action action, float reward);
  
  /**
   * Get grid state from the model.
   *
   * @param hash Hash of the grid state
   * @return Tuple of reward and best action
   */
  public Pair<Float, Action> fetch(int hash);
  
  /** Return number of elements in the model. */
  public long getElemCount();
  
  /** Clear the entries in the model. */
  public void clear();

}
