package ca.yorku.eecs3311.connect4.model;

/**
 * Value object representing a single Connect Four move.
 * <p>
 * In Connect Four the only decision is which column to drop into,
 * so a move is fully described by a column index.
 * Returned by {@link Player#getMove(ConnectFour)} implementations.
 * </p>
 */
public class Move {
    private final int column;

    /** @param column the 0-indexed column to drop into */
    public Move(int column) {
        this.column = column;
    }

    /** @return the target column index */
    public int getColumn() {
        return this.column;
    }

    @Override
    public String toString() {
        return "(col=" + this.column + ")";
    }
}
