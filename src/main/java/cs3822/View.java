package cs3822;

import java.util.List;
import java.util.LinkedList;

/**
 * Interface for user interfaces (no pun intended).
 *
 * @author Daniil Kuznetsov
 */
interface View {
    /** Converts String object into a corresponding Action. */
    public static List<Action> convertStringToActions(String actionString) {
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
  
  public List<Action> getInput();

  /** Update the interface with the new grid state. */
  public GameStats play(Grid grid, Algorithm algo);
}
