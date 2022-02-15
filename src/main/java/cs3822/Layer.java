package cs3822;

import java.util.Random;
import org.ejml.simple.SimpleMatrix;


final class Layer {

  final private SimpleMatrix weights;
  final private SimpleMatrix bias;
  
  final private SimpleMatrix x_cache;
  final private SimpleMatrix z_cache;


  final private SimpleMatrix weight_momentum;
  final private SimpleMatrix bias_momentum;

  final Random generator = new Random();


  private SimpleMatrix weightInit(SimpleMatrix weights) {
    for (int y = 0; y < weights.numRows(); y++) {
      for (int x = 0; x < weights.numCols(); x++) {
        weights.set(y, x, generator.nextGaussian() / weights.numCols());
      }
    }
    return weights;
  }

  public Layer(SimpleMatrix weights) {

    this.weights = weightInit(weights);
    
    this.weight_momentum = this.weights.createLike();
    this.weight_momentum.fill(0);

    this.bias = new SimpleMatrix(weights.numRows(), 1);
    this.bias.fill(0);

    this.bias_momentum = this.bias.createLike();
    this.bias_momentum.fill(0);

    this.x_cache = null;
    this.z_cache = null;
  }

  
  public Layer(SimpleMatrix weights, SimpleMatrix bias, SimpleMatrix weight_momentum, SimpleMatrix bias_momentum) {
    this.weights = weights;
    this.bias = bias;

    this.weight_momentum = weight_momentum;
    this.bias_momentum = bias_momentum;

    this.x_cache = null;
    this.z_cache = null;
  }


  public Layer(Layer layer, SimpleMatrix x_cache, SimpleMatrix z_cache) {
    this.weights = layer.weights;
    this.bias = layer.bias;

    this.weight_momentum = layer.weight_momentum;
    this.bias_momentum = layer.bias_momentum;

    this.x_cache = x_cache;
    this.z_cache = z_cache;
  }

  public SimpleMatrix getWeights() {
    return weights;
  }

  public SimpleMatrix getBias() {
    return bias;
  }

  public SimpleMatrix get_X() {
    return x_cache;
  }

  public SimpleMatrix get_Z() {
    return z_cache;
  }

  public Layer gradientStep(SimpleMatrix error_wrt_weight, SimpleMatrix error_wrt_bias, double alpha) {
    
    SimpleMatrix weight_momentum_new = weight_momentum.scale(0.9).plus(error_wrt_weight.scale(0.1)); 
    SimpleMatrix bias_momentum_new = bias_momentum.scale(0.9).plus(error_wrt_bias.scale(0.1)); 

    return new Layer(weights.minus(weight_momentum_new.scale(alpha)), bias.minus(bias_momentum_new.scale(alpha)), weight_momentum_new, bias_momentum_new);
  }

}
