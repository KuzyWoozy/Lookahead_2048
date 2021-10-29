package cs3822;

import java.util.LinkedList;
import java.util.List;


public class App 
{
    public static void main( String[] args ) throws InvalidMapSizeException, InvalidMapSymbolException, MaxPosNotInitializedException, NoValueException, MovingOutOfBoundsException, InvalidActionException, UnknownNodeTypeException, NoMoveFlagException, UnknownStateException {
    String map0 = "##\n##";
    String map1 = "####\n####\n####\n####";
    String map2 = "#~~~~~\n##~~~~\n###~~~\n####~~\n#####~\n######\n";
    String map3 = "~~#~~\n~###~\n#####\n~###~\n~~#~~";
    String map4 = "~###\n#~##\n##~#\n###~";

    Controller control1 = new Controller(new StdoutView(), map0, 16, 1f);
    Controller control2 = new Controller(new StdoutView(), map1, 16, 1f);
    Controller control3 = new Controller(new StdoutView(), map4, 54, 0.9f);

   
    
    System.out.println(String.valueOf(control1.getGrid().hashCode()));
    System.out.println(String.valueOf(control2.getGrid().hashCode()));
    System.out.println(String.valueOf(control3.getGrid().hashCode()));
    





    //control.play();
    
    /* 
    System.out.println(control.getGrid().stringify());
    System.out.println();
    TreeGeneratorMDP tree = new TreeGeneratorMDP(control.getGrid());

    System.out.println("Won nodes: " + String.valueOf(tree.getWon()));
    System.out.println("Lost nodes: " + String.valueOf(tree.getLost()));
    System.out.println("In progress nodes: " + String.valueOf(tree.getInProgress()));
    System.out.println("Total nodes: " + String.valueOf(tree.getTotal()));
    */

    
  }
}
