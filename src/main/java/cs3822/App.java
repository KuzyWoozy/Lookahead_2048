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
    //GraphicsMain.main(args);
    
    /*
    StdoutView view = new StdoutView(System.in);
      
    Grid grid = null;
    try {
      grid = new Grid("####|####|####|####", 2048, 0.9f, true);
    } catch(InvalidMapSizeException e) {
      e.printStackTrace();
      System.exit(1);
    }

    view.play(new GridManager(grid), new ReinLearn(grid, 100, 0.01d, 0d, 0d, 2));
    */
    FFN neural = new FFN(3, 10, 4, 0.01d, 0d, 0d, 2);

      
  }
}
