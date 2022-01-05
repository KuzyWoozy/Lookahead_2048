package cs3822;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;


class SQLStorage implements ModelStorage {
  private Connection con;
  private int count;

  PreparedStatement insertStmt;
  PreparedStatement selectActionStmt;
  PreparedStatement selectRewardStmt;
  PreparedStatement containsStmt;

  public SQLStorage(String location) {
    this.count = 0;
    try {
      // Establish a connection
      this.con = DriverManager.getConnection("jdbc:sqlite:" + location);
      String cleanTable = "DROP TABLE IF EXISTS db;"; 
      String createTable = "CREATE TABLE db (instance INT PRIMARY KEY NOT NULL, action TINYINT NOT NULL, expReward FLOAT(24) NOT NULL);";
      String createIndex = "CREATE UNIQUE INDEX indexDB ON db (instance);";

      Statement stmt = con.createStatement();
      stmt.executeUpdate(cleanTable);
      stmt.executeUpdate(createTable);
      stmt.executeUpdate(createIndex);
      stmt.close();


      insertStmt = con.prepareStatement("INSERT INTO db VALUES (?,?,?);");
      selectActionStmt = con.prepareStatement("SELECT action FROM db WHERE instance = ?;");
      selectRewardStmt = con.prepareStatement("SELECT expReward FROM db WHERE instance = ?;");
      containsStmt = con.prepareStatement("SELECT * FROM db WHERE instance = ?;");

    } catch(SQLException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
  
  public void insert(int hash, Action action, float reward) {
    count++;
    try { 
      insertStmt.setInt(1, hash);
      insertStmt.setInt(2, Action.convertToInt(action));
      insertStmt.setFloat(3, reward);
      insertStmt.executeUpdate();

    } catch(SQLException | UnknownNodeTypeException e) {
      e.printStackTrace();
      System.exit(1);
    }

  }
  
  public Action fetchAction(int hash) {
    int x = 0;
    Action action = Action.NONE;
    try {
      selectActionStmt.setInt(1, hash);
      ResultSet rs = selectActionStmt.executeQuery();
      rs.next(); 
      x = rs.getInt("action");
      rs.close();
      action = Action.convertToAction(x);
    } catch(SQLException | UnknownNodeTypeException e) {
      e.printStackTrace();
      System.exit(1);
    }
    return action;
  }
  
  public float fetchReward(int hash) {
    float x = 0;
    try {
      selectRewardStmt.setInt(1, hash);
      ResultSet rs = selectRewardStmt.executeQuery();
      rs.next();
      x = rs.getFloat("expReward");
      rs.close();
    } catch(SQLException e) {
      e.printStackTrace();
      System.exit(1);
    }
    return x;
  }
  
  public boolean contains(int hash) {
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
  
  public int getElemCount() {
    return count;
  }
}
