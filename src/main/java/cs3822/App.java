package cs3822;

import java.util.LinkedList;
import java.util.List;


public class App 
{
    public static void main( String[] args ) throws InvalidMapSizeException, InvalidMapSymbolException, MaxPosNotInitializedException, NoValueException, MovingOutOfBoundsException, InvalidActionException, UnknownNodeTypeException 
    {

      String map1 = "####\n####\n####\n####";
      String map2 = "#~~~~~\n##~~~~\n###~~~\n####~~\n#####~\n######\n";
      String map3 = "~~#~~\n~###~\n#####\n~###~\n~~#~~";


      String map4 = "~###\n#~##\n##~#\n###~";

      Controller control = new Controller(new StdoutView(), map4, 0.9f);

      control.play(); 
  }
}
