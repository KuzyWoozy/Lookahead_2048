package cs3822;

import java.util.List;
import java.util.ArrayList;
import org.ejml.simple.SimpleMatrix;


class FFN {

  private List<Layer> layers = new ArrayList<Layer>();
  final private int layers_num; 


  final private double alpha;

  final private int inputSize;
  final private int middleSize;
  final private int outputSize;

    
  private SimpleMatrix middleFunc(SimpleMatrix input) {
    SimpleMatrix mat = input.createLike();
   
    for (int y = 0; y < input.numRows(); y++) {
      Double val = input.get(y, 0);
      if (val > 0) {
        mat.set(y, 0, val);
      } else {
        mat.set(y, 0, 0);
      }
    }

    return mat;
  }

  private SimpleMatrix middleFunc_derv(SimpleMatrix input) {
    SimpleMatrix mat = input.createLike();
   
    for (int y = 0; y < input.numRows(); y++) {
      Double val = input.get(y, 0);
      if (val > 0) {
        mat.set(y, 0, 1);
      } else {
        mat.set(y, 0, 0);
      }
    }

    return mat;  
  }

  private SimpleMatrix outputFunc(SimpleMatrix input) {
    return input;
  }

  private SimpleMatrix outputFunc_derv(SimpleMatrix input) {
    SimpleMatrix mat = input.createLike();
    mat.fill(1);
    return mat;
  }


  private SimpleMatrix cost(SimpleMatrix output, SimpleMatrix label) {
    return output.minus(label).elementPower(2);
  }

  private SimpleMatrix cost_derv(SimpleMatrix output, SimpleMatrix label) {
    return output.minus(label).scale(2);
  }

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

  public FFN(int inputSize, int middleSize, int outputSize, double alpha, int layers_num) {

    this.layers.add(new Layer(new SimpleMatrix(middleSize, inputSize)));

    for (int i = 1; i < layers_num; i++) {
      this.layers.add(new Layer(new SimpleMatrix(middleSize, middleSize)));
    }
    this.layers.add(new Layer(new SimpleMatrix(outputSize, middleSize)));

    
    this.alpha = alpha;
    this.inputSize = inputSize;
    this.middleSize = middleSize;
    this.outputSize = outputSize;
    this.layers_num = layers_num;
  }

  public SimpleMatrix backprop(SimpleMatrix input, SimpleMatrix label) {
    
    SimpleMatrix output = cost_derv(prop(input), label);
    
    SimpleMatrix error_wrt_y = new SimpleMatrix(output);
    
    SimpleMatrix y_wrt_z = outputFunc_derv(layers.get(layers_num).get_Z());
    SimpleMatrix z_wrt_weight = repeatRowMatrix(outputSize, layers.get(layers_num).get_X().transpose());
    SimpleMatrix z_wrt_bias = new SimpleMatrix(outputSize, 1);
    // z_wrt_bias is always 1
    z_wrt_bias.fill(1);

    SimpleMatrix error_wrt_z = error_wrt_y.elementMult(y_wrt_z);

    SimpleMatrix error_wrt_weight = repeatColMatrix(middleSize, error_wrt_z).elementMult(z_wrt_weight);
    SimpleMatrix error_wrt_bias = error_wrt_z.elementMult(z_wrt_bias); 
   
    error_wrt_y = layers.get(layers_num).getWeights().transpose().mult(error_wrt_z);
  
    layers.set(layers_num, layers.get(layers_num).gradientStep(error_wrt_weight, error_wrt_bias, alpha));


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
     
      layers.set(i, layers.get(i).gradientStep(error_wrt_weight, error_wrt_bias, alpha));
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
   
    layers.set(0, layers.get(0).gradientStep(error_wrt_weight, error_wrt_bias, alpha));    
  
    return output;
  }

  public SimpleMatrix prop(SimpleMatrix input) {
    for (int i = 0; i < layers_num; i++) {
      SimpleMatrix z = layers.get(i).getWeights().mult(input).plus(layers.get(i).getBias());
      layers.set(i, new Layer(layers.get(i), input, z));

      input = middleFunc(z);
    }
    
    SimpleMatrix z = layers.get(layers_num).getWeights().mult(input).plus(layers.get(layers_num).getBias());
    layers.set(layers_num, new Layer(layers.get(layers_num), input, z));
    input = outputFunc(z);


    // Just so we don't output 'input'
    SimpleMatrix output = input; 
    return output;
  }

}
