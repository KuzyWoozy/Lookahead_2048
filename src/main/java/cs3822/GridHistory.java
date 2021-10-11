package cs3822;

import java.util.Stack;
import java.util.List;
import java.util.Arrays;


class GridHistory {
  private Stack<Node[]> instances;
  private Stack<Node[]> temp;

  public GridHistory() {
    instances = new Stack<Node[]>();
    temp = new Stack<Node[]>();
  }

  private void clearBuffer() {
    while(!temp.empty()) {
      temp.pop();
    }
  }

  public List<Node> undo() throws EmptyGridHistoryException {
    if (instances.empty()) {
      throw new EmptyGridHistoryException();
    }
    Node[] item = instances.pop();
    temp.push(item);
    
    return Arrays.asList(instances.peek());
  }

  public List<Node> redo() throws NoFutureGridException {
    if (temp.empty()) {
      throw new NoFutureGridException();
    }
    Node[] item = temp.pop();
    instances.push(item);
    return Arrays.asList(item);
  }

  public void add(List<Node> instance) {
    
    instances.push(instance.toArray(new Node[instance.size()]));
    clearBuffer();
  }
   
}
