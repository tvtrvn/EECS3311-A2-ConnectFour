package ca.yorku.eecs3311.connect4.model;

/**
 * Represents the state of a standard 6-row by 7-column Connect Four board.
 * <p>
 * This class is a pure data/logic layer with no UI or observer dependencies.
 * It handles token placement (gravity-based drop), win detection across all
 * four directions (horizontal, vertical, and both diagonals), draw detection,
 * and deep-copy support used by snapshot-based undo and AI look-ahead.
 * </p>
 */
public class ConnectFourBoard {
    /** Character representing an empty cell. */
    public static final char EMPTY = ' ';
    /** Character representing Player 1's token. */
    public static final char P1 = 'X';
    /** Character representing Player 2's token. */
    public static final char P2 = 'O';
    /** Sentinel value returned by {@link #getWinner()} when the board is full with no winner. */
    public static final char DRAW = 'D';

    public static final int ROWS = 6;
    public static final int COLS = 7;

    private final char[][] board;

    /** Constructs an empty board (all cells set to {@link #EMPTY}). */
    public ConnectFourBoard() {
        this.board = new char[ROWS][COLS];
        this.clear();
    }

    /** Resets every cell to {@link #EMPTY}. */
    public void clear() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                this.board[r][c] = EMPTY;
            }
        }
    }

    /**
     * Creates a deep copy of this board.
     * Modifications to the copy do not affect the original.
     *
     * @return an independent duplicate of this board
     */
    public ConnectFourBoard copy() {
        ConnectFourBoard copy = new ConnectFourBoard();
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                copy.board[r][c] = this.board[r][c];
            }
        }
        return copy;
    }

    /**
     * @param row board row (0 = top)
     * @param col board column (0 = left)
     * @return the token at (row, col), or {@link #EMPTY} if out of bounds
     */
    public char get(int row, int col) {
        if (!this.validCoordinate(row, col)) return EMPTY;
        return this.board[row][col];
    }

    /**
     * @param player {@link #P1} or {@link #P2}
     * @return the opposing player's token, or {@link #EMPTY} if invalid
     */
    public char otherPlayer(char player) {
        if (player == P1) return P2;
        if (player == P2) return P1;
        return EMPTY;
    }

    /** @return {@code true} if {@code col} is within [0, COLS) */
    public boolean validColumn(int col) {
        return 0 <= col && col < COLS;
    }

    /** @return {@code true} if (row, col) is within the board boundaries */
    public boolean validCoordinate(int row, int col) {
        return 0 <= row && row < ROWS && 0 <= col && col < COLS;
    }

    /** @return {@code true} if the top cell of {@code col} is empty (i.e. column is not full) */
    public boolean columnHasSpace(int col) {
        return this.validColumn(col) && this.board[0][col] == EMPTY;
    }

    /**
     * Finds the lowest empty row in {@code col} (simulating gravity).
     *
     * @param col the column to inspect
     * @return the row index where a token would land, or -1 if the column is full
     */
    public int landingRow(int col) {
        if (!this.columnHasSpace(col)) return -1;
        for (int r = ROWS - 1; r >= 0; r--) {
            if (this.board[r][col] == EMPTY) return r;
        }
        return -1;
    }

    /**
     * Drops a token into {@code col}, placing it in the lowest empty row.
     *
     * @param col    the target column
     * @param player the token character ({@link #P1} or {@link #P2})
     * @return the row the token landed in, or -1 if the column is full
     */
    public int drop(int col, char player) {
        int row = this.landingRow(col);
        if (row < 0) return -1;
        this.board[row][col] = player;
        return row;
    }

    /**
     * Directly sets a cell's value. Used for loading saved games and tests.
     *
     * @param row   board row
     * @param col   board column
     * @param token the character to place
     */
    public void set(int row, int col, char token) {
        if (this.validCoordinate(row, col)) {
            this.board[row][col] = token;
        }
    }

    /**
     * @param player the token to count
     * @return total number of cells occupied by {@code player}
     */
    public int getCount(char player) {
        int total = 0;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (this.board[r][c] == player) total++;
            }
        }
        return total;
    }

    /** @return {@code true} if at least one column still has space for a token */
    public boolean hasMove() {
        for (int c = 0; c < COLS; c++) {
            if (this.columnHasSpace(c)) return true;
        }
        return false;
    }

    /**
     * Checks whether 4 consecutive cells starting at (row, col) in direction
     * (drow, dcol) all belong to {@code player}.
     */
    private boolean hasLineFrom(int row, int col, int drow, int dcol, char player) {
        for (int k = 0; k < 4; k++) {
            int rr = row + k * drow;
            int cc = col + k * dcol;
            if (!this.validCoordinate(rr, cc) || this.board[rr][cc] != player) return false;
        }
        return true;
    }

    /**
     * Scans the entire board for a line of 4 in any direction.
     *
     * @param player the token to check
     * @return {@code true} if {@code player} has four in a row
     */
    public boolean hasWon(char player) {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (this.board[r][c] != player) continue;
                // Check horizontal, vertical, diagonal-down-right, diagonal-down-left
                if (this.hasLineFrom(r, c, 0, 1, player)) return true;
                if (this.hasLineFrom(r, c, 1, 0, player)) return true;
                if (this.hasLineFrom(r, c, 1, 1, player)) return true;
                if (this.hasLineFrom(r, c, 1, -1, player)) return true;
            }
        }
        return false;
    }

    /**
     * Determines the game outcome.
     *
     * @return {@link #P1} or {@link #P2} if that player has won,
     *         {@link #DRAW} if the board is full with no winner,
     *         or {@link #EMPTY} if the game is still in progress
     */
    public char getWinner() {
        if (this.hasWon(P1)) return P1;
        if (this.hasWon(P2)) return P2;
        if (!this.hasMove()) return DRAW;
        return EMPTY;
    }

    /**
     * Locates the four cells forming the winning line (if any).
     * Used by {@link ca.yorku.eecs3311.connect4.viewcontroller.BoardView}
     * to highlight the winning tokens with a green stroke.
     *
     * @return a 4-element array of {row, col} pairs, or {@code null} if no winner
     */
    public int[][] getWinningCells() {
        char[] players = {P1, P2};
        int[][] dirs = {{0,1},{1,0},{1,1},{1,-1}};
        for (char player : players) {
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    if (board[r][c] != player) continue;
                    for (int[] d : dirs) {
                        boolean found = true;
                        int[][] cells = new int[4][2];
                        for (int k = 0; k < 4; k++) {
                            int rr = r + k * d[0];
                            int cc = c + k * d[1];
                            if (!validCoordinate(rr, cc) || board[rr][cc] != player) {
                                found = false;
                                break;
                            }
                            cells[k] = new int[]{rr, cc};
                        }
                        if (found) return cells;
                    }
                }
            }
        }
        return null;
    }

    /** @return a multi-line ASCII representation of the board (useful for debugging) */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("  ");
        for (int c = 0; c < COLS; c++) sb.append(c).append(" ");
        sb.append("\n");
        for (int r = 0; r < ROWS; r++) {
            sb.append("|");
            for (int c = 0; c < COLS; c++) sb.append(this.board[r][c]).append("|");
            sb.append(" ").append(r).append("\n");
        }
        return sb.toString();
    }
}
