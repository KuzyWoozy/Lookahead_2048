package cs3822;

import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


class GridHistory {
  private Stack<Node[]> instances;
  private Stack<Node[]> temp;

  public GridHistory(List<Node> initialState) {
    instances = new Stack<Node[]>();
    instances.push(initialState.toArray(new Node[initialState.size()]));
    temp = new Stack<Node[]>();
  }

  private void clearBuffer() {
    while(!temp.empty()) {
      temp.pop();
    }
  }

  public List<Node> undo() throws EmptyGridHistoryException {
    // Do not take initial state into account for instances size
    if (instances.size() <= 1) {
      throw new EmptyGridHistoryException();
    }
    Node[] item = instances.pop();
    temp.push(item);
   
    return new ArrayList<Node>(Arrays.asList(instances.peek()));
  }

  public List<Node> redo() throws NoFutureGridException {
    if (temp.empty()) {
      throw new NoFutureGridException();
    }
    Node[] item = temp.pop();
    instances.push(item);

    return new ArrayList<Node>(Arrays.asList(item));
  }

  public void add(List<Node> instance) {
    
    instances.push(instance.toArray(new Node[instance.size()]));
    clearBuffer();
  }
   
}
