package edu.brown.cs.commonground.graph;

import java.util.Set;

/**
 * Vertex interface with reference to class implementing the interface and the
 * class implementing the Edge connecting it with other vertices.
 *
 * @author calderhoover
 *
 * @param <V>
 *          : class implementing Vertex
 * @param <E>
 *          : class implementing connecting Edges
 */
public interface Vertex<V extends Vertex<V, E>, E extends Edge<V, E>> {

  /**
   * Returns the edges of a Vertex.
   *
   * @return a Collection of Edges
   */
  Set<E> getEdges();
}
