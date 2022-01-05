package cs3822;

import java.io.IOException;


interface ModelStorage {

  public void insert(int hash, Action action, float reward);
  public Action fetchAction(int hash);
  public float fetchReward(int hash);
  public int getElemCount();
  public boolean contains(int hash);

}
