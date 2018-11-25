package joey.assignment2.controller;

import java.util.Arrays;
import java.util.Observable;
import joey.assignment2.enums.ShapeType;
import joey.assignment2.enums.Tool;



/**
 *  back system extends Observable, user change command from MapMaker,then it will notify its observer to update info 
 * @author Joey Yin
 *
 */
public class ToolState extends Observable {

	/***** fields *******/

	private static final ToolState S = new ToolState();

	private ShapeType type;
	private Tool tool;
	private int side;


	/******** getterSetter ***********/

	public ShapeType getType() {
		return type;
	}

	public void setType(ShapeType type) {
		this.type = type;
	}

	public Tool getTool() {
		return tool;
	}

	public void setTool(Tool t) {
		this.tool = t;
	}

	public int getSide() {
		return side;
	}

	public void setSide(int side) {
		this.side = side;
	}

	public static final ToolState state() {
		return S;
	}

	// overload method for custom shape in tool of ROOM.
	public void command(Tool t, int num) {
		command(t, ShapeType.CUSTOM);
		setSide(num);
		setChanged();
		notifyObservers(Arrays.asList(tool, type, side));
	}

	// update status for observers.
	public void command(Tool t, ShapeType s) {
		setTool(t);
		if (s != null) {
			setType(s);
			setSide(s.getValue());
		}	
		
		setChanged();
		notifyObservers(Arrays.asList(tool, type, side));
	}

}
