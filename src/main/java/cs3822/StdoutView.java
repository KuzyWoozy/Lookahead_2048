package cs3822;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;


/*
  1000|0002|0003|0004
  -------------------
  0002|0003|0004|0005
  -------------------
  0003|0004|0004|0005
  -------------------
*/
class StdoutView implements View {
 
  private Scanner scan = new Scanner(System.in);
  
  private List<Action> convertStringToAction(String actionString) throws UnknownNodeTypeException {
    actionString = actionString.toLowerCase();
    LinkedList<Action> list = new LinkedList<Action>();
    for (char x : actionString.toCharArray()) {
      Action ac = null;
      try {
        ac = Action.getAction(x);
      } catch(UnknownNodeTypeException e) {
        continue;
      }

      switch(ac) {
        case SWIPE_UP:
          list.add(Action.SWIPE_UP) ; 
          break;
        case SWIPE_RIGHT:
          list.add(Action.SWIPE_RIGHT);  
          break;
        case SWIPE_DOWN:
          list.add(Action.SWIPE_DOWN); 
          break;
        case SWIPE_LEFT:
          list.add(Action.SWIPE_LEFT); 
          break;
        case UNDO:
          list.add(Action.UNDO); 
          break;
        case REDO:
          list.add(Action.REDO); 
          break;
        case RESET:
          list.add(Action.RESET); 
          break;
        default:
      }
    }
    return list;
  }
  
  @Override
  public List<Action> getInput() throws UnknownNodeTypeException {
    System.out.print("\nEnter command: ");
    return convertStringToAction(scan.nextLine()); 
  }
  

  private String clear() {
    LinkedList<String> list = new LinkedList<String>();
    for (int i = 0; i<2; i++) {
      list.push("\n");
    }
    return String.join("", list);
  }

  
  
  @Override
  public void display(Grid grid) throws MaxPosNotInitializedException, UnknownNodeTypeException, NoValueException {
    System.out.println(clear() + grid.stringify() + "\n\n\n");
  }
  
  
}
