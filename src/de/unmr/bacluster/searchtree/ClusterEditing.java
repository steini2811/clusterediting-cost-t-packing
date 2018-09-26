package de.unmr.bacluster.searchtree;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.unmr.bacluster.models.Edge;
import de.unmr.bacluster.models.Graph;
import de.unmr.bacluster.models.Result;
import de.unmr.bacluster.models.Subgraph;
import de.unmr.bacluster.models.datareduction.DataReduction;
import de.unmr.bacluster.models.datareduction.SearchPartialSolutionGraphs;
import de.unmr.bacluster.util.Config;
import de.unmr.bacluster.util.Debug;

public abstract class ClusterEditing {

    protected int recursiveSteps = 0;

    public Result ce(final List<Edge> edges, final boolean calc) {
        final Graph graph = new Graph(edges);

        if (!graph.hasP3()) {
            return new Result(Optional.of(new ArrayList<>()), 0);
        }

        if (Config.useStartPartialSolutionRule()) {
            final DataReduction dr = new SearchPartialSolutionGraphs(graph);
            dr.reduce();
        }

        int k = graph.getLowerBoundByConfig();
        final int partialSolution = graph.getModifiedEdges().size();
        System.out.println("#lower bound: " + (k + partialSolution) + " (" + partialSolution + " exact edges)");
        Result result = new Result(Optional.empty(), 0);

        while (calc) {
            Debug.log("k = " + k);
            result = ceBranch(graph, k);
            if (result.getList().isPresent()) {
                Debug.log("#solution found at k = " + k);
                result.getList().get().addAll(graph.getModifiedEdges());
                return result;
            }
            k++;
        }

        return result;
    }

    public Result ce(final Subgraph graph, final boolean calc) {
        if (!graph.hasP3()) {
            return new Result(Optional.of(new ArrayList<>()), 0);
        }

        int k = graph.getEdgeDisjointLowerBound();
        Result result = new Result(Optional.empty(), 0);

        while (calc) {
            result = ceBranch(graph, k);
            if (result.getList().isPresent()) {
                result.getList().get().addAll(graph.getModifiedEdges());
                return result;
            }
            k++;
        }

        return result;
    }

    protected abstract Result ceBranch(final Graph graph, final int k);
}
