package ca.yorku.eecs3311.connect4.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import ca.yorku.eecs3311.connect4.model.CommandManager;
import ca.yorku.eecs3311.connect4.model.ConnectFour;
import ca.yorku.eecs3311.connect4.model.ConnectFourBoard;
import ca.yorku.eecs3311.connect4.model.DropTokenCommand;

/**
 * JUnit test suite for the {@link ConnectFour} game model.
 * <p>
 * Tests cover turn alternation, reset behaviour, illegal-move rejection,
 * game-over enforcement, snapshot-based undo/redo via the Command pattern,
 * and save/load round-trip persistence.
 * </p>
 */
public class ConnectFourTest {

    /** After one move, the turn should switch from P1 to P2. */
    @Test
    public void testMoveAlternatesTurn() {
        ConnectFour game = new ConnectFour();
        assertEquals(ConnectFourBoard.P1, game.getWhosTurn());
        assertTrue(game.move(0));
        assertEquals(ConnectFourBoard.P2, game.getWhosTurn());
    }

    /** Reset should restore the board to its initial empty state. */
    @Test
    public void testResetClearsBoardAndState() {
        ConnectFour game = new ConnectFour();
        game.move(0);
        game.move(1);
        game.reset();

        assertEquals(ConnectFourBoard.P1, game.getWhosTurn());
        assertEquals(0, game.getNumMoves());
        for (int r = 0; r < ConnectFourBoard.ROWS; r++) {
            for (int c = 0; c < ConnectFourBoard.COLS; c++) {
                assertEquals(ConnectFourBoard.EMPTY, game.getToken(r, c));
            }
        }
    }

    /** Out-of-range column indices should be rejected without changing state. */
    @Test
    public void testInvalidColumnMoveFails() {
        ConnectFour game = new ConnectFour();
        assertFalse(game.move(-1));
        assertFalse(game.move(ConnectFourBoard.COLS));
        assertEquals(0, game.getNumMoves());
        assertEquals(ConnectFourBoard.P1, game.getWhosTurn());
    }

    /** Once a player has won, no further moves should be accepted. */
    @Test
    public void testGameOverPreventsFurtherMoves() {
        ConnectFour game = new ConnectFour();
        game.getBoard().set(5, 0, ConnectFourBoard.P1);
        game.getBoard().set(5, 1, ConnectFourBoard.P1);
        game.getBoard().set(5, 2, ConnectFourBoard.P1);
        game.getBoard().set(5, 3, ConnectFourBoard.P1);

        assertTrue(game.isGameOver());
        assertEquals(ConnectFourBoard.P1, game.getWinner());
        assertFalse(game.move(4));
    }

    /** Undo should restore the snapshot; redo should re-apply the command. */
    @Test
    public void testCommandUndoRedoRestoresGameState() {
        ConnectFour game = new ConnectFour();
        CommandManager manager = new CommandManager();

        assertTrue(manager.execute(new DropTokenCommand(game, 0)));
        assertTrue(manager.execute(new DropTokenCommand(game, 1)));
        assertEquals(2, game.getNumMoves());
        assertEquals(ConnectFourBoard.P1, game.getWhosTurn());
        assertEquals(2, manager.getUndoCount());
        assertEquals(0, manager.getRedoCount());

        assertTrue(manager.undo());
        assertEquals(1, game.getNumMoves());
        assertEquals(ConnectFourBoard.P2, game.getWhosTurn());
        assertEquals(1, manager.getUndoCount());
        assertEquals(1, manager.getRedoCount());
        assertEquals(ConnectFourBoard.EMPTY, game.getToken(5, 1));

        assertTrue(manager.redo());
        assertEquals(2, game.getNumMoves());
        assertEquals(ConnectFourBoard.P1, game.getWhosTurn());
        assertEquals(2, manager.getUndoCount());
        assertEquals(0, manager.getRedoCount());
        assertEquals(ConnectFourBoard.P2, game.getToken(5, 1));
    }

    /** Saving then loading should reproduce the exact same board and metadata. */
    @Test
    public void testSaveLoadRoundTrip() throws IOException {
        ConnectFour original = new ConnectFour();
        original.move(0);
        original.move(1);
        original.move(0);

        File temp = File.createTempFile("connect4-", ".txt");
        temp.deleteOnExit();
        original.save(temp.getAbsolutePath());

        ConnectFour loaded = new ConnectFour();
        loaded.load(temp.getAbsolutePath());

        assertEquals(original.getWhosTurn(), loaded.getWhosTurn());
        assertEquals(original.getNumMoves(), loaded.getNumMoves());
        for (int r = 0; r < ConnectFourBoard.ROWS; r++) {
            for (int c = 0; c < ConnectFourBoard.COLS; c++) {
                assertEquals(original.getToken(r, c), loaded.getToken(r, c));
            }
        }
    }

    // TODO A2-23: done
    // Expanded tests for reset, illegal move, game-over, undo/redo, and save/load.
}
