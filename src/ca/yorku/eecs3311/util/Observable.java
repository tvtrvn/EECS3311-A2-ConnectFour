package ca.yorku.eecs3311.util;

import java.util.ArrayList;
/**
 * Subject side of the <strong>Observer</strong> design pattern.
 * <p>
 * Any class that extends {@code Observable} (e.g. {@link ca.yorku.eecs3311.connect4.model.ConnectFour})
 * can register {@link Observer} instances and broadcast state changes to them
 * via {@link #notifyObservers()}. This decouples the Model from the Views in
 * the MVC architecture.
 * </p>
 *
 * @see Observer
 * @see <a href="https://www.oodesign.com/observer-pattern.html">Observer Pattern (oodesign.com)</a>
 * @author student
 */
public class Observable {
	private ArrayList<Observer> observers = new ArrayList<Observer>();

	/** Registers an observer that will be notified on state changes. */
	public void attach(Observer o) {
		observers.add(o);
	}

	/** Removes a previously registered observer. */
	public void detach(Observer o) {
		observers.remove(o);
	}

	/** Notifies all attached observers by calling {@link Observer#update(Observable)}. */
	public void notifyObservers() {
		for(Observer o:observers) {
			o.update(this);
		}
	}
	
}
