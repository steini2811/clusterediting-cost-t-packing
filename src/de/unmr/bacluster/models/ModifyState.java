package de.unmr.bacluster.models;

public enum ModifyState {
    REDUCE_K(true, true, false), FREE(true, true, true), UNCHANGED(true, false, true);

    private boolean allowed;
    private boolean changed;
    private boolean free;

    ModifyState(final boolean allowed, final boolean changed, final boolean free) {
        this.allowed = allowed;
        this.changed = changed;
        this.free = free;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public boolean isChanged() {
        return changed;
    }

    public boolean isFree() {
        return free;
    }
}
