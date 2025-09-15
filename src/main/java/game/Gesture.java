package game;

public enum Gesture {
    ROCK, PAPER, SCISSORS;

    public static Gesture fromString(String input) {
        if (input == null){
            return null;
        }
        input = input.toLowerCase();
        return input.equals("r") ? ROCK
                : input.equals("p") ? PAPER
                : input.equals("s") ? SCISSORS
                : null;
    }

    public int compareWith(Gesture other) {
        if (this == other) {
            return 0;
        }
        switch (this) {
            case ROCK:
                return (other == SCISSORS) ? 1 : -1;
            case PAPER:
                return (other == ROCK) ? 1 : -1;
            case SCISSORS:
                return (other == PAPER) ? 1 : -1;
        }
        return 0; // technicznie nieosiągalne
    }

}
