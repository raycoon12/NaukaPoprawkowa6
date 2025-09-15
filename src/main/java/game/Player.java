package game;

import server.Database;

public class Player {
    private Duel duel;
    private Gesture gesture;

    // gracz wybiera gest w trakcie pojedynku
    public void makeGesture(Gesture gesture) {
        this.gesture = gesture;
        if (duel != null) {
            duel.handleGesture(this, gesture);
        }
    }

    public void enterDuel(Duel duel) {
        this.duel = duel;
    }

    public void leaveDuel() {
        this.duel = null;
        this.gesture = null;
    }

    public boolean isDueling() {
        return this.duel != null;
    }

}
