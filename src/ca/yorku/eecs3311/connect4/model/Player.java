package ca.yorku.eecs3311.connect4.model;

/**
 * Strategy pattern interface for AI and human players.
 * <p>
 * Each concrete strategy ({@link PlayerHuman}, {@link PlayerRandom},
 * {@link PlayerGreedy}, {@link PlayerDefensive}) encapsulates a different
 * algorithm for choosing a column. The active strategy is selected at
 * runtime via the {@link OpponentFactory} and swapped through the GUI's
 * opponent {@link javafx.scene.control.ComboBox}.
 * </p>
 *
 * @see OpponentFactory
 */
public interface Player {
    /**
     * Chooses the next column to drop into based on the current game state.
     *
     * @param game the current game model (read-only use recommended)
     * @return a {@link Move} indicating the chosen column, or {@code null}
     *         if no legal move exists (human players also return {@code null})
     */
    Move getMove(ConnectFour game);

    /** @return a display-friendly name for this strategy (e.g. "Greedy") */
    String getName();
}
