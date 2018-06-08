package edu.brown.cs.commonground.graph;

/**
 * Class implementing basic functionality of an Edge.
 *
 */
public class SimpleEdge implements Edge<SimpleVertex, SimpleEdge> {
  private SimpleVertex src;
  private SimpleVertex dest;
  private double weight;

  /**
   * Constructor for SimpleEdge.
   *
   * @param src
   *          : source of edge
   * @param dest
   *          : destination of edge
   * @param weight
   *          : weight of edge
   */
  public SimpleEdge(SimpleVertex src, SimpleVertex dest, double weight) {
    this.src = src;
    this.dest = dest;
    this.weight = weight;
  }

  @Override
  public SimpleVertex getSource() {
    return new SimpleVertex(src.getId(), src.getEdges());
  }

  @Override
  public SimpleVertex getDestination() {
    return new SimpleVertex(dest.getId(), dest.getEdges());
  }

  @Override
  public double getWeight() {
    return weight;
  }
}
