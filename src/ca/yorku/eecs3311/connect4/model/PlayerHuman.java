package ca.yorku.eecs3311.connect4.model;

/**
 * Placeholder strategy for a human player.
 *
 * In the JavaFX GUI, the human move is typically supplied by the controller
 * when the user clicks a board button, so this class mainly exists to make the
 * strategy abstraction consistent.
 */
public class PlayerHuman implements Player {
    @Override
    public Move getMove(ConnectFour game) {
        return null;
    }

    @Override
    public String getName() {
        return "Human";
    }
}
