package game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DuelTest {

    @Test
    void testPlayersAreDuelingAfterDuelCreation() {
        Player player1 = new Player();
        Player player2 = new Player();

        Duel duel = new Duel(player1, player2);

        assertTrue(player1.isDueling(), "Player1 powinien być w pojedynku");
        assertTrue(player2.isDueling(), "Player2 powinien być w pojedynku");
    }

    @Test
    void testEvaluate_Player1Wins() {
        Player player1 = new Player();
        Player player2 = new Player();

        Duel duel = new Duel(player1, player2);

        // ustawiamy gesty graczy
        player1.makeGesture(Gesture.ROCK);
        player2.makeGesture(Gesture.SCISSORS);

        // oceniamy pojedynek
        Duel.Result result = duel.evaluate();

        assertNotNull(result, "Powinien być zwycięzca");
        assertEquals(player1, result.winner(), "Zwycięzcą powinien być player1");
        assertEquals(player2, result.loser(), "Przegranym powinien być player2");
    }

    @Test
    void testEvaluate_Draw() {
        Player player1 = new Player();
        Player player2 = new Player();

        Duel duel = new Duel(player1, player2);

        // obaj gracze wybierają ten sam gest
        player1.makeGesture(Gesture.PAPER);
        player2.makeGesture(Gesture.PAPER);

        Duel.Result result = duel.evaluate();

        assertNull(result, "Powinien być remis (null)");
    }

}