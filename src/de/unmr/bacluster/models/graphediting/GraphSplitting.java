package de.unmr.bacluster.models.graphediting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.unmr.bacluster.io.Mapper;
import de.unmr.bacluster.models.Graph;
import de.unmr.bacluster.models.NodeOrdering;
import de.unmr.bacluster.models.P3;
import de.unmr.bacluster.models.Subgraph;
import de.unmr.bacluster.models.datareduction.PartialSolutionRule;
import de.unmr.bacluster.util.Config;
import de.unmr.bacluster.util.Debug;
import de.unmr.bacluster.util.SetOperations;

public class GraphSplitting {
    private final Graph originGraph;
    private final List<Subgraph> subgraphs = new ArrayList<>();
    private final int t;
    private final boolean usePartialSolutionRule;

    public GraphSplitting(final Graph graph, final int t) {
        super();
        this.t = t;
        this.usePartialSolutionRule = Config.usePackingPartialSolutionRule();
        this.originGraph = graph;

        splitByLowerBound();
    }

    public int getLowerBoundSum() {
        int sum = 0;
        int halfSteps = 0;

        for (final Subgraph subgraph : subgraphs) {
            final int subgraphLB = subgraph.getSolutionSize();
            sum += subgraphLB;

            if (usePartialSolutionRule) {
                if (subgraph.isNodeDisjoint(subgraphs)) {
                    Debug.log("Found node-disjoint graph");
                    halfSteps++;
                    // if (!isSubgraphConnectedToOthers(subgraph)) {
                    // Debug.log("Found node-disjoint graph w/o connection to
                    // other subgraphs");
                    // halfSteps++;
                    // }
                }
            }
        }

        return sum + ((int) Math.ceil(halfSteps * 0.5));
    }

    private boolean isSubgraphConnectedToOthers(final Subgraph subgraph) {
        for (final Subgraph otherSubgraph : subgraphs) {
            if (subgraph.equals(otherSubgraph)) {
                continue;
            }
            if (!SetOperations.getIntersection(originGraph.getSubgraphNeighbors(otherSubgraph), subgraph.getAllNodes())
                    .isEmpty()) {
                return true;
            }
        }

        return false;
    }

    private void searchForNeighbors(final List<Subgraph> subgraphs, final Subgraph subgraph) {
        final Set<Integer> unuseableNeighbors = new HashSet<>();
        int currentSize = subgraph.getSolutionSize();
        while (true) {
            final Set<Integer> neighborsOfSubgraph = originGraph.getSubgraphNeighbors(subgraph);
            neighborsOfSubgraph.removeAll(unuseableNeighbors);

            final NodeOrdering nodeOrdering = new NodeOrdering(originGraph, subgraph);
            final Iterator<Integer> neighborIterator = nodeOrdering.getOrderedIterator(neighborsOfSubgraph);

            boolean nodeFound = false;
            while (neighborIterator.hasNext()) {
                final int node = neighborIterator.next();

                Debug.log("Try Node " + Mapper.getInstance().toName(node));
                subgraph.addNode(node, originGraph.getNeighbors(node));
                final int newSize = subgraph.getSolutionSize();
                Debug.log("new Size: " + newSize);

                if (!subgraph.isValid(subgraphs)) {
                    subgraph.removeNode(node);
                    unuseableNeighbors.add(node);
                    Debug.log("Node " + Mapper.getInstance().toName(node) + "not allowed");
                } else if (newSize > t) {
                    subgraph.removeNode(node);
                    unuseableNeighbors.add(node);
                    Debug.log("Node " + Mapper.getInstance().toName(node) + "increases solution size too much");
                } else if (newSize <= currentSize) {
                    subgraph.removeNode(node);
                    if (Config.useOldNB()) {
                        unuseableNeighbors.add(node);
                    }
                    Debug.log("Node " + Mapper.getInstance().toName(node) + "does not increase solution size");
                } else {
                    Debug.log("Node " + Mapper.getInstance().toName(node) + "added to Subgraph");
                    if (newSize == t) {
                        return;
                    }
                    currentSize = newSize;
                    nodeFound = true;
                    break;
                }
            }

            if (!nodeFound) {
                // there are no nodes that could extend current subgraph
                return;
            }
        }
    }

    private void splitByLowerBound() {
        final Set<P3> p3s = new HashSet<>(originGraph.getAllP3s());
        final Iterator<P3> p3Iterator = p3s.iterator();

        while (p3Iterator.hasNext()) {
            final P3 p3 = p3Iterator.next();
            if (!originGraph.getAllP3s().contains(p3)) {
                Debug.log("Skip former " + p3);
                continue;
            }
            Debug.log("Try " + p3);

            final Subgraph subgraph = new Subgraph(p3.getU(), p3.getV());
            subgraph.addNode(p3.getW(), originGraph.getNeighbors(p3.getW()));
            if (!subgraph.isValid(subgraphs)) {
                continue;
            }

            Debug.log(p3 + " is taken");
            subgraphs.add(subgraph);

            searchForNeighbors(subgraphs, subgraph);

            if (usePartialSolutionRule) {
                // Subgraph finished - test for solution (RR 6.2)
                final PartialSolutionRule reductionRule = new PartialSolutionRule(originGraph, subgraph);
                if (reductionRule.reduce()) {
                    subgraphs.remove(subgraph);
                }
            }
        }
    }
}
