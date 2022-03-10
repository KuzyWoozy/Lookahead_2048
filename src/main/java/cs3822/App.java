package cs3822;

import org.ejml.simple.SimpleMatrix;

import java.util.LinkedList;
import java.util.List;

/**
 * Main class of the application
 *
 * @author Daniil Kuznetsov
 */
public class App {
  public static void main( String[] args ) {
    // Call the javafx supported main method
    GraphicsMain.main(args);
    
    /*
    Grid grid = null;
    try {
      grid = new Grid("8##2|####|####|#4##", 2048, 0.9f, false);
    } catch(InvalidMapSizeException e) {
      e.printStackTrace();
      System.exit(1);
    }
    System.out.println(grid.getImmediateWeightedScore());
    */
    /* 
    StdoutView view = new StdoutView(System.in);
      
    Grid grid = null;
    try {
      grid = new Grid("####|####|####|####", 2048, 0.9f, true);
    } catch(InvalidMapSizeException e) {
      e.printStackTrace();
      System.exit(1);
    }
   
    ReinLearn rein = new ReinLearn(grid, 70, 0.0000001d, 0.9d, 0.1d, 5);
    
    rein.train(100000);

    view.play(new GridManager(grid), rein);
    */
    
  }
}
