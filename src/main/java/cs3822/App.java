package cs3822;

import java.util.HashMap;


public class App 
{
    public static void main( String[] args ) throws InvalidMapSizeException, InvalidMapSymbolException, MaxPosNotInitializedException, NoValueException, MovingOutOfBoundsException, InvalidActionException, UnknownNodeTypeException, NoMoveFlagException, UnknownStateException {
    // 161
    String map0 = "##\n##";
    String map1 = "###\n###\n###";
    String map2 = "#~~~~~\n##~~~~\n###~~~\n####~~\n#####~\n######\n";
    String map3 = "~~#~~\n~###~\n#####\n~###~\n~~#~~";
    String map4 = "~###\n#~##\n##~#\n###~";

    Controller control = new Controller(new StdoutView(), map0, 256, 1f);

    //control.play(); 
    
    HashMap<Integer, Actions> hashMap = new TreeGeneratorMDP(control.getGrid()).getMapRef();
    System.out.println(String.valueOf(hashMap.size()));

  }
}
