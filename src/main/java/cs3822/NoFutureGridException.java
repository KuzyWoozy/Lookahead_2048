package cs3822;

/** Raised when the redo operation attempts to access an empty buffer. */
public class NoFutureGridException extends Exception {
  public NoFutureGridException() {
    super();
  }
}
