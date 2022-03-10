package cs3822;

import org.ejml.simple.SimpleMatrix;


final class LayerCache {

  final private SimpleMatrix x;
  final private SimpleMatrix z;
  final private SimpleMatrix y;

  public LayerCache(SimpleMatrix x, SimpleMatrix z, SimpleMatrix y) {
    this.x = new SimpleMatrix(x);
    this.z = new SimpleMatrix(z);
    this.y = new SimpleMatrix(y);
  }

  public SimpleMatrix get_X() {
    return x;
  }

  public SimpleMatrix get_Z() {
    return z;
  }

  public SimpleMatrix get_Y() {
    return y;
  }

}
