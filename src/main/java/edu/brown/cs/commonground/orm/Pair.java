package edu.brown.cs.commonground.orm;

import java.util.Objects;

/**
 * Class representing a pair of two objects.
 *
 * @author calderhoover
 *
 * @param <A>
 *          : type of first object
 * @param <B>
 *          : type of second object
 */
public class Pair<A, B> {
  private A a;
  private B b;

  /**
   * Constructor for Pair.
   *
   * @param a
   *          : first element in Pair
   * @param b
   *          : second element in Pair
   */
  public Pair(A a, B b) {
    this.a = a;
    this.b = b;
  }

  /**
   * Gets first element in the Pair.
   *
   * @return first element of Pair
   */
  public A getFirst() {
    return a;
  }

  /**
   * Gets second element in this Pair.
   *
   * @return second element in Pair
   */
  public B getSecond() {
    return b;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null || !Pair.class.isAssignableFrom(object.getClass())) {
      return false;
    }
    // Suppress warnings here because types inside Pair will be checked
    // automatically in their equals() methods used below.
    @SuppressWarnings("rawtypes")
    Pair pair = (Pair) object;
    return getFirst().equals(pair.getFirst())
        && getSecond().equals(pair.getSecond());
  }

  @Override
  public int hashCode() {
    return Objects.hash(a, b);
  }
}
