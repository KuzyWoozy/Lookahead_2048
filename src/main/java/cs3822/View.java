package cs3822;


interface View {

  public void display(Grid grid) throws MaxPosNotInitializedException, UnknownNodeTypeException, NoValueException;
}
