package cs3822;

import java.util.List;
import java.util.LinkedList;
import org.ejml.simple.SimpleMatrix;

/**
 * Layer parameter derivitives. 
 *
 * @author Daniil Kuznetsov
 */
public final class LayerGrad {

  final private SimpleMatrix weightGrad;
  final private SimpleMatrix biasGrad;

  /**
   * Aggregates array of network gradients.
   *
   * @param gradients Two dimensional LayerGrad matrix to aggregate
   * @return Aggregated array
   */
  public static List<LayerGrad> avgRows(List<List<LayerGrad>> gradients) {
    
    List<LayerGrad> grads = new LinkedList<LayerGrad>(); 

    for (LayerGrad layer : gradients.get(0)) {
      grads.add(layer);
    }
    for (int i = 1; i < gradients.size(); i++) {
      for (int j = 0; j < grads.size(); j++) {
        grads.set(j, grads.get(j).add(gradients.get(i).get(j)));
      }
    }

    for (int j = 0; j < grads.size(); j++) {
      grads.set(j, grads.get(j).div(gradients.size()));
    }

    return grads;
  }

  /**
   * Standard constructor.
   *
   * @param weightGrad Layer weight gradients
   * @param biasGrad Layer bias gradients
   */
  public LayerGrad(SimpleMatrix weightGrad, SimpleMatrix biasGrad) {
    this.weightGrad = new SimpleMatrix(weightGrad);
    this.biasGrad = new SimpleMatrix(biasGrad);
  }
  
  /** Return layer weight gradients. */
  public SimpleMatrix getWeightGrad() {
    return weightGrad;
  }

  /** Return layer bias gradients. */
  public SimpleMatrix getBiasGrad() {
    return biasGrad;
  }

  /** Return addition between two LayerGrad objects. */
  public LayerGrad add(LayerGrad layer_grad) {
    return new LayerGrad(weightGrad.plus(layer_grad.getWeightGrad()), biasGrad.plus(layer_grad.getBiasGrad()));
  }

  /** Return elementwise division on LayerGrad by a double */
  public LayerGrad div(double magnitude) {
    return new LayerGrad(weightGrad.divide(magnitude), biasGrad.divide(magnitude));
  }


}
