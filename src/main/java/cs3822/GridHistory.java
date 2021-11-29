package cs3822;

import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * History of a Grid instance, going back to its initial state
 * 
 * @author Daniil Kuznetsov
 */
class GridHistory {
  private Stack<Node[]> instances;
  private Stack<Node[]> temp;

  
  public GridHistory(List<Node> initialState) {
    instances = new Stack<Node[]>();
    instances.push(initialState.toArray(new Node[initialState.size()]));
    temp = new Stack<Node[]>();
  }

  public GridHistory(GridHistory history) throws UnknownNodeTypeException, NoValueException {
    instances = new Stack<Node[]>();
    Node[] list;
    Node[] new_list;
    for (int i = 0; i < history.instances.size(); i++) {
      list = history.instances.get(i);
      new_list = new Node[list.length];
      for (int x = 0; x < list.length; x++) {
        new_list[x] = Node.copyNode(list[x]);
      }
      instances.push(new_list);
    }

    temp = new Stack<Node[]>();
    for (int i = 0; i < history.temp.size(); i++) {
      list = history.temp.get(i);
      new_list = new Node[list.length];
      for (int x = 0; x < list.length; x++) {
        new_list[x] = Node.copyNode(list[x]);
      }
      temp.push(new_list);
    }
  }

  private void clearBuffer() {
    while(!temp.empty()) {
      temp.pop();
    }
  }

  public List<Node> undo() throws UnknownNodeTypeException, NoValueException {
    // Do not take initial state into account for instances size
    if (instances.size() == 1) {

      Node[] items = instances.peek();
      Node[] items_copy = new Node[items.length];
      for (int i = 0; i < items.length; i++) {
        items_copy[i] = Node.copyNode(items[i]);
      }

      return new ArrayList<Node>(Arrays.asList(items_copy));
    }

    temp.push(instances.pop());
  
    Node[] items = instances.peek();
    Node[] items_copy = new Node[items.length];
    for (int i = 0; i < items.length; i++) {
      items_copy[i] = Node.copyNode(items[i]);
    }

    return new ArrayList<Node>(Arrays.asList(items_copy));
  }

  public List<Node> redo() throws NoFutureGridException, UnknownNodeTypeException, NoValueException {
    if (temp.empty()) {
      throw new NoFutureGridException();
    }
    Node[] items = temp.pop();
    instances.push(items);

    Node[] items_copy = new Node[items.length];
    for (int i = 0; i < items.length; i++) {
      items_copy[i] = Node.copyNode(items[i]); 
    }

    return new ArrayList<Node>(Arrays.asList(items_copy));
  }

  public void add(List<Node> instance) {
    instances.push(instance.toArray(new Node[instance.size()]));
    clearBuffer();
  }
   
}
