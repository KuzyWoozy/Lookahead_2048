package cs3822;

import java.util.Random;
import org.ejml.simple.SimpleMatrix;


/**
 * Neural network layer.
 *
 * @author Daniil Kuznetsov
 */
public final class Layer {

  final private SimpleMatrix weights;
  final private SimpleMatrix bias;
  

  final private SimpleMatrix x_cache;
  final private SimpleMatrix z_cache;
  final private SimpleMatrix y_cache;


  final private SimpleMatrix weight_momentum;
  final private SimpleMatrix bias_momentum;

  final private Random generator = new Random();

  /**
   * Layer constructor.
   *
   * @param weights Weights to initialize
   */
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
    this.y_cache = null;
  }

  /**
   * Layer parameter initialization constructor.
   *
   * @param weights Weights to copy
   * @param bias Bias to copy
   * @param weight_momentum Weight momentum to copy
   * @param bias_momentum Bias momentum to copy
   */
  public Layer(SimpleMatrix weights, SimpleMatrix bias, SimpleMatrix weight_momentum, SimpleMatrix bias_momentum) {
    this.weights = weights;
    this.bias = bias;

    this.weight_momentum = weight_momentum;
    this.bias_momentum = bias_momentum;

    this.x_cache = null;
    this.z_cache = null;
    this.y_cache = null;
  }

  /**
   * Copy constructor, with updated cache.
   *
   * @param layer Layer to copy
   * @param x_cache x input of the layer
   * @param z_cache z input of the layer
   * @param y_cache y output of the layer
   */
  public Layer(Layer layer, SimpleMatrix x_cache, SimpleMatrix z_cache, SimpleMatrix y_cache) {
    this.weights = layer.weights;
    this.bias = layer.bias;

    this.weight_momentum = layer.weight_momentum;
    this.bias_momentum = layer.bias_momentum;

    this.x_cache = x_cache;
    this.z_cache = z_cache;
    this.y_cache = y_cache;
  }

  /**
   * Initialize the layer weights.
   *
   * @param weights Weight matrix to initialize
   */
  private SimpleMatrix weightInit(SimpleMatrix weights) {
    for (int y = 0; y < weights.numRows(); y++) {
      for (int x = 0; x < weights.numCols(); x++) {
        weights.set(y, x, generator.nextGaussian() / weights.numCols());
      }
    }
    return weights;
  }

  /** Return layer weights. */
  public SimpleMatrix getWeights() {
    return weights;
  }

  /** Return layer bias. */
  public SimpleMatrix getBias() {
    return bias;
  }

  /** Return layer input. */
  public SimpleMatrix get_X() {
    return x_cache;
  }

  /** Return layer activation function input. */
  public SimpleMatrix get_Z() {
    return z_cache;
  }

  /** Return layer output. */
  public SimpleMatrix get_Y() {
    return y_cache;
  }

  /**
   * Apply the gradient step to the layer parameters.
   * 
   * @param error_wrt_weight Derivitive of error with respect to weighs
   * @param error_wrt_bias Derivitive of error with respect to bias
   * @param alpha Learning rate
   * @param beta Momentum
   * @param lambda Regularization
   */
  public Layer gradientStep(SimpleMatrix error_wrt_weight, SimpleMatrix error_wrt_bias, double alpha, double beta, double lambda) {
    // Note the L2 regularization
    SimpleMatrix weight_momentum_new = weight_momentum.scale(beta).plus(error_wrt_weight.plus(this.weights.scale(2).scale(lambda)).scale(alpha)); 
    SimpleMatrix bias_momentum_new = bias_momentum.scale(beta).plus(error_wrt_bias.scale(alpha)); 

    return new Layer(weights.minus(weight_momentum_new), bias.minus(bias_momentum_new), weight_momentum_new, bias_momentum_new);
  }

  /** Return layer state cache. */
  public LayerCache saveCache() {
    return new LayerCache(x_cache, z_cache, y_cache);
  }

  /** Load layer state cache. */
  public Layer loadCache(LayerCache cache) {
    return new Layer(this, cache.get_X(), cache.get_Z(), cache.get_Y());
  } 

}
