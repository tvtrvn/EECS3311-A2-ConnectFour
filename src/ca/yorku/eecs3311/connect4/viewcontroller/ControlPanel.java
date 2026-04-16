package ca.yorku.eecs3311.connect4.viewcontroller;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;

/**
 * Toolbar-style {@link HBox} containing the top-level GUI controls:
 * opponent selector, restart, undo, redo, save, and load.
 * <p>
 * This panel does not handle events itself — it exposes each control via
 * getters so that {@link ConnectFourApplication} can attach the appropriate
 * event handlers, maintaining MVC separation.
 * </p>
 */
public class ControlPanel extends HBox {
    private final ComboBox<String> opponentChoice = new ComboBox<String>();
    private final Button restartButton = new Button("Restart");
    private final Button undoButton = new Button("Undo");
    private final Button redoButton = new Button("Redo");

    // TODO A2-11: // done
    // Add Save and Load buttons and corresponding getters.
    // You may also want labels, score displays, player selectors, etc.
    private final Button saveButton = new Button("Save");
    private final Button loadButton = new Button("Load");

    public ControlPanel() {
        this.setSpacing(10);
        this.setPadding(new Insets(10));
        this.opponentChoice.getItems().addAll("Human", "Random", "Greedy", "Defensive");
        this.opponentChoice.getSelectionModel().select("Human");
        // this.getChildren().addAll(this.opponentChoice, this.restartButton, this.undoButton, this.redoButton);
        this.getChildren().addAll(this.opponentChoice, this.restartButton,
            this.undoButton, this.redoButton, this.saveButton, this.loadButton);
    }

    /** @return the Save button (handler attached in {@link ConnectFourApplication}) */
    public Button getSaveButton() {
        return this.saveButton;
    }

    /** @return the Load button (handler attached in {@link ConnectFourApplication}) */
    public Button getLoadButton() {
        return this.loadButton;
    }

    /** @return the opponent strategy drop-down (Human / Random / Greedy / Defensive) */
    public ComboBox<String> getOpponentChoice() {
        return this.opponentChoice;
    }

    /** @return the Restart button */
    public Button getRestartButton() {
        return this.restartButton;
    }

    /** @return the Undo button */
    public Button getUndoButton() {
        return this.undoButton;
    }

    /** @return the Redo button */
    public Button getRedoButton() {
        return this.redoButton;
    }
}
