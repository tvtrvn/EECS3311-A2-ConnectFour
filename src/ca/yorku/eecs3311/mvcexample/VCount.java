package ca.yorku.eecs3311.mvcexample;

import ca.yorku.eecs3311.util.*;


import javafx.scene.control.Label;

public class VCount extends Label implements Observer {
	
	@Override
	public void update(Observable o) {
		MCounter mcounter = (MCounter)o;
		this.setText(""+mcounter.getCount());
	}
	
}
