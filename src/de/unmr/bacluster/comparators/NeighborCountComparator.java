package de.unmr.bacluster.comparators;

import java.util.Set;

import de.unmr.bacluster.models.Graph;
import de.unmr.bacluster.models.Subgraph;

public class NeighborCountComparator extends NodeComparator {
    private final Subgraph subgraph;

    public NeighborCountComparator(final Graph graph, final Subgraph subgraph, final boolean ascending) {
        super(graph, ascending);
        this.subgraph = subgraph;
    }

    @Override
    public int compare(final Integer nodeA, final Integer nodeB) {
        final Set<Integer> neighborsA = graph.getNeighbors(nodeA);
        neighborsA.retainAll(subgraph.getAllNodes());

        final Set<Integer> neighborsB = graph.getNeighbors(nodeB);
        neighborsB.retainAll(subgraph.getAllNodes());

        if (neighborsA.size() > neighborsB.size()) {
            return ascending ? -1 : 1;
        }
        if (neighborsA.size() < neighborsB.size()) {
            return ascending ? 1 : -1;
        }

        return 0;
    }

}
