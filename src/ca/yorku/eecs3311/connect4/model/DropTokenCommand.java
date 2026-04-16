package ca.yorku.eecs3311.connect4.model;

/**
 * Concrete {@link GameCommand} that drops a token into one column.
 * <p>
 * Uses a <em>snapshot-based</em> undo strategy: before executing, a full
 * deep copy of the {@link ConnectFour} model is saved. On undo, the entire
 * game state is restored from that snapshot via {@link ConnectFour#restoreFrom}.
 * This is simpler and more robust than manually reversing individual fields,
 * especially when the model tracks derived state like last-move position and
 * turn order.
 * </p>
 */
public class DropTokenCommand implements GameCommand {
    private final ConnectFour game;
    private final int column;
    /** Snapshot of the full game state captured <em>before</em> this move. */
    private ConnectFour snapshot;

    /**
     * @param game   the live game model this command will mutate
     * @param column the column to drop the current player's token into
     */
    public DropTokenCommand(ConnectFour game, int column) {
        this.game = game;
        this.column = column;
    }

    @Override
    public boolean execute() {
        this.snapshot = this.game.copy();
        return this.game.move(this.column);
    }

    @Override
    public boolean undo() {
        this.game.restoreFrom(this.snapshot);
        return true;
    }

    @Override
    public String getName() {
        return "DropToken(" + this.column + ")";
    }

    // TODO A2-19: done
    // Consider whether undo should restore a full snapshot or explicit row/token
    // information rather than just removing from the same column.
}