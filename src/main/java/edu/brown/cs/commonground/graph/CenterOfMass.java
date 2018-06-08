package edu.brown.cs.commonground.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Class providing functionality for finding elements related to a graph's
 * center of mass.
 *
 * @param <V>
 *          : class implementing Vertex
 * @param <E>
 *          : class implementing Edge
 */
public class CenterOfMass<V extends Vertex<V, E>, E extends Edge<V, E>> {

  /**
   * Gets a list of unique vertices of size that are similar to the vertices
   * passed in.
   *
   * @param vertices
   *          : list of unique vertices to find similar vertices to. Must be of
   *          size 2 or greater
   * @param size
   *          : number of similar vertices to find. Must be greater than 0
   * @return list of unique vertices of size that are similar to passed in
   *         vertices, null if impossible to compose such a list
   * @throws IllegalArgumentException
   *           : if vertices.size() < 2 or size <= 0
   */
  public List<V> getSimilar(List<V> vertices, int size)
      throws IllegalArgumentException {
    if (vertices.size() < 2) {
      throw new IllegalArgumentException(
          "ERROR: Vertices must have at least 2 elements.");
    }
    if (size <= 0) {
      throw new IllegalArgumentException(
          "ERROR: Size argument must be greater than 0.");
    }
    List<V> intersections = getLenaSim(vertices, size, 0);
    if (intersections.size() < size) {
      intersections.addAll(getAnySim(vertices));
    }
    if (intersections.size() > size) {
      return intersections.subList(0, size);
    }
    return intersections;
  }

  private List<V> getAnySim(List<V> vertices) {
    List<V> supplemental = new ArrayList<>();
    for (V v : vertices) {
      Set<E> edges = v.getEdges();
      for (E edge : edges) {
        supplemental.add(edge.getOpposite(v));
      }
    }
    return supplemental;
  }

  /**
   * Private helper method for getSimilar.
   *
   * @param vertices
   *          : list of unique vertices to find similar vertices to
   * @param size
   *          : number of vertices to find
   * @param depth
   *          : current recursion depth
   * @return list of unique vertices of size that are similar to passed in
   *         vertices, null if vertices.size() < 2 or list of size cannot be
   *         created
   */
  private List<V> getLenaSim(List<V> vertices, int size, int depth) {
    if (depth > ThreadLocalRandom.current().nextInt(3, 6 + 1)) {
      Collections.shuffle(vertices);
      return vertices;
    }
    List<Path<V, E>> paths = new ArrayList<>();
    for (int i = 0; i < vertices.size(); i++) {
      for (int j = 0; j < vertices.size(); j++) {
        if (i < j) {
          V src = vertices.get(i);
          V dest = vertices.get(j);
          Dijkstra<V, E> dj = new Dijkstra<>(src, dest);
          Path<V, E> path = dj.getShortestPath();
          if (path != null) {
            paths.add(path);
          }
        }
      }
    }
    Set<V> centerVerts = new HashSet<>();
    for (Path<V, E> path : paths) {
      centerVerts.addAll(path.getCenters());
    }
    if (centerVerts.size() >= size) {
      return getLenaSim(new ArrayList<>(centerVerts), size, depth + 1);
    } else {
      for (Path<V, E> path : paths) {
        centerVerts.addAll(path.getIntermediates());
      }
      return getLenaSim(new ArrayList<>(centerVerts), size, depth + 1);
    }
  }

}
