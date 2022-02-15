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
   
    FFN network = new FFN(2, 2, 2, 0.9d, 2);

    SimpleMatrix matMat = new SimpleMatrix(2, 1, true, new float[] {1, 1});
    SimpleMatrix label = new SimpleMatrix(2, 1, true, new float[] {2, 2});

    network.backprop(matMat, label);
  }
}
