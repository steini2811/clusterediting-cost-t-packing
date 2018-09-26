package de.unmr.bacluster.models.modifications;

import de.unmr.bacluster.models.Edge;

public class Modification {
    protected final boolean remove;
    protected final Edge edge;

    public Modification(final boolean remove, final Edge edge) {
        super();
        this.remove = remove;
        this.edge = edge;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Modification other = (Modification) obj;
        if (edge == null) {
            if (other.edge != null) {
                return false;
            }
        } else if (!edge.equals(other.edge)) {
            return false;
        }
        if (remove != other.remove) {
            return false;
        }
        return true;
    }

    public Edge getEdge() {
        return edge;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (edge == null ? 0 : edge.hashCode());
        result = prime * result + (remove ? 1231 : 1237);
        return result;
    }

    public boolean isInvertedOf(final Modification mod) {
        return mod.getEdge().equals(edge) && remove != mod.isRemove();
    }

    public boolean isRemove() {
        return remove;
    }

    @Override
    public String toString() {
        return "Modification [remove=" + remove + ", edge=" + edge + "]";
    }
}
