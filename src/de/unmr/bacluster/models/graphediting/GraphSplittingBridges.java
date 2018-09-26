package de.unmr.bacluster.models.graphediting;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import de.unmr.bacluster.models.Edge;
import de.unmr.bacluster.models.Graph;
import de.unmr.bacluster.models.P3;

public class GraphSplittingBridges {
    private final Graph originGraph;

    public GraphSplittingBridges(final Graph graph) {
        super();
        this.originGraph = graph;
    }

    protected boolean areBridge(final P3 first, final P3 second) {
        if (first.equals(second)) {
            return false;
        }

        final int u1;
        final int w2;

        final int v1 = first.getV();
        final int v2 = second.getV();

        if (v1 == second.getU()) {
            w2 = second.getW();
        } else if (v1 == second.getW()) {
            w2 = second.getU();
        } else {
            return false;
        }

        if (v2 == first.getU()) {
            u1 = first.getW();
        } else if (v2 == first.getW()) {
            u1 = first.getU();
        } else {
            return false;
        }

        if (!Collections.disjoint(originGraph.getNeighbors(v1), originGraph.getNeighbors(v2))) {
            return false;
        }

        if (originGraph.areNeighbors(u1, w2)) {
            return false;
        }

        // make sure a part of a bridge is not a lonely vertex
        if (originGraph.getDegree(v1) < 3 || originGraph.getDegree(v2) < 3) {
            return false;
        }

        return true;
    }

    protected Set<Edge> buildComponentFromNode(final int node, final int ignoreNode) {
        final Set<Integer> set = new HashSet<>();
        set.add(ignoreNode);
        return this.buildComponentFromNode(node, new HashSet<>(), set, new HashSet<>());
    }

    protected Set<Edge> buildComponentFromNode(final int node, final Set<Integer> whitelist,
            final Set<Integer> blacklist, final Set<Edge> result) {
        blacklist.add(node);
        whitelist.remove(node);
        for (final int neighbor : originGraph.getNeighbors(node)) {
            if (blacklist.contains(neighbor)) {
                continue;
            }
            result.add(new Edge(node, neighbor));
            whitelist.add(neighbor);
        }

        if (whitelist.isEmpty()) {
            return result;
        }

        return buildComponentFromNode(whitelist.iterator().next(), whitelist, blacklist, result);
    }

    public Optional<Integer> splitByBridges() {
        final Set<Edge> bridges = new HashSet<>();
        for (final P3 p3 : originGraph.getAllP3s()) {
            for (final P3 nextp3 : originGraph.getAllP3s()) {
                if (areBridge(p3, nextp3)) {
                    bridges.add(new Edge(p3.getV(), nextp3.getV()));
                }
            }
        }

        for (final Edge bridge : bridges) {
            final Set<Edge> componentA = buildComponentFromNode(bridge.getNodeA(), bridge.getNodeB());
            final Set<Edge> componentB = buildComponentFromNode(bridge.getNodeB(), bridge.getNodeA());
            if (Collections.disjoint(componentA, componentB)) {
                final Graph graph1 = new Graph(componentA);
                final Graph graph2 = new Graph(componentB);

                return Optional.of(graph1.getLowerBoundByConfig() + graph2.getLowerBoundByConfig());
            }
        }

        return Optional.empty();
    }
}
