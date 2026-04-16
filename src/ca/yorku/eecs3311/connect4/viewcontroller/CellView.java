package ca.yorku.eecs3311.connect4.viewcontroller;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * A single clickable cell in the {@link BoardView} grid.
 * <p>
 * Each cell is a JavaFX {@link Button} containing a {@link Circle} graphic.
 * The circle's fill colour is updated by {@link BoardView#update} to reflect
 * the token occupying this position (red / yellow / white). Clicking any cell
 * in a column triggers a drop into that column via the shared
 * {@link DropTokenEventHandler}.
 * </p>
 */
public class CellView extends Button {
    private final int row;
    private final int col;
    private final Circle circle;

    /**
     * @param row the board row this cell represents (0 = top)
     * @param col the board column this cell represents (0 = left)
     */
    public CellView(int row, int col) {
        this.row = row;
        this.col = col;
        this.circle = new Circle(25);
        this.circle.setFill(Color.WHITE);
        this.circle.setStroke(Color.LIGHTGRAY);
        this.circle.setStrokeWidth(2);
        this.setGraphic(this.circle);
        this.setText("");
        this.setMinSize(64, 64);
        this.setPrefSize(64, 64);
        this.setFocusTraversable(false);
        this.setStyle("-fx-background-color: #0f3460; -fx-border-color: transparent;");

        // TODO A2-10: // done
        // Add style classes, graphics, tooltips, hover behaviour, etc.
    }

    /** @return the circle graphic whose fill is changed to show tokens */
    public Circle getCircle() {
        return this.circle;
    }

    /** @return this cell's board row index */
    public int getRowIndex() {
        return this.row;
    }

    /** @return this cell's board column index (used by the event handler to determine which column to drop into) */
    public int getColIndex() {
        return this.col;
    }
}
