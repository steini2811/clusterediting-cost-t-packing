package de.unmr.bacluster.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.unmr.bacluster.searchtree.ClusterEditing;
import de.unmr.bacluster.searchtree.ClusterEditingRefined;
import de.unmr.bacluster.searchtree.ClusterEditingSimple;
import de.unmr.bacluster.util.Config;

public class Subgraph extends Graph {
    private Collection<Edge> currentSolution;
    private boolean graphChanged = true;

    private Subgraph(final Collection<Edge> list) {
        super(list);
    }

    public Subgraph(final int node) {
        super(new ArrayList<>());

        adjacentList.put(node, new HashSet<>());
    }

    public Subgraph(final int nodeA, final int nodeB) {
        super(new ArrayList<>());

        final ArrayList<Edge> list = new ArrayList<>();
        list.add(new Edge(nodeA, nodeB));
        addEdges(list);
    }

    public Subgraph(final P3 p3, final Graph originGraph) {
        this(p3.getU(), p3.getV());
        addNode(p3.getW(), originGraph.getNeighbors(p3.getW()));
    }

    public void addNode(final int node, final Set<Integer> connectedNodes) {
        adjacentList.put(node, new HashSet<>());
        for (final int connectedNode : connectedNodes) {
            if (containsNode(connectedNode)) {
                adjacentList.get(node).add(connectedNode);
                adjacentList.get(connectedNode).add(node);
                setState(new Edge(node, connectedNode), EdgeState.CONNECTED);
            }
        }

        generateP3s();

        graphChanged = true;
    }

    public boolean containsNode(final int node) {
        return adjacentList.containsKey(node);
    }

    @Override
    public int getLowerBoundByConfig() {
        return getEdgeDisjointLowerBound();
    }

    int getSharedNodesCount(final Subgraph other) {
        final Set<Integer> set = new HashSet<>();
        set.addAll(adjacentList.keySet());
        set.retainAll(other.adjacentList.keySet());

        return set.size();
    }

    public Collection<Edge> getSolution() {
        if (!graphChanged) {
            return currentSolution;
        }

        final ClusterEditing clusterEditing = Config.useSimpleBranchingT() ? new ClusterEditingSimple()
                : new ClusterEditingRefined();
        final Result result = clusterEditing.ce(this, true);

        currentSolution = result.getList().get();
        graphChanged = false;

        rollback(currentSolution.size());
        resetUnmodifiableEdges();

        return currentSolution;
    }

    public int getSolutionSize() {
        return getSolution().size();
    }

    public boolean isNodeDisjoint(final List<Subgraph> subgraphs) {
        return testForMaximumSharedNodes(subgraphs, 0);
    }

    public boolean isValid(final List<Subgraph> subgraphs) {
        return testForMaximumSharedNodes(subgraphs, 1);
    }

    public void removeNode(final int node) {
        for (final int neighbor : adjacentList.get(node)) {
            adjacentList.get(neighbor).remove(node);
        }
        adjacentList.remove(node);

        p3s.clear();
        generateP3s();

        graphChanged = true;
    }

    private void resetUnmodifiableEdges() {
        for (final Map.Entry<Edge, EdgeState> entry : edgeList.entrySet()) {
            final EdgeState oldState = entry.getValue();
            if (!oldState.isModifiable()) {
                entry.setValue(oldState.isConnected() ? EdgeState.CONNECTED : EdgeState.DISCONNECTED);
            }
        }
    }

    private boolean testForMaximumSharedNodes(final List<Subgraph> subgraphs, final int maxSharedNodes) {
        for (final Subgraph otherGraph : subgraphs) {
            if (this == otherGraph) {
                continue;
            }

            if (getSharedNodesCount(otherGraph) > maxSharedNodes) {
                return false;
            }
        }

        return true;
    }

}
