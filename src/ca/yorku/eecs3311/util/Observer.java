package ca.yorku.eecs3311.util;

/**
 * Observer side of the <strong>Observer</strong> design pattern.
 * <p>
 * Classes that implement this interface (e.g.
 * {@link ca.yorku.eecs3311.connect4.viewcontroller.BoardView},
 * {@link ca.yorku.eecs3311.connect4.viewcontroller.StatusView})
 * register with an {@link Observable} and receive automatic callbacks
 * whenever the observed model's state changes.
 * </p>
 *
 * @see Observable
 * @see <a href="https://www.oodesign.com/observer-pattern.html">Observer Pattern (oodesign.com)</a>
 * @author student
 */
public interface Observer {
	/**
	 * Called by the {@link Observable} when its state changes.
	 *
	 * @param o the observable that triggered this update
	 */
	public void update(Observable o);
}
