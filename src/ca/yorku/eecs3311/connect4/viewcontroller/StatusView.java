package ca.yorku.eecs3311.connect4.viewcontroller;

import ca.yorku.eecs3311.connect4.model.ConnectFour;
import ca.yorku.eecs3311.util.Observable;
import ca.yorku.eecs3311.util.Observer;
import javafx.scene.control.Label;

/**
 * A status-bar {@link Label} that implements {@link Observer} to display
 * live game information: whose turn it is (or winner/draw), and the
 * current move count.
 * <p>
 * Text colour switches to red when the game is over to draw attention
 * to the result.
 * </p>
 */
public class StatusView extends Label implements Observer {
    /**
     * Refreshes the label text and colour whenever the model changes.
     *
     * @param observable the {@link ConnectFour} model that triggered the update
     */
    @Override
    public void update(Observable observable) {
        ConnectFour game = (ConnectFour) observable;
        String msg = game.getStatusMessage();
        msg += "  |  Move #" + game.getNumMoves();
        this.setText(msg);

        if (game.isGameOver()) {
            this.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #e94560;");
        } else {
            this.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: white;");
        }

        // TODO A2-15: done
        // Improve the status area.
        // You might show whose turn it is, winner, move number, hints, errors,
        // save/load messages, or strategy name.
    }
}
