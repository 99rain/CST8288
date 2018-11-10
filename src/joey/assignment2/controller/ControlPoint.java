package joey.assignment2.controller;


import javafx.beans.value.ChangeListener;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import joey.assignment2.interfaces.Highlightable;
import joey.assignment2.interfaces.Locker;

/**
 * CONTROL POINT IS BASIC CONTROL UNIT OF EACH SHAPE.
 * @author JOEY YIN
 *
 */
public class ControlPoint extends Circle implements Locker, Highlightable{
	
	private Shape parent;
	
	

	public ControlPoint(double x, double y) {
		super(x, y, 5.5);
		this.setId("CPoint");
		

	}

	
	public Shape getParentShape() {
		return parent;
	}

	public void setParentShape(Shape parent) {
		this.parent = parent;
	}

	@Override
	public void translate(double x, double y) {
		setCenterX(getCenterX()+x);
		setCenterY(getCenterY()+y);
		
	}
 

	@Override
	public void lockPoint(ChangeListener<Number> x, ChangeListener<Number> y) {
		centerXProperty().addListener(x);
		centerYProperty().addListener(y);
		
	}

	@Override
	public void setHighlight(boolean turnOn) {
		setId(turnOn? "CPoint-highlighted":"CPoint");
	}

	
	
}
