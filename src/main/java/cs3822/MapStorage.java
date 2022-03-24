package cs3822;

import java.util.Map;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;


/**
 * Hash table model storage.
 *
 * @author Daniil Kuznetsov
 */
public class MapStorage implements ModelStorage {

  private Map<Integer, Pair<Float, Action>> map;

  /** Copy constructor. */
  public MapStorage(Map<Integer, Pair<Float, Action>> map) {
    this.map = map;
  }
  
  @Override
  public void insert(int hash, Action action, float reward) {
    map.put(hash, new Pair<Float, Action>(reward, action));
  }

  @Override
  public Pair<Float, Action> fetch(int hash) {
    return map.get(hash);
  }

  @Override
  public long getElemCount() {
    return map.size();
  }

  @Override 
  public void clear() {
    map.clear();
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
