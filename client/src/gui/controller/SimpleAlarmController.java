package gui.controller;

import java.net.URL;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.AnchorPane;
import main.GuiClient;
import modules.timer.Alarm;
import modules.timer.Alarm.Mode;
import modules.timer.SimpleAlarm;

public class SimpleAlarmController implements Initializable, AlarmController {

	private ControlController	controlCon;
	private static final Logger	LOG	= Logger.getLogger(SimpleAlarmController.class);
	@FXML
	private ComboBox<Mode>		modeBox;
	@FXML
	private ComboBox<Integer>	timeHour, timeMinute;
	@FXML
	private DatePicker			datePicker;
	@FXML
	private AnchorPane			controllerPane;
	private SimpleAlarm			alarm;


	@Override
	public void initialize(URL location, ResourceBundle resources) {

		modeBox.getItems().setAll(Mode.values());
		modeBox.getSelectionModel().select(0);
		for (int i = 0; i <= 24; i++) {
			timeHour.getItems().add(i);
		}
		for (int i = 0; i <= 60; i++) {
			timeMinute.getItems().add(i);
		}
		loadContoller();
		try {
			controlCon.setBulbs(GuiClient.getInstance().getServer().getBulbList());
		}

		catch (RemoteException e) {
			LOG.error("Unable to load Bulbs from Server", e);
		}
	}

	private void loadContoller() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/gui/Control.fxml"));
			Parent p = loader.load();
			controlCon = loader.getController();
			controlCon.setSendRawData(false);
			controllerPane.getChildren().add(p);
			AnchorPane.setBottomAnchor(p, 0.0);
			AnchorPane.setTopAnchor(p, 0.0);
			AnchorPane.setLeftAnchor(p, 0.0);
			AnchorPane.setRightAnchor(p, 0.0);
		}
		catch (Exception e) {
			LOG.error("Unable to load Controller", e);
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
			alarm.setCommand(controlCon.getCommand());
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
		controlCon.showCommand(this.alarm.getCommand());
	}

}
