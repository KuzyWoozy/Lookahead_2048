package cs3822;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;

import java.util.Map;
import java.util.HashMap;


/**
 * SQL database model storage. 
 *
 * @author Daniil Kuznetsov
 */
public class SQLStorage implements ModelStorage {
  final private int subBatch = 10000;

  private Connection con;
  private long count;

  private Integer latestHash = null;
  private Action latestAction;
  private float latestReward;

  private HashMap<Integer, Pair<Float, Action>> buffer;
  private int bufferSize;

  private PreparedStatement insertStmt;
  private PreparedStatement selectSolInfoStmt;
  
  /** 
   * Standard constructor.
   *
   * @param location Location of the database
   * @param bufferSize Size of the local hash table buffer
   */
  public SQLStorage(String location, int bufferSize) {
    this.count = 0;
    this.buffer = new HashMap<Integer, Pair<Float, Action>>();
    this.bufferSize = bufferSize;

    try {
      // Establish a connection
      this.con = DriverManager.getConnection("jdbc:sqlite:" + location);
      String cleanTable = "DROP TABLE IF EXISTS db;"; 
      String createTable = "CREATE TABLE db (instance INT PRIMARY KEY, action TINYINT, expReward FLOAT(24));";
      //String createIndex = "CREATE INDEX indexDB ON db (instance);";
      String pragmaOff = "PRAGMA synchronous = OFF;";

      Statement stmt = con.createStatement();
      stmt.executeUpdate(pragmaOff);
      stmt.executeUpdate(cleanTable);
      stmt.executeUpdate(createTable);
      //stmt.executeUpdate(createIndex);
      stmt.close();


      insertStmt = con.prepareStatement("INSERT INTO db VALUES (?,?,?);");
      selectSolInfoStmt = con.prepareStatement("SELECT action, expReward FROM db WHERE instance = ?;");

    } catch(SQLException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
  
  @Override
  public void insert(int hash, Action action, float reward) {
    count++;

    buffer.put(hash, new Pair<Float, Action>(reward, action));
    if (buffer.size() >= bufferSize) {
      try {
        con.setAutoCommit(false);
        int i = 0;
        for (Map.Entry<Integer, Pair<Float, Action>> item : buffer.entrySet()) {
          i++;

          insertStmt.setInt(1, item.getKey());
          insertStmt.setInt(2, Action.convertActionToInt(item.getValue().getSecond()));
          insertStmt.setFloat(3, item.getValue().getFirst());

          insertStmt.addBatch();

          if (i % subBatch == 0) {
            insertStmt.executeBatch();
            con.commit();
          }
        }
        insertStmt.executeBatch();
        con.commit();

        con.setAutoCommit(true);
      } catch(SQLException | InvalidActionException e) {
        e.printStackTrace();
        System.exit(1);
      }
    buffer.clear();
    }
  }
  
  @Override
  public Pair<Float, Action> fetch(int hash) {
    Pair<Float, Action> item = null;
    if (latestHash != null && latestHash.equals(hash)) {
      return new Pair<Float, Action>(latestReward, latestAction);
    // Check buffer
    } else if (buffer.containsKey(hash)) {
      return buffer.get(Integer.valueOf(hash));
    // check database
    } else {
      try {
        selectSolInfoStmt.setInt(1, hash);
        ResultSet rs = selectSolInfoStmt.executeQuery();
        if (!rs.next()) {
          rs.close();
          return null;
        }

        latestHash = Integer.valueOf(hash);
        latestAction = Action.convertIntToAction(rs.getInt("action"));
        latestReward = rs.getFloat("expReward");

        item = new Pair<Float, Action>(latestReward, latestAction); 
        
        rs.close();
        return item;
      } catch(SQLException | InvalidActionException e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
    return item;
  }
  
  @Override
  public long getElemCount() {
    return count;
  }

  @Override
  public void clear() {
    try {
      String cleanTable = "DROP TABLE IF EXISTS db;"; 
      String createTable = "CREATE TABLE db (instance INT PRIMARY KEY, action TINYINT, expReward FLOAT(24));";

      Statement stmt = con.createStatement();
      stmt.executeUpdate(cleanTable);
      stmt.executeUpdate(createTable);
      stmt.close();
    } catch(SQLException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
