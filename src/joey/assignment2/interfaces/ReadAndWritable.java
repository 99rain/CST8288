package joey.assignment2.interfaces;

import java.util.List;



import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;


public interface ReadAndWritable extends Movable{
	/**		
	 * <p>
	 * this interface created for code reusing.
	 * use these static final variables to convert form and to string.</br>
	 * allowing changes for key words to be in one place.</br>
	 * </p>
	 */
     String POINTS_COUNT = "sides";
	 String IDENTIFIER = "ID";
	 String ANGLE = "angle";
	 String FILL = "fill";
	 String STROKE = "stroke";
	 String WIDTH = "strokeWidth";
	 String POINTS = "points";
	 String LOCKS = "locks";
	 
	 /**
	  * declare a readable method to retrieve a shape from a String format include attributes above.
	  * @param list String format of a PolyShape or CurvePath 
	  */
	void convertFromString( List< String> list);
	
	/**
	 *  declare a Writable method to transfer a shape into a String format include attributes above.
	 * @return String 
	 */
	String convertToString();
	
	/**
	 * <p>
	 * convert a {@link Paint} to a string in hex format followed by a space and alpha channel.</br>
	 * this method just calls {@link PolyShape#colorToString(Color)}.</br>
	 * </p>
	 * @param p - paint object to be converted
	 * @return string format of {@link Paint} in hex format plus alpha
	 */
	public default String colorToString( Paint p){
		if(p== null) return "null";
		return colorToString( Color.class.cast( p));
	}

	/**
	 * <p>
	 * convert a {@link Color} to a string in hex format followed by a space and alpha channel.</br>
	 * </p>
	 * @param c - color object to be converted
	 * @return string format of {@link Color} in hex format plus alpha
	 */
	public default String colorToString( Color c){
		return String.format( "#%02X%02X%02X %f",
				(int) (c.getRed() * 255),
				(int) (c.getGreen() * 255),
				(int) (c.getBlue() * 255),
				c.getOpacity());
	}

	/**
	 * <p>
	 * convert a string and given alpha to a {@link Color} object using {@link Color#web(String, double)}.</br>
	 * </p>
	 * @param color - hex value of a color in #ffffff
	 * @param alpha - alpha value of color between 0 and 1
	 * @return color object created from input
	 */
	public default Color stringToColor( String color, String alpha){
		return Color.web( color, Double.valueOf( alpha));
	}
}
