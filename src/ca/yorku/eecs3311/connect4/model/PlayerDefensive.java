package ca.yorku.eecs3311.connect4.model;

/**
 * A defensive {@link Player} strategy that prioritises blocking over attacking.
 * <p>
 * Priority order:
 * <ol>
 *   <li>Block the opponent's winning move (if any).</li>
 *   <li>Take its own winning move (if any).</li>
 *   <li>Fall back to a random legal column via {@link PlayerRandom}.</li>
 * </ol>
 * This contrasts with {@link PlayerGreedy}, which tries to win first and
 * block second. Demonstrates extensibility of the Strategy pattern: adding
 * a new AI only requires a new {@code Player} subclass and a one-line
 * addition to {@link OpponentFactory}.
 * </p>
 */
public class PlayerDefensive extends PlayerRandom {
    private final char me;

    /** @param me the token character this AI plays as ({@code 'X'} or {@code 'O'}) */
    public PlayerDefensive(char me) {
        this.me = me;
    }

    @Override
    public Move getMove(ConnectFour game) {
        java.util.List<Integer> legal = this.legalColumns(game);
        if (legal.isEmpty()) return null;

        // Block first
        char other = game.getBoard().otherPlayer(this.me);
        for (int col : legal) {
            if (this.wouldWin(game, col, other)) return new Move(col);
        }
        // Then win if possible
        for (int col : legal) {
            if (this.wouldWin(game, col, this.me)) return new Move(col);
        }
        // Otherwise random
        return super.getMove(game);
    }

    /** Simulates a drop on a copy to check if it would produce a four-in-a-row. */
    private boolean wouldWin(ConnectFour game, int col, char player) {
        ConnectFour copy = game.copy();
        if (copy.getBoard().drop(col, player) < 0) return false;
        return copy.getBoard().hasWon(player);
    }

    @Override
    public String getName() {
        return "Defensive";
    }
}
