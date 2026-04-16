package ca.yorku.eecs3311.connect4.model;

/**
 * Command pattern interface for game actions that support undo/redo.
 * <p>
 * Each concrete command (e.g. {@link DropTokenCommand}) encapsulates a
 * single reversible operation. The {@link CommandManager} maintains stacks
 * of executed and undone commands to provide full undo/redo history.
 * </p>
 *
 * @see CommandManager
 * @see DropTokenCommand
 */
public interface GameCommand {
    /**
     * Performs the action and saves enough state to reverse it later.
     *
     * @return {@code true} if the action succeeded
     */
    boolean execute();

    /**
     * Reverses the effect of a prior {@link #execute()} call.
     *
     * @return {@code true} if the undo succeeded
     */
    boolean undo();

    /** @return a short description of this command (e.g. "DropToken(3)") */
    String getName();
}
