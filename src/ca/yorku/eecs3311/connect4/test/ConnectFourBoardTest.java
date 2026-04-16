package ca.yorku.eecs3311.connect4.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.yorku.eecs3311.connect4.model.ConnectFourBoard;

/**
 * JUnit test suite for {@link ConnectFourBoard}.
 * <p>
 * Covers gravity-based token drops, all three win orientations (vertical,
 * horizontal, diagonal), full-column rejection, deep-copy independence,
 * and draw detection.
 * </p>
 *
 * TODO A2-22: done
 * Expanded board tests for win patterns, full columns, copy behaviour, and draw.
 */
public class ConnectFourBoardTest {

    /** Dropping into an empty column should land the token in the bottom row. */
    @Test
    public void testDropPlacesTokenAtBottom() {
        ConnectFourBoard board = new ConnectFourBoard();
        assertEquals(5, board.drop(0, ConnectFourBoard.P1));
        assertEquals(ConnectFourBoard.P1, board.get(5, 0));
    }

    /** Four tokens stacked in the same column should register as a win. */
    @Test
    public void testVerticalWin() {
        ConnectFourBoard board = new ConnectFourBoard();
        for (int i = 0; i < 4; i++) board.drop(0, ConnectFourBoard.P1);
        assertTrue(board.hasWon(ConnectFourBoard.P1));
    }

    /** Four tokens in consecutive columns (same row) should register as a win. */
    @Test
    public void testHorizontalWin() {
        ConnectFourBoard board = new ConnectFourBoard();
        for (int c = 0; c < 4; c++) board.drop(c, ConnectFourBoard.P1);
        assertTrue(board.hasWon(ConnectFourBoard.P1));
    }

    /** A diagonal line of four (set manually) should register as a win. */
    @Test
    public void testDiagonalWin() {
        ConnectFourBoard board = new ConnectFourBoard();
        board.set(5, 0, ConnectFourBoard.P1);
        board.set(4, 1, ConnectFourBoard.P1);
        board.set(3, 2, ConnectFourBoard.P1);
        board.set(2, 3, ConnectFourBoard.P1);
        assertTrue(board.hasWon(ConnectFourBoard.P1));
    }

    /** Dropping into a completely full column should return -1. */
    @Test
    public void testDropInFullColumnReturnsMinusOne() {
        ConnectFourBoard board = new ConnectFourBoard();
        for (int i = 0; i < ConnectFourBoard.ROWS; i++) {
            assertTrue(board.drop(0, ConnectFourBoard.P1) >= 0);
        }
        assertEquals(-1, board.drop(0, ConnectFourBoard.P2));
        assertFalse(board.columnHasSpace(0));
    }

    /** Mutating a copy should not affect the original board. */
    @Test
    public void testCopyIsIndependent() {
        ConnectFourBoard board = new ConnectFourBoard();
        board.drop(0, ConnectFourBoard.P1);
        ConnectFourBoard copy = board.copy();

        copy.drop(1, ConnectFourBoard.P2);
        assertEquals(ConnectFourBoard.EMPTY, board.get(5, 1));
        assertEquals(ConnectFourBoard.P2, copy.get(5, 1));
    }

    /** A completely filled board with no four-in-a-row should be detected as DRAW. */
    @Test
    public void testDrawBoardDetected() {
        ConnectFourBoard board = new ConnectFourBoard();
        char[][] draw = {
            {'X', 'X', 'O', 'O', 'X', 'X', 'O'},
            {'O', 'O', 'X', 'X', 'O', 'O', 'X'},
            {'X', 'X', 'O', 'O', 'X', 'X', 'O'},
            {'O', 'O', 'X', 'X', 'O', 'O', 'X'},
            {'X', 'X', 'O', 'O', 'X', 'X', 'O'},
            {'O', 'O', 'X', 'X', 'O', 'O', 'X'}
        };

        for (int r = 0; r < ConnectFourBoard.ROWS; r++) {
            for (int c = 0; c < ConnectFourBoard.COLS; c++) {
                board.set(r, c, draw[r][c]);
            }
        }

        assertFalse(board.hasWon(ConnectFourBoard.P1));
        assertFalse(board.hasWon(ConnectFourBoard.P2));
        assertEquals(ConnectFourBoard.DRAW, board.getWinner());
    }
}
