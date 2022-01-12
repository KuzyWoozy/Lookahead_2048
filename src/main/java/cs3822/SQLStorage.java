package cs3822;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;

import java.util.Map;
import java.util.HashMap;


class SQLStorage implements ModelStorage {
  private int subBatch = 10000;

  private Connection con;
  private long count;

  private Integer latestHash = null;
  private Action latestAction;
  private float latestReward;

  private HashMap<Integer, SolTableItem> buffer;
  private int bufferSize;

  private PreparedStatement insertStmt;
  private PreparedStatement selectSolInfoStmt;
  private PreparedStatement containsStmt;

  public SQLStorage(String location, int bufferSize) {
    this.count = 0;
    this.buffer = new HashMap<Integer, SolTableItem>();
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
      containsStmt = con.prepareStatement("SELECT * FROM db WHERE instance = ?;");

    } catch(SQLException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
  
  public void insert(int hash, Action action, float reward) {
    count++;

    buffer.put(hash, new SolTableItem(action, reward));
    if (buffer.size() >= bufferSize) {
      try {
        con.setAutoCommit(false);
        int i = 0;
        for (Map.Entry<Integer, SolTableItem> item : buffer.entrySet()) {
          i++;

          insertStmt.setInt(1, item.getKey());
          insertStmt.setInt(2, Action.convertToInt(item.getValue().getAction()));
          insertStmt.setFloat(3, item.getValue().getReward());

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
  
  public Action fetchAction(int hash) {
    if (latestHash != null && latestHash.equals(hash)) {
      return latestAction;
    // Check buffer
    } else if (buffer.containsKey(hash)) {
      latestHash = Integer.valueOf(hash);
      SolTableItem item = buffer.get(hash);
      latestAction = item.getAction();
      latestReward = item.getReward();
      return latestAction;
    // check database
    } else {
      latestHash = Integer.valueOf(hash);
      try {
        selectSolInfoStmt.setInt(1, hash);
        ResultSet rs = selectSolInfoStmt.executeQuery();
        rs.next(); 
        latestAction = Action.convertToAction(rs.getInt("action"));
        latestReward = rs.getFloat("expReward");
        rs.close();
      } catch(SQLException | InvalidActionException e) {
        e.printStackTrace();
        System.exit(1);
      }
      return latestAction;
    }
  }
  
  public float fetchReward(int hash) {
    if (latestHash != null && latestHash.equals(hash)) {
      return latestReward;
    } else if (buffer.containsKey(hash)) {
      latestHash = Integer.valueOf(hash);
      SolTableItem item = buffer.get(hash);
      latestAction = item.getAction();
      latestReward = item.getReward();
      return latestReward;
    } else {
      latestHash = Integer.valueOf(hash);
      try {
        selectSolInfoStmt.setInt(1, hash);
        ResultSet rs = selectSolInfoStmt.executeQuery();
        rs.next(); 
        latestAction = Action.convertToAction(rs.getInt("action"));
        latestReward = rs.getFloat("expReward");
        rs.close();
      } catch(SQLException | InvalidActionException e) {
        e.printStackTrace();
        System.exit(1);
      }
      return latestReward;
    }
  }
  
  public boolean contains(int hash) {
    if (buffer.containsKey(hash)) {
      return true;
    } else {
      boolean x = false;
      try {
        containsStmt.setInt(1, hash);
        ResultSet rs = containsStmt.executeQuery();
        if (rs.next()) {
          x = true;
        } else {
          x = false;
        }
        rs.close();
      } catch(SQLException e) {
        e.printStackTrace();
        System.exit(1);
      }
      return x;
    }
  }
  
  public long getElemCount() {
    return count;
  }
}
