package joey.assignment2.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.sun.xml.internal.ws.encoding.soap.SOAP12Constants;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import joey.assignment2.interfaces.ReadAndWritable;
import joey.assignment2.view.MapArea;
import joey.assignment2.controller.ControlPoint;


public class CurvePath extends CubicCurve implements ReadAndWritable{
	
	private int id;
    private List<Integer> locksIDs;
	private double angle;
	private ObservableList<Double> POLY_POINTS;
	private ObservableList<PolyShape> lockedShapes;
	private ControlPoint[] cPoints;
	
	/**
	 * constructor to initialize a CurvePath while drawing on mapArea.
	 * create instance just after mouse pressed. and parameters could be extracted from e.getX() and e.getY().
	 * @param startX 
	 * @param startY
	 */
	public CurvePath(double startX, double startY) {
		super();
		locksIDs = new ArrayList<>();
		lockedShapes = FXCollections.observableArrayList();
		POLY_POINTS = FXCollections.observableArrayList();
		setId("Path");
		setStartX(startX);
		setStartY(startY);
		
		setControlX1(getStartX());
		setControlY1(getStartY());
		setID(MapArea.shapeIdGenerator());
	}
	
	/**
	 * constructor to initialize a CurvePath while load map from file or other resources
	 * by given value of properties that are decode from List<String>
	 * 
	 * @param list
	 */
	public CurvePath(List<String> list) {
		locksIDs = new ArrayList<>();
		lockedShapes  = FXCollections.observableArrayList();
		POLY_POINTS  = FXCollections.observableArrayList();
		convertFromString(list);
		setUpPoints();
		registerControlPoints();
		checkLocks();
	}
	/**
	 * help method for constructor when load map.
	 */
	private void setUpPoints() {
		setStartX(POLY_POINTS.get(0));
		setControlX1(POLY_POINTS.get(0));
		setStartY(POLY_POINTS.get(1));
		setControlY1(POLY_POINTS.get(1));
		setEndX(POLY_POINTS.get(2));
		setControlX2(POLY_POINTS.get(2));
		setEndY(POLY_POINTS.get(3));
		setControlY2(POLY_POINTS.get(3));
	}
	
	public void registerControlPoints() {
		cPoints = new ControlPoint[2];
		
			cPoints[0] = new ControlPoint(getStartX(), getStartY());
			cPoints[1] = new ControlPoint(getEndX(), getEndY());
			
			cPoints[0].setParentShape(this);
			cPoints[1].setParentShape(this);
			
			
			cPoints[0].lockPoint((value, oldvalue, newValue) -> setStartX(newValue.doubleValue()),
					(value, oldvalue, newValue) -> setStartY(newValue.doubleValue()));
			
			cPoints[1].lockPoint((value, oldvalue, newValue) -> setEndX(newValue.doubleValue()),
					(value, oldvalue, newValue) -> setEndY(newValue.doubleValue()));
	}
	
	public void redrawPath(double startX, double startY, double endX, double endY) {
		setControlX2(endX);
		setControlY2(endY);
		angle = radianShift(startX, startY, endX, endY);
		setStartX(startX);setStartY(startY);setEndX(endX);setEndY(endY);
	}
	
	private double radianShift(double startX, double startY, double endX, double endY) {
		 return Math.atan2(endY - startY, endX - startX);
	}

	public ObservableList<PolyShape> getLockedShapes() {
		return lockedShapes;
	}

	public void addLockedShapes(PolyShape lockedShapes) {
		this.lockedShapes.add(lockedShapes);
	}

	public ControlPoint[] getControlPoints() {
		return cPoints;
	}
	
	
	public boolean isEmpty() {
		return this == null;
	}
	/**
	 * set lock relationship between poly shape and path
	 */
	public void checkLocks() {
		getLockedShapes().forEach(p -> {
			for (ControlPoint c : getControlPoints())
				if (p.getBoundsInLocal().contains(c.getCenterX(), c.getCenterY()))
					p.addLocker(c);
		});
	}
	
	/**
	 * set ID for connected shapes
	 */
	public void addIDEachOther() {
		PolyShape p0 = getLockedShapes().get(0);
		PolyShape p1 = getLockedShapes().get(1);
		// set for itself
		setLocksIDs(p0.getShapeID(),p1.getShapeID());
		
		//set for start and end shapes
		p0.setLocksIDs(this.getID());
		p1.setLocksIDs(this.getID());
	}
	
	public void setID(int id) {
		this.id = id;
	}
	public int getID() {
		return id;
	}
	public List<Integer> getLocksIDs() {
		return locksIDs;
	}

	public void setLocksIDs(Integer... locksIDs) {
		this.locksIDs.addAll(Arrays.asList(locksIDs)) ;
	}
	
	public String convertToString() {
		return String.format("sides %d%nID %d%nangle %f%nstrokeWidth %f%nfill %s %f%nstroke %s %f%npoints %s%nlocks %s%n",
				2, getID(), this.angle, getStrokeWidth(), getFill(), getOpacity(), getStroke(), getOpacity(), getPointsStatement(getControlPoints()),
				getLocksIDs().toString().replace("[", "").replace(",", "").replace("]", ""));
		
	}
	
		@Override
		public void convertFromString( List< String> list){
			
			list.forEach( line -> {
				String[] tokens = line.split( " ");
				switch( tokens[0]){
					case POINTS_COUNT:
					//	sides = Integer.valueOf( tokens[1]);
						break;
					case IDENTIFIER:
						setShapeID(Integer.valueOf( tokens[1]));
						break;
					case ANGLE:
						angle = Double.valueOf( tokens[1]);
						break;
					case FILL:
						 setFill( Color.TRANSPARENT);
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
	
	
	private void setShapeID(Integer id) {
			this.id = id;
		}
	/**
	 * help method to convert cPoint to String
	 * @param ps
	 * @return
	 */
	private String getPointsStatement(ControlPoint[] ps) {
		StringBuilder sb = new StringBuilder();
		Arrays.asList(ps).forEach(c->{
			sb.append(c.getCenterX() + " ");
			sb.append(c.getCenterY() + " ");
		});
		
		return sb.toString();
	}

	@Override
	public void translate(double x, double y) {
		getLockedShapes().forEach(m->m.translate(x, y));
	}
}
