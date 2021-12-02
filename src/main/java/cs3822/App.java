package cs3822;

import java.util.HashMap;
import java.io.IOException;


/** Class with the main method, contains all the setup. */
public class App 
{
    public static void main( String[] args ) throws InvalidMapSizeException, InvalidMapSymbolException, MaxPosNotInitializedException, NoValueException, MovingOutOfBoundsException, InvalidActionException, UnknownNodeTypeException, NoMoveFlagException, IOException, InvalidValueException {
    
    String map0 = "##\n##";
    String map1 = "###\n###\n###";
    String map2 = "#~~~~~\n##~~~~\n###~~~\n####~~\n#####~\n######\n";
    String map3 = "~~#~~\n~###~\n#####\n~###~\n~~#~~";
    String map4 = "~###\n#~##\n##~#\n###~";

    Controller control = new Controller(new StdoutView(), map0, 32, 0.9f);

    //control.play(); 
    //TreeGeneratorMDP gen = new TreeGeneratorMDP(control.getGrid(), 0.9f);
    //HashMap<Integer, SolTableItem> hashMap = gen.getMapRef();   
    //System.out.println(String.valueOf(hashMap.size()));
    //System.out.println(control.getGrid().stringify());
    //System.out.println(hashMap.get(control.getGrid().hashCode()).getReward());
    //gen.save("saved-map-1.serial");
   
  }
}
