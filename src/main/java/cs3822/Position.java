package cs3822;

import java.util.Objects;


/**
 * Represents an index within the grid.
 *
 * @author Daniil Kuznetsov
 */
final class Position {

  final private int x;
  final private int y;

  /** Constucts object with specified coordinates. */
  public Position(int x, int y) {
    this.x = x;
    this.y = y;
  }

  /** Copy constructor. */
  public Position(Position pos) {
    this.x = pos.x;
    this.y = pos.y;
  }

  /** Returns x coordinate. */
  public int getX() {
    return x;
  }

  /** Returns y coordinate. */
  public int getY() {
    return y;
  }

  /** Move up in the grid. */
  public Position moveUp() {
    return new Position(x, y - 1);
  }

  /** Move right in the grid. */
  public Position moveRight() {
    return new Position(x + 1, y);
  }
  
  /** Move down in the grid. */
  public Position moveDown() {
    return new Position(x, y + 1);
  }

  /** Move left in the grid. */
  public Position moveLeft() {
    return new Position(x - 1, y);
  }

  /** Return string representation. */
  @Override
  public String toString() {
    return String.format("(%d, %d)", x, y);
  }

  /** Return true if positions equal each other. */
  public boolean equals(Position pos) {
    return x == pos.x && y == pos.y;
  }

  /** Return hash of position. */
  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }


}
