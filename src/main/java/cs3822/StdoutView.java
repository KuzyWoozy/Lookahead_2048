package cs3822;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;


/**
 * The CLI interface.
 *
 * @author Daniil Kuznetsov
 */
class StdoutView implements View {
 
  private Scanner scan = new Scanner(System.in);
 
  /** Converts String object into a corresponding Action. */
  private List<Action> convertStringToAction(String actionString) {
    actionString = actionString.toLowerCase();
    LinkedList<Action> list = new LinkedList<Action>();
    for (char x : actionString.toCharArray()) {
      Action ac = null;
      // Chech if this is a valid action, ignore if not
      try {
        ac = Action.getAction(x);
      } catch(InvalidActionException e) {
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
        case EXIT:
          System.exit(0);
        default:
      }
    }
    return list;
  }
  
  /** Obtain the list of user Actions from the stdin. */
  @Override
  public List<Action> getInput() {
    System.out.print("\nEnter command(s): ");
    return convertStringToAction(scan.nextLine()); 
  }
  
  /** Clear the stdout. */
  private String clear() {
    LinkedList<String> list = new LinkedList<String>();
    for (int i = 0; i<2; i++) {
      list.push("\n");
    }
    return String.join("", list);
  }

  
  /** Print the specified grid to stdout. */ 
  @Override
  public void display(Grid grid) {
    System.out.println(clear() + grid.stringify() + "\n\n\n");
  }
  
  
}
