package de.unmr.bacluster.models;

public enum EdgeState {
    DISCONNECTED(false, true), CONNECTED(true, true), FORBIDDEN(false, false), PERMANENT(true, false);

    private boolean connected;
    private boolean modifiable;

    EdgeState(final boolean connected, final boolean modifiable) {
        this.connected = connected;
        this.modifiable = modifiable;
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isModifiable() {
        return modifiable;
    }
}
