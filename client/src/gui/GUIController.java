package gui;

import java.net.URL;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import data.Alarm;
import data.Alarm.Mode;
import data.Button;
import data.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import net.Client;
import net.ServerFinder;
import net.ServerInterface;

public class GUIController extends Client implements Initializable {

	private static final long serialVersionUID = -165050278898546292L;
	public static final String MAIN_NAME = "../gui/Mimimi.fxml";
	private static final Logger LOG = Logger.getLogger(GUIController.class);
	private static final double COLORS = 255;
	private static GUIController instance;
	private ServerInterface server;
	@FXML
	private Slider slider, sliderBright;
	@FXML
	private HBox colorPane, brightnessPane;
	@FXML
	private VBox anchor;
	@FXML
	private Circle circle;
	@FXML
	private CheckMenuItem showMessages;
	@FXML
	private SplitMenuButton modeButton;
	/* Alarm Section */
	@FXML
	private ListView<Alarm> alarmList;
	@FXML
	private javafx.scene.control.Button addAlarm, removeAlarm;
	@FXML
	private DatePicker alarmDate;
	@FXML
	private ComboBox<Mode> alarmCombo;
	@FXML
	private ComboBox<Integer> hours, minutes;
	@FXML
	private ListView<Message> list;
	private int color = 0;

	public GUIController() throws RemoteException {
		super();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;
		String ip = ServerFinder.findServer();
		if (ip == null) {
			LOG.error("Unable to find Server");
			Platform.exit();
			System.exit(0);
		}
		if (ip != null) {
			LOG.info("Server: " + ip);
			connect(ip);
			server = getServer();
		}
		initGUI();
	}

	private void initGUI() {
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
				try {
					server.sendBrightness((int) Math.round(sliderBright.getValue()));
				}
				catch (RemoteException e) {
					LOG.error(e);
				}
			}
		});
		sliderBright.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				try {
					server.sendBrightness((int) Math.round(sliderBright.getValue()));
				}
				catch (RemoteException e) {
					LOG.error(e);
				}
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
		list.visibleProperty().bind(showMessages.selectedProperty());
		list.managedProperty().bind(showMessages.selectedProperty());
		list.setCellFactory(e -> new MessageCell());
		initAlarm();
	}

	private void initAlarm() {
		alarmList.setCellFactory(e -> new AlarmCell());
		alarmCombo.getItems().addAll(Mode.values());
		try {
			ArrayList<Alarm> alarmList = server.getAlarmList();
			this.alarmList.getItems().addAll(alarmList);
		}
		catch (RemoteException e) {
			LOG.error("Unable to get Alarms from Server", e);
		}
		addAlarm.disableProperty().bind(alarmCombo.valueProperty().isNull().or(alarmDate.valueProperty().isNull()).or(hours.valueProperty().isNull()).or(minutes.valueProperty().isNull()));
		removeAlarm.disableProperty().bind(alarmList.getSelectionModel().selectedItemProperty().isNull());
		for (int hours = 0; hours <= 23; hours++) {
			this.hours.getItems().add(hours);
		}
		for (int minutes = 0; minutes <= 59; minutes++) {
			this.minutes.getItems().add(minutes);
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
				server.sendColor(value);
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

	public static GUIController getInstance() {
		return instance;
	}

	// TODO Adding icon depending on Message type
	@Override
	public void notify(Message message) throws RemoteException {
		// TODO Update GUI incase of external Color update
		switch (message.getType()) {
		case LIGHT_COLOR:
			updateColor((int) message.getValue(), false);
			slider.setValue(color);
			break;
		default:
			break;
		}
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				list.getItems().add(message);
			}
		});
	}

	@Override
	public void ping() throws RemoteException {
		LOG.debug("Ping received");
	}

	private void sendBtn(Button btn) {
		try {
			server.sendButton(btn);
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
			server.mode(count);
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
	private void addAlarm(ActionEvent e) {
		// TODO
		Mode mode = alarmCombo.getValue();
		LocalDate cal = alarmDate.getValue();
		GregorianCalendar date = new GregorianCalendar();
		date.set(GregorianCalendar.YEAR, cal.getYear());
		date.set(GregorianCalendar.MONTH, cal.getMonthValue() - 1);
		date.set(GregorianCalendar.DAY_OF_MONTH, cal.getDayOfMonth());
		date.set(GregorianCalendar.HOUR_OF_DAY, hours.getValue());
		date.set(GregorianCalendar.MINUTE, minutes.getValue());
		date.set(GregorianCalendar.SECOND, 0);
		Alarm alarm = new Alarm(date, mode);
		try {
			server.addAlarm(alarm);
			alarmCombo.setValue(null);
			alarmDate.setValue(null);
			hours.setValue(null);
			minutes.setValue(null);
		}
		catch (RemoteException e1) {
			LOG.error("Unable to set alarm on server", e1);
		}
	}

	@FXML
	private void removeAlarm(ActionEvent e) {
		Alarm alarm = alarmList.getSelectionModel().getSelectedItem();
		try {
			server.removeAlarm(alarm);
		}
		catch (RemoteException e1) {
			LOG.error("Cannot reach server to remove alarm", e1);
		}
	}

	@Override
	public void updateAlarms(ArrayList<Alarm> alarmList) throws RemoteException {
		this.alarmList.getItems().setAll(alarmList);
	}
}
