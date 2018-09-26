package de.unmr.bacluster.comparators;

import de.unmr.bacluster.models.Graph;

public class NodeDegreeComparator extends NodeComparator {

    public NodeDegreeComparator(final Graph graph, final boolean ascending) {
        super(graph, ascending);
    }

    @Override
    public int compare(final Integer nodeA, final Integer nodeB) {
        if (graph.getDegree(nodeA) > graph.getDegree(nodeB)) {
            return ascending ? -1 : 1;
        }
        if (graph.getDegree(nodeA) < graph.getDegree(nodeB)) {
            return ascending ? 1 : -1;
        }

        return 0;
    }

}
