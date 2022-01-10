package cs3822;

import javafx.application.Application;
import javafx.stage.Stage;



/** Class with the main method, contains all the setup. */
public class GraphicsMain extends Application 
{
    public static void main( String[] args ) {
      launch(args);
    }

    public void start(Stage stage) {
     
      String map0 = "##\n##";
      String map1 = "###\n###\n###";
      String map2 = "#~~~~~\n##~~~~\n###~~~\n####~~\n#####~\n######\n";
      String map3 = "~~#~~\n~###~\n#####\n~###~\n~~#~~";
      String map4 = "~###\n#~##\n##~#\n###~";
      String map5 = "####\n####\n####\n####";
    
    
      float twoProb = 0.9f;
      
      Grid grid = null;
      Algorithm algo = null;
      View view = null;
      try {
        grid = new Grid(map5, 2048, twoProb);
        //algo = new TreeGeneratorMDP(grid, new SQLStorage("model.db", 1000000), twoProb);
        //
        view = new GraphicsView(grid, stage, 400, 400);
        //view = new StdoutView(System.in);
        algo = new PlayerAlgo(view);
        //algo = new UniformRandomPlay();
      } catch(InvalidMapSizeException e) {
        e.printStackTrace();
        System.exit(1);
      }
      
      view.play(grid, algo);

      /*  
      GameStats stats = null;
      stats = control.play(); 

      System.out.println(String.valueOf(stats.getWon()) + " " + String.valueOf(stats.getLost()) + " " + String.valueOf(stats.getNumGames()));
      */
    } 
}
