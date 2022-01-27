package cs3822;

import java.lang.Thread;


class GridThread extends Thread {
  
  private Grid grid;
  private Reward reward;
  private Lookahead algo;

  public GridThread(Grid grid, ModelStorage db, Reward reward, float reward_max, long depth_max) {
    this.grid = grid;
    this.reward = reward;
    this.algo = new Lookahead(db, reward_max, depth_max);
  }

  @Override
  public void run() {
    this.reward.setReward(algo.move_reward(grid));
  }
  
}
