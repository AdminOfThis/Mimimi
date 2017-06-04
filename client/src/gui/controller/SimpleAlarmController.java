package gui.controller;

import java.net.URL;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import data.Bulb;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import main.GuiClient;
import modules.timer.Alarm;
import modules.timer.Alarm.Mode;
import modules.timer.SimpleAlarm;

public class SimpleAlarmController implements Initializable, AlarmController {

	private static final Logger	LOG	= Logger.getLogger(SimpleAlarmController.class);
	@FXML
	private ComboBox<Mode>		modeBox;
	@FXML
	private ComboBox<Integer>	timeHour, timeMinute;
	@FXML
	private DatePicker			datePicker;
	@FXML
	private ListView<Bulb>		bulbList;
	@FXML
	private AnchorPane			controllerPane;
	private SimpleAlarm			alarm;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			bulbList.getItems().setAll(GuiClient.getInstance().getServer().getBulbList());
		}

		catch (RemoteException e) {
			LOG.error("Unable to load Bulbs from Server", e);
		}
		bulbList.setCellFactory(CheckBoxListCell.forListView(new Callback<Bulb, ObservableValue<Boolean>>() {

			@Override
			public ObservableValue<Boolean> call(Bulb item) {
				boolean prop = false;
				if (alarm != null) {
					for (Bulb b : alarm.getBulbList()) {
						if (b.equals(item)) {
							prop = true;
							break;
						}
					}
				}
				SimpleBooleanProperty isActiveOnAlarm = new SimpleBooleanProperty(prop);
				isActiveOnAlarm.addListener(new ChangeListener<Boolean>() {

					@Override
					public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

						if (newValue) {
							alarm.addBulb(item);
						} else {
							alarm.removeBulb(item);
						}
					}
				});
				return isActiveOnAlarm;
			}
		}));
		modeBox.getItems().setAll(Mode.values());
		modeBox.getSelectionModel().select(0);
		for (int i = 0; i <= 24; i++) {
			timeHour.getItems().add(i);
		}
		for (int i = 0; i <= 60; i++) {
			timeMinute.getItems().add(i);
		}

	}

	@Override
	public void save() {
		try {
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

	@Override
	public void showAlarm(Alarm alarm) {

		this.alarm = (SimpleAlarm) alarm;
		GregorianCalendar date = alarm.getDate();
		LocalDate localDate = date.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		datePicker.setValue(localDate);
		timeHour.setValue(date.get(GregorianCalendar.HOUR_OF_DAY));
		timeMinute.setValue(date.get(GregorianCalendar.MINUTE));
		modeBox.setValue(alarm.getMode());
		bulbList.refresh();

	}

}
