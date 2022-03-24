package cs3822;

import java.util.List;
import java.util.LinkedList;

/**
 * Interface for user interfaces (no pun intended).
 *
 * @author Daniil Kuznetsov
 */
public interface View {
    /** Converts String object into a corresponding Action. */
    public static List<Action> convertStringToActions(String actionString) {
      actionString = actionString.toLowerCase();
      LinkedList<Action> list = new LinkedList<Action>();
      for (char x : actionString.toCharArray()) {
        Action ac = null;
        // Chech if this is a valid action, ignore if not
        try {
          ac = Action.convertCharToAction(x);
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
  
  /** Return user input in form of Actions. */
  public List<Action> getInput();

  /**
   * Play the game from the given start state and use the algorithm specified.
   *
   * @param manager Grid history, with the latest state used as the start state
   * @param algo Algorithm used to play the game
   * @return Post game statistics
   */
  public GameStats play(GridManager manager, Algorithm algo);
}
