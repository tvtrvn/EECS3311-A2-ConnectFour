package ca.yorku.eecs3311.connect4.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A {@link Player} strategy that picks a random legal column each turn.
 * <p>
 * Also serves as the base class for smarter strategies ({@link PlayerGreedy},
 * {@link PlayerDefensive}) which inherit the {@link #legalColumns} helper and
 * fall back to random selection when no heuristic preference applies.
 * </p>
 */
public class PlayerRandom implements Player {
    private final Random rng = new Random();

    @Override
    public Move getMove(ConnectFour game) {
        List<Integer> legal = this.legalColumns(game);
        if (legal.isEmpty()) return null;
        return new Move(legal.get(this.rng.nextInt(legal.size())));
    }

    /**
     * Collects all columns that still have at least one empty cell.
     *
     * @param game the current game state
     * @return list of column indices with available space
     */
    protected List<Integer> legalColumns(ConnectFour game) {
        List<Integer> cols = new ArrayList<Integer>();
        for (int c = 0; c < ConnectFourBoard.COLS; c++) {
            if (game.getBoard().columnHasSpace(c)) cols.add(c);
        }
        return cols;
    }

    @Override
    public String getName() {
        return "Random";
    }
}
