package cs3822;

import java.util.Random;
import java.util.List;
import java.util.LinkedList;
import org.ejml.simple.SimpleMatrix;


class ReinLearn implements Algorithm {

  final private FFN neural;
  final private GridManager manager;
 
  private Random generator = new Random();
  UniformRandomPlay explorer = new UniformRandomPlay();

  
  public ReinLearn(Grid instance, int middleLayerSize, double alpha, double beta, double lambda, int layers_num) {
    this.manager = new GridManager(instance);
    this.neural = new FFN(instance.getRows() * instance.getCols(), middleLayerSize, 4, alpha, beta, lambda, layers_num);

    this.generator = new Random();
    this.explorer = new UniformRandomPlay();
  }

  private int argmax(SimpleMatrix vector) {
    int maxArg = 0;
    Double maxValue = Double.NEGATIVE_INFINITY;
    
    for (int i = 0; i < vector.numRows(); i++) {
      Double value = new Double(vector.get(i, 0));
      if (maxValue < value) {
        maxArg = i;
        maxValue = value;
      }
    }
    return maxArg;
  }
  
  private List<Triplet<List<LayerCache>, Action, Double>> play(double epsilon) {
    List<Triplet<List<LayerCache>, Action, Double>> neural_cache = new LinkedList<Triplet<List<LayerCache>, Action, Double>>();

    Grid grid = manager.show();
    // Assuming we do not start with a won or lost state
    while (true) {
      List<Action> actions = moveEpsilon(grid, epsilon);
      List<Grid> frames = manager.process(actions);
      
      int score_old = grid.getScore();
      grid = manager.show(); 

      if (grid.lost()) {
        neural_cache.add(0, new Triplet(neural.saveCache(), actions.get(0), new Double(-grid.getMaxValue()) * 2));
        break;
      }
      
      // Rely on 'madness' to break out if looping
      if (!GridManager.hasMoved(frames)) {
        neural_cache.add(0, new Triplet(neural.saveCache(), actions.get(0), new Double(0)));
        continue; 
      }

      neural_cache.add(0, new Triplet(neural.saveCache(), actions.get(0), new Double(grid.getScore() - score_old)));
    }

    return neural_cache;
  }

  private SimpleMatrix createLabel(SimpleMatrix output, Action action, double reward) {
    SimpleMatrix label = new SimpleMatrix(output);
    
    try {
      label.set(Action.convertActionToInt(action), reward);
    } catch(InvalidActionException e) {
      e.printStackTrace();
      System.exit(1);
    }

    return label;
  }
  
  public void train(int games) {
    List<Triplet<List<LayerCache>, Action, Double>> cache = null;
    List<List<LayerGrad>> gradients = null;

    for (int i = 0; i < games; i++) {
      
      cache = play(0.1d);
      
      gradients = new LinkedList<List<LayerGrad>>();
      double futureReward = 0;
      for (Triplet<List<LayerCache>, Action, Double> neural_cache : cache) {
        neural.loadCache(neural_cache.getFirst());

        double immediateReward = neural_cache.getThird() + 1 * futureReward;
        gradients.add(neural.grad(neural.getLastLayer().get_Y(), createLabel(neural.getLastLayer().get_Y(), neural_cache.getSecond(), immediateReward)));
        
        futureReward = immediateReward;
      }

      String printable = "";
      for (Layer layer : neural.getLayers()) {
        printable += layer.getWeights().elementSum() + "\n";
        printable += layer.getWeights().elementMaxAbs() + "\n"; 
        printable += "---\n";
      }
      printable += "----\n";
      printable += cache.get(0).getFirst().get(cache.get(0).getFirst().size() - 1).get_Y() + "\n";
      
      System.out.println(printable);

      neural.backprop(LayerGrad.avgRows(gradients));

      manager.restart();
      
      System.out.println("Game finished: " + String.valueOf(i));
    }
  }
  

  private List<Action> moveEpsilon(Grid instance, double epsilon) {
    List<Action> actions = null;
    try {
      SimpleMatrix y = neural.prop(instance.toVector());
      if (epsilon <= generator.nextDouble()) {
        actions = new LinkedList<Action>();
        actions.add(Action.convertIntToAction(argmax(y)));
      } else {
        actions = explorer.move(instance);
      }
    } catch(InvalidActionException e) {
      e.printStackTrace();
      System.exit(1);
    }
    return actions;
  }

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
