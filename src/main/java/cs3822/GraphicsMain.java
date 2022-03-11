package cs3822;

import java.util.HashMap;

import javafx.application.Application;
import javafx.stage.Stage;


/** Class with the main method, contains all the setup. */
public class GraphicsMain extends Application {
    
    private static int winCondition = 2048;
    private static String map = "####|####|####|####";
    private static float twoProb = 0.9f;
    private static String algoName = "player";
    private static boolean guiFlag = false;
    private static boolean generate = true;
    private static Grid grid;
    private static GridManager manager;
    private static View view;
    private static Algorithm algo;

    /**
     * Initialize the specified algorithm.
     *
     * @param name Algorithm name
     * @throws UnknownAlgorithmException Unknown algorithm specified
     */
    private static void processAlgo(String name) throws UnknownAlgorithmException {
      switch(name.toLowerCase()) {
        case "optimal":
          try {
            algo = new TreeGeneratorMDP(grid, new SQLStorage("db.sql", 1000000));
          } catch(InvalidActionException e) {
            e.printStackTrace();
            System.exit(1);
          }
          break;
        case "lookahead":
          algo = new Lookahead(3);
          break;
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

    /**
     * Process a single argument from the args.
     *
     * @param i Current position in the args array
     * @param args Args array
     * @return New position after processing
     * @throws UnknownArgumentException Unknown argument specified
     */
    private static int processArg(int i, String[] args) throws UnknownArgumentException {
      switch(args[i]) {
        case "--gui":
          guiFlag = true;
          break;
        case "--map":
          i++;
          map = args[i];
          break;
        case "--algo":
          i++;
          algoName = args[i];
          break;
        case "--win":
          i++;
          winCondition = Integer.valueOf(args[i]);
          break;
        case "--nogen":
          generate = false;
          break;
        default:
          throw new UnknownArgumentException();
      }
      return i;
    }

    /** Main class method. */
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
        // Create the grid specified or the default one
        grid = new Grid(map, winCondition, twoProb, generate);
        manager = new GridManager(grid);
      } catch(InvalidMapSizeException e) {
        e.printStackTrace();
        System.exit(1);
      }

      if (guiFlag) {
        // Initialize javafx graphics window
        launch(args);
      } else {
        view = new StdoutView(System.in);
        try {
          processAlgo(algoName);
        } catch(UnknownAlgorithmException e) {
          e.printStackTrace();
          System.exit(1);
        }

        GameStats stats = new GameStats();
        // Loop can be altered to increase number of games played
        for (int x = 0; x < 1; x++) {
          stats.merge(view.play(manager, algo));
          manager.reset();
        }
        System.out.println(String.valueOf(stats.getWon()) + " " + String.valueOf(stats.getLost()) + " " + String.valueOf(stats.getNumGames()));

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
      GraphicsMain.view.play(manager, algo);
    } 
}
