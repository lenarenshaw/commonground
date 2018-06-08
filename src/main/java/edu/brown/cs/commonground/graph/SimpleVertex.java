package edu.brown.cs.commonground.graph;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Class implementing basic functionality of a Vertex.
 *
 */
public class SimpleVertex implements Vertex<SimpleVertex, SimpleEdge> {
  private Set<SimpleEdge> edges = new HashSet<>();
  private String id;

  /**
   * Empty constructor for SimpleVertex.
   */
  public SimpleVertex() {
  }

  /**
   * Constructor for SimpleVertex.
   *
   * @param id
   *          the id for the SimpleVertex
   */
  public SimpleVertex(String id) {
    this.id = id;
  }

  /**
   * Constructor that accepts a Set of SimpleEdges.
   *
   * @param edges
   *          : Set of SimpleEdges
   * @param id
   *          the id for the SimpleVertex
   */
  public SimpleVertex(String id, Set<SimpleEdge> edges) {
    this.id = id;
    this.edges = edges;
  }

  /**
   * Adds an edge to the vertex.
   *
   * @param e
   *          : SimpleEdge to be added
   */
  public void addEdge(SimpleEdge e) {
    edges.add(e);
  }

  /**
   * gets the Id of the simpleVertex.
   *
   * @return the Id
   */
  public String getId() {
    return id;
  }

  @Override
  public Set<SimpleEdge> getEdges() {
    return Collections.unmodifiableSet(edges);
  }

  @Override
  public boolean equals(Object object) {
    if (object == null
        || !SimpleVertex.class.isAssignableFrom(object.getClass())) {
      return false;
    }
    SimpleVertex v = (SimpleVertex) object;
    return edges.size() == v.getEdges().size()
        && edges.containsAll(v.getEdges());
  }

  @Override
  public int hashCode() {
    return Objects.hash(edges);
  }

  @Override
  public String toString() {
    return "SimpleVertex [id=" + id + "]";
  }

}
