package joey.assignment2.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.shape.Polygon;
import joey.assignment2.interfaces.ReadAndWritable;
import joey.assignment2.view.MapArea;
import joey.assignment2.controller.ControlPoint;
import joey.assignment2.interfaces.Highlightable;
import joey.assignment2.interfaces.Locker;


public class PolyShape extends Polygon implements Locker, Highlightable, ReadAndWritable {
	
	

	
	protected final ObservableList<Double> POLY_POINTS;
	private Integer id; // identifier of this polyShape
	private int sides;	// sides of polyShape
	private double angle; //the angle of direction when drag the mouse
	private double dx, dy; // distance between points and the center point
	private double centerX, centerY; // Coordinator X and Y of this polyShape's centerPoint
	protected ControlPoint[] cPoints; //controlPoints stand on the points of polygon that be able to move this shape
	
	protected ObservableList<ControlPoint> locks; //controlPoints of Path which locked this shape
	private ArrayList<Integer> locksIDs = new ArrayList<>(); // other shapes locked with this shape by path

	// Default constructor for irregular shapes created by clicking multiple
	// locations.
	public PolyShape(){
		this(0);
	}

	public PolyShape(int sides) {
		POLY_POINTS = getPoints();
		locks = FXCollections.observableArrayList();
		this.sides = sides;
		id = MapArea.shapeIdGenerator();
		setId("shape");

	}

	/**
	 * <p>
	 * create a PolyShape from given list of strings.</br>
	 * each row will contain one property and it is separated by spaces.</br>
	 * </p>
	 * @param list - list of string representing a PolyShape
	 */
	public PolyShape( List< String> list){
		POLY_POINTS = getPoints();
		convertFromString( list);
		//shape is complete so registerControlPoints is called in constructor
		registerControlPoints();
		locks = FXCollections.observableArrayList();
	}

	private void setShapeID(Integer id) {
		this.id = id;
	}
	

	public Integer getShapeID() {
		return id;
	}

	private void cacluatePoints() {
		for (int side = 0; side < sides; side++) {
			POLY_POINTS.addAll(centerX + point(Math::cos, dx / 2, angle, side, sides),
					centerY + point(Math::sin, dy / 2, angle, side, sides));
		}
	}

	private double radianShift(double x1, double y1, double x2, double y2) {
		return Math.atan2(y2 - y1, x2 - x1);
	}

	private double point(DoubleUnaryOperator operation, double radius, double shift, double side, final int SIDES) {
		return radius * operation.applyAsDouble(shift + side * 2.0 * Math.PI / SIDES);
	}

	public void registerControlPoints() {
		// to register control points create an array of control points,
		cPoints = new ControlPoint[sides];
		// have in mind every two points the polygon class getPoints() counts as one
		// control point.
		// loop through all points of polygon getPoints() index by index,
		for (int i = 0; i < sides*2; i += 2) {
			cPoints[i / 2] = new ControlPoint(POLY_POINTS.get(i), POLY_POINTS.get(i + 1));

			// set relationship between control points and the polyshape.
			cPoints[i / 2].setParentShape(this);
			// for every two indices manually add a ChangeListener to centerXProperty and
			// centerYProperty
			// of your control point which extends Circle.
			// each ChangeListener will updated the corresponding index inside of the
			// Polygon getPoints().

			final int index = i;// get rid of the error
			cPoints[i / 2].lockPoint((value, oldvalue, newValue) -> POLY_POINTS.set(index, newValue.doubleValue()),
					(value, oldvalue, newValue) -> POLY_POINTS.set(index + 1, newValue.doubleValue()));
		}

	}
	/*public void cancelControlPoints() {
		for(ControlPoint c : getControlPoints()) {
			
		}
	}*/

	/**
	 * measure the distance between 2 points
	 */
	private double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}

	public void reDraw(double x1, double y1, double x2, double y2, boolean symmetrical) {
		// using radianShift to measure the drawing angle
		angle = radianShift(x1, y1, x2, y2);
		// if shape is symmetrical measure the distance between x1,y1 and x2,y2 and
		// assign it to dx and dy
		if (symmetrical) {
			dx = distance(x1, y1, x2, y2);
			dy = distance(x1, y1, x2, y2);
		}
		// if not dx is difference between x1 and x2 and dy is difference between y1 and
		// y2
		else {
			dx = Math.abs(x2 - x1);
			dy = Math.abs(y2 - y1);
		}
		// calculate the center of your shape:
		// x1 is x1 plus half the difference between x1 and x2
		this.centerX = x1 + (x2 - x1) * 0.5;
		// y1 is y1 plus half the difference between y1 and y2
		this.centerY = y1 + (y2 - y1) * 0.5;
		// clear points
		POLY_POINTS.clear();
		// call calculate
		cacluatePoints();

	}

	public String getInfoStatement(List<? extends Object> list) {
		StringBuilder sb = new StringBuilder();
		list.forEach(i-> sb.append(i +" "));
		return sb.toString();
	}
	public boolean isEmpty() {
		return POLY_POINTS.isEmpty();
	}

	public ControlPoint[] getControlPoints() {
		return cPoints;
	}

	

	
	
	public ArrayList<Integer> getLocksIDs(){
		return this.locksIDs;
	}
	
	public void setLocksIDs(Integer... ids) {
		this.locksIDs.addAll(Arrays.asList(ids));
	}
	
	
	/**
	 * <p>
	 * convert current object to a string.</br>
	 * each property is located in one line separated by {@link System#lineSeparator()}.</br>
	 * each line starts with a name of property and its value/s in front of it all separated by space.</br>
	 * </p>
	 * @return a single string with explained format.
	 */
	public String convertToString(){
		String newLine = System.lineSeparator();
		StringBuilder builder = new StringBuilder();
		
		builder.append( POINTS_COUNT).append(" ").append(sides).append(newLine);
		builder.append(IDENTIFIER).append(" ").append(getShapeID()).append(newLine);
		builder.append(ANGLE).append(" ").append(angle).append(newLine);
		builder.append( WIDTH).append(" ").append( getStrokeWidth()).append( newLine);
		builder.append( FILL).append(" ").append( colorToString( getFill())).append(" ").append(getOpacity()).append( newLine);
		builder.append( STROKE).append(" ").append( colorToString( getStroke())).append(" ").append(getOpacity()).append( newLine);
		//join every point in POLY_POINTS and add to builder 
		builder.append( POINTS).append(" ").append( POLY_POINTS.stream().map(e -> Double.toString(e)).collect( Collectors.joining(" "))).append(newLine);
		builder.append(LOCKS).append(" ").append(locksIDs.stream().map(e->Integer.toString(e)).collect(Collectors.joining(" ")));
		return builder.toString();
	}

	/**
	 * <p>
	 * convert array of strings to a PolyShape. called from constructor.</br>
	 * each property is located in one index of the list.</br>
	 * each index starts with a name of property and its value/s in front of it all separated by space.</br>
	 * </p>
	 * @param list - a list of properties for this shape
	 */
	
	public void convertFromString( List< String> list){
		list.forEach( line -> {
			String[] tokens = line.split( " ");
			switch( tokens[0]){
				case POINTS_COUNT:
					sides = Integer.valueOf( tokens[1]);
					break;
				case IDENTIFIER:
					setShapeID(Integer.valueOf( tokens[1]));
					break;
				case ANGLE:
					angle = Double.valueOf( tokens[1]);
					break;
				case FILL:
					 setFill( stringToColor( tokens[1], tokens[2]));
					break;
				case STROKE:
					setStroke( stringToColor( tokens[1], tokens[2]));
					break;
				case WIDTH:
					setStrokeWidth( Double.valueOf( tokens[1]));
					break;
				case POINTS:
					//create a stream of line.split( " ") and skip the first element as it is the name, add the rest to POLY_POINTS
					Stream.of( tokens).skip(1).mapToDouble( Double::valueOf).forEach( POLY_POINTS::add);
					break;
				case LOCKS:
					//for(int i = 0 ; i<tokens.length ; i ++) System.out.print(tokens[i]+"\t"); System.out.println();
					Stream.of(tokens).skip(1).mapToInt( Integer::valueOf).forEach(locksIDs::add);
					break;
				default:
					throw new UnsupportedOperationException( "\"" + tokens[0] + "\" is not supported");
			}
		});
	}

	public int getSide() {
		return this.sides;
	}

	public void setSide(int side) {
		this.sides = side;
	}
	
	public double getAngle() {
		return this.angle;
	}

	// pass a point to be locked in the shape
	public void addLocker(ControlPoint locker) {
		locks.add(locker);

	}


	

	/**
	 *  for bonus: allow control points to be erased. 
	 *  this will also remove the point attached to control point, reducing the number of sides.
	 * @param t
	 */
	public void reduce(ControlPoint t) {
		getPoints().removeAll(t.getCenterX(), t.getCenterY());
		setSide(getSide()-1);
		registerControlPoints();
	}

	@Override
	public void setHighlight(boolean turnOn) {
		setId(turnOn ? "shape-highlighted" : "shape");
		
	}

	@Override
	public void translate(double x, double y) {
		Arrays.asList(getControlPoints()).forEach(n->n.translate(x, y));
		locks.forEach(c->c.translate(x, y));
	}

	@Override
	public void lockPoint(ChangeListener<Number> x, ChangeListener<Number> y) {
		new SimpleDoubleProperty(centerX).addListener(x);
		new SimpleDoubleProperty(centerY).addListener(y);
		
	}

	
}
