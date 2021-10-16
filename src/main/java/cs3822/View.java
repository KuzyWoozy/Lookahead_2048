package cs3822;

import java.util.List;



interface View {

  public List<Actions> getInput() throws UnknownNodeTypeException;

  public void display(Grid grid) throws MaxPosNotInitializedException, UnknownNodeTypeException, NoValueException;
}
