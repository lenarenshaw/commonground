package edu.brown.cs.commonground.graph;

/**
 * Edge interface with reference to class implementing the interface and the
 * class implementing the Vertices that it connects.
 *
 * @author calderhoover
 *
 * @param <V>
 *          : class implementing connecting Vertices
 * @param <E>
 *          : class implementing Edge
 */
public interface Edge<V extends Vertex<V, E>, E extends Edge<V, E>> {

  /**
   * Returns source Vertex of Edge.
   *
   * @return source Vertex of Edge
   */
  V getSource();

  /**
   * Returns destination Vertex of Edge.
   *
   * @return destination Vertex of Edge
   */
  V getDestination();

  /**
   * Returns vertex opposing v in current Edge.
   *
   * @param v
   *          : Vertex to get opposite of
   * @return opposing Vertex of v in current Edge if v is a vertex connected by
   *         the current edge, and null otherwise
   */
  default V getOpposite(V v) {
    if (v.equals(getSource())) {
      return getDestination();
    } else if (v.equals(getDestination())) {
      return getSource();
    }
    return null;
  }

  /**
   * Returns weight of Edge.
   *
   * @return weight of Edge
   */
  double getWeight();
}
