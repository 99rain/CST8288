package joey.assignment2.view;

import java.util.ArrayList;

import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import joey.assignment2.model.CurvePath;
import joey.assignment2.model.PolyShape;

public class InFoManager {

	private ObservableList<Label> polyLabel;
	private ListView<Label> shapesList;
	private GridPane gp;
	private Button points;
	private String coordinates;
	private VBox rightArea;

	public InFoManager() {
		rightArea = new VBox();
		rightArea.prefHeightProperty().bind(MapMapker.rootPane.heightProperty());
		rightArea.prefWidthProperty().bind(MapMapker.rootPane.widthProperty().divide(5.0));
		rightArea.getChildren().addAll(getListView(), getGridPane());

	}

	private Node getGridPane() {
		gp = new GridPane();
		points = new Button("SHOW POINTS");
		points.setOnAction(e -> {
			if (coordinates.length() > 10)
				showPoints();
		});
		gp.setPadding(new Insets(80, 10, 30, 10));
		gp.setVgap(10);
		gp.setId("details");
		// add detail string labels
		String[] titles = new String[] { "ID:", "angle:", "sides:", "width:", "fill:", "strock:", "locks:",
				"Points:   " };
		gp.addColumn(0, getLabels(titles));
		gp.addColumn(1, getLabels(titles.length - 1));
		gp.add(points, 1, titles.length - 1);
		gp.prefHeightProperty().bind(rightArea.heightProperty().multiply(0.5));
		gp.prefWidthProperty().bind(rightArea.widthProperty());
		return gp;
	}

	/**
	 * help method for gridPane to show the Points of POLYSHAPEs, while button has
	 * been pressed.
	 */
	private void showPoints() {
		String[] pp = coordinates.split(" ");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < pp.length; i += 2)
			sb.append("x: ").append(pp[i]).append("\t y: ").append(pp[i + 1]).append(System.lineSeparator());
		new Alert(AlertType.INFORMATION, "Points: \n" + sb.toString()).show();
	}

	/**
	 * ListView to show shape list. each item hold by a Label
	 * 
	 * @return ListView<Label>
	 */
	private ListView<Label> getListView() {
		polyLabel = FXCollections.observableArrayList();
		shapesList = new ListView<>(polyLabel);
		shapesList.setId("List");

		shapesList.prefHeightProperty().bind(rightArea.heightProperty().multiply(0.5));
		shapesList.prefWidthProperty().bind(rightArea.widthProperty());
		shapesList.getSelectionModel().selectedItemProperty().addListener((ov, oldV, newV) -> {
			if (newV != null) {
				PolyShape p = (PolyShape) newV.getUserData();
				setInfor(p);
				
				newV.setTextFill(Color.GOLD);
				newV.setFont(Font.font("arial black", FontWeight.BOLD, 18));
				p.addEventHandler(MouseEvent.MOUSE_ENTERED, e->{
					newV.setTextFill(Color.GOLD);
					newV.setFont(Font.font("arial black", FontWeight.BOLD, 18));
				});
			}
		});
		return shapesList;

	}

	/**
	 * when ListView and GridPane has been assembled, call this method by this in
	 * MapMaker class to set Right area to show details of map.
	 * 
	 * @return VBox
	 */
	public VBox getRightArea() {
		return this.rightArea;
	}

	/**
	 * 
	 * @return ListChangeListener<? super Node>
	 */
	public ListChangeListener<? super Node> getListener() {
		// initialize a listener
		ListChangeListener<Node> listener = change -> {
			while (change.next()) {
				// for addition change
				change.getAddedSubList().stream().filter(n -> n instanceof PolyShape)

						.forEach(name -> {
							int sides = PolyShape.class.cast(name).getSide();
							if (name instanceof CurvePath) {
							} else {
								/**
								 * this part to be finish later
								 */
								/*
								 * StringProperty tag = new SimpleStringProperty(sides == -1 ? "Irregular" :
								 * sides == 2 ? "Line" : sides == 3 ? "Triangle" : sides == 4 ? "Square" : sides
								 * == 5 ? "Pentagon" : sides == 6 ? "Hexacon" : "Polygon");
								 */
								Label tag = new Label(sides == -1 ? "Irregular"
										: sides == 2 ? "Line"
												: sides == 3 ? "Triangle"
														: sides == 4 ? "Square"
																: sides == 5 ? "Pentagon"
																		: sides == 6 ? "Hexacon" : "Polygon");

								// set PolyshapeSkeleton highlighting event handler when cursor hover on the
								// label in listView
								tag.setUserData(name);
								tag.setMinWidth(100);
								//PolyShape currentShape = (PolyShape) name;
								// setInfor(currentShape);
								tag.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
									((PolyShape) tag.getUserData()).setHighlight(true);
									
									  tag.setTextFill(Color.GOLD); tag.setFont(Font.font("arial black",
									  FontWeight.BOLD, 18));
									 
								});
								tag.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
									((PolyShape) tag.getUserData()).setHighlight(false);
									
									  tag.setTextFill(Color.BLACK); tag.setFont(Font.font(12));
									 
								});
								/*
								 * currentShape.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
								 * 
								 * tag.setTextFill(Color.GOLD); tag.setFont(Font.font("arial black",
								 * FontWeight.BOLD, 18)); });
								 * currentShape.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
								 * tag.setTextFill(Color.BLACK); tag.setFont(Font.font(12)); });
								 */
								polyLabel.add(tag);
							}
						});

				// for elimination change
				ArrayList<Label> toDel = new ArrayList<>();// store the labels to be removed
				change.getRemoved().stream().forEach(r -> {
					polyLabel.stream().filter((l) -> (l instanceof Label)).forEach(l -> {
						Label label = (Label) l;
						if (label.getUserData() == r) {
							toDel.add(l);
						}
					});
				});
				polyLabel.removeAll(toDel);
			}
		};
		return listener;
	}

	private Label[] getLabels(String[] titles) {
		int size = titles.length;
		Label[] infos = new Label[size];
		for (int i = 0; i < size; i++) {
			infos[i] = new Label(titles[i]);
			infos[i].setFont(Font.font("arial black", 14));
			infos[i].setWrapText(true);
		}
		return infos;
	}

	private Label[] getLabels(int size) {
		Label[] infos = new Label[size];
		for (int i = 0; i < size; i++) {
			infos[i] = new Label();
			infos[i].setFont(Font.font("arial", 14));
			infos[i].setWrapText(true);
		}
		return infos;
	}

	private void setInfor(Object currentShape) {
		if (currentShape == null || !(currentShape instanceof PolyShape))
			return;
		else {
			PolyShape shape = PolyShape.class.cast(currentShape);
			boolean background = shape.getFill().equals(Color.BLACK);

			List<Label> labels = gp.getChildren().stream().filter(Label.class::isInstance).map(Label.class::cast)
					.collect(Collectors.toList());
			labels.get(8).setText(Integer.toString(shape.getShapeID()));
			labels.get(9).setText(Double.toString(shape.getAngle()));
			labels.get(10).setText(Integer.toString(shape.getSide()));
			labels.get(11).setText(Double.toString(shape.getStrokeWidth()));
			labels.get(12).setText(shape.colorToString(shape.getFill()));
			labels.get(12).setBackground(
					new Background(new BackgroundFill(background ? Color.TRANSPARENT : shape.getFill(), null, null)));
			labels.get(13).setText(shape.colorToString(shape.getStroke()));
			labels.get(13).setBackground(new Background(new BackgroundFill(shape.getStroke(), null, null)));
			labels.get(14).setText(shape.getInfoStatement(shape.getLocksIDs()));
			coordinates = (shape.getInfoStatement(shape.getPoints()));
		}
	}

}
