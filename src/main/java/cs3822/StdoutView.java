package cs3822;

import java.util.LinkedList;




/*
  1000|0002|0003|0004
  -------------------
  0002|0003|0004|0005
  -------------------
  0003|0004|0004|0005
  -------------------
*/
class StdoutView implements View {
  
  /*
  private List<Actions> process(String) {

  }

  public String getInput() {

  }
  */

  private String clear() {
    LinkedList<String> list = new LinkedList<String>();
    for (int i = 0; i<200; i++) {
      list.push("\n");
    }
    return String.join("", list);
  }

  private String nodeStringify(Node node) throws UnknownNodeTypeException, NoValueException {
    switch(node.getType()) {
      case BRICK:
        return "XXXX";
      case EMPTY:
        return "    ";
      case VALUE:
        return String.format("%4d", node.getValue());
      default:
        throw new UnknownNodeTypeException(); 
    }
  }

  public void display(Grid grid) throws MaxPosNotInitializedException, UnknownNodeTypeException, NoValueException {

    LinkedList<String> output = new LinkedList<String>();
    LinkedList<String> innerStringBuffer = new LinkedList<String>();
    String str;


    for (int y = 0; y < grid.getRows()-1; y++) {
      innerStringBuffer.clear();
      for (int x = 0; x < grid.getCols(); x++) {
        innerStringBuffer.add(nodeStringify(grid.getNodes().get(grid.index(new Position(x, y)))));
      }

      str = String.join("|", innerStringBuffer);
      output.add(str);
      innerStringBuffer.clear();

      for (int i = 0; i < str.length(); i++) {
        innerStringBuffer.add("-");
      }
      output.add(String.join("", innerStringBuffer));
    }

    innerStringBuffer.clear();
    for (int x = 0; x < grid.getCols(); x++) {
      innerStringBuffer.add(nodeStringify(grid.getNodes().get(grid.index(new Position(x, grid.getRows()-1)))));
      }

    str = String.join("|", innerStringBuffer);
    output.add(str);
    
    
    System.out.println(clear() + String.join("\n", output) + "\n\n\n");
  }
  
  
}
