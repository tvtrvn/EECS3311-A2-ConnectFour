package ca.yorku.eecs3311.connect4.model;

import java.util.List;

/**
 * A greedy {@link Player} strategy that evaluates moves with simple heuristics.
 * <p>
 * Priority order:
 * <ol>
 *   <li>Take an immediate winning move.</li>
 *   <li>Block the opponent's immediate winning move.</li>
 *   <li>Otherwise, pick the column that maximises a score combining
 *       longest-line-through and centre-preference.</li>
 * </ol>
 * Uses {@link ConnectFour#copy()} to simulate moves without mutating the
 * live game state.
 * </p>
 */
public class PlayerGreedy extends PlayerRandom {
    private final char me;

    /** @param me the token character this AI plays as ({@code 'X'} or {@code 'O'}) */
    public PlayerGreedy(char me) {
        this.me = me;
    }

    @Override
    public Move getMove(ConnectFour game) {
        List<Integer> legal = this.legalColumns(game);
        if (legal.isEmpty()) return null;

        for (int col : legal) {
            if (this.isWinningMove(game, col, this.me)) return new Move(col);
        }

        char other = game.getBoard().otherPlayer(this.me);
        for (int col : legal) {
            if (this.isWinningMove(game, col, other)) return new Move(col);
        }

        int bestCol = legal.get(0);
        int bestScore = Integer.MIN_VALUE;
        for (int col : legal) {
            int score = this.scoreAfterMove(game, col, this.me);
            if (score > bestScore || (score == bestScore && col < bestCol)) {
                bestScore = score;
                bestCol = col;
            }
        }
        return new Move(bestCol);
    }

    /** Simulates a drop on a copy and checks whether it produces a win. */
    private boolean isWinningMove(ConnectFour game, int col, char player) {
        ConnectFour copy = game.copy();
        if (copy.getBoard().drop(col, player) < 0) return false;
        return copy.getBoard().hasWon(player);
    }

    /**
     * Heuristic score: heavily weights the longest consecutive line through the
     * newly placed token, with a small tie-breaker favouring centre columns.
     */
    protected int scoreAfterMove(ConnectFour game, int col, char player) {
        ConnectFour copy = game.copy();
        int row = copy.getBoard().drop(col, player);
        if (row < 0) return Integer.MIN_VALUE;

        int centerPreference = 10 - Math.abs(3 - col);
        int longest = this.longestLineThrough(copy.getBoard(), row, col, player);
        return 100 * longest + centerPreference;
    }

    /** Returns the length of the longest line passing through (row, col) in any direction. */
    private int longestLineThrough(ConnectFourBoard board, int row, int col, char player) {
        int best = 1;
        best = Math.max(best, this.lineLength(board, row, col, 0, 1, player));
        best = Math.max(best, this.lineLength(board, row, col, 1, 0, player));
        best = Math.max(best, this.lineLength(board, row, col, 1, 1, player));
        best = Math.max(best, this.lineLength(board, row, col, 1, -1, player));
        return best;
    }

    /** Counts consecutive same-player tokens in both directions along (drow, dcol). */
    private int lineLength(ConnectFourBoard board, int row, int col, int drow, int dcol, char player) {
        return 1 + this.countDirection(board, row, col, drow, dcol, player)
                 + this.countDirection(board, row, col, -drow, -dcol, player);
    }

    /** Walks in one direction, counting consecutive matching tokens. */
    private int countDirection(ConnectFourBoard board, int row, int col, int drow, int dcol, char player) {
        int count = 0;
        int rr = row + drow;
        int cc = col + dcol;
        while (board.validCoordinate(rr, cc) && board.get(rr, cc) == player) {
            count++;
            rr += drow;
            cc += dcol;
        }
        return count;
    }

    @Override
    public String getName() {
        return "Greedy";
    }

    // TODO A2-21: done
    // Added an additional strategy (PlayerDefensive) via Strategy/Factory.
}
