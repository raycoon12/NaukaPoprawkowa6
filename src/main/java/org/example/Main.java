package org.example;

import game.Duel;
import game.Gesture;
import game.Player;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Player one = new Player();
        Player two = new Player();

        Duel duel = new Duel(one, two);

        one.makeGesture(Gesture.SCISSORS);
        two.makeGesture(Gesture.PAPER);

        Duel.Result result = duel.evaluate();

        if(result != null) {
            System.out.println("Winner: " + result.winner());
            System.out.println("Loser: " + result.loser());
        } else {
            System.out.println("Draw!");
        }
    }
}
