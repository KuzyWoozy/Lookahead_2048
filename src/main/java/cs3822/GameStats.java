package cs3822;


/**
 * Representation for post game information.
 *
 * @author Daniil Kuznetsov
 */
public class GameStats {

  private int lost;
  private int won;
  private int numberOfGames;

  public GameStats() {
    this.lost = 0;
    this.won = 0;
    this.numberOfGames = 0;
  }

  /** Combines states of another GameStats object. */
  public void merge(GameStats stats) {
    lost += stats.lost;
    won += stats.won;
    numberOfGames += stats.numberOfGames;
  }

  /** Increase the lost counter. */
  public void lost() {
    lost++;
    numberOfGames++;
  }

  /** Increase the win counter. */
  public void won() {
    won++;
    numberOfGames++;
  }

  /** Return number of games lost. */
  public int getLost() {
    return lost;
  }

  /** Return number of games won. */
  public int getWon() {
    return won;
  }
  
  /** Return number of games played. */
  public int getNumGames() {
    return numberOfGames;
  }

}

