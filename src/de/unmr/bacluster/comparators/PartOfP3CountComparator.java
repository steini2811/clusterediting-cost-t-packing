package de.unmr.bacluster.comparators;

import java.util.Map;

import de.unmr.bacluster.models.Graph;

public class PartOfP3CountComparator extends NodeComparator {
    private final Map<Integer, Integer> countMap;

    public PartOfP3CountComparator(final Graph graph, final boolean ascending) {
        super(graph, ascending);
        countMap = graph.getNodeinP3CountMap();
    }

    @Override
    public int compare(final Integer nodeA, final Integer nodeB) {
        if (countMap.get(nodeA) > countMap.get(nodeB)) {
            return ascending ? -1 : 1;
        }
        if (countMap.get(nodeA) < countMap.get(nodeB)) {
            return ascending ? 1 : -1;
        }

        return 0;
    }

}
