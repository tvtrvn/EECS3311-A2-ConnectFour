package ca.yorku.eecs3311.connect4.viewcontroller;

import ca.yorku.eecs3311.connect4.model.CommandManager;
import ca.yorku.eecs3311.connect4.model.ConnectFour;
import ca.yorku.eecs3311.connect4.model.DropTokenCommand;
import ca.yorku.eecs3311.connect4.model.Move;
import ca.yorku.eecs3311.connect4.model.Player;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * The primary <strong>Controller</strong> in the MVC architecture.
 * <p>
 * Handles click events from any {@link CellView} on the board. When the
 * human player clicks a cell, this handler:
 * <ol>
 *   <li>Extracts the column from the clicked cell.</li>
 *   <li>Wraps the move in a {@link DropTokenCommand} and executes it via
 *       the {@link CommandManager} (enabling undo/redo).</li>
 *   <li>If an AI opponent is active, immediately requests and executes
 *       the computer's response move.</li>
 * </ol>
 * A {@code processing} flag prevents re-entrant clicks while the computer
 * turn is being resolved.
 * </p>
 */
public class DropTokenEventHandler implements EventHandler<ActionEvent> {
    private final ConnectFour game;
    private final CommandManager commandManager;
    private Player opponent;
    /** Guard flag to ignore clicks while the computer's move is being processed. */
    private boolean processing;

    /**
     * @param game           the game model to mutate
     * @param commandManager the command history for undo/redo support
     * @param opponent       the AI strategy, or {@code null} for human-vs-human
     */
    public DropTokenEventHandler(ConnectFour game, CommandManager commandManager, Player opponent) {
        this.game = game;
        this.commandManager = commandManager;
        this.opponent = opponent;
        this.processing = false;
    }

    /**
     * Swaps the current AI opponent strategy at runtime.
     *
     * @param opponent the new strategy, or {@code null} to revert to human-vs-human
     */
    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

    @Override
    public void handle(ActionEvent event) {
        if (!(event.getSource() instanceof CellView)) return;
        if (this.game.isGameOver()) return;
        if (this.processing) return;

        CellView cell = (CellView) event.getSource();
        int selectedColumn = cell.getColIndex();

        // TODO A2-12: done — clicking any cell in a column drops in that column
        // TODO A2-13: done — input disabled during computer turn via processing flag;
        //             last move tracked in model via lastMoveRow/lastMoveCol

        this.processing = true;
        boolean moved = this.commandManager.execute(new DropTokenCommand(this.game, selectedColumn));
        if (moved) {
            this.maybeMakeComputerMove();
        }
        this.processing = false;
    }

    /** If an AI opponent is set and the game is still in progress, lets the AI take its turn. */
    private void maybeMakeComputerMove() {
        if (this.opponent == null || this.game.isGameOver()) return;
        Move move = this.opponent.getMove(this.game);
        if (move != null && !this.game.isGameOver()) {
            this.commandManager.execute(new DropTokenCommand(this.game, move.getColumn()));
        }

        // TODO A2-14: clean enough done
        // For a cleaner design, you may move opponent-turn logic elsewhere.
        // Example: a dedicated game controller or turn manager.
    }
}
