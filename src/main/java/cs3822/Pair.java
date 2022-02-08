package cs3822;

/**
 * Represents a node in the model DAG.
 *
 * @author Daniil Kuznetsov
 */
class Pair<A, B> implements java.io.Serializable {

  final private A item1;
  final private B item2;

  public Pair(A item1, B item2) {
    this.item1 = item1;
    this.item2 = item2;
  }

  public A getFirst() {
    return item1;
  }

  public B getSecond() {
    return item2;
  }

}
