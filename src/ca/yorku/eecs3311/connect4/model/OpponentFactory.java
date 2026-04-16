package ca.yorku.eecs3311.connect4.model;

/**
 * Factory class that instantiates {@link Player} strategies by name.
 * <p>
 * Called from the GUI's opponent {@link javafx.scene.control.ComboBox} handler
 * in {@link ca.yorku.eecs3311.connect4.viewcontroller.ConnectFourApplication}
 * to swap AI strategies at runtime — a classic application of the
 * <strong>Factory + Strategy</strong> patterns together.
 * </p>
 */
public class OpponentFactory {
    /**
     * Creates a {@link Player} instance matching the requested strategy name.
     *
     * @param type  case-insensitive strategy name ("Human", "Random", "Greedy", "Defensive")
     * @param token the token character the AI will play as ({@code 'X'} or {@code 'O'})
     * @return the corresponding {@link Player} implementation; defaults to {@link PlayerHuman}
     */
    public static Player create(String type, char token) {
        if (type == null) return new PlayerHuman();
        if (type.equalsIgnoreCase("human")) return new PlayerHuman();
        if (type.equalsIgnoreCase("random")) return new PlayerRandom();
        if (type.equalsIgnoreCase("greedy")) return new PlayerGreedy(token);
        if (type.equalsIgnoreCase("defensive")) return new PlayerDefensive(token);

        // TODO A2-20: done
        // Support more strategies here, e.g. defensive, minimax, replay, etc.
        return new PlayerHuman();
    }
}
