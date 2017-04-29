package gui.controller;

import java.net.URL;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import data.LightState;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
		alarmList.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.DELETE) {
					Alarm alarm = alarmList.getSelectionModel().getSelectedItem();
					try {
						GuiClient.getInstance().getServer().removeAlarm(alarm);
					}
					catch (RemoteException e) {
						LOG.error("Unable to remove alarm", e);
					}
				}
			}
		});
	}


	protected void updateAlarmEditor(Alarm newValue) {
		if (newValue != null) {
			GregorianCalendar date = newValue.getDate();
			LocalDate localDate = date.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			datePicker.setValue(localDate);
			timeHour.setValue(date.get(GregorianCalendar.HOUR_OF_DAY));
			timeMinute.setValue(date.get(GregorianCalendar.MINUTE));
			modeBox.setValue(newValue.getMode());
		} else {
			datePicker.setValue(null);
			timeHour.setValue(null);
			timeMinute.setValue(null);
			modeBox.setValue(null);
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

	@FXML
	private void saveAlarm(ActionEvent e) {
		Alarm alarm = alarmList.getSelectionModel().getSelectedItem();
		if (alarm != null) {
			try {
				GregorianCalendar oldCal = alarm.getDate();
				Date date = Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(date);

				cal.set(GregorianCalendar.HOUR_OF_DAY, timeHour.getValue());
				cal.set(GregorianCalendar.MINUTE, timeMinute.getValue());
				GuiClient.getInstance().getServer().removeAlarm(alarm);
				alarm.setDate(cal);
				alarm.setMode(modeBox.getValue());
				GuiClient.getInstance().getServer().addAlarm(alarm);

			}
			catch (RemoteException ex) {
				LOG.error("Unable to save Alarm", ex);
			}
		}
	}

	@FXML
	private void cancelAlarm(ActionEvent e) {

	}

}
