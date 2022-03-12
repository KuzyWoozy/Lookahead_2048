package cs3822;


/**
 * Mutable version of the java Float class.
 *
 * @author Daniil Kuznetsov
 */
class MutableFloat {

  private float value;
  
  /** Default constructor. */
  public MutableFloat() {
    this.value = 0f;
  }
  
  /** Initialization constructor. */
  public MutableFloat(float value) {
    this.value = value;
  }

  /** Return value. */
  public float get() {
    return value;
  }

  /** Set value. */
  public void set(float value) {
    this.value = value;
  }

}
