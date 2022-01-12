package cs3822;

import java.util.Objects;


/**
 * Represents an index within the grid.
 *
 * @author Daniil Kuznetsov
 */
class Position {

  private int x;
  private int y;

  private static int x_max = -1;
  private static int x_min = 0;
  private static int y_max = -1;
  private static int y_min = 0;

  /** Sets the bounds of the grid, assumes to start from 0. */
  public static void setMax(int x_maximum, int y_maximum) {
    x_max = x_maximum;
    y_max = y_maximum;
  }

  /** Constucts object with specified coordinates. */
  public Position(int x, int y) throws MaxPosNotInitializedException {
    if (x_max == -1 || y_max == -1) {
      throw new MaxPosNotInitializedException();
    }
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

  /** Returns true if node can move up. */
  public boolean canMoveUp() {
    return y > y_min ? true : false;
  }

  /** Returns true if node can move right. */
  public boolean canMoveRight() {
    return x < (x_max-1) ? true : false;
  }

  /** Returns true if node can move down. */
  public boolean canMoveDown() {
    return y < (y_max-1) ? true : false;
  }
  
  /** Returns true if node can move left. */
  public boolean canMoveLeft() {
    return x > x_min ? true : false;
  }

  /** Move up in the grid. */
  public void moveUp() throws MovingOutOfBoundsException {
    if (canMoveUp()) {
      this.y -= 1;
    } else {
      throw new MovingOutOfBoundsException();
    }
  }

  /** Move right in the grid. */
  public void moveRight() throws MovingOutOfBoundsException {
    if (canMoveRight()) {
      this.x += 1;
    } else {
      throw new MovingOutOfBoundsException();
    }
  }
  
  /** Move down in the grid. */
  public void moveDown() throws MovingOutOfBoundsException {
    if (canMoveDown()) {
      this.y += 1;
    } else {
      throw new MovingOutOfBoundsException();
    }
  }

  /** Move left in the grid. */
  public void moveLeft() throws MovingOutOfBoundsException {
    if (canMoveLeft()) {
      this.x -= 1;
    } else {
      throw new MovingOutOfBoundsException();
    }
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
