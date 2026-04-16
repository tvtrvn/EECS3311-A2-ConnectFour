package ca.yorku.eecs3311.connect4.viewcontroller;

import ca.yorku.eecs3311.connect4.model.ConnectFour;
import ca.yorku.eecs3311.connect4.model.ConnectFourBoard;
import ca.yorku.eecs3311.util.Observable;
import ca.yorku.eecs3311.util.Observer;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * The board <strong>View</strong> in the MVC architecture.
 * <p>
 * Implements {@link Observer} and is attached to the {@link ConnectFour} model.
 * Whenever the model calls {@link ca.yorku.eecs3311.util.Observable#notifyObservers()},
 * this view's {@link #update} method refreshes every cell's circle colour,
 * highlights the winning four cells (if any), and plays a drop animation for
 * the most recent token placement.
 * </p>
 */
public class BoardView extends GridPane implements Observer {
    private final CellView[][] cells = new CellView[ConnectFourBoard.ROWS][ConnectFourBoard.COLS];

    /** Builds a 6x7 grid of {@link CellView} buttons with circle graphics. */
    public BoardView() {
        this.setHgap(4);
        this.setVgap(4);
        this.setPadding(new Insets(10));
        this.setStyle("-fx-background-color: #0f3460; -fx-background-radius: 10;");
        for (int r = 0; r < ConnectFourBoard.ROWS; r++) {
            for (int c = 0; c < ConnectFourBoard.COLS; c++) {
                CellView cell = new CellView(r, c);
                cell.setText(" ");
                this.cells[r][c] = cell;
                this.add(cell, c, r);
            }
        }

        // TODO A2-7: done
        // Replace text-based cells with a richer visual design.
        // For example: colored circles, images, custom panes, or CSS classes.
    }

    /**
     * @param row board row
     * @param col board column
     * @return the {@link CellView} at the given position
     */
    public CellView getCell(int row, int col) {
        return this.cells[row][col];
    }

    /**
     * Called automatically by the Observer pattern whenever the model changes.
     * <ol>
     *   <li>Re-colours every circle (red for P1, yellow for P2, white for empty).</li>
     *   <li>If a player has won, highlights the winning cells with a lime-green stroke.</li>
     *   <li>If the last move cell is non-empty, plays a gravity-style drop animation.</li>
     * </ol>
     */
    @Override
    public void update(Observable observable) {
        ConnectFour game = (ConnectFour) observable;

        // Repaint all cells based on current board state
        for (int r = 0; r < ConnectFourBoard.ROWS; r++) {
            for (int c = 0; c < ConnectFourBoard.COLS; c++) {
                char token = game.getToken(r, c);
                this.cells[r][c].getCircle().setStroke(javafx.scene.paint.Color.LIGHTGRAY);
                this.cells[r][c].getCircle().setStrokeWidth(2);
                if (token == ConnectFourBoard.P1) {
                    this.cells[r][c].getCircle().setFill(javafx.scene.paint.Color.RED);
                } else if (token == ConnectFourBoard.P2) {
                    this.cells[r][c].getCircle().setFill(javafx.scene.paint.Color.YELLOW);
                } else {
                    this.cells[r][c].getCircle().setFill(javafx.scene.paint.Color.WHITE);
                }

                // TODO A2-8: done
                // Update the visual appearance of each cell here.
                // Example: set CSS style classes based on X / O / empty.
            }
        }
        // TODO A2-9: done
        // You may also wish to highlight the winning line or show the last move.

        // Highlight winning four-in-a-row cells
        int[][] winCells = game.getBoard().getWinningCells();
        if (winCells != null) {
            for (int[] pos : winCells) {
                this.cells[pos[0]][pos[1]].getCircle().setStroke(javafx.scene.paint.Color.LIME);
                this.cells[pos[0]][pos[1]].getCircle().setStrokeWidth(4);
            }
        }

        // Drop animation — only fires when the last-move cell actually has a token
        // (guards against undo/reset/load where lastMoveRow/Col may still be set
        // but the cell has been cleared)
        int lastRow = game.getLastMoveRow();
        int lastCol = game.getLastMoveCol();
        if (lastRow >= 0 && lastCol >= 0
                && game.getToken(lastRow, lastCol) != ConnectFourBoard.EMPTY) {
            Circle circle = this.cells[lastRow][lastCol].getCircle();
            double dropDistance = lastRow * (64 + this.getVgap());
            circle.setTranslateY(-dropDistance);
            TranslateTransition drop = new TranslateTransition(Duration.millis(150 + lastRow * 50), circle);
            drop.setFromY(-dropDistance);
            drop.setToY(0);
            drop.play();
        }
    }

}
