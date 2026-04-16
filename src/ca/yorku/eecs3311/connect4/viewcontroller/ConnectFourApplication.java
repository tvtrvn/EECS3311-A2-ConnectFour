package ca.yorku.eecs3311.connect4.viewcontroller;

import ca.yorku.eecs3311.connect4.model.CommandManager;
import ca.yorku.eecs3311.connect4.model.ConnectFour;
import ca.yorku.eecs3311.connect4.model.ConnectFourBoard;
import ca.yorku.eecs3311.connect4.model.OpponentFactory;
import ca.yorku.eecs3311.connect4.model.Player;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * JavaFX entry point — wires together the <strong>MVC</strong> architecture.
 * <p>
 * This class acts as the <strong>Controller/Assembler</strong>: it creates the
 * Model ({@link ConnectFour}, {@link CommandManager}), the Views
 * ({@link BoardView}, {@link StatusView}, {@link ControlPanel}), and the
 * event handlers that bridge them. It also registers the views as
 * {@link ca.yorku.eecs3311.util.Observer}s on the model so they refresh
 * automatically via the Observer pattern.
 * </p>
 *
 * <p><strong>VM arguments for Eclipse / lab machines:</strong><br>
 * {@code --module-path "/usr/share/openjfx/lib" --add-modules javafx.controls,javafx.fxml}</p>
 */
public class ConnectFourApplication extends Application {
    // REMEMBER: To run this in the lab put
    // --module-path "/usr/share/openjfx/lib" --add-modules javafx.controls,javafx.fxml
    // in the run configuration under VM arguments.
	// For Mac I also unchecked XStartOnFirstThread argument

    @Override
    public void start(Stage stage) throws Exception {
        // ================================================================
        // MODEL — game state and command history (undo/redo)
        // ================================================================
        ConnectFour game = new ConnectFour();
        CommandManager commandManager = new CommandManager();

        // TODO A2-1: done
        // Decide whether this class should create ALL model objects directly,
        // or whether some of this setup belongs in helper/factory classes.


        // ================================================================
        // VIEWS — observe the model and render its state
        // ================================================================
        BoardView boardView = new BoardView();
        StatusView statusView = new StatusView();
        ControlPanel controlPanel = new ControlPanel();

        // TODO A2-2: done
        // Improve the GUI so the board uses tokens/images/circles instead of
        // plain button text. You may also add menus, labels, score views, etc.

        // ================================================================
        // CONTROLLERS — translate user actions into model operations
        // ================================================================
        Player opponent = null; // Human vs Human by default
        DropTokenEventHandler boardHandler = new DropTokenEventHandler(game, commandManager, opponent);

        // TODO A2-3: done
        // Consider whether one controller class is enough. You may want
        // separate controllers/handlers for board clicks, restart, save/load,
        // undo/redo, and opponent selection.

        // ================================================================
        // VIEW -> CONTROLLER hookup (each board cell delegates to boardHandler)
        // ================================================================
        for (int r = 0; r < ConnectFourBoard.ROWS; r++) {
            for (int c = 0; c < ConnectFourBoard.COLS; c++) {
                boardView.getCell(r, c).setOnAction(boardHandler);
            }
        }

        controlPanel.getRestartButton().setOnAction(e -> {
            commandManager.clear();
            game.reset();
        });

        controlPanel.getUndoButton().setOnAction(e -> commandManager.undo());
        controlPanel.getRedoButton().setOnAction(e -> commandManager.redo());

        // Strategy swap at runtime via OpponentFactory
        controlPanel.getOpponentChoice().setOnAction(e -> {
            String selected = controlPanel.getOpponentChoice().getValue();
            if ("Human".equalsIgnoreCase(selected)) {
                boardHandler.setOpponent(null);
            } else {
                boardHandler.setOpponent(OpponentFactory.create(selected, ConnectFourBoard.P2));
            }
        });

        // TODO A2-4: // Done
        // Add save/load buttons and connect them to controller logic.
        // Add any other GUI events required by your design.

        // Save handler — opens a FileChooser and delegates to model's save()
        controlPanel.getSaveButton().setOnAction(e -> {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Save Game");
            fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("Text Files", "*.txt"));
            java.io.File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                game.save(file.getAbsolutePath());
            }
        });

        // Load handler — restores state from file and clears undo history
        controlPanel.getLoadButton().setOnAction(e -> {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Load Game");
            fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("Text Files", "*.txt"));
            java.io.File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                game.load(file.getAbsolutePath());
                commandManager.clear();
            }
        });

        // ================================================================
        // MODEL -> VIEW hookup (Observer pattern)
        // ================================================================
        game.attach(boardView);
        game.attach(statusView);
        game.notifyObservers();

        // TODO A2-5: done
        // StatusView already provides observer-driven status + move count.

        // ================================================================
        // LAYOUT & STYLING
        // ================================================================
        VBox top = new VBox();
        top.setPadding(new Insets(10));
        top.getChildren().addAll(controlPanel, statusView);

        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(boardView);

        // TODO A2-6: done
        // Add CSS styling, images, a nicer window size, and any other UX
        // improvements your version of the assignment requires.
        stage.setMinWidth(520);
        stage.setMinHeight(580);
        root.setStyle("-fx-background-color: #1a1a2e;");
        boardView.setStyle("-fx-background-color:rgb(15, 57, 96); -fx-padding: 15; -fx-background-radius: 10;");
        statusView.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: white;");
        Scene scene = new Scene(root);
        stage.setTitle("Connect Four");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
