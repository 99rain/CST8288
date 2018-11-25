package joey.assignment2.view;

import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javafx.collections.ObservableList;
import javafx.event.EventTarget;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import joey.assignment2.controller.ControlPoint;
import joey.assignment2.enums.ShapeType;
import joey.assignment2.enums.Tool;
import joey.assignment2.interfaces.ReadAndWritable;
import joey.assignment2.interfaces.Highlightable;
import joey.assignment2.interfaces.Movable;
import joey.assignment2.model.CurvePath;
import joey.assignment2.model.PolyShape;
import joey.assignment2.model.SelectionArea;
/**
 * this class to create map and receive update from toolstate as observer
 * @author joey yin
 *
 */
public final class MapArea extends Pane implements Observer {
	/**
	 * ID generator
	 */
	private static AtomicInteger IDgenerator = new AtomicInteger(0);
	/**
	 * store the shapes to avoid calling this.getChildren() whenever adding the node
	 */

	private ObservableList<Node> children;
	/**
	 * currently shape operation
	 */
	private PolyShape active;

	/**
	 * store the cursor
	 */
	private double startX, startY;
	/**
	 * singleton patterned rectangle for selection
	 */
	private SelectionArea slcArea;

	/**
	 * Path has two controlPoint and two CurvedTo points which stay with start point
	 * and end point
	 */
	private CurvePath curvedPath;

	/**
	 * tool shows option for left tool bar to be used for status
	 */
	private Tool tool;

	/**
	 * used to store the number of the current shape side
	 */
	private int shapeSide;
	/**
	 * labels on status bar to be set on the bottom of rootPane
	 */
	private Label toolInput, shapeInput, xInput,yInput;

	// constructor
	public MapArea() {
		children = getChildren();
		
		registerMouseEvent();

		setId("mapArea");// just set background-color

	}

	// registerEvents
	private void registerMouseEvent() {
		this.addEventHandler(MouseEvent.MOUSE_PRESSED, this::mousePressedHandler);
		this.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::mouseDraggedHandler);
		this.addEventHandler(MouseEvent.MOUSE_RELEASED, this::mouseReleasedHandler);
		this.addEventHandler(MouseEvent.MOUSE_MOVED, this::updateMouseCursor);

	}
	
	// distribute ID for shapes
	public static int shapeIdGenerator() {
		return IDgenerator.getAndIncrement();

	}

	private void mousePressedHandler(MouseEvent e) {
		startX = e.getX();
		startY = e.getY();
		EventTarget shape = e.getTarget();// for path or curved path
		if (tool != null)
			switch (tool) {
			case SELECT:
				// store the mouse-cursor start point to draw a slcArea
				slcArea = SelectionArea.getInstance();
				if (!children.contains(slcArea))
					children.add(slcArea);
				slcArea.start(startX, startY);
				break;
			case MOVE:
				setCursor(Cursor.MOVE);
				//set up highlight before read to move
				if (e.getTarget() instanceof Highlightable)
					((Highlightable) e.getTarget()).setHighlight(true);

				break;
			case ROOM:
				setCursor(Cursor.CROSSHAIR);
				// BNOUS: -1 stands for create irregular shapes created by clicking multiple
				// locations.
				if (shapeSide == -1) {
					active.getPoints().addAll(e.getX(), e.getY());
					active.setSide(active.getPoints().size() / 2);
				} else {
					active = new PolyShape(shapeSide);
					children.add(active);
				}

				break;

			case CURVEDPATH:
					//in case only mouse pick a shape, then create a path
				if (shape != null && shape instanceof PolyShape) {
					curvedPath = new CurvePath(e.getX(), e.getY());
					curvedPath.getLockedShapes().add(PolyShape.class.cast(shape));
					children.add(curvedPath);
				}
				break;
			case ERASE:

				break;
			case DOOR:

				break;
			default:
				throw new UnsupportedOperationException("Cursor for Tool \"" + tool.name() + "\" is not implemneted");
			}

	}

	private void mouseDraggedHandler(MouseEvent e) {
		double xp = e.getX(), yp = e.getY();
		if (tool != null)
			switch (tool) {
			case SELECT:
				// Continuing to find endPoint for the slcArea
				slcArea.end(e.getX(), e.getY());
				break;
			case MOVE:
				/**
				 * if has something to be moved in selected list.
				 */
				if (slcArea != null && !slcArea.getList().isEmpty()) {
					/**
					 * cause of poly shape's translate method have already moved control point
					 * filter selected control points to avoid being moved double times.
					 */
					slcArea.getList().filtered(ControlPoint.class::isInstance)
					.forEach(m -> m.translate(e.getX() - startX, e.getY() - startY));
				/**
				 * else if just move something by picking
				 */
				} else if (e.getTarget() instanceof Movable) {
					Movable.class.cast(e.getTarget()).translate(e.getX() - startX, e.getY() - startY);
				} else// just in case clicked map
					{return;}
				startX = e.getX();
				startY = e.getY();
				break;
			case ROOM:
					if(shapeSide == -1) {return;}else {
					System.out.println(shapeSide);
					active.reDraw(startX, startY, e.getX(), e.getY(), true);}
				break;
			case CURVEDPATH:
				if (curvedPath != null)curvedPath.redrawPath(startX, startY, xp, yp);
				break;
			case ERASE:

				break;
			case DOOR:

				break;
			default:
				throw new UnsupportedOperationException("Drag for Tool \"" + tool.name() + "\" is not implemneted");
			}
		e.consume();
	}

	private void mouseReleasedHandler(MouseEvent e) {
		if (tool != null)
			switch (tool) {
			case SELECT:
				//when click white space , clear the list.
				if (startX == e.getX() && startY == e.getY() && !(e.getTarget() instanceof Movable)) {
					slcArea.getList().stream().filter(Highlightable.class::isInstance).map(Highlightable.class::cast).forEach(h->h.setHighlight(false));
					slcArea.getList().clear();}
				// Bonus: select one node only without drawing the box in one click.
				if (startX == e.getX() && startY == e.getY() && e.getTarget() instanceof Movable) 
					slcArea.justSelectShapeByClick((Movable) e.getTarget());
				slcArea.containsAny(children.filtered(m -> m instanceof Movable),
						n -> slcArea.getList().add((Movable) n));
				slcArea.clear();
				
				break;
			case MOVE:
				children.filtered(Highlightable.class::isInstance)
						.forEach(n -> ((Highlightable) n).setHighlight(false));
				if (slcArea != null)
					slcArea.getList().clear();
				break;
			case ROOM:
				if (shapeSide == -1) {// used to draw irregular polygon
					if (active.getSide() > 0) {
						if (active.getControlPoints() != null)
							children.removeAll(active.getControlPoints());
						active.registerControlPoints();
						if (children != null && active != null && active.getControlPoints() != null)
							children.addAll(active.getControlPoints());
						return;
					} else
						return;
				}
				if (active != null) {
					if (active.isEmpty()) {
						children.remove(active);
					} else {

						active.registerControlPoints();
						children.addAll(active.getControlPoints());

					}
					active = null;
				}
				break;
			case CURVEDPATH:
				children.stream().filter(node -> !(node instanceof CurvePath))// end shape must not be Path
						.filter(PolyShape.class::isInstance)// it is a PolyShape
						.map(PolyShape.class::cast)// cast Type
						.forEach(s -> { // just assign the top one if multiple shape stacked
							if (s.contains(e.getX(), e.getY()))
								curvedPath.getLockedShapes().add(s);
						});

				if (curvedPath != null) {
					curvedPath.registerControlPoints();
					children.addAll(curvedPath.getControlPoints());
					curvedPath.checkLocks();
					curvedPath.addIDEachOther();
				}

				break;
			case ERASE:
				EventTarget t = e.getTarget();
				children.remove(t);
				// erase selected shape
				if (t instanceof PolyShape) {
					children.removeAll(PolyShape.class.cast(t).getControlPoints());
				} else if (t instanceof CurvePath) {
					children.removeAll(CurvePath.class.cast(t).getControlPoints());
				}
				// Bonus: allow control points to be erased.
				// this will also remove the point attached to control point, reducing the
				// number of sides.
				else if (t instanceof ControlPoint) {
					ControlPoint controlPoint = ((ControlPoint) t);
					PolyShape temp = (PolyShape) controlPoint.getParentShape();
					children.remove(temp);
					children.removeAll(temp.getControlPoints());
					temp.reduce(controlPoint);
					children.add(temp);
					children.addAll(temp.getControlPoints());
				}

				break;
			case DOOR:

				break;
			default:
				throw new UnsupportedOperationException("Release for Tool \"" + tool.name() + "\" is not implemneted");
			}
		e.consume();
	}
	/**
	 * when command is draw irregular shape
	 * to avoid duplicate adding shape into children, initialize here.
	 */
	public void setIrregShape() {
		active = new PolyShape();
		children.add(active);
	}
	
	
	/**
	 * clear map
	 */
	public void clearMap() {
		children.clear();

	}
	
	
	/**
	 * when load map from file ,need this help method to find shape by given ID
	 * @param id
	 * @return
	 */
	public PolyShape findShapeByID(int id) {
		PolyShape locks = null;
		for (Node s : children) {
			if (s instanceof PolyShape && !(s instanceof CurvePath)) {
				PolyShape polyShapeSkeleton = PolyShape.class.cast(s);
				if (polyShapeSkeleton.getShapeID() == id) {
					locks = polyShapeSkeleton;
					break;
				}
			}
		}
		return locks;
	}

	/**
	 * <p>
	 * create all shapes that are stored in given map. each key contains one list
	 * representing on PolyShape.</br>
	 * </p>
	 * 
	 * @param map - a data set which contains all shapes in this object.
	 */
	public void convertFromString(Map<Object, List<String>> map) {
		map.keySet().stream().forEach(key -> {
			// identify the path from value of attribute Fill in file.
			if (map.get(key).get(4).contains("null") || map.get(key).get(4).contains("0x00000000")) {
				CurvePath p = new CurvePath(map.get(key));
				children.add(p);
				children.addAll(p.getControlPoints());

			} else {
				PolyShape p = new PolyShape(map.get(key));
				children.add(p);
				children.addAll(p.getControlPoints());
			}
		});
		// set up relationship each other after all shape done loading instead of getting NullPointExcetion
		//  while finding shape by ID to link each other in process of looping  
		children.stream().filter(CurvePath.class::isInstance).map(CurvePath.class::cast).forEach(p -> {
			p.getLocksIDs().forEach(i -> p.addLockedShapes(findShapeByID(i)));
			p.checkLocks();
		});
	}

	/**
	 * <p>
	 * create a new string that adds all shapes to one string separated by
	 * {@link System#lineSeparator()}.</br>
	 * </p>
	 * 
	 * @return string containing all shapes.
	 */
	public String convertToString() {
		// for each node in children
		return children.stream()
				// filter out any node that is not PolyShape
				.filter((ReadAndWritable.class::isInstance))
				// cast filtered nodes to PolyShapes
				.map(ReadAndWritable.class::cast)
				// convert each shape to a string format
				.map(ReadAndWritable::convertToString)
				// join all string formats together using new line
				.collect(Collectors.joining(System.lineSeparator()));
	}

	/**
	 * track x, y of cursor
	 * 
	 * @param e
	 */
	public void updateMouseCursor(MouseEvent e) {
		
		xInput.setText(Double.toString(e.getX()));
		yInput.setText(Double.toString(e.getY()));
	}

	@Override
	public void update(Observable o, Object arg) {
		@SuppressWarnings("unchecked")
		List<? extends Object> update = (List<? extends Object>) arg;
		toolInput.setText(((Tool)update.get(0)).name());
		if(update.get(1) != null) shapeInput.setText(((ShapeType) update.get(1)).name());
		
		this.tool = (Tool) update.get(0);
		this.shapeSide = (int) update.get(2);
		if (shapeSide == -1)
			setIrregShape();
	}
	
	/**
	 *  status bar set on the bottom of rootLayOut
	 * @return HBox
	 */
	public HBox getStatus() {
		Label tool = new Label("TOOL");
		
		 toolInput = new Label();
		toolInput.setMinWidth(80);
		toolInput.setTextFill(Color.RED);
		toolInput.setFont(Font.font("Times New Roman", FontWeight.BOLD, 16));
		Label shape = new Label("SHAPE");
		 shapeInput = new Label();
		shapeInput.setMinWidth(80);
		shapeInput.setTextFill(Color.CYAN);
		shapeInput.setFont(Font.font("Times New Roman", FontWeight.BOLD, 16));
		Label cursor = new Label("CURSOR:");
		
		Label x = new Label("X ");
		 xInput = new Label();
		Label y = new Label("Y ");
		 yInput = new Label();
		HBox status = new HBox();
		status.setId("bottomBar");
		status.setPrefHeight(20);
		status.getChildren().addAll(tool, toolInput,new Separator(), shape,shapeInput,new Separator(),cursor,new Separator(),x,xInput,y,yInput);
		return status;
	}
	
	

	
}