package de.unmr.bacluster.models.datareduction;

import static de.unmr.bacluster.util.SetOperations.getDifference;
import static de.unmr.bacluster.util.SetOperations.getIntersection;
import static de.unmr.bacluster.util.SetOperations.isSubset;

import java.util.Set;

import de.unmr.bacluster.models.Edge;
import de.unmr.bacluster.models.Graph;
import de.unmr.bacluster.models.Subgraph;
import de.unmr.bacluster.models.modifications.Modification;
import de.unmr.bacluster.util.Debug;

/**
 * Tests if the subgraph has an optimal solution that can be used for the
 * complete graph. This is the case when every two nodes of the subgraph either
 * are in the same cluster and have the same closed neighborhood outside of the
 * subgraph or have no common neighbors outside of the subgraph otherwise.
 */
public class PartialSolutionRule extends DataReduction {
    private final Subgraph subgraph;

    public PartialSolutionRule(final Graph graph, final Subgraph subgraph) {
        super(graph);
        this.subgraph = subgraph;
    }

    @Override
    public boolean reduce() {
        final Set<Integer> subgraphNodes = subgraph.getAllNodes();

        for (final int nodeA : subgraphNodes) {
            for (final int nodeB : subgraphNodes) {
                if (nodeA >= nodeB) {
                    continue;
                }

                final Set<Integer> neighborsOfA = graph.getNeighbors(nodeA);
                final Set<Integer> neighborsOfB = graph.getNeighbors(nodeB);

                final Edge edge = new Edge(nodeA, nodeB);

                if (getDifference(neighborsOfA, subgraphNodes).equals(getDifference(neighborsOfB, subgraphNodes))) {
                    switch (graph.getState(edge)) {
                    case FORBIDDEN:
                        Debug.log(edge + " forbidden", 1);
                        modifications.clear();
                        return false;
                    case DISCONNECTED:
                        Debug.log(edge + " will be created", 1);
                        modifications.add(new Modification(false, edge));
                        continue;
                    default:
                        continue;
                    }
                }

                if (isSubset(getIntersection(neighborsOfA, neighborsOfB), subgraphNodes)) {
                    switch (graph.getState(edge)) {
                    case PERMANENT:
                        Debug.log(edge + " permanent", 1);
                        modifications.clear();
                        return false;
                    case CONNECTED:
                        Debug.log(edge + " will be removed", 1);
                        modifications.add(new Modification(true, edge));
                        continue;
                    default:
                        continue;
                    }
                }

                Debug.log("Nodes of " + edge
                        + " have neighbors outside subgraph but closed neighborhood is not identical");
                return false;
            }
        }

        if (modifications.size() > subgraph.getSolutionSize()) {
            modifications.clear();
            return false;
        }

        graph.addPartialSolution(modifications);

        return true;
    }
}
