package gui.controller;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import data.Bulb;
import data.Button;
import data.Command;
import data.Message;
import data.State;
import data.State.FIELD;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.GuiClient;

public class ControlController implements Initializable {

	public static final String			MAIN_NAME	= "../gui/Mimimi.fxml";
	private static final Logger			LOG			= Logger.getLogger(ControlController.class);
	private static final double			COLORS		= 255;
	private static ControlController	instance;
	@FXML
	private Slider						slider, sliderBright;
	@FXML
	private HBox						colorPane, brightnessPane;
	@FXML
	private VBox						anchor;
	@FXML
	private Circle						circle;
	@FXML
	private ListView<Bulb>				lightList;

	@FXML
	private SplitMenuButton				modeButton;
	/* Alarm Section */
	private int							color		= 0;

	public ControlController() throws RemoteException {
		super();
		instance = this;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;

		initRemote();
	}

	private void initRemote() {
		slider.prefWidthProperty().bindBidirectional(colorPane.prefWidthProperty());
		slider.setOnMouseReleased(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				updateColor(Math.round(new Float(slider.getValue())), true);
			}
		});
		slider.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				updateColor(Math.round(new Float(slider.getValue())), true);
			}
		});
		sliderBright.setOnMouseReleased(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				updateBrightness((int) Math.round(sliderBright.getValue()), true);
			}
		});
		sliderBright.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				updateBrightness((int) Math.round(sliderBright.getValue()), true);
			}
		});
		circle.setStyle("-fx-fill:" + parseToColor(0));
		circle.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				updateColor(color, true);
			}
		});
		for (int i = 0; i < COLORS; i++) {
			Pane color = new Pane();
			color.setPrefHeight(1000.0);
			color.setPrefWidth(10.0);
			color.setStyle("-fx-background-color:" + parseToColor(i));
			colorPane.getChildren().add(color);
		}
		int shades = (int) Math.round(sliderBright.getMax() * 10);
		for (int i = 0; i < shades; i++) {
			Pane color = new Pane();
			color.setPrefHeight(1000.0);
			color.setPrefWidth(10.0);
			int bright = 255 / shades * i;
			String bri = Integer.toHexString(bright);
			if (bri.length() < 2) {
				bri = "0" + bri;
			}
			color.setStyle("-fx-background-color:#" + bri + bri + bri);
			brightnessPane.getChildren().add(color);
		}
		lightList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		try {
			lightList.getItems().setAll(GuiClient.getInstance().getServer().getBulbList());
		}
		catch (RemoteException e1) {
			LOG.error("Unable to load Lights from Server", e1);
		}
		lightList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Bulb>() {

			@Override
			public void changed(ObservableValue<? extends Bulb> observable, Bulb oldValue, Bulb newValue) {

				anchor.setDisable(newValue == null);
				if (newValue != null && newValue.getState() != null) {
					updateColor(newValue.getState().getColor(), false);
					updateBrightness(newValue.getState().getBrightness(), false);
				}

			}
		});

	}

	private void updateBrightness(int value, boolean send) {
		if (send) {
			try {
				State state = new State(Button.BRIGHTNESS);
				state.setBrightness((int) Math.round(sliderBright.getValue()));
				Command cmd = new Command(state);
				for (Bulb b : lightList.getSelectionModel().getSelectedItems()) {
					cmd.addBulb(b);
				}
				GuiClient.getInstance().getServer().update(cmd);
			}
			catch (RemoteException e) {
				LOG.error(e);
			}
		} else {
			sliderBright.setValue(value);
		}
	}

	private void updateColor(int value, boolean send) {
		try {
			String hexValue = parseToColor(value);
			circle.setStyle("-fx-fill:" + hexValue);
			for (Node subNode : slider.getChildrenUnmodifiable()) {
				if (subNode.getStyleClass().contains("thumb")) {
					subNode.setStyle("-fx-background-color:" + hexValue);
				}
			}
			if (send) {
				State state = new State(value);
				Command cmd = new Command(state);
				for (Bulb b : lightList.getSelectionModel().getSelectedItems()) {
					cmd.addBulb(b);
				}
				GuiClient.getInstance().getServer().update(cmd);
			} else {
				slider.setValue(value);
			}
			color = value;
		}
		catch (RemoteException e) {
			LOG.error(e);
			e.printStackTrace();
		}
	}

	public static String parseToColor(int initialValue) {
		int value = initialValue; // correction, so 0x00 is pure red
		int[] color = new int[3];
		String[] colorString = new String[3];
		if (value < 0) {
			value = value + (int) COLORS;
		}
		double x = value / COLORS;
		color[0] = (int) ((Math.cos(x * 2 * Math.PI) + 1) * 127);
		color[1] = (int) ((Math.cos(x * 2 * Math.PI + (2 * Math.PI / 3.0)) + 1) * 127);
		color[2] = (int) ((Math.cos(x * 2 * Math.PI - (2 * Math.PI / 3.0)) + 1) * 127);
		for (int i = 0; i < color.length; i++) {
			if (color[i] < 0) {
				color[i] = 0;
			}
			colorString[i] = Integer.toHexString(color[i]);
			if (colorString[i].length() < 2) {
				colorString[i] = "0" + colorString[i];
			}
		}
		String result = "#" + colorString[0] + colorString[2] + colorString[1];
		result = result.toUpperCase();
		return result;
	}

	public static ControlController getInstance() {

		return instance;
	}

	public void notify(Message message) {
		switch (message.getType()) {
		case LIGHT_COLOR:
			updateColor((int) message.getValue(), false);
			slider.setValue(color);
			break;
		case LIGHT_BRIGHTNESS:
			updateBrightness((int) message.getValue(), false);
		default:
			break;
		}
	}

	private void sendBtn(Button btn) {
		try {
			State state = new State(btn);
			Command cmd = new Command(state);
			for (Bulb b : lightList.getSelectionModel().getSelectedItems()) {
				cmd.addBulb(b);
			}
			GuiClient.getInstance().getServer().update(cmd);
		}
		catch (RemoteException e) {
			LOG.error(e);
		}
	}

	@FXML
	private void mode(ActionEvent e) {
		sendBtn(Button.MODE);
	}

	@FXML
	private void modeSlower(ActionEvent e) {
		sendBtn(Button.MODE_SLOWER);
	}

	@FXML
	private void modeFaster(ActionEvent e) {
		sendBtn(Button.MODE_FASTER);
	}

	@FXML
	private void modeTotal(ActionEvent e) {
		try {
			MenuItem node = (MenuItem) e.getSource();
			int count = modeButton.getItems().indexOf(node);
			State state = new State(FIELD.MODE, count);
			Command cmd = new Command(state);
			for (Bulb b : lightList.getSelectionModel().getSelectedItems()) {
				cmd.addBulb(b);
			}
			GuiClient.getInstance().getServer().update(cmd);
		}
		catch (Exception ex) {
			LOG.error(ex);
		}
	}

	@FXML
	private void allOn(ActionEvent e) {
		sendBtn(Button.ALL_ON);
	}

	@FXML
	private void allOff(ActionEvent e) {
		sendBtn(Button.ALL_OFF);
	}

	@FXML
	private void group1On(ActionEvent e) {
		sendBtn(Button.GROUP1_ON);
	}

	@FXML
	private void group2On(ActionEvent e) {
		sendBtn(Button.GROUP2_ON);
	}

	@FXML
	private void group3On(ActionEvent e) {
		sendBtn(Button.GROUP3_ON);
	}

	@FXML
	private void group4On(ActionEvent e) {
		sendBtn(Button.GROUP4_ON);
	}

	@FXML
	private void group1Off(ActionEvent e) {
		sendBtn(Button.GROUP1_OFF);
	}

	@FXML
	private void group2Off(ActionEvent e) {
		sendBtn(Button.GROUP2_OFF);
	}

	@FXML
	private void group3Off(ActionEvent e) {
		sendBtn(Button.GROUP3_OFF);
	}

	@FXML
	private void group4Off(ActionEvent e) {
		sendBtn(Button.GROUP4_OFF);
	}

	@FXML
	private void allWhite(ActionEvent e) {
		sendBtn(Button.ALL_WHITE);
	}

	@FXML
	private void group1White(ActionEvent e) {
		sendBtn(Button.GROUP1_WHITE);
	}

	@FXML
	private void group2White(ActionEvent e) {
		sendBtn(Button.GROUP2_WHITE);
	}

	@FXML
	private void group3White(ActionEvent e) {
		sendBtn(Button.GROUP3_WHITE);
	}

	@FXML
	private void group4White(ActionEvent e) {
		sendBtn(Button.GROUP4_WHITE);
	}

	@FXML
	private void close(ActionEvent e) {
		LOG.info("Terminating Client");
		Platform.exit();
		System.exit(0);
	}

	@FXML
	private void addLight(ActionEvent e) {
		try {
			Stage stage = new Stage();
			Parent p = FXMLLoader.load(getClass().getResource("../gui/AddBulb.fxml"));
			stage.setScene(new Scene(p));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Pair Light");
			stage.setAlwaysOnTop(true);
			stage.showAndWait();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void updateBulbs(ArrayList<Bulb> bulbList) throws RemoteException {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// ObservableList<Bulb> oldSelected = lightList.getSelectionModel().getSelectedItems();
				lightList.getItems().setAll(bulbList);
// for (Bulb b : oldSelected) {
// for (Bulb b2 : bulbList) {
// if (b.equals(b2)) {
// lightList.getSelectionModel().select(b2);
// }
// }
// }
			}
		});
	}

}
