package de.unmr.bacluster.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;

import de.unmr.bacluster.models.graphediting.GraphSplitting;
import de.unmr.bacluster.models.graphediting.GraphSplittingBridges;
import de.unmr.bacluster.models.modifications.Modification;
import de.unmr.bacluster.models.modifications.ModificationGroup;
import de.unmr.bacluster.util.Config;

public class Graph {
    protected final HashMap<Integer, Set<Integer>> adjacentList = new HashMap<>();
    protected final HashMap<Edge, EdgeState> edgeList = new HashMap<>();
    protected final Stack<Modification> modifications = new Stack<>();
    protected final Set<P3> p3s = new HashSet<>();
    protected final HashMap<Integer, Integer> nodeP3Counts = new HashMap<>();

    public Graph(final Collection<Edge> list) {
        super();

        addEdges(list);
    }

    protected void addEdges(final Collection<Edge> list) {
        for (final Edge edge : list) {
            if (adjacentList.get(edge.getNodeA()) == null) {
                adjacentList.put(edge.getNodeA(), new HashSet<>());
            }
            if (adjacentList.get(edge.getNodeB()) == null) {
                adjacentList.put(edge.getNodeB(), new HashSet<>());
            }
            setState(edge, EdgeState.CONNECTED);
        }

        generateP3s();
    }

    private void addP3(final P3 p3) {
        p3s.add(p3);
        final int u = p3.getU();
        final int v = p3.getV();
        final int w = p3.getW();

        nodeP3Counts.putIfAbsent(u, 0);
        nodeP3Counts.putIfAbsent(v, 0);
        nodeP3Counts.putIfAbsent(w, 0);

        nodeP3Counts.put(u, nodeP3Counts.get(u) + 1);
        nodeP3Counts.put(v, nodeP3Counts.get(v) + 1);
        nodeP3Counts.put(w, nodeP3Counts.get(w) + 1);
    }

    public void addPartialSolution(final Collection<Modification> mods) {
        for (final Modification mod : mods) {
            if (!modify(mod)) {
                throw new RuntimeException("Partial Solution invalid.");
            }
        }
    }

    public boolean areNeighbors(final int nodeA, final int nodeB) {
        return adjacentList.get(nodeA).contains(nodeB);
    }

    /**
     * does a BFS on a vertex and adds every P3 if there is found a vertex at
     * distance two
     */
    protected void bfs2(final int index) {
        final Set<Integer> neighbors = getNeighbors(index);
        for (final int neighbor : neighbors) {
            // neighbor2 means the two-degree-neighbor (neighbor of neighbor)
            for (final int neighbor2 : getNeighbors(neighbor)) {
                if (neighbor2 != index && !neighbors.contains(neighbor2)) {
                    final P3 p3 = new P3(index, neighbor, neighbor2);
                    addP3(p3);
                }
            }
        }
    }

    protected void execute(final Modification mod, final boolean revert) {
        final Edge edge = mod.getEdge();

        if (mod.isRemove() ^ revert) {
            setState(edge, revert ? EdgeState.DISCONNECTED : EdgeState.FORBIDDEN);
            updateP3sRemove(edge);
        } else {
            setState(edge, revert ? EdgeState.CONNECTED : EdgeState.PERMANENT);
            updateP3sAdd(edge);
        }
    }

    protected void generateP3s() {
        for (final int v : adjacentList.keySet()) {
            bfs2(v);
        }
    }

    public Set<Integer> getAllNodes() {
        return new HashSet<>(adjacentList.keySet());
    }

    public Set<P3> getAllP3s() {
        return p3s;
    }

    protected int getBridgeSplittingLowerBound() {
        final GraphSplittingBridges splitting = new GraphSplittingBridges(this);
        final Optional<Integer> result = splitting.splitByBridges();
        if (result.isPresent()) {
            return result.get();
        }
        return 0;
    }

    /**
     * Returns a common neighbor of two nodes {@code nodeA} and {@code nodeB}
     * that is not {@code forbiddenNode}.
     *
     * @param nodeA
     * @param nodeB
     * @param forbiddenNode
     * @return
     */
    public Optional<Integer> getCommonNeighbor(final int nodeA, final int nodeB, final int forbiddenNode) {
        final Set<Integer> neighborsOfA = new HashSet<>();
        neighborsOfA.addAll(getNeighbors(nodeA));
        neighborsOfA.retainAll(getNeighbors(nodeB));
        neighborsOfA.remove(forbiddenNode);

        if (neighborsOfA.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(neighborsOfA.iterator().next());
    }

    public int getDegree(final int node) {
        return adjacentList.get(node).size();
    }

    public int getEdgeDisjointLowerBound() {
        final HashSet<Edge> blacklist = new HashSet<>();
        int bound = 0;

        for (final P3 p3 : p3s) {
            final Set<Edge> p3Edges = p3.getAllEdges();
            if (Collections.disjoint(blacklist, p3Edges)) {
                bound++;
                blacklist.addAll(p3Edges);
            }
        }
        return bound;
    }

    public int getLowerBoundByConfig() {
        if (!Config.useLowerBound()) {
            return 0;
        }

        if (Config.useCostT()) {
            if (Config.useSplitBridges()) {
                return getBridgeSplittingLowerBound();
            } else if (Config.getTValue() > 0) {
                return getPathSplittingLowerBound(Config.getTValue());
            }
        }

        return getEdgeDisjointLowerBound();
    }

    public Set<Edge> getModifiedEdges() {
        final HashSet<Edge> edges = new HashSet<>();
        for (final Modification mod : modifications) {
            edges.add(mod.getEdge());
        }
        return edges;
    }

    public Set<Integer> getNeighbors(final int nodeA) {
        return new HashSet<>(adjacentList.get(nodeA));
    }

    public Map<Integer, Integer> getNodeinP3CountMap() {
        return nodeP3Counts;
    }

    public Optional<P3> getP3() {
        if (!p3s.isEmpty()) {
            Optional<P3> temp = Optional.empty();
            Optional<P3> temp3 = Optional.empty();
            for (final P3 p3 : p3s) {
                final int branchingNumber = p3.getEditableEdges(this).size();
                if (branchingNumber == 1) {
                    return Optional.of(p3);
                } else if (!temp.isPresent() && branchingNumber == 2) {
                    temp = Optional.of(p3);
                } else if (!temp.isPresent() && !temp3.isPresent()
                        && !getCommonNeighbor(p3.getU(), p3.getW(), p3.getV()).isPresent()) {
                    temp3 = Optional.of(p3);
                }
            }

            if (temp.isPresent()) {
                return temp;
            }
            if (temp3.isPresent()) {
                return temp3;
            }
            return Optional.of(p3s.iterator().next());
        }

        return Optional.empty();
    }

    public int getPathSplittingLowerBound(final int t) {
        final GraphSplitting splitting = new GraphSplitting(this, t);
        return splitting.getLowerBoundSum();
    }

    public EdgeState getState(final Edge edge) {
        return edgeList.containsKey(edge) ? edgeList.get(edge) : EdgeState.DISCONNECTED;
    }

    public Set<Integer> getSubgraphNeighbors(final Subgraph subgraph) {
        final Set<Integer> neighborsOfSubgraph = new HashSet<>();
        for (final int node : subgraph.getAllNodes()) {
            neighborsOfSubgraph.addAll(getNeighbors(node));
        }
        neighborsOfSubgraph.removeAll(subgraph.getAllNodes());

        return neighborsOfSubgraph;
    }

    protected int getVertexDisjointLowerBound() {
        final HashSet<Integer> blacklist = new HashSet<>();
        int bound = 0;

        for (final P3 p3 : p3s) {
            final Set<Integer> p3Nodes = p3.getAllNodes();
            if (Collections.disjoint(blacklist, p3Nodes)) {
                bound++;
                blacklist.addAll(p3Nodes);
            }
        }

        return bound;
    }

    public boolean hasP3() {
        return !p3s.isEmpty();
    }

    protected boolean isEdgeAllowed(final Edge edge) {
        final EdgeState currentState = edgeList.get(edge);
        return currentState == null || currentState.isModifiable();
    }

    public boolean modify(final Modification mod) {
        final Edge edge = mod.getEdge();
        if (!isEdgeAllowed(edge)) {
            return false;
        }

        modifications.push(mod);
        execute(mod, false);

        return true;
    }

    public boolean modify(final ModificationGroup group) {
        int i = 0;
        for (final Modification mod : group.getModifications()) {
            if (!modify(mod)) {
                rollback(i);
                return false;
            }
            i++;
        }

        for (final Edge edge : group.getForbiddenEdges()) {
            switch (setState(edge, EdgeState.FORBIDDEN)) {
            case UNCHANGED:
                group.removeUnchangedForbiddenEdge(edge);
                break;
            case REDUCE_K:
                throw new RuntimeException(edge + " in " + group);
            default:
                break;
            }
        }

        for (final Edge edge : group.getPermanentEdges()) {
            switch (setState(edge, EdgeState.PERMANENT)) {
            case UNCHANGED:
                group.removeUnchangedPermanentEdge(edge);
                break;
            case REDUCE_K:
                throw new RuntimeException(edge + " in " + group);
            default:
                break;
            }
        }

        return true;
    }

    private void removeP3(final P3 p3) {
        p3s.remove(p3);
        final int u = p3.getU();
        final int v = p3.getV();
        final int w = p3.getW();

        nodeP3Counts.put(u, nodeP3Counts.get(u) - 1);
        nodeP3Counts.put(v, nodeP3Counts.get(v) - 1);
        nodeP3Counts.put(w, nodeP3Counts.get(w) - 1);
    }

    public void rollback() {
        final Modification mod = modifications.pop();
        execute(mod, true);
    }

    public void rollback(final int count) {
        for (int i = 0; i < count; i++) {
            rollback();
        }
    }

    public void rollback(final ModificationGroup group) {
        rollback(group.getNeededSteps());

        for (final Edge edge : group.getForbiddenEdges()) {
            if (setState(edge, EdgeState.DISCONNECTED) != ModifyState.FREE) {
                throw new RuntimeException(edge + " in " + group);
            }
        }

        for (final Edge edge : group.getPermanentEdges()) {
            if (setState(edge, EdgeState.CONNECTED) != ModifyState.FREE) {
                throw new RuntimeException(edge + " in " + group);
            }
        }
    }

    /**
     * Sets a state for a given Edge. Returns true when this modification
     * changed the state of this edge.
     *
     * @param edge
     * @param state
     * @return
     */
    protected ModifyState setState(final Edge edge, final EdgeState state) {
        final int nodeA = edge.getNodeA();
        final int nodeB = edge.getNodeB();

        final EdgeState oldState = getState(edge);

        if (oldState == state) {
            return ModifyState.UNCHANGED;
        }

        if (state.isConnected()) {
            adjacentList.get(nodeA).add(nodeB);
            adjacentList.get(nodeB).add(nodeA);
        } else {
            adjacentList.get(nodeA).remove(nodeB);
            adjacentList.get(nodeB).remove(nodeA);
        }

        if (state == EdgeState.DISCONNECTED) {
            edgeList.remove(edge);
        } else {
            edgeList.put(edge, state);
        }

        if (oldState.isConnected() == state.isConnected()) {
            return ModifyState.FREE;
        }

        return ModifyState.REDUCE_K;
    }

    protected void updateP3sAdd(final Edge edge) {
        // remove every p3 that has u and w filled with nodeA and nodeB
        final List<P3> toberemoved = new ArrayList<>();
        for (final P3 p3 : p3s) {
            if (p3.missesEdge(edge)) {
                toberemoved.add(p3);
            }
        }

        for (final P3 p3 : toberemoved) {
            removeP3(p3);
        }

        final int nodeA = edge.getNodeA();
        final int nodeB = edge.getNodeB();

        // search a nodeC so that either nodeA or nodeB has an edge with nodeC
        final Set<Integer> adjacentA = getNeighbors(nodeA);
        final Set<Integer> adjacentB = getNeighbors(nodeB);

        for (final int nodeC : adjacentA) {
            if (!adjacentB.contains(nodeC) && nodeB != nodeC) {
                // add a new p3 with nodeB being u, nodeA being v, nodeC being w
                final P3 newP3 = new P3(nodeB, nodeA, nodeC);
                addP3(newP3);
            }
        }
        for (final int nodeC : adjacentB) {
            if (!adjacentA.contains(nodeC) && nodeA != nodeC) {
                // add a new p3 with nodeA being u, nodeB being v, nodeC being w
                final P3 newP3 = new P3(nodeA, nodeB, nodeC);
                addP3(newP3);
            }
        }
    }

    protected void updateP3sRemove(final Edge edge) {
        // remove every p3 that has an edge with nodeA and nodeB
        final List<P3> toberemoved = new ArrayList<>();
        for (final P3 p3 : p3s) {
            if (p3.hasEdge(edge)) {
                toberemoved.add(p3);
            }
        }

        for (final P3 p3 : toberemoved) {
            removeP3(p3);
        }

        final int nodeA = edge.getNodeA();
        final int nodeB = edge.getNodeB();

        // search every nodeC so that nodeA and nodeB have both an edge with
        // nodeC
        final Set<Integer> adjacentA = getNeighbors(nodeA);
        final Set<Integer> adjacentB = getNeighbors(nodeB);

        for (final int nodeC : adjacentA) {
            if (adjacentB.contains(nodeC)) {
                // add a new P3 with nodeA being u, nodeC being v, nodeB being w
                final P3 newP3 = new P3(nodeA, nodeC, nodeB);
                addP3(newP3);
            }
        }
    }
}
