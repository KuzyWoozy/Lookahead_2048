package cs3822;

import org.ejml.simple.SimpleMatrix;


final class LayerCache {

  final private SimpleMatrix x;
  final private SimpleMatrix z;

  public LayerCache(SimpleMatrix x, SimpleMatrix z) {
    this.x = new SimpleMatrix(x);
    this.z = new SimpleMatrix(z);
  }

  public SimpleMatrix get_X() {
    return x;
  }

  public SimpleMatrix get_Z() {
    return z;
  }

}
