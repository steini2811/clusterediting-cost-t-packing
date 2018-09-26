package de.unmr.bacluster.models;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

import de.unmr.bacluster.comparators.NeighborCountComparator;
import de.unmr.bacluster.comparators.NodeDegreeComparator;
import de.unmr.bacluster.comparators.PartOfP3CountComparator;
import de.unmr.bacluster.util.Config;

public class NodeOrdering {
    private final Graph graph;
    private final Subgraph currentSubgraph;

    public NodeOrdering(final Graph graph, final Subgraph subgraph) {
        this.graph = graph;
        this.currentSubgraph = subgraph;
    }

    public Iterator<Integer> getOrderedIterator(final Set<Integer> nodes) {
        final boolean ascending = Config.isAscending();
        Comparator<Integer> comp;

        switch (Config.getOrderType()) {
        case NODEDEGREE:
            comp = new NodeDegreeComparator(graph, ascending);
            break;
        case PARTOFP3S:
            comp = new PartOfP3CountComparator(graph, ascending);
            break;
        case SUBGRAPHNBCOUNT:
            comp = new NeighborCountComparator(graph, currentSubgraph, ascending);
            break;
        default:
            return nodes.iterator();
        }

        return nodes.stream().sorted(comp).iterator();
    }
}
