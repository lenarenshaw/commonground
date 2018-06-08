package edu.brown.cs.commonground.graph;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class CenterOfMassTest {

  // @Test
  // public void testBasicGraph() {
  // SimpleVertex a = new SimpleVertex();
  // SimpleVertex b = new SimpleVertex();
  // SimpleVertex c = new SimpleVertex();
  // SimpleVertex d = new SimpleVertex();
  // SimpleVertex e = new SimpleVertex();
  // SimpleEdge ab = new SimpleEdge(a, b, 3);
  // SimpleEdge ac = new SimpleEdge(a, c, 2);
  // SimpleEdge ae = new SimpleEdge(a, e, 4);
  // SimpleEdge bc = new SimpleEdge(b, c, 8);
  // SimpleEdge cd = new SimpleEdge(c, d, 1);
  // SimpleEdge ed = new SimpleEdge(e, d, 3);
  // a.addEdge(ab);
  // b.addEdge(ab);
  // a.addEdge(ac);
  // c.addEdge(ac);
  // a.addEdge(ae);
  // e.addEdge(ae);
  // b.addEdge(bc);
  // c.addEdge(bc);
  // c.addEdge(cd);
  // d.addEdge(cd);
  // e.addEdge(ed);
  // d.addEdge(ed);
  // List<SimpleVertex> list = new ArrayList<>();
  // list.add(a);
  // list.add(b);
  // list.add(c);
  // list.add(d);
  // list.add(e);
  // CenterOfMass<SimpleVertex, SimpleEdge> com = new CenterOfMass<>();
  // List<SimpleVertex> ret = com.getSimilar(list, 5);
  // System.out.println(ret);
  // }

  @Test
  public void testFourByFourGraph() {
    SimpleVertex a = new SimpleVertex("a");
    SimpleVertex b = new SimpleVertex("b");
    SimpleVertex c = new SimpleVertex("c");
    SimpleVertex d = new SimpleVertex("d");
    SimpleVertex e = new SimpleVertex("e");
    SimpleVertex f = new SimpleVertex("f");
    SimpleVertex g = new SimpleVertex("g");
    SimpleVertex h = new SimpleVertex("h");
    SimpleVertex i = new SimpleVertex("i");
    SimpleVertex j = new SimpleVertex("j");
    SimpleVertex k = new SimpleVertex("k");
    SimpleVertex l = new SimpleVertex("l");
    SimpleVertex m = new SimpleVertex("m");
    SimpleVertex n = new SimpleVertex("n");
    SimpleVertex o = new SimpleVertex("o");
    SimpleVertex p = new SimpleVertex("p");

    SimpleEdge ab = new SimpleEdge(a, b, 1);
    a.addEdge(ab);
    b.addEdge(ab);
    SimpleEdge ae = new SimpleEdge(a, e, 1);
    a.addEdge(ae);
    e.addEdge(ae);
    SimpleEdge bc = new SimpleEdge(b, c, 1);
    b.addEdge(bc);
    c.addEdge(bc);
    SimpleEdge bf = new SimpleEdge(b, f, 1);
    b.addEdge(bf);
    f.addEdge(bf);
    SimpleEdge cd = new SimpleEdge(c, d, 1);
    c.addEdge(cd);
    d.addEdge(cd);
    SimpleEdge cg = new SimpleEdge(c, g, 1);
    c.addEdge(cg);
    g.addEdge(cg);
    SimpleEdge dh = new SimpleEdge(d, h, 1);
    d.addEdge(dh);
    h.addEdge(dh);
    SimpleEdge ei = new SimpleEdge(e, i, 1);
    e.addEdge(ei);
    i.addEdge(ei);
    SimpleEdge ef = new SimpleEdge(e, f, 1);
    e.addEdge(ef);
    f.addEdge(ef);
    SimpleEdge fg = new SimpleEdge(f, g, 1);
    f.addEdge(fg);
    g.addEdge(fg);
    SimpleEdge fj = new SimpleEdge(f, j, 1);
    f.addEdge(fj);
    j.addEdge(fj);
    SimpleEdge gh = new SimpleEdge(g, h, 1);
    g.addEdge(gh);
    h.addEdge(gh);
    SimpleEdge gk = new SimpleEdge(g, k, 1);
    g.addEdge(gk);
    k.addEdge(gk);
    SimpleEdge hl = new SimpleEdge(h, l, 1);
    h.addEdge(hl);
    l.addEdge(hl);
    SimpleEdge ij = new SimpleEdge(i, j, 1);
    i.addEdge(ij);
    j.addEdge(ij);
    SimpleEdge im = new SimpleEdge(i, m, 1);
    i.addEdge(im);
    m.addEdge(im);
    SimpleEdge jk = new SimpleEdge(j, k, 1);
    j.addEdge(jk);
    k.addEdge(jk);
    SimpleEdge jn = new SimpleEdge(j, n, 1);
    j.addEdge(jn);
    n.addEdge(jn);
    SimpleEdge kl = new SimpleEdge(k, l, 1);
    k.addEdge(kl);
    l.addEdge(kl);
    SimpleEdge ko = new SimpleEdge(k, o, 1);
    k.addEdge(ko);
    o.addEdge(ko);
    SimpleEdge lp = new SimpleEdge(l, p, 1);
    l.addEdge(lp);
    p.addEdge(lp);
    SimpleEdge mn = new SimpleEdge(m, n, 1);
    m.addEdge(mn);
    n.addEdge(mn);
    SimpleEdge no = new SimpleEdge(n, o, 1);
    n.addEdge(no);
    o.addEdge(no);
    SimpleEdge op = new SimpleEdge(o, p, 1);
    o.addEdge(op);
    p.addEdge(op);

    List<SimpleVertex> list = new ArrayList<>();
    list.add(a);
    list.add(p);
    CenterOfMass<SimpleVertex, SimpleEdge> com = new CenterOfMass<>();
    List<SimpleVertex> ret = com.getSimilar(list, 4);
    for (SimpleVertex s : ret) {
      System.out.println(s.toString());
    }

  }
}
