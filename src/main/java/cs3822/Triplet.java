package cs3822;

/**
 * Tuple of three arbitary objects.
 *
 * @author Daniil Kuznetsov
 */
class Triplet<A, B, C> implements java.io.Serializable {

  final private A item1;
  final private B item2;
  final private C item3;
  
  /** Construct a tuple of three objects. */
  public Triplet(A item1, B item2, C item3) {
    this.item1 = item1;
    this.item2 = item2;
    this.item3 = item3;
  }
  
  /** Return first element of the tuple. */
  public A getFirst() {
    return item1;
  }

  /** Return second element of the tuple. */
  public B getSecond() {
    return item2;
  }
  
  /** Return three element of the tuple. */
  public C getThird() {
    return item3;
  }


}
