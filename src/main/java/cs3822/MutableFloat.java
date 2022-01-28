package cs3822;



class MutableFloat {

  private float value;

  public MutableFloat() {
    this.value = 0f;
  }

  public MutableFloat(float value) {
    this.value = value;
  }

  public float get() {
    return value;
  }

  public void set(float value) {
    this.value = value;
  }

}
