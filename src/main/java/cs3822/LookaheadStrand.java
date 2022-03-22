package cs3822;


/**
 * A possibility strand to be processed by a thread.
 *
 * @author Daniil Kuznetsov
 */
class LookaheadStrand implements Runnable {
  
  private Grid grid;
  private MutableFloat reward;
  private Lookahead algo;

  /**
   * Standard constructor. 
   *
   * @param grid State to start the lookahead from
   * @param db Model storage to use
   * @param reward Mutable reward to store the result in
   * @param depth_max Max depth for lookahead
   */
  public LookaheadStrand(Grid grid, ModelStorage db, MutableFloat reward, long depth_max, Heuristic heuristic) {
    this.grid = grid;
    this.reward = reward;
    this.algo = new Lookahead(db, depth_max, heuristic);
  }

  @Override
  public void run() {
    this.reward.set(algo.move_reward(grid, false));
  }
  
}
