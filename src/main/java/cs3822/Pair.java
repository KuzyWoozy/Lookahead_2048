package cs3822;

/**
 * Tuple of two arbitary objects.
 *
 * @author Daniil Kuznetsov
 */
class Pair<A, B> implements java.io.Serializable {

  final private A item1;
  final private B item2;
  
  /** Construct a tuple of two objects. */
  public Pair(A item1, B item2) {
    this.item1 = item1;
    this.item2 = item2;
  }
  
  /** Return first element of the tuple. */
  public A getFirst() {
    return item1;
  }

  /** Return second element of the tuple. */
  public B getSecond() {
    return item2;
  }

}
