package ca.yorku.eecs3311.mvcexample;

import ca.yorku.eecs3311.util.*;


import javafx.scene.control.Label;

public class VParity extends Label implements Observer {
	
	@Override
	public void update(Observable o) {
		MCounter mcounter = (MCounter)o;
		if(mcounter.getCount()%2==0) {
			this.setText("EVEN");
		} else {
			this.setText("ODD");
		}
	}
	
}
