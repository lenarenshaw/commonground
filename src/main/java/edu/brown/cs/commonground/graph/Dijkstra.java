package edu.brown.cs.commonground.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Class with Dijkstra's algorithm functionality, capable of finding the
 * shortest distance between two vertices.
 *
 * @author calderhoover
 *
 * @param <V>
 *          : class implementing Vertex
 * @param <E>
 *          : class implementing Edge
 */
public class Dijkstra<V extends Vertex<V, E>, E extends Edge<V, E>> {
  private V src;
  private V dest;

  /**
   * Constructor for Dijkstra.
   *
   * @param src
   *          : source Vertex
   * @param dest
   *          : destination Vertex to find shortest path to
   */
  public Dijkstra(V src, V dest) {
    this.src = src;
    this.dest = dest;
  }

  /**
   * Returns a Path representing the shortest path between the passed in source
   * and destination vertices.
   *
   * @return Path from source to destination
   */
  public Path<V, E> getShortestPath() {
    // Maps vertex to distance from source vertex
    Map<V, Double> distance = new HashMap<>();
    // Maps vertex to previous edge in shortest path from source vertex
    Map<V, E> previous = new HashMap<>();
    // Records visited vertices to avoid cycles
    Set<V> visited = new HashSet<>();
    distance.put(src, 0.0);
    boolean pathPossible = false;
    PriorityQueue<V> pq = new PriorityQueue<>(
        new VertexDistanceComparator(distance));
    pq.add(src);
    while (!pq.isEmpty()) {
      V curr = pq.poll();
      visited.add(curr);
      // PriorityQueue guarantees that current node stores the shortest
      // path up to that node. Therefore, if current node is destination,
      // we can stop.
      if (curr.equals(dest)) {
        pathPossible = true;
        break;
      }
      for (E edge : curr.getEdges()) {
        V adjacent = edge.getOpposite(curr);
        if (!visited.contains(adjacent)) {
          double dist = distance.get(curr) + edge.getWeight();
          if (dist < distance.getOrDefault(adjacent, Double.MAX_VALUE)) {
            distance.put(adjacent, dist);
            previous.put(adjacent, edge);
            pq.add(adjacent);
          }
        }
      }
    }
    // If path is possible, builds Path by backtracking previous edge pointers
    // starting from the destination.
    if (pathPossible) {
      List<E> edges = new ArrayList<>();
      V curr = dest;
      while (!curr.equals(src)) {
        E edge = previous.get(curr);
        edges.add(edge);
        curr = edge.getOpposite(curr);
      }
      Collections.reverse(edges);
      Path<V, E> path = new Path<>(src, edges);
      return path;
    } else {
      return null;
    }
  }

  /**
   * Naive implementation of shortest path algorithm. Used for testing.
   *
   * @return Path representing the shortest path from source to destination
   */
  public Path<V, E> getShortestPathNaive() {
    Path<V, E> path = new Path<>(src);
    Set<V> visited = new HashSet<V>();
    visited.add(src);
    return getPath(src, path, visited);
  }

  /**
   * Helper method for getting naive shortest path.
   *
   * @param curr
   *          : current vertex
   * @param path
   *          : current path
   * @param visited
   *          : Set of visited vertices
   * @return shortest path (naively)
   */
  private Path<V, E> getPath(V curr, Path<V, E> path, Set<V> visited) {
    if (curr.equals(dest)) {
      return path;
    }
    Path<V, E> min = null;
    for (E edge : curr.getEdges()) {
      V adjacent = edge.getOpposite(curr);
      if (!visited.contains(adjacent)) {
        Path<V, E> newPath = new Path<>(src, path.getEdges());
        newPath.add(edge);
        Set<V> newVisited = new HashSet<>(visited);
        newVisited.add(adjacent);
        Path<V, E> possibleMin = getPath(adjacent, newPath, newVisited);
        if (possibleMin != null
            && (min == null || possibleMin.getDistance() < min.getDistance())) {
          min = possibleMin;
        }
      }
    }
    return min;
  }

  /**
   * Comparator for comparing distance from vertices to a source vertex.
   *
   * @author calderhoover
   *
   */
  private class VertexDistanceComparator implements Comparator<V> {
    private Map<V, Double> distance;

    /**
     * Constructor for VertexDistanceComparator.
     *
     * @param distance
     *          : Map of vertex to distance from source vertex.
     */
    VertexDistanceComparator(Map<V, Double> distance) {
      this.distance = distance;
    }

    /**
     * Compares v1 and v2 by distance from the source.
     *
     * @return positive integer if v1 is farther from the source than v2,
     *         negative integer if v2 is farther from the source than v1, and 0
     *         if v1 and v2 are equally far from the source
     */
    public int compare(V v1, V v2) {
      return Double.compare(distance.get(v1), distance.get(v2));
    }
  }
}
