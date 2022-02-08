package cs3822;

import java.io.IOException;


interface ModelStorage {
  
  public void insert(int hash, Action action, float reward);
  public Pair<Float, Action> fetch(int hash);
  public long getElemCount();
  public void clear();
}
