package joey.assignment2.interfaces;


/**
 *  declare an interface to move operation
 * @author Joey Yin
 *
 */
public interface Movable {
	/**
	 * 
	 * @param x moving distance from last place in x-coordinate
	 * @param y moving distance from last place in y-coordinate
	 */
	void translate(double x, double y);
}
