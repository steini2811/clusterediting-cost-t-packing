package de.unmr.bacluster.models.datareduction;

import java.util.HashSet;
import java.util.Set;

import de.unmr.bacluster.models.Edge;
import de.unmr.bacluster.models.EdgeState;
import de.unmr.bacluster.models.Graph;
import de.unmr.bacluster.models.P3;
import de.unmr.bacluster.models.modifications.Modification;

public class ObviousP3Reduction extends DataReduction {

    public ObviousP3Reduction(final Graph graph) {
        super(graph);
    }

    private Set<Modification> helpReduce() {
        final Set<Modification> mods = new HashSet<>();
        final Set<P3> p3set = new HashSet<>(graph.getAllP3s());

        for (final P3 p3 : p3set) {
            final Set<Edge> edges = p3.getEditableEdges(graph);
            if (edges.size() == 1) {
                final Edge edge = edges.iterator().next();
                final EdgeState state = graph.getState(edge);

                if (state.isModifiable()) {
                    mods.add(new Modification(state.isConnected(), edge));
                }
            }
        }

        return mods;
    }

    @Override
    public boolean reduce() {
        boolean running = true;
        do {
            final Set<Modification> mods = helpReduce();
            graph.addPartialSolution(mods);
            modifications.addAll(mods);
            running = !mods.isEmpty();
        } while (running);

        return !modifications.isEmpty();
    }
}
