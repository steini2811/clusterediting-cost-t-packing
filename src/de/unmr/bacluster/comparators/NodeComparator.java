package de.unmr.bacluster.comparators;

import java.util.Comparator;

import de.unmr.bacluster.models.Graph;

public abstract class NodeComparator implements Comparator<Integer> {
    final Graph graph;
    final boolean ascending;

    public NodeComparator(final Graph graph, final boolean ascending) {
        this.graph = graph;
        this.ascending = ascending;
    }

    @Override
    public abstract int compare(Integer nodeA, Integer nodeB);
}
