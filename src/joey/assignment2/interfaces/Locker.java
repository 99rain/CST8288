package joey.assignment2.interfaces;

import javafx.beans.value.ChangeListener;

public interface Locker extends Movable{
	
	/**
	 * lock the point, make suer it will mover as long as locker move.
	 * @param x changeListener that show how x-coordinate of a point perform  
	 * @param y changeListener that show how x-coordinate of a point perform  
	 */
	void lockPoint(ChangeListener<Number> x, ChangeListener<Number> y);
	
	
}
