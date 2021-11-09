package cs3822;


class SolTableItem implements java.io.Serializable {

  private Action action;
  private float reward;

  public SolTableItem(Action action, float reward) {
    this.action = action;
    this.reward = reward;
  }

  public Action getAction() {
    return action;
  }

  public float getReward() {
    return reward;
  }

}
