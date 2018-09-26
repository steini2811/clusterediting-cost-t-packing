package de.unmr.bacluster.models.datareduction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.unmr.bacluster.models.Graph;
import de.unmr.bacluster.models.P3;
import de.unmr.bacluster.models.Subgraph;

public class SearchPartialSolutionGraphs extends DataReduction {

    public SearchPartialSolutionGraphs(final Graph graph) {
        super(graph);
    }

    private boolean isReduceable(final Subgraph subgraph) {
        final PartialSolutionRule rule = new PartialSolutionRule(graph, subgraph);
        final boolean result = rule.reduce();
        if (result) {
            rule.rollback();
        }

        return result;
    }

    @Override
    public boolean reduce() {
        tryP3sForRR();
        return !modifications.isEmpty();
    }

    private boolean tryNodesForSubgraph(final Graph graph, final Subgraph subgraph, final List<Subgraph> subgraphs) {
        final Set<Integer> neighborsOfSubgraph = graph.getSubgraphNeighbors(subgraph);

        final int prevSolutionSize = subgraph.getSolutionSize();

        for (final int node : neighborsOfSubgraph) {
            subgraph.addNode(node, graph.getNeighbors(node));
            if (subgraph.isValid(subgraphs) && isReduceable(subgraph)
                    && subgraph.getSolutionSize() > prevSolutionSize) {
                return true;
            }
            subgraph.removeNode(node);
        }

        return false;
    }

    private void tryP3sForRR() {
        final List<Subgraph> subgraphs = new ArrayList<>();

        final Set<P3> p3set = new HashSet<>(graph.getAllP3s());
        for (final P3 p3 : p3set) {
            final Subgraph subgraph = new Subgraph(p3, graph);

            if (isReduceable(subgraph)) {
                // search neighbors
                boolean running = true;
                do {
                    running = tryNodesForSubgraph(graph, subgraph, subgraphs);
                } while (running);

                subgraphs.add(subgraph);
            }
        }

        for (final Subgraph subgraph : subgraphs) {
            final PartialSolutionRule rule = new PartialSolutionRule(graph, subgraph);
            rule.reduce();
            modifications.addAll(rule.modifications);
        }
    }

}
