package cs3822;

import java.util.LinkedList;
import java.util.List;


public class App 
{
    public static void main( String[] args ) throws InvalidMapSizeException, InvalidMapSymbolException, MaxPosNotInitializedException, NoValueException, MovingOutOfBoundsException, InvalidActionException, UnknownNodeTypeException 
    {
      Controller control = new Controller(new StdoutView(), "4#4#\n2~##\n2#4#\n####", 0.1f, 0.9f);

      control.play(); 
  }
}
