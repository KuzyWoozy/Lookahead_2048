package cs3822;

import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

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
    private static int steps = 2;
    private static int n = 1;
    private static Heuristic heuristic = Heuristic.HIGHSCORE_ORDER;
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
          algo = new Lookahead(steps+1, heuristic);
          break;
        case "lookahead_threaded":
          algo = new ThreadedLookahead(steps+1, heuristic); 
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
        case "--s":
          i++;
          steps = Integer.valueOf(args[i]);
          break;
        case "--n":
          i++;
          n = Integer.valueOf(args[i]);
          break;
        case "--heuristic":
          i++;
          heuristic = null;
          try {
            heuristic = Heuristic.convertStringToHeuristic(args[i]);
          } catch(UnknownHeuristicException e) {
            e.printStackTrace();
            System.exit(1);
          }
          break;
          
        case "--help":
          i++;
          System.out.println(
          """
          Usage:
            --ARGUMENT <TYPE> [DEFAULT VALUE] {SUPPORTED INTERFACE}

          Options:
            --map <STRING> ["####|####|####|####"]
                  Construct a string map representation of the 
                  grid, on which to play out the game.

                  Symbols:
                      | == Separator of grid rows
                      # == Empty node
                      x == Brick node
                      2 == Value node 2
                      4 == Value node 4
                      8 == Value node 8

                  Example:
                    "####|2###|#2##|####"

                        |    |    |
                    -------------------
                       2|    |    |
                    -------------------
                        |   2|    |
                    -------------------
                        |    |    |
             

            --algo <player|optimal|lookahead|lookahead_threaded> [player]
                  The algorithm to use, note that 'player' simply
                  allows the user to play the game themselves.

            --win <INTEGER> [2048] {TEXT INTERFACE ONLY}
                  The value of the victory node.

            --nogen <BOOL> [false]
                  If true, will not generate the two starting 
                  nodes.

            --s <INTEGER> [2]
                  Lookahead steps, will only take effect if any 
                  of the lookahead alrogrithms is specified.

            --n <INTEGER> [1] {TEXT INTERFACE ONLY}
                  Number of games to play out.

            --heuristic <empty|highscore|order> [order]
                  Lookahead heuristic to use when evaluating 
                  leafe states.

          """);
          System.exit(0);
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
        for (int x = 0; x < n; x++) {
          stats.merge(view.play(manager, algo));
          manager.reset();
          System.out.println("Won: " + String.valueOf(stats.getWon()) + "\nLost: " + String.valueOf(stats.getLost()) + "\nPlayed: " + String.valueOf(stats.getNumGames()));
        }
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
