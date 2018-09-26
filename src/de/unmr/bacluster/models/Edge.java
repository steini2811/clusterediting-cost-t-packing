package de.unmr.bacluster.models;

import de.unmr.bacluster.io.Mapper;

public class Edge {
    private final int nodeA;
    private final int nodeB;

    public Edge(final int a, final int b) {
        super();

        if (a < b) {
            nodeA = a;
            nodeB = b;
        } else {
            nodeA = b;
            nodeB = a;
        }
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
        final Edge other = (Edge) obj;
        if (nodeA != other.nodeA) {
            return false;
        }
        if (nodeB != other.nodeB) {
            return false;
        }
        return true;
    }

    public int getNodeA() {
        return nodeA;
    }

    public int getNodeB() {
        return nodeB;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + nodeA;
        result = prime * result + nodeB;
        return result;
    }

    @Override
    public String toString() {
        final Mapper mapper = Mapper.getInstance();
        return "Edge [nodeA=" + mapper.toName(nodeA) + ", nodeB=" + mapper.toName(nodeB) + "]";
    }

}
