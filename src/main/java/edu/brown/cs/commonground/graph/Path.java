package edu.brown.cs.commonground.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Class representing a path from one Vertex to another connected by Edges.
 *
 * @param <V>
 *          : class implementing Vertex
 * @param <E>
 *          : class implementing Edge
 */
public class Path<V extends Vertex<V, E>, E extends Edge<V, E>> {
  private V src;
  private V curr;
  private LinkedList<E> path = new LinkedList<>();
  private double distance;

  /**
   * Constructor for Path.
   *
   * @param src
   *          : starting Vertex of the Path
   */
  public Path(V src) {
    this.src = src;
    this.curr = this.src;
  }

  /**
   * Constructor for Path accepting both a starting Vertex and a List of edges
   * from that source in order.
   *
   * @param src
   *          : starting Vertex of the Path
   * @param edges
   *          : List of Edges from src onwards
   */
  public Path(V src, List<E> edges) {
    this.src = src;
    this.curr = this.src;
    for (E edge : edges) {
      add(edge);
    }
  }

  /**
   * Adds an Edge to the path. Checks that this edge is connected to the last
   * Vertex in the Path.
   *
   * @param edge
   *          : Edge to be added, must be connected to the last Vertex
   * @return true if Edge was added, false if edge was not connected to the last
   *         Vertex (and thus was not added)
   */
  public boolean add(E edge) {
    if (edge.getOpposite(curr) == null) {
      return false;
    }
    path.add(edge);
    curr = edge.getOpposite(curr);
    distance += edge.getWeight();
    return true;
  }

  /**
   * Removes last Edge in this Path.
   */
  public void removeLast() {
    E removedEdge = path.removeLast();
    curr = removedEdge.getOpposite(curr);
    distance -= removedEdge.getWeight();
  }

  /**
   * Gets the distance of the last Vertex in this Path to the source Vertex.
   *
   * @return distance from last Vertex to source Vertex
   */
  public double getDistance() {
    return distance;
  }

  /**
   * Gets the number of Edges in this Path.
   *
   * @return number of Edges in this Path
   */
  public int size() {
    return path.size();
  }

  /**
   * Gets the source Vertex of this Path.
   *
   * @return source Vertex of this Path
   */
  public V getSource() {
    return src;
  }

  /**
   * Returns the destination of this Path (the last Vertex in this Path).
   *
   * @return destination of this Path
   */
  public V getDestination() {
    return curr;
  }

  /**
   * Gets specified Edge of this Path. Used primarily for testing.
   *
   * @param i
   *          : index of Edge in Path
   * @return Edge at index i
   */
  public E getEdge(int i) {
    return path.get(i);
  }

  /**
   * Gets an unmodifiable List of the Edges in this Path.
   *
   * @return unmodifiable List of the Edges in this Path
   */
  public List<E> getEdges() {
    return Collections.unmodifiableList(path);
  }

  /**
   * Gets the V in the center of the path.
   *
   * @return the V in the center of the path.
   */
  public V getCenter() {
    E middleEdge = path.get(path.size() / 2);
    return middleEdge.getDestination();
  }

  /**
   * Gets the V in the center of the path.
   *
   * @return the V in the center of the path.
   */
  public List<V> getCenters() {
    E middleEdge = path.get(path.size() / 2);
    List<V> result = new ArrayList<>();
    result.add(middleEdge.getSource());
    result.add(middleEdge.getDestination());
    return result;
  }

  // /**
  // * Gets the V in the center of the path excluding first and last V in the
  // * path.
  // *
  // * @return the V in the center of the path excluding first and last V, null
  // if
  // * the first and last V are the only Vs in the path
  // */
  // public V getIntermediateCenter() {
  // if (path.size() <= 1) {
  // return null;
  // }
  // E middleEdge = path.get(path.size() / 2);
  // return middleEdge.getDestination();
  // }

  /**
   * Gets a list of all V between (not including) the first and last V in the
   * path.
   *
   * @return list of V between first and last V in this path
   */
  public List<V> getIntermediates() {
    if (path.size() <= 1) {
      return new ArrayList<>();
    }
    List<V> res = new ArrayList<>();
    V currV = path.get(0).getOpposite(src);
    res.add(currV);
    for (int i = 1; i < path.size() - 1; i++) {
      currV = path.get(i).getOpposite(currV);
      res.add(currV);
    }
    return res;
  }
}
