package cs3822;



class LookaheadStrand implements Runnable {
  
  private Grid grid;
  private MutableFloat reward;
  private Lookahead algo;

  public LookaheadStrand(Grid grid, ModelStorage db, MutableFloat reward, float reward_max, long depth_max) {
    this.grid = grid;
    this.reward = reward;
    this.algo = new Lookahead(db, reward_max, depth_max);
  }

  @Override
  public void run() {
    this.reward.set(algo.move_reward(grid));
  }
  
}
