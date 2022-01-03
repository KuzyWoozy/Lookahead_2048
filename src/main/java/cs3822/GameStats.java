package cs3822;

class GameStats {

  private int lost;
  private int won;
  private int numberOfGames;

  public GameStats() {
    this.lost = 0;
    this.won = 0;
    this.numberOfGames = 0;
  }

  public void merge(GameStats stats) {
    lost += stats.lost;
    won += stats.won;
    numberOfGames += stats.numberOfGames;
  }

  public void lost() {
    lost++;
    numberOfGames++;
  }

  public void won() {
    won++;
    numberOfGames++;
  }

  public int getLost() {
    return lost;
  }

  public int getWon() {
    return won;
  }

  public int getNumGames() {
    return numberOfGames;
  }

}
