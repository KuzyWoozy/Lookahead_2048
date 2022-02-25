package cs3822;

import java.util.List;
import java.util.LinkedList;
import org.ejml.simple.SimpleMatrix;


class ReinLearn implements Algorithm {

  final private FFN neural;
  final private Grid instance;

  public ReinLearn(Grid instance, int middleLayerSize, double alpha, double beta, double lambda, int layers_num) {
    this.instance = instance;
    this.neural = new FFN(instance.getRows() * instance.getCols(), middleLayerSize, 4, alpha, beta, lambda, layers_num);
  }

  private int argmax(SimpleMatrix vector) {
    int maxArg = 0;
    double maxValue = 0d;
    
    for (int i = 0; i < vector.numRows(); i++) {
      if (maxValue < vector.get(i, 0)) {
        maxArg = i;
        maxValue = vector.get(i, 0);
      }
    }

    return maxArg;
  }

  /*
  public void train(int games) {
    Grid instance = new Grid(grid);
    for () {
      
      grid.play();

      instance.restart();
    }

  }
  */

  public List<Action> move(Grid instance) {
    List<Action> actions = new LinkedList<Action>();
    try {
      actions.add(Action.convertIntToAction(argmax(neural.prop(instance.toVector()))));
    } catch(InvalidActionException e) {
      e.printStackTrace();
      System.exit(1);
    }
    return actions;
  } 


}
