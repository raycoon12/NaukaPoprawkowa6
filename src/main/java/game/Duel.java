package game;

public class Duel {
    private final Player playerone;
    private final Player playertwo;

    private Gesture gesture1;
    private Gesture gesture2;
    private Runnable onEnd;

    public Duel(Player player1, Player player2) {
        this.playerone = player1;
        this.playertwo = player2;

        this.playerone.enterDuel(this);
        this.playertwo.enterDuel(this);
    }

    public void setOnEnd(Runnable onEnd) {
        this.onEnd = onEnd;
    }

    // ustawienie gestów
    public void setGestures(Gesture gesture1, Gesture gesture2) {
        this.gesture1 = gesture1;
        this.gesture2 = gesture2;

        playerone.makeGesture(gesture1);
        playertwo.makeGesture(gesture2);
    }

    // zakończenie pojedynku
    public void endDuel() {
        playerone.leaveDuel();
        playertwo.leaveDuel();
        gesture1 = null;
        gesture2 = null;
    }

    // pomocnicze gettery
    public Player getPlayer1() {
        return playerone;
    }

    public Player getPlayer2() {
        return playertwo;
    }

    public void handleGesture(Player player, Gesture gesture) {
        if (player == playerone) {
            gesture1 = gesture;
        } else if (player == playertwo) {
            gesture2 = gesture;
        }

        // jeśli obaj gracze wybrali gesty, wywołujemy onEnd
        if (gesture1 != null && gesture2 != null && onEnd != null) {
            onEnd.run();
        }
    }

    // pomocnicze gettery
    public Gesture getGesture(Player player) {
        if (player == playerone) {
            return gesture1;
        }
        if (player == playertwo) {
            return gesture2;
        }
        return null;
    }

    public record Result(Player winner, Player loser){

    }

    public Result evaluate() {
        if (gesture1 == null || gesture2 == null) {
            return null; // brak gestów
        }

        int comparison = gesture1.compareWith(gesture2);

        if (comparison > 0) {
            return new Result(playerone, playertwo); // player1 wygrywa
        } else if (comparison < 0) {
            return new Result(playertwo, playerone); // player2 wygrywa
        } else {
            return null; // remis
        }
    }

}
