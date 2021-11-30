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

  /** Constructs from specified initial state of nodes. */ 
  public GridHistory(List<Node> initialState) {
    instances = new Stack<Node[]>();
    instances.push(initialState.toArray(new Node[initialState.size()]));
    temp = new Stack<Node[]>();
  }

  /** Copy constructor. */
  public GridHistory(GridHistory history) throws UnknownNodeTypeException, NoValueException {
    instances = new Stack<Node[]>();
    Node[] list;
    Node[] new_list;
    for (int i = 0; i < history.instances.size(); i++) {
      list = history.instances.get(i);
      // Make sure to peform deep copies
      new_list = new Node[list.length];
      for (int x = 0; x < list.length; x++) {
        new_list[x] = Node.copyNode(list[x]);
      }
      instances.push(new_list);
    }

    temp = new Stack<Node[]>();
    for (int i = 0; i < history.temp.size(); i++) {
      list = history.temp.get(i);
      // Make sure to peform deep copies
      new_list = new Node[list.length];
      for (int x = 0; x < list.length; x++) {
        new_list[x] = Node.copyNode(list[x]);
      }
      temp.push(new_list);
    }
  }
  
  /** Clear out the undone changes buffer. */
  private void clearBuffer() {
    while(!temp.empty()) {
      temp.pop();
    }
  }

  /** Return list of nodes representing the new Grid after an undo operation. */
  public List<Node> undo() throws UnknownNodeTypeException, NoValueException {
    // Do not take initial state into account for instances size
    if (instances.size() > 1) {
      temp.push(instances.pop());
    }
  
    Node[] items = instances.peek();
    Node[] items_copy = new Node[items.length];
    for (int i = 0; i < items.length; i++) {
      items_copy[i] = Node.copyNode(items[i]);
    }

    return new ArrayList<Node>(Arrays.asList(items_copy));
  }

  /** 
   * Perform a redo operation on the Grid instance.
   *
   * @return list of nodes representing the new Grid
   * @throws NoFutureGridException 
   */
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

  /** Add a new state to the history and overwrite the current future. */
  public void add(List<Node> instance) {
    instances.push(instance.toArray(new Node[instance.size()]));
    // Delete the current future
    clearBuffer();
  }
   
}
