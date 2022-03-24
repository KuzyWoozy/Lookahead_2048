package cs3822;

import org.ejml.simple.SimpleMatrix;


/**
 * Saves an instance of a Layer.
 *
 * @author Daniil Kuznetsov
 */
public final class LayerCache {

  final private SimpleMatrix x;
  final private SimpleMatrix z;
  final private SimpleMatrix y;

  /** 
   * Standard constructor.
   *
   * @param x Layer input
   * @param z Layer activation function input
   * @param y Layer output
   */
  public LayerCache(SimpleMatrix x, SimpleMatrix z, SimpleMatrix y) {
    this.x = new SimpleMatrix(x);
    this.z = new SimpleMatrix(z);
    this.y = new SimpleMatrix(y);
  }

  /** Return layer input. */
  public SimpleMatrix get_X() {
    return x;
  }

  /** Return layer activation function input. */
  public SimpleMatrix get_Z() {
    return z;
  }

  /** Return layer activation function output. */
  public SimpleMatrix get_Y() {
    return y;
  }

}
