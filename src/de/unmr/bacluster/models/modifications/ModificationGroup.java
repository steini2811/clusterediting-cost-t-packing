package de.unmr.bacluster.models.modifications;

import java.util.ArrayList;
import java.util.List;

import de.unmr.bacluster.models.Edge;

public class ModificationGroup {
    private final int caseID;
    private final List<Modification> modifications;
    private final List<Edge> forbiddenEdges = new ArrayList<>();
    private final List<Edge> permanentEdges = new ArrayList<>();

    public ModificationGroup(final int caseID, final List<Modification> modifications, final List<Edge> forbiddenEdges,
            final List<Edge> permanentEdges) {
        super();

        this.caseID = caseID;
        this.modifications = modifications;
        this.forbiddenEdges.addAll(forbiddenEdges);
        this.permanentEdges.addAll(permanentEdges);
    }

    public int getCaseID() {
        return caseID;
    }

    public List<Edge> getForbiddenEdges() {
        final List<Edge> edges = new ArrayList<>();
        edges.addAll(forbiddenEdges);
        return edges;
    }

    public List<Modification> getModifications() {
        return modifications;
    }

    public int getNeededSteps() {
        return modifications.size();
    }

    public List<Edge> getPermanentEdges() {
        final List<Edge> edges = new ArrayList<>();
        edges.addAll(permanentEdges);
        return edges;
    }

    public void removeUnchangedForbiddenEdge(final Edge edge) {
        forbiddenEdges.remove(edge);
    }

    public void removeUnchangedPermanentEdge(final Edge edge) {
        permanentEdges.remove(edge);
    }

    @Override
    public String toString() {
        return "ModificationGroup [caseID=" + caseID + ", modifications=" + modifications + ", forbiddenEdges="
                + forbiddenEdges + ", permanentEdges=" + permanentEdges + "]";
    }
}
