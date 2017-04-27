package gui.controller;

import java.net.URL;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import data.LightState;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.GridPane;
import main.GuiClient;
import modules.timer.Alarm;
import modules.timer.Alarm.Mode;
import modules.timer.SimpleAlarm;

public class AlarmController implements Initializable {

	private static final Logger LOG = Logger.getLogger(AlarmController.class);
	private static AlarmController instance;

	@FXML
	private ListView<Alarm> alarmList;
	@FXML
	private GridPane alarmEditor;
	@FXML
	private ComboBox<Mode> modeBox;
	@FXML
	private ComboBox<Integer> timeHour, timeMinute;
	@FXML
	private DatePicker datePicker;

	public AlarmController() throws RemoteException {
		super();
		instance = this;
	}

	public static AlarmController getInstance() {
		return instance;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;

		alarmList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		modeBox.getItems().setAll(Mode.values());
		modeBox.getSelectionModel().select(0);
		alarmEditor.disableProperty().bind(alarmList.getSelectionModel().selectedItemProperty().isNull());
		for (int i = 0; i <= 24; i++) {
			timeHour.getItems().add(i);
		}
		for (int i = 0; i <= 60; i++) {
			timeMinute.getItems().add(i);
		}
		try {
			alarmList.getItems().setAll(GuiClient.getInstance().getServer().getAlarmList());
		}
		catch (RemoteException e) {
			LOG.error("Unable to load alarms from server", e);
		}
		alarmList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Alarm>() {

			@Override
			public void changed(ObservableValue<? extends Alarm> observable, Alarm oldValue, Alarm newValue) {
				updateAlarmEditor(newValue);
			}
		});
		datePicker.valueProperty().addListener(new ChangeListener<LocalDate>() {

			@Override
			public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
				// TODO Auto-generated method stub

			}
		});
	}


	protected void updateAlarmEditor(Alarm newValue) {
		if (newValue != null) {
// LocalDate localDate = datePicker.getValue();
// Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
// newValue.getDate().setTime(Date.from(instant));
			datePicker.setValue(LocalDate.from(newValue.getDate().toInstant()));
		}
	}

	@FXML
	private void addAlarm(ActionEvent e) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.add(GregorianCalendar.DAY_OF_MONTH, 1);
		Alarm a = new SimpleAlarm(cal, Mode.ONCE, new LightState(0x00));
		try {
			GuiClient.getInstance().getServer().addAlarm(a);
		}
		catch (RemoteException e1) {
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

}
