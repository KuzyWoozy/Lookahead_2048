package cs3822;

/**
 * Represents a node in the model DAG.
 *
 * @author Daniil Kuznetsov
 */
class SolTableItem implements java.io.Serializable {

  private Action action;
  private float reward;

  /** Constructs object in which an action corresponds to a reward. */
  public SolTableItem(Action action, float reward) {
    this.action = action;
    this.reward = reward;
  }

  /** Returns the stores action. */
  public Action getAction() {
    return action;
  }

  /** Returns the stored reward. */
  public float getReward() {
    return reward;
  }

}
