package joey.assignment2.model;

import javafx.scene.shape.Rectangle;
import joey.assignment2.interfaces.Highlightable;
import joey.assignment2.interfaces.Movable;

import java.util.function.Consumer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;


/**
 * due to mapArea never remove selectionArea among its children.
 * consider using singleton pattern to implement user's function.
 * it has a list to store Movables call toBeMoves.
 * it will call native method highlightList() to change style of Movables when has been clear(); 
 * @author JOEY
 *
 */
public class SelectionArea extends Rectangle{
	private static SelectionArea SELECTION;
	private Point2D start;
	private ObservableList<Movable> toBeMoves;
	
	private SelectionArea(){
		super();
		setOpacity( .4);
		setStrokeWidth( 2);
		setStroke( Color.VIOLET);
		setFill( Color.GAINSBORO);
		toBeMoves = FXCollections.observableArrayList();
	}
	public static SelectionArea getInstance() {
		if(SELECTION == null) SELECTION = new SelectionArea();
		return SELECTION;
	}
	public void start( double x, double y){
		start = new Point2D( x, y);
		setX( x);
		setY( y);
	}

	public void end( double x, double y){
		double width = x - start.getX();
		double height = y - start.getY();
		setX( width < 0 ? x : start.getX());
		setY( height < 0 ? y : start.getY());
		setWidth( Math.abs( width));
		setHeight( Math.abs( height));
	}

	public void clear(){
		setX( 0);
		setY( 0);
		setWidth( 0);
		setHeight( 0);
		highLightList();
	}

	public boolean contains(Node node){
		return getBoundsInLocal().contains( node.getBoundsInLocal());
	}

	public void containsAny( FilteredList<Node> filteredList, Consumer<Node> consumer){
		filteredList.filtered( this::contains).forEach( consumer);
	}
	
	
	public ObservableList<Movable> getList(){
		return toBeMoves;
	}
	public void justSelectShapeByClick(Movable node) {
		
		toBeMoves.add(node);
	}
	private void highLightList() {
		getList().filtered(Highlightable.class::isInstance).forEach(h->((Highlightable) h).setHighlight(true));
	}
}
