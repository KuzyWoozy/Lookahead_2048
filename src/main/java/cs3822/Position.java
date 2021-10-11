package cs3822;


class Position {
  
  private int x;
  private int y;

  private static int x_max = -1;
  private static int x_min = 0;
  private static int y_max = -1;
  private static int y_min = 0;

  public static void setMax(int x_maximum, int y_maximum) {
    x_max = x_maximum;
    y_max = y_maximum;
  }

  public Position(int x, int y) throws MaxPosNotInitializedException {
    if (x_max == -1 || y_max == -1) {
      throw new MaxPosNotInitializedException();
    }
    this.x = x;
    this.y = y;
  }

  public Position(Position pos) {
    this.x = pos.x;
    this.y = pos.y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public boolean canMoveUp() {
    return y > y_min ? true : false;
  }

  public boolean canMoveRight() {
    return x < (x_max-1) ? true : false;
  }

  public boolean canMoveDown() {
    return y < (y_max-1) ? true : false;
  }

  public boolean canMoveLeft() {
    return x > x_min ? true : false;
  }

  public void moveUp() throws MovingOutOfBoundsException {
    if (canMoveUp()) {
      this.y -= 1;
    } else {
      throw new MovingOutOfBoundsException();
    }
  }

  public void moveRight() throws MovingOutOfBoundsException {
    if (canMoveRight()) {
      this.x += 1;
    } else {
      throw new MovingOutOfBoundsException();
    }
  }

  public void moveDown() throws MovingOutOfBoundsException {
    if (canMoveDown()) {
      this.y += 1;
    } else {
      throw new MovingOutOfBoundsException();
    }
  }

  public void moveLeft() throws MovingOutOfBoundsException {
    if (canMoveDown()) {
      this.x -= 1;
    } else {
      throw new MovingOutOfBoundsException();
    }
  }

  @Override
  public String toString() {
    return String.format("(%d, %d)", x, y);
  }

}
