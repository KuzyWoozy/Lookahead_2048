package cs3822;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import java.io.InputStream;


/**
 * The CLI interface.
 *
 * @author Daniil Kuznetsov
 */
class StdoutView implements View {
 
  private Scanner scan;
 
  /** Clear the stdout. */
  private String clear() {
    LinkedList<String> list = new LinkedList<String>();
    for (int i = 0; i<2; i++) {
      list.push("\n");
    }
    return String.join("", list);
  }
 
  /** Print the specified grid to stdout. */ 
  private void display(Grid grid) {
    System.out.println(clear() + grid.stringify() + "\n\n\n");
  }

  public StdoutView(InputStream stream) {
    this.scan = new Scanner(stream);
  }
  
  /** Obtain the list of user Actions from the stdin. */
  @Override
  public List<Action> getInput() {
    System.out.print("\nEnter command(s): ");
    return View.convertStringToActions(scan.nextLine());
  }
   
  /** Execute the game logic. */
  @Override
  public GameStats play(GridManager manager, Algorithm algo) {
    GameStats stat = new GameStats();
    while (true) {
      Grid grid = manager.show();
      display(grid);
      if (grid.won()) {
        stat.won();
        break;
      } else if (grid.lost()) {
        stat.lost();
        break;
      }
      List<Action> action = algo.move(grid);
      manager.process(action);
    }
    display(manager.show());

    return stat;
  }

}
