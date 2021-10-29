package cs3822;

import java.util.Objects;


class BrickNode extends Node {

  public BrickNode() {
    super();
  }

  public BrickNode(Position pos) {
    super(pos);
  }

  public BrickNode(BrickNode node) {
    super(node);
  }

  public BrickNode(Node node) {
    super(node);
  }

  @Override
  public NodeType getType() {
    return NodeType.BRICK;
  }

  @Override
  public int getValue() throws NoValueException {
    throw new NoValueException();
  }

  @Override
  public void setValue(int value) {
    
  }


  @Override
  public boolean getMoveFlag() throws NoMoveFlagException {
    throw new NoMoveFlagException();
  }

  @Override
  public void onMoveFlag() {

  }

  @Override
  public void offMoveFlag() {

  }

  @Override 
  public int hashCode() {
    return Objects.hash(pos, NodeType.BRICK);
  }

  @Override
  public String toString() {
    return "[" + pos + " Brick]";
  }

} 


