package ca.yorku.eecs3311.connect4.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import ca.yorku.eecs3311.util.Observable;

/**
 * The <strong>Model</strong> in the MVC architecture for Connect Four.
 * <p>
 * Extends {@link Observable} so that attached {@link ca.yorku.eecs3311.util.Observer}
 * views (e.g. {@link ca.yorku.eecs3311.connect4.viewcontroller.BoardView},
 * {@link ca.yorku.eecs3311.connect4.viewcontroller.StatusView}) are notified
 * automatically whenever the game state changes.
 * </p>
 * <p>
 * Responsibilities include managing the board, turn order, move counting,
 * win/draw detection, undo/redo support (via snapshot copies), and
 * file-based save/load persistence.
 * </p>
 */
public class ConnectFour extends Observable {
    private ConnectFourBoard board;
    private char whosTurn;
    private int numMoves;
    /** Row of the most recent token drop; -1 when no move has been made yet. */
    private int lastMoveRow = -1;
    /** Column of the most recent token drop; -1 when no move has been made yet. */
    private int lastMoveCol = -1;

    // TODO A2-16: done
    // Add any additional model state you need.
    // Examples: move history, last move, game mode, scores, save metadata, etc.

    /**
     * Replaces this game's entire state with a deep copy of {@code other}.
     * Used by the Command pattern ({@link DropTokenCommand}) to implement
     * snapshot-based undo: the command saves a copy before executing, then
     * restores it on undo.
     *
     * @param other the snapshot to restore from
     */
    public void restoreFrom(ConnectFour other) {
        this.board = other.board.copy();
        this.whosTurn = other.whosTurn;
        this.numMoves = other.numMoves;
        this.lastMoveRow = other.lastMoveRow;
        this.lastMoveCol = other.lastMoveCol;
        this.notifyObservers();
    }

    /** Creates a new game with an empty board and P1 ('X') to move first. */
    public ConnectFour() {
        this.board = new ConnectFourBoard();
        this.whosTurn = ConnectFourBoard.P1;
        this.numMoves = 0;
    }

    /** @return the underlying board representation */
    public ConnectFourBoard getBoard() {
        return this.board;
    }

    /** @return {@code 'X'} or {@code 'O'} indicating whose turn it is */
    public char getWhosTurn() {
        return this.whosTurn;
    }

    /** @return the total number of tokens that have been dropped so far */
    public int getNumMoves() {
        return this.numMoves;
    }

    /**
     * @param row board row (0 = top)
     * @param col board column (0 = left)
     * @return the token at (row, col), or {@link ConnectFourBoard#EMPTY}
     */
    public char getToken(int row, int col) {
        return this.board.get(row, col);
    }

    /**
     * Attempts to drop the current player's token into {@code col}.
     * If the move is legal, updates the board, increments move count,
     * records the landing position, switches turns (unless the game just
     * ended), and notifies all observers.
     *
     * @param col the column to drop into (0-indexed)
     * @return {@code true} if the move succeeded, {@code false} if the
     *         column is full, out of range, or the game is already over
     */
    public boolean move(int col) {
        if (this.isGameOver()) return false;
        int row = this.board.drop(col, this.whosTurn);
        if (row < 0) return false;
        this.lastMoveRow = row;
        this.lastMoveCol = col;
        this.numMoves++;
        if (!this.isGameOver()) {
            this.whosTurn = this.board.otherPlayer(this.whosTurn);
        }
        this.notifyObservers();
        return true;
    }

    /** @return the row where the last token landed, or -1 if none */
    public int getLastMoveRow() {
        return this.lastMoveRow;
    }

    /** @return the column where the last token was dropped, or -1 if none */
    public int getLastMoveCol() {
        return this.lastMoveCol;
    }

    /** Clears the board, resets turn to P1, zeroes move count, and notifies observers. */
    public void reset() {
        this.board.clear();
        this.whosTurn = ConnectFourBoard.P1;
        this.numMoves = 0;
        this.lastMoveRow = -1;
        this.lastMoveCol = -1;
        this.notifyObservers();
    }

    /** @return {@code true} if a player has won or the board is full (draw) */
    public boolean isGameOver() {
        return this.board.getWinner() != ConnectFourBoard.EMPTY;
    }

    /**
     * @return {@link ConnectFourBoard#P1}, {@link ConnectFourBoard#P2},
     *         {@link ConnectFourBoard#DRAW}, or {@link ConnectFourBoard#EMPTY}
     */
    public char getWinner() {
        return this.board.getWinner();
    }

    /** @return a human-readable status string suitable for the {@link ca.yorku.eecs3311.connect4.viewcontroller.StatusView} */
    public String getStatusMessage() {
        char winner = this.getWinner();
        if (winner == ConnectFourBoard.P1) return "Player X wins";
        if (winner == ConnectFourBoard.P2) return "Player O wins";
        if (winner == ConnectFourBoard.DRAW) return "Draw";
        return "Player " + this.whosTurn + " to move";
    }

    /**
     * Creates a deep copy of this game (board, turn, move count, last move).
     * Used for snapshot-based undo/redo and AI look-ahead simulations.
     *
     * @return an independent copy of this game state
     */
    public ConnectFour copy() {
        ConnectFour copy = new ConnectFour();
        copy.board = this.board.copy();
        copy.whosTurn = this.whosTurn;
        copy.numMoves = this.numMoves;
        copy.lastMoveRow = this.lastMoveRow;
        copy.lastMoveCol = this.lastMoveCol;
        return copy;
    }

    // TODO A2-17: done
    // Add save/load methods or move this responsibility to separate classes.

    /**
     * Persists the current game state to a plain-text file.
     * <p>Format: line 1 = whose turn, line 2 = move count,
     * lines 3–8 = board rows (each row is 7 chars).</p>
     *
     * @param filename the path to write to
     */
    public void save(String filename) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.println(this.whosTurn);
            pw.println(this.numMoves);
            for (int r = 0; r < ConnectFourBoard.ROWS; r++) {
                for (int c = 0; c < ConnectFourBoard.COLS; c++) {
                    pw.print(this.board.get(r, c));
                }
                pw.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads a previously saved game state from a plain-text file and
     * notifies observers so the GUI refreshes.
     *
     * @param filename the path to read from
     */
    public void load(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            this.whosTurn = br.readLine().charAt(0);
            this.numMoves = Integer.parseInt(br.readLine().trim());
            for (int r = 0; r < ConnectFourBoard.ROWS; r++) {
                String line = br.readLine();
                for (int c = 0; c < ConnectFourBoard.COLS; c++) {
                    this.board.set(r, c, line.charAt(c));
                }
            }
            this.notifyObservers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
