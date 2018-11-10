package joey.assignment2.view;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.*;
import javafx.application.*;
import javafx.event.*;
import javafx.geometry.Orientation;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.stage.FileChooser.ExtensionFilter;
import joey.assignment2.controller.*;
import joey.assignment2.enums.ShapeType;
import joey.assignment2.enums.Tool;


/**
 * MapMaker a FX application for user to draw simple Polygon shape with function of curved-path, control points
 *  BorderPane used for rootLayout, file and help menu bar set on top
 *  left tool bar for user to choose specific tools including SELECTION, MOVE, ROOM, PATH,ERASE,DOOR.
 *  ROOM is main tool to draw a shape from LINE to CUSTOM / IRREGULAR option.
 *  map is an instance of Type MapArea which extends javafx.scene.layout.Pane  
 *  
 * 
 * @author Joey Yin
 * @version 3.0
 * @since JavaFX 2.0  JDK 1.81
 * 
 * @DATE 2018-11-8
 */

public class MapMapker extends Application{
	/**
	 * static final reference linked to resources
	 */
	public static final String INFO_PATH = "resources/icons/info.txt";
	public static final String HELP_PATH = "resources/icons/help.txt";
	public static final String CREDITS_PATH = "resources/icons/credits.txt";
	public static final String CSS_PATH = "resources/css/style.css";
	public static final String MAPS_DIRECTORY = "resources/maps";
	
	/**
	 * rootPane  rootLayout use BorderPane
	 * map    center Pane 
	 * statistics  right side of rootLayOut to show details of selected shape
	 * tool back system to shift tool state and other command. 
	 */
	public static BorderPane rootPane;
	private MapArea map;
	private InFoManager statistics;
	private ToolState tool;

	@Override
	public void init() throws Exception {
		super.init();
		/**
		 * initialization
		 */
		tool = ToolState.state();
		rootPane = new BorderPane();
		statistics = new InFoManager();
		map = new MapArea();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// tool is observable instance, add observer on map
		tool.addObserver(map);
		//map add listener of right side VBox
		map.getChildren().addListener(statistics.getListener());
		// center
				rootPane.setCenter(map);
		// top
		MenuBar menuBar = new MenuBar(
				new Menu("File", null, createMenuItem("New", (e) -> showConfirmationDialog("new",primaryStage)),
						createMenuItem("Open", (e) -> loadMap(primaryStage)),
						createMenuItem("Save", (e) -> saveMap(primaryStage)), new SeparatorMenuItem(),
						createMenuItem("Exit", (e) -> showConfirmationDialog("Exit",primaryStage))),
				new Menu("Help", null, createMenuItem("Credit", (e) -> displayCredit()),
						createMenuItem("Info", (e) -> displayInfo()), new SeparatorMenuItem(),
						createMenuItem("Help", (e) -> displayHelp())));
		menuBar.setId("top-menu");
		rootPane.setTop(menuBar);

		// left
		ToolBar toolBar = new ToolBar(

				getToolButton("Select", e -> tool.command(Tool.SELECT, null)

				), getToolButton("Move", e -> tool.command(Tool.MOVE, null)

				),
				getToolButton("Path",e -> tool.command(Tool.CURVEDPATH, null))
				/*getToolButton("Path", e -> tool.setTool(Tool.PATH, 2, 4, map)

				)*/, getToolButton("Erase", e -> tool.command(Tool.ERASE, null)

				), getToolButton("Door", e -> tool.command(Tool.DOOR, null)

				),
				 getToolMenu("Room", createMenuItem("Line", e -> tool.command(Tool.ROOM, ShapeType.LINE)

							), createMenuItem("Triangle", e -> tool.command(Tool.ROOM, ShapeType.TRIANGLE)

							), createMenuItem("Square", e -> tool.command(Tool.ROOM, ShapeType.SQUARE)

							), createMenuItem("Pentagon", e -> tool.command(Tool.ROOM, ShapeType.PENTAGON)

							), createMenuItem("Hexacon", e -> tool.command(Tool.ROOM, ShapeType.HEXAGON)

							), new SeparatorMenuItem(), createMenuItem("Custome", e -> getDialog()
									// negative 1 stands for the signal of drawing a irregular PolyShape
							), createMenuItem("Irregular", e ->{ tool.command(Tool.ROOM, ShapeType.IREGULAR);
																
							})
							));

		toolBar.setPrefWidth(50);
		toolBar.setOrientation(Orientation.VERTICAL);

		rootPane.setLeft(toolBar);

		

		// BOTTOM STATUS
		rootPane.setBottom(map.getStatus());

		// right detail
		rootPane.setRight(statistics.getRightArea());

		Scene scene = new Scene(rootPane, 1200, 900);
		scene.getStylesheets().add(new File(CSS_PATH).toURI().toString());
		

		primaryStage.setScene(scene);
		primaryStage.setTitle("Map Maker Skeleton");
		primaryStage.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
			if (e.getCode() == KeyCode.ESCAPE)
				showConfirmationDialog("Exist", primaryStage);
		});
		primaryStage.show();
	}

	/**
	 * following 3 help methods for file operations
	 * 
	 */

	private void displayCredit() {
		displayAlert("Credit", loadFile(CREDITS_PATH, System.lineSeparator()));
	}
	private void displayHelp() {
		displayAlert("Help", loadFile(HELP_PATH,System.lineSeparator()));
	}

	private void displayInfo() {
		displayAlert("Info", loadFile(INFO_PATH, System.lineSeparator()));
	}

	
	@Override
	public void stop() throws Exception {
		super.stop();
	}
	
	/**
	 * create menuItems 
	 * @param name
	 * @param handler
	 * @return
	 */
	private MenuItem createMenuItem(String name, EventHandler<ActionEvent> handler) {
		Label icon = new Label();
		icon.setId(name + "-icon");
		MenuItem item = new MenuItem(name, icon);
		item.setId(name);
		item.setOnAction(handler);
		return item;
	}
	
	private void displayAlert(String title, String context) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("info");
		alert.setHeaderText("information of author");
		alert.setContentText(context
				+System.lineSeparator()
				+"\nDate:" + new Date()
				+System.lineSeparator()
				+ "\nemail: yin00031@algonquinlive.com");
		alert.show();
	}

	


	public static void main(String[] args) {
		setUserAgentStylesheet(STYLESHEET_CASPIAN);
		launch(args);
	}

	/****************** help method for left side tool bar*******************/

	private Button getToolButton(String id, EventHandler<ActionEvent> e) {
		Button b = new Button();
		b.setId(id);
		b.setOnAction(e);
		return b;
	}

	private MenuButton getToolMenu(String id, MenuItem... bs) {

		MenuButton mb = new MenuButton();
		
		mb.getItems().addAll(bs);
		mb.setId(id);
		
		return mb;
	}

	
	/**
	 * when user want to draw a custom sides poly shape
	 * open a dialog to set up state and side of shape
	 */
	private void getDialog() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("");
		dialog.setHeaderText("Enter the sides of polygon.");
		Optional<String> result = dialog.showAndWait();
		int entered = 0;
		if (result.isPresent()) {entered = Integer.parseInt(result.get());}
		tool.command(Tool.ROOM, entered);
	}

	/**
	 * <p>
	 * ask the user what file they need to open then pass the content to 
	 * {@link MapArea#convertFromString(java.util.Map)}.</br>
	 * </p>
	 * @param primary - {@link Stage} object that will own the {@link FileChooser}.
	 */
	public void loadMap(Stage stage) {
		map.clearMap();
		File file = getFileChooser(stage, false);
		if (file == null || !file.exists()) 
			return;
		try {
			AtomicLong index = new AtomicLong(0);
			map.convertFromString(Files.lines(file.toPath()).collect(Collectors.groupingBy(l->index.getAndIncrement()/8)));
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * <p>
	 * read a file and convert it to one string separated with provided separator.</br>
	 * </p>
	 * @param path - {@link String} object containing the path to desired file.
	 * @param separator - {@link String} object containing the separator
	 */
	private String loadFile(String path, String seperator) {
		try {
			return Files.lines(Paths.get(path)).reduce("", (x, y)-> x.concat(seperator).concat(y));
		}catch (IOException e) {
			return String.format("%s was probably not found.%nmessage: %s", path, e.getMessage());
		}
	}
	
	public void saveMap(Stage stage) {
		File file = getFileChooser(stage, true);
		if(file == null) return;
		try {
			if(!file.exists()) file.createNewFile();
			Files.write(file.toPath(), map.convertToString().getBytes());
		}catch (IOException e) {
			e.printStackTrace();
		}
	
	}


	
	/**
	 * <p>
	 * using the {@link FileChooser} open a new window only showing .map extension;
	 * in starting path of {@link MapMaker#MAPS_DIRECTORY}.</br>
	 * this function can be used to save or open file depending on the boolean argument.</br>
	 * </p>
	 * @param primary - {@link Stage} object that will own the {@link FileChooser}.
	 * @param save - if true show {@link FileChooser#showSaveDialog(javafx.stage.Window)} 
	 * 					else {@link FileChooser#showOpenDialog(javafx.stage.Window)}
	 * @return a {@link File} representing the save or load file object
	 */
	private File getFileChooser( Stage primary, boolean save){
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add( new ExtensionFilter( "Maps", "*.map"));
		fileChooser.setInitialDirectory( Paths.get( MAPS_DIRECTORY).toFile());
		return save?fileChooser.showSaveDialog( primary):fileChooser.showOpenDialog( primary);
	}
	
	
	
	
	
	
	

	
	/**
	 * dialog for confirmation
	 * @param name name of title for confirmation
	 * @param stage primaryStage
	 */
	private void showConfirmationDialog(String name,Stage stage) {
		
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("Look, a Confirmation Dialog");
		alert.setContentText("do you save the current map ?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
		    saveMap(stage);
		    
		} else if(name=="new")map.clearMap();
	    	else stage.hide();
	}

	
}
