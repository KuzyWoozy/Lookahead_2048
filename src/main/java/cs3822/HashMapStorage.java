package cs3822;

import java.util.HashMap;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;


class HashMapStorage implements ModelStorage {

  private HashMap<Integer, SolTableItem> map;

  public HashMapStorage() {
    map = new HashMap<Integer, SolTableItem> ();
  }

  public void insert(int hash, Action action, float reward) {
    map.put(hash, new SolTableItem(action, reward));
  }

  public Action fetchAction(int hash) {
    return map.get(hash).getAction();
  }

  public float fetchReward(int hash) {
    return map.get(hash).getReward();
  }

  public int getElemCount() {
    return map.size();
  }

  public boolean contains(int hash) {
    return map.containsKey(hash);
  }

  /** Save the model to the specified file. */
  public void save(String fileName) throws IOException {
    FileOutputStream file = null;
    ObjectOutputStream out = null;
    try {    
      file = new FileOutputStream(fileName);
      out = new ObjectOutputStream(file);
  
      out.writeObject(map);

    } finally {
      if (file != null) {
        file.close();
      }
      if (out != null) {
        out.close();
      }
    }
  }

}
