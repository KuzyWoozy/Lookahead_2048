package cs3822;

import java.util.LinkedList;
import java.util.List;


public class App 
{
    public static void main( String[] args ) throws InvalidMapSizeException, InvalidMapSymbolException, MaxPosNotInitializedException, NoValueException, MovingOutOfBoundsException, InvalidActionException, UnknownNodeTypeException 
    {
      Controller control = new Controller("4#4\n2~#\n2#4", 0.1f, 0.9f);

      for (Node node : control.getGrid().getNodes()) {
        System.out.print(node + ", ");
      } 
      System.out.println();
      System.out.println(); 
      List<Actions> list;
      list = new LinkedList<Actions>();
      list.add(Actions.SWIPE_RIGHT);
      control.process(list);
      for (Node node : control.getGrid().getNodes()) {
        System.out.print(node + ", ");
      } 
      System.out.println();
      System.out.println(); 
      list = new LinkedList<Actions>();
      list.add(Actions.UNDO);
      control.process(list);
      for (Node node : control.getGrid().getNodes()) {
        System.out.print(node + ", ");
      } 
      System.out.println();
      System.out.println(); 
      list = new LinkedList<Actions>();
      list.add(Actions.REDO);
      control.process(list);
      for (Node node : control.getGrid().getNodes()) {
        System.out.print(node + ", ");
      } 
      System.out.println();
      System.out.println(); 
      list = new LinkedList<Actions>();
      list.add(Actions.RESET);
      control.process(list);
      for (Node node : control.getGrid().getNodes()) {
        System.out.print(node + ", ");
      }
      System.out.println();
     
  }
}
