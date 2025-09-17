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
        return switch (this) {
            case ROCK -> (other == SCISSORS) ? 1 : -1;
            case PAPER -> (other == ROCK) ? 1 : -1;
            case SCISSORS -> (other == PAPER) ? 1 : -1;
        };
    }

}
