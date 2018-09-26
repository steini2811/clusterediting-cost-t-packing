package de.unmr.bacluster.models;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.unmr.bacluster.io.Mapper;

public class P3 {
    private final int u;
    private final int v;
    private final int w;
    private final Edge uv;
    private final Edge vw;
    private final Edge uw;

    P3(final int u, final int v, final int w) {
        super();

        this.uw = new Edge(u, w);
        if (u < w) {
            this.u = u;
            this.v = v;
            this.w = w;
            this.uv = new Edge(u, v);
            this.vw = new Edge(v, w);
        } else {
            this.u = w;
            this.v = v;
            this.w = u;
            this.uv = new Edge(w, v);
            this.vw = new Edge(v, u);
        }
    }

    boolean containsNode(final int node) {
        return u == node || v == node || w == node;
    }

    public boolean containsOneNode(final int[] nodes) {
        for (final int node : nodes) {
            if (containsNode(node)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final P3 other = (P3) obj;
        if (u != other.u) {
            return false;
        }
        if (v != other.v) {
            return false;
        }
        if (w != other.w) {
            return false;
        }
        return true;
    }

    public Set<Edge> getAllEdges() {
        return new HashSet<Edge>(Arrays.asList(uv, vw, uw));
    }

    public Set<Integer> getAllNodes() {
        return new HashSet<Integer>(Arrays.asList(u, v, w));
    }

    public Set<Edge> getEditableEdges(final Graph graph) {
        final Set<Edge> result = new HashSet<>();
        for (final Edge edge : getAllEdges()) {
            if (graph.isEdgeAllowed(edge)) {
                result.add(edge);
            }
        }

        return result;
    }

    public int getU() {
        return u;
    }

    public Edge getUv() {
        return uv;
    }

    public Edge getUw() {
        return uw;
    }

    public int getV() {
        return v;
    }

    public Edge getVw() {
        return vw;
    }

    public int getW() {
        return w;
    }

    public boolean hasEdge(final Edge edge) {
        return uv.equals(edge) || vw.equals(edge);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + u;
        result = prime * result + v;
        result = prime * result + w;
        return result;
    }

    public boolean missesEdge(final Edge edge) {
        return uw.equals(edge);
    }

    @Override
    public String toString() {
        final Mapper mapper = Mapper.getInstance();
        return "P3 [u=" + mapper.toName(u) + ", v=" + mapper.toName(v) + ", w=" + mapper.toName(w) + "]";
    }

}
