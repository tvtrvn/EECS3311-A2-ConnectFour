package ca.yorku.eecs3311.connect4.model;

import java.util.Stack;

/**
 * Manages a history of {@link GameCommand} objects for undo/redo support.
 * <p>
 * Maintains two stacks:
 * <ul>
 *   <li><strong>undoStack</strong> – commands that have been executed and can be undone.</li>
 *   <li><strong>redoStack</strong> – commands that have been undone and can be re-executed.</li>
 * </ul>
 * Executing a new command clears the redo stack (no branching history).
 * </p>
 *
 * @see GameCommand
 */
public class CommandManager {
    private final Stack<GameCommand> undoStack = new Stack<GameCommand>();
    private final Stack<GameCommand> redoStack = new Stack<GameCommand>();

    /**
     * Executes the given command. If successful, pushes it onto the undo stack
     * and clears the redo stack (a new action invalidates the redo branch).
     *
     * @param command the command to execute
     * @return {@code true} if the command executed successfully
     */
    public boolean execute(GameCommand command) {
        boolean ok = command.execute();
        if (ok) {
            this.undoStack.push(command);
            this.redoStack.clear();
        }
        return ok;
    }

    /** @return {@code true} if there is at least one command to undo */
    public boolean canUndo() {
        return !this.undoStack.isEmpty();
    }

    /** @return {@code true} if there is at least one command to redo */
    public boolean canRedo() {
        return !this.redoStack.isEmpty();
    }

    /**
     * Undoes the most recent command, moving it from the undo stack to the redo stack.
     *
     * @return {@code true} if an undo was performed successfully
     */
    public boolean undo() {
        if (!this.canUndo()) return false;
        GameCommand command = this.undoStack.pop();
        boolean ok = command.undo();
        if (ok) this.redoStack.push(command);
        return ok;
    }

    /**
     * Re-executes the most recently undone command, moving it back to the undo stack.
     *
     * @return {@code true} if a redo was performed successfully
     */
    public boolean redo() {
        if (!this.canRedo()) return false;
        GameCommand command = this.redoStack.pop();
        boolean ok = command.execute();
        if (ok) this.undoStack.push(command);
        return ok;
    }

    /** Empties both stacks. Called on game restart or after loading a saved game. */
    public void clear() {
        this.undoStack.clear();
        this.redoStack.clear();
    }

    // TODO A2-18: done
    // Expose simple history counts for UI/testing if needed.

    /** @return the number of commands available for undo */
    public int getUndoCount() {
        return this.undoStack.size();
    }

    /** @return the number of commands available for redo */
    public int getRedoCount() {
        return this.redoStack.size();
    }
}
