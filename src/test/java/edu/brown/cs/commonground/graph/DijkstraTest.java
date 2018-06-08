package edu.brown.cs.commonground.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DijkstraTest {

  // @Test
  public void testBasicGraph() {
    SimpleVertex a = new SimpleVertex();
    SimpleVertex b = new SimpleVertex();
    SimpleVertex c = new SimpleVertex();
    SimpleVertex d = new SimpleVertex();
    SimpleVertex e = new SimpleVertex();
    SimpleEdge ab = new SimpleEdge(a, b, 3);
    SimpleEdge ac = new SimpleEdge(a, c, 2);
    SimpleEdge ae = new SimpleEdge(a, e, 4);
    SimpleEdge bc = new SimpleEdge(b, c, 8);
    SimpleEdge cd = new SimpleEdge(c, d, 1);
    SimpleEdge ed = new SimpleEdge(e, d, 3);
    a.addEdge(ab);
    b.addEdge(ab);
    a.addEdge(ac);
    c.addEdge(ac);
    a.addEdge(ae);
    e.addEdge(ae);
    b.addEdge(bc);
    c.addEdge(bc);
    c.addEdge(cd);
    d.addEdge(cd);
    e.addEdge(ed);
    d.addEdge(ed);
    Path<SimpleVertex, SimpleEdge> path = new Dijkstra<SimpleVertex, SimpleEdge>(
        a, d).getShortestPath();
    assertEquals(path.size(), 2);
    assertEquals(path.getEdge(0), ac);
    assertEquals(path.getEdge(1), cd);
    path = new Dijkstra<SimpleVertex, SimpleEdge>(a, d).getShortestPathNaive();
    assertEquals(path.size(), 2);
    assertEquals(path.getEdge(0), ac);
    assertEquals(path.getEdge(1), cd);
    path = new Dijkstra<SimpleVertex, SimpleEdge>(b, e).getShortestPath();
    assertEquals(path.size(), 2);
    assertEquals(path.getEdge(0), ab);
    assertEquals(path.getEdge(1), ae);
    path = new Dijkstra<SimpleVertex, SimpleEdge>(b, e).getShortestPathNaive();
    assertEquals(path.size(), 2);
    assertEquals(path.getEdge(0), ab);
    assertEquals(path.getEdge(1), ae);
  }

  // @Test
  public void testNoPossiblePath() {
    SimpleVertex a = new SimpleVertex();
    SimpleVertex b = new SimpleVertex();
    SimpleVertex c = new SimpleVertex();
    SimpleEdge ab = new SimpleEdge(a, b, 3);
    a.addEdge(ab);
    b.addEdge(ab);
    Path<SimpleVertex, SimpleEdge> path = new Dijkstra<SimpleVertex, SimpleEdge>(
        a, c).getShortestPath();
    assertNull(path);
    path = new Dijkstra<SimpleVertex, SimpleEdge>(a, c).getShortestPathNaive();
    assertNull(path);
  }

  // @Test
  public void testShortestPathSelf() {
    SimpleVertex a = new SimpleVertex();
    SimpleVertex b = new SimpleVertex();
    SimpleVertex c = new SimpleVertex();
    SimpleEdge ab = new SimpleEdge(a, b, 3);
    SimpleEdge bc = new SimpleEdge(b, c, 3);
    SimpleEdge ca = new SimpleEdge(c, a, 3);
    SimpleEdge aa = new SimpleEdge(a, a, 3);
    a.addEdge(ab);
    b.addEdge(ab);
    b.addEdge(bc);
    c.addEdge(bc);
    c.addEdge(ca);
    a.addEdge(ca);
    a.addEdge(aa);
    Path<SimpleVertex, SimpleEdge> path = new Dijkstra<SimpleVertex, SimpleEdge>(
        a, a).getShortestPath();
    assertEquals(path.size(), 0);
    assertFalse(path.getEdges().contains(aa));
    path = new Dijkstra<SimpleVertex, SimpleEdge>(a, a).getShortestPathNaive();
    assertEquals(path.size(), 0);
    assertFalse(path.getEdges().contains(aa));
  }

  // @Test
  public void testMultipleShortestPathsTieBreak() {
    SimpleVertex a = new SimpleVertex();
    SimpleVertex b = new SimpleVertex();
    SimpleVertex c = new SimpleVertex();
    SimpleVertex d = new SimpleVertex();
    SimpleVertex e = new SimpleVertex();
    SimpleEdge ab = new SimpleEdge(a, b, 3);
    SimpleEdge ac = new SimpleEdge(a, c, 2);
    SimpleEdge ae = new SimpleEdge(a, e, 4);
    SimpleEdge bc = new SimpleEdge(b, c, 3);
    SimpleEdge cd = new SimpleEdge(c, d, 1);
    SimpleEdge ed = new SimpleEdge(e, d, 3);
    a.addEdge(ab);
    b.addEdge(ab);
    a.addEdge(ac);
    c.addEdge(ac);
    a.addEdge(ae);
    e.addEdge(ae);
    b.addEdge(bc);
    c.addEdge(bc);
    c.addEdge(cd);
    d.addEdge(cd);
    e.addEdge(ed);
    d.addEdge(ed);
    Path<SimpleVertex, SimpleEdge> path = new Dijkstra<SimpleVertex, SimpleEdge>(
        e, b).getShortestPath();
    // Paths (e -> a -> b) and (e -> d -> c -> b) are equally short, but one
    // is arbitrarily selected.
    if (path.size() == 3) {
      assertEquals(path.getEdge(0), ed);
      assertEquals(path.getEdge(1), cd);
      assertEquals(path.getEdge(2), bc);
    } else if (path.size() == 2) {
      assertEquals(path.getEdge(0), ae);
      assertEquals(path.getEdge(1), ab);
    }
  }

  // !!! IMPORTANT NOTE !!! Test takes up to ~50 seconds to run.
  // @Test
  public void testLargeRandomGraphs() {
    Random r = new Random();
    for (int numVertices = 10; numVertices <= 30; numVertices += 5) {
      List<SimpleVertex> vertices = new ArrayList<>();
      for (int v = 0; v <= numVertices; v++) {
        vertices.add(new SimpleVertex());
      }
      int numEdges = r.nextInt(numVertices * 2);
      for (int e = 1; e <= numEdges; e++) {
        SimpleVertex v1 = vertices.get(r.nextInt(numVertices));
        SimpleVertex v2 = vertices.get(r.nextInt(numVertices));
        SimpleEdge edge = new SimpleEdge(v1, v2, r.nextDouble() * numVertices);
        v1.addEdge(edge);
        v2.addEdge(edge);
      }
      for (int i = 0; i < numVertices / 4; i++) {
        Dijkstra<SimpleVertex, SimpleEdge> dij = new Dijkstra<>(
            vertices.get(r.nextInt(numVertices)),
            vertices.get(r.nextInt(numVertices)));
        Path<SimpleVertex, SimpleEdge> p = dij.getShortestPath();
        Path<SimpleVertex, SimpleEdge> pN = dij.getShortestPathNaive();
        if (p == null) {
          assertNull(pN);
        } else {
          assert (p.getDistance() == pN.getDistance());
        }
      }
    }
  }
}
