package gui.controller;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import data.Command;
import data.State;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import main.GuiClient;
import modules.timer.Alarm;
import modules.timer.Alarm.Mode;
import modules.timer.SimpleAlarm;

public class AlarmFrameController implements Initializable {

	private static final Logger			LOG	= Logger.getLogger(AlarmFrameController.class);
	private static AlarmFrameController	instance;

	@FXML
	private ListView<Alarm>				alarmList;
	@FXML
	private AnchorPane					alarmPane;
	private AlarmController				controller;

	public AlarmFrameController() throws RemoteException {
		super();
		instance = this;
	}

	public static AlarmFrameController getInstance() {
		return instance;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;

		alarmList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		alarmPane.disableProperty().bind(alarmList.getSelectionModel().selectedItemProperty().isNull());

		try {
			alarmList.getItems().setAll(GuiClient.getInstance().getServer().getAlarmList());
		} catch (RemoteException e) {
			LOG.error("Unable to load alarms from server", e);
		}
		alarmList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Alarm>() {

			@Override
			public void changed(ObservableValue<? extends Alarm> observable, Alarm oldValue, Alarm newAlarm) {
				updateAlarmEditor(newAlarm);
			}
		});
		alarmList.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.DELETE) {
					Alarm alarm = alarmList.getSelectionModel().getSelectedItem();
					try {
						GuiClient.getInstance().getServer().removeAlarm(alarm);
					} catch (RemoteException e) {
						LOG.error("Unable to remove alarm", e);
					}
				}
			}
		});
	}

	protected void updateAlarmEditor(Alarm newValue) {
		if (newValue instanceof SimpleAlarm) {
			try {
				loadAlarmGUI("SimpleAlarm");
				controller.showAlarm(newValue);
			} catch (IOException e) {
				LOG.error("Unable to load Simple Alarm GUI", e);
			}
		}
	}

	private void loadAlarmGUI(String string) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("../gui/" + string + ".fxml"));
		Parent p = loader.load();

		controller = loader.getController();
		alarmPane.getChildren().add(p);
		AnchorPane.setLeftAnchor(p, 0.0);
		AnchorPane.setTopAnchor(p, 0.0);
		AnchorPane.setRightAnchor(p, 0.0);
		AnchorPane.setBottomAnchor(p, 0.0);

	}

	@FXML
	private void addAlarm(ActionEvent e) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.add(GregorianCalendar.DAY_OF_MONTH, 1);
		Alarm a = new SimpleAlarm(cal, Mode.ONCE, new Command(new State(0x00)));
		try {
			GuiClient.getInstance().getServer().addAlarm(a);
		} catch (RemoteException e1) {
			LOG.error("Unable to send alarm to server", e1);
		}
	}

	@FXML
	private void addFadingAlarm(ActionEvent e) {

	}

	public void updateAlarms(ArrayList<Alarm> alarmList2) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				alarmList.getItems().setAll(alarmList2);
			}
		});
	}

	@FXML
	private void saveAlarm(ActionEvent e) {
		if (controller != null) {
			controller.save();
		}
	}

	@FXML
	private void cancelAlarm(ActionEvent e) {

	}

}
