package cs3822;

import java.util.HashMap;
import java.io.IOException;


/** Class with the main method, contains all the setup. */
public class App 
{
    public static void main( String[] args ) {
    
    String map0 = "##\n##";
    String map1 = "#2#\n#2#\n###";
    String map2 = "#~~~~~\n##~~~~\n###~~~\n####~~\n#####~\n######\n";
    String map3 = "~~#~~\n~###~\n#####\n~###~\n~~#~~";
    String map4 = "~###\n#~##\n##~#\n###~";
    String map5 = "#2##\n#2##\n####\n####";
    
    
    float twoProb = 0.9f;
    View view = new StdoutView();

    Grid grid = null;
    Algorithm algo = null;
    try {
      grid = new Grid(map5, 2048, twoProb);
      algo = new TreeGeneratorMDP(grid, new SQLStorage("model.db", 10000), twoProb);
    } catch(InvalidMapSizeException | InvalidActionException e) {
      e.printStackTrace();
      System.exit(1);
    }
      
    //Algorithm algo = new PlayerAlgo(view);
    Controller control = new Controller(grid, view, algo);
    GameStats stats = null;
    try {
      stats = control.play(10000, true); 
    } catch(InvalidNumberOfGamesException e) {
      e.printStackTrace();
      System.exit(1);
    }

    System.out.println(String.valueOf(stats.getWon()) + " " + String.valueOf(stats.getLost()) + " " + String.valueOf(stats.getNumGames()));
    
  }
}
