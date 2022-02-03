package cs3822;

/**
 * Represents a node in the model DAG.
 *
 * @author Daniil Kuznetsov
 */
class Triplet<A, B, C> implements java.io.Serializable {

  final private A item1;
  final private B item2;
  final private C item3;

  public Triplet(A item1, B item2, C item3) {
    this.item1 = item1;
    this.item2 = item2;
    this.item3 = item3;
  }

  public A getFirst() {
    return item1;
  }

  public B getSecond() {
    return item2;
  }
  
  public C getThird() {
    return item3;
  }


}
