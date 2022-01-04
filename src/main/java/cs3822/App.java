package cs3822;

import java.util.HashMap;
import java.io.IOException;


/** Class with the main method, contains all the setup. */
public class App 
{
    public static void main( String[] args ) throws InvalidMapSizeException, InvalidMapSymbolException, MaxPosNotInitializedException, NoValueException, MovingOutOfBoundsException, InvalidActionException, UnknownNodeTypeException, NoMoveFlagException, IOException, InvalidValueException, InvalidNumberOfGamesException {
    
    String map0 = "##\n##";
    String map1 = "#2#\n#2#\n###";
    String map2 = "#~~~~~\n##~~~~\n###~~~\n####~~\n#####~\n######\n";
    String map3 = "~~#~~\n~###~\n#####\n~###~\n~~#~~";
    String map4 = "~###\n#~##\n##~#\n###~";
    String map5 = "#2##\n#2##\n####\n####";

    float twoProb = 0.9f;
    View view = new StdoutView();
    Grid grid = new Grid(map1, 128, twoProb);
    Algorithm algo = new TreeGeneratorMDP(grid, new HashMapStorage(), twoProb);
    //Algorithm algo = new PlayerAlgo(view);
    Controller control = new Controller(grid, view, algo);
    //GameStats stats = control.play(10000, true); 
     
    //System.out.println(String.valueOf(stats.getWon()) + " " + String.valueOf(stats.getLost()) + " " + String.valueOf(stats.getNumGames()));
    /*
    TreeGeneratorMDP gen = new TreeGeneratorMDP(control.getGrid(), twoProb);
    HashMap<Integer, SolTableItem> hashMap = gen.getMapRef(); 
    System.out.println("-----------------\nUnique nodes in DAG " + String.valueOf(hashMap.size()) + "\nInitial state:\n" + control.getGrid().stringify() + "\nExpected win rate (%): " + String.valueOf(hashMap.get(control.getGrid().hashCode()).getReward() * 100));
  */
  }
}
