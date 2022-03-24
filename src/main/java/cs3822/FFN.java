package cs3822;

import java.util.List;
import java.util.LinkedList;
import org.ejml.simple.SimpleMatrix;


/**
 * Feedforward neural network.
 *
 * @author Daniil Kuznetsov
 */
public class FFN {

  private List<Layer> layers = new LinkedList<Layer>();
  final private int layers_num; 

  // learning rate
  final private double alpha;
  // momentum
  final private double beta;
  // regularization
  final private double lambda;
  
  // Input layer network size
  final private int inputSize;
  // Deep layer network size
  final private int middleSize;
  // Output layer network size
  final private int outputSize;

  /**
   * Neural Network constructor.  
   *
   * @param inputSize Input layer network size 
   * @param middleSize Deep layer network size
   * @param outputSize Output layer network size
   * @param alpha Learning rate
   * @param beta Momentum
   * @param lambda Regularization
   * @param layers_num Number of hidden layers
   */
  public FFN(int inputSize, int middleSize, int outputSize, double alpha, double beta, double lambda, int layers_num) {

    this.layers.add(new Layer(new SimpleMatrix(middleSize, inputSize)));

    for (int i = 1; i < layers_num; i++) {
      this.layers.add(new Layer(new SimpleMatrix(middleSize, middleSize)));
    }
    this.layers.add(new Layer(new SimpleMatrix(outputSize, middleSize)));

    this.alpha = alpha;
    this.beta = beta;
    this.lambda = lambda;
    this.inputSize = inputSize;
    this.middleSize = middleSize;
    this.outputSize = outputSize;
    this.layers_num = layers_num;
  }

  
  /** LeakyRelu deep layer activation function. */ 
  private SimpleMatrix middleFunc(SimpleMatrix input) {
    SimpleMatrix mat = input.createLike();
    
    // Iterate over all the elements of the input vector
    for (int y = 0; y < input.numRows(); y++) {
      Double val = input.get(y, 0);
      if (val > 0) {
        mat.set(y, 0, val);
      } else {
        mat.set(y, 0, val * 0.01);
      }
    }

    return mat;
  }

  /** LeakyRelu derivitive of the deep layer  activation function. */
  private SimpleMatrix middleFunc_derv(SimpleMatrix input) {
    SimpleMatrix mat = input.createLike();
   
    for (int y = 0; y < input.numRows(); y++) {
      Double val = input.get(y, 0);
      if (val > 0) {
        mat.set(y, 0, 1);
      } else {
        mat.set(y, 0, 0.01);
      }
    }

    return mat;  
  }

  /** Linear output function. */
  private SimpleMatrix outputFunc(SimpleMatrix input) {
    return input;
  }

  /** Linear derivitive output function. */
  private SimpleMatrix outputFunc_derv(SimpleMatrix input) {
    SimpleMatrix mat = input.createLike();
    mat.fill(1);
    return mat;
  }

  /** Repeat matrix n times, along row axis. */ 
  private SimpleMatrix repeatRowMatrix(int repeat, SimpleMatrix matrix) {
    SimpleMatrix output = new SimpleMatrix(repeat * matrix.numRows(), matrix.numCols());
    for (int i = 0; i < repeat; i++) {
      for (int y = 0; y < matrix.numRows(); y++) {
        for (int x = 0; x < matrix.numCols(); x++) {
          output.set(y + (matrix.numRows() * i), x, matrix.get(y, x));
        }
      }
    }
    return output;
  }
  
  /** Repeat matrix n times, along column axis. */ 
  private SimpleMatrix repeatColMatrix(int repeat, SimpleMatrix matrix) {
    SimpleMatrix output = new SimpleMatrix(matrix.numRows(), repeat * matrix.numCols());
    for (int i = 0; i < repeat; i++) {
      for (int y = 0; y < matrix.numRows(); y++) {
        for (int x = 0; x < matrix.numCols(); x++) {
          output.set(y, x + (matrix.numCols() * i), matrix.get(y, x));
        }
      }
    }
    return output;
  }

  /** Mean squared cost of the output against the label. */
  public SimpleMatrix cost(SimpleMatrix output, SimpleMatrix label) {
    return output.minus(label).elementPower(2);
  }

  /** Mean squared derivitive cost of the output against the label. */
  public SimpleMatrix cost_derv(SimpleMatrix output, SimpleMatrix label) {
    return output.minus(label).scale(2);
  }
 
  /** 
   * Compute gradients for the parameters of each network layer.
   *
   * @param output Network output vector 
   * @param label Label vector
   * @return Return gradients with respect to error, for each network layer. 
   */
  public List<LayerGrad> grad(SimpleMatrix output, SimpleMatrix label) {
    // Manually compute the necessary derivitives

    List<LayerGrad> gradients = new LinkedList<LayerGrad>();

    SimpleMatrix error_wrt_y = cost_derv(output, label);
    
    SimpleMatrix y_wrt_z = outputFunc_derv(layers.get(layers_num).get_Z());
    SimpleMatrix z_wrt_weight = repeatRowMatrix(outputSize, layers.get(layers_num).get_X().transpose());
    SimpleMatrix z_wrt_bias = new SimpleMatrix(outputSize, 1);
    // z_wrt_bias is always 1
    z_wrt_bias.fill(1);

    SimpleMatrix error_wrt_z = error_wrt_y.elementMult(y_wrt_z);

    SimpleMatrix error_wrt_weight = repeatColMatrix(middleSize, error_wrt_z).elementMult(z_wrt_weight);
    SimpleMatrix error_wrt_bias = error_wrt_z.elementMult(z_wrt_bias); 
   
    error_wrt_y = layers.get(layers_num).getWeights().transpose().mult(error_wrt_z);
    
    // Insert at the beginning of the linked list to avoid having to reverse it
    gradients.add(0, new LayerGrad(error_wrt_weight, error_wrt_bias));

    for (int i = layers_num - 1; i > 0; i--) {
      y_wrt_z = middleFunc_derv(layers.get(i).get_Z());
      z_wrt_weight = repeatRowMatrix(middleSize, layers.get(i).get_X().transpose());
      z_wrt_bias = new SimpleMatrix(middleSize, 1);
      // z_wrt_bias is always 1
      z_wrt_bias.fill(1);

      error_wrt_z = error_wrt_y.elementMult(y_wrt_z);

      error_wrt_weight = repeatColMatrix(middleSize, error_wrt_z).elementMult(z_wrt_weight);
      error_wrt_bias = error_wrt_z.elementMult(z_wrt_bias); 
     
      error_wrt_y = layers.get(i).getWeights().transpose().mult(error_wrt_z);
     
      gradients.add(0, new LayerGrad(error_wrt_weight, error_wrt_bias));
    }

    y_wrt_z = middleFunc_derv(layers.get(0).get_Z());
    z_wrt_weight = repeatRowMatrix(middleSize, layers.get(0).get_X().transpose());
    z_wrt_bias = new SimpleMatrix(middleSize, 1);
    // z_wrt_bias is always 1
    z_wrt_bias.fill(1);

    error_wrt_z = error_wrt_y.elementMult(y_wrt_z);

    error_wrt_weight = repeatColMatrix(inputSize, error_wrt_z).elementMult(z_wrt_weight);
    error_wrt_bias = error_wrt_z.elementMult(z_wrt_bias); 
   
    error_wrt_y = layers.get(0).getWeights().transpose().mult(error_wrt_z);
   
    gradients.add(0, new LayerGrad(error_wrt_weight, error_wrt_bias));

    return gradients;
  }

  /** Apply the gradients to each layer, updating network parameters via Stochastic Gradient Descent. */
  public void backprop(List<LayerGrad> grads) {
    for (int i = 0 ; i < layers.size() - 1; i++) {
      layers.set(i, layers.get(i).gradientStep(grads.get(i).getWeightGrad(), grads.get(i).getBiasGrad(), alpha, beta, lambda));
    }
    
    layers.set(layers.size() - 1, layers.get(layers.size() - 1).gradientStep(grads.get(layers.size() - 1).getWeightGrad(), grads.get(layers.size() - 1).getBiasGrad(), alpha, beta, lambda));
  }

  /** Forward propogate the input vector throught the network. */
  public SimpleMatrix prop(SimpleMatrix input) {
    SimpleMatrix output = null;

    for (int i = 0; i < layers_num; i++) {
      SimpleMatrix z = layers.get(i).getWeights().mult(input).plus(layers.get(i).getBias());
      output = middleFunc(z);

      layers.set(i, new Layer(layers.get(i), input, z, output));
      input = output;
    }
    
    SimpleMatrix z = layers.get(layers_num).getWeights().mult(input).plus(layers.get(layers_num).getBias());
    
    output = outputFunc(z);
    layers.set(layers_num, new Layer(layers.get(layers_num), input, z, output));
    
    return output;
  }

  /** Save network state. */
  public List<LayerCache> saveCache() {
    List<LayerCache> list = new LinkedList<LayerCache>();

    for (Layer layer : layers) {
      list.add(layer.saveCache());
    }

    return list;
  }

  /** Load network state. */
  public void loadCache(List<LayerCache> layers_cache) {
    for (int i = 0; i < layers.size(); i++) {
      layers.set(i, layers.get(i).loadCache(layers_cache.get(i)));
    }
  }
  
  /** Return list of network layers. */
  public List<Layer> getLayers() {
    return layers;
  }

  /** Return last layer in the network. */
  public Layer getLastLayer() {
    return layers.get(layers.size() - 1);
  }

}
