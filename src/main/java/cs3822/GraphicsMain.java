package cs3822;

import javafx.application.Application;
import javafx.stage.Stage;


/** Class with the main method, contains all the setup. */
public class GraphicsMain extends Application {
    
    private static int winCondition = 2048;
    private static String map = "####|####|####|####";
    private static float twoProb = 0.9f;
    private static String algoName = "player";
    private static boolean guiFlag = false;
    
    private static Grid grid;
    private static View view;
    private static Algorithm algo;


    private static void processAlgo(String name) throws UnknownAlgorithmException {
      switch(name.toLowerCase()) {

        //algo = new TreeGeneratorMDP(grid, new HashMapStorage(), twoProb);
        case "uniform":
          algo = new UniformRandomPlay();
          break;
        case "player":
          algo = new PlayerAlgo(view);
          break;
        default:
          throw new UnknownAlgorithmException();
      }
    }

    private static int processArg(int i, String[] args) throws UnknownArgumentException {
      switch(args[i]) {
        case "--gui":
          GraphicsMain.guiFlag = true;
          break;
        case "--map":
          i++;
          GraphicsMain.map = args[i];
          break;
        case "--algo":
          i++;
          GraphicsMain.algoName = args[i];
          break;
        default:
          throw new UnknownArgumentException();
      }
      return i;
    }

    public static void main(String[] args) {
      int i = 0;
      try {
        while (true) {
          if (i >= args.length) {
            break;
          }
          i = processArg(i, args);
          i++;
        }
      } catch(UnknownArgumentException e) {
        e.printStackTrace();
        System.exit(1);
      }
      
      try {
        grid = new Grid(map, winCondition, twoProb);
      } catch(InvalidMapSizeException e) {
        e.printStackTrace();
        System.exit(1);
      }

      if (guiFlag) {
        launch(args);
      } else {
        view = new StdoutView(System.in);
        try {
          processAlgo(algoName);
        } catch(UnknownAlgorithmException e) {
          e.printStackTrace();
          System.exit(1);
        }
        GraphicsMain.view.play(grid, algo);
      }
    }

    public void start(Stage stage) {
      GraphicsMain.view = new GraphicsView(grid, stage, 400, 400);
      try {
        processAlgo(algoName);
      } catch(UnknownAlgorithmException e) {
        e.printStackTrace();
        System.exit(1);
      }

      GraphicsMain.view.play(grid, algo);
      
      /* 
      GameStats stats = new GameStats();
      for (int i=0; i < 10000; i++) {
        stats.merge(view.play(grid, algo));
      }
      System.out.println(String.valueOf(stats.getWon()) + " " + String.valueOf(stats.getLost()) + " " + String.valueOf(stats.getNumGames()));
      */
    } 
}
