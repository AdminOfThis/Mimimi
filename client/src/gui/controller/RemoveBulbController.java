package gui.controller;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import data.Address;
import data.Bulb;
import data.Command;
import data.Remote;
import data.State;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import main.GuiClient;

public class RemoveBulbController implements Initializable {

	private static final Logger	LOG		= Logger.getLogger(RemoveBulbController.class);
	private static final double	WAIT	= 10000;
	@FXML
	private ProgressBar			bar;
	@FXML
	private Label				text;
	@FXML
	private Button				btnOn, btnOff, removeBulb;
	@FXML
	private Spinner<Integer>	groupID, remoteID;
	@FXML
	private HBox				idBox;
	Address						address;
	private long				start;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		groupID.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000));
		remoteID.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000));
		text.setVisible(false);
		text.setManaged(false);
		removeBulb.disableProperty().bind(groupID.valueProperty().isNull().or(remoteID.valueProperty().isNull()));

		AnimationTimer timer = new AnimationTimer() {

			@Override
			public void handle(long now) {
				if (start + WAIT >= System.currentTimeMillis()) {
					text.setText("Wait: " + Math.round((WAIT - (System.currentTimeMillis() - start) + 800) / 1000) + "s");
					bar.setProgress((System.currentTimeMillis() - start) / WAIT);
				} else {
					bar.getStyleClass().add("greenBar");
					text.setText("Turn on Light");
					text.getStyleClass().remove("blackLabel");
					text.getStyleClass().add("whiteLabel");
					if (start + WAIT + 1500 <= System.currentTimeMillis()) {
						LOG.info("Sending ON MESSAGE");
						text.setText("Command sent");
						try {
							GuiClient.getInstance().getServer().removeLightFromBulbList(new Bulb(new Address(new Remote(remoteID.getValue()), groupID.getValue())));
						}
						catch (Exception e) {
							LOG.error(e);
						}
						btnOn.setVisible(true);
						btnOff.setVisible(true);
						btnOn.setManaged(true);
						btnOff.setManaged(true);
						removeBulb.setManaged(true);
						btnOff.setManaged(true);

						stop();
					}
				}
			}
		};
		removeBulb.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				removeBulb.setVisible(false);
				btnOn.setVisible(false);
				btnOff.setVisible(false);
				text.setVisible(true);
				text.setManaged(true);
				idBox.setVisible(false);
				idBox.setManaged(false);
				start = System.currentTimeMillis();
				timer.start();
			}
		});

	}

	@FXML
	private void btnOn(ActionEvent e) {
		if (address != null) {
			data.Button btn = null;
			switch (address.getGroup()) {
			case 1:
				btn = data.Button.GROUP1_ON;
				break;
			case 2:
				btn = data.Button.GROUP2_ON;
				break;
			case 3:
				btn = data.Button.GROUP3_ON;
				break;
			case 4:
				btn = data.Button.GROUP4_ON;
				break;
			}
			Command cmd = new Command(new State(btn), new Bulb(address));
			try {
				GuiClient.getInstance().getServer().update(cmd);
			}
			catch (RemoteException e1) {
				LOG.error(e);
			}
		}
	}

	@FXML
	private void btnOff(ActionEvent e) {
		if (address != null) {
			data.Button btn = null;
			switch (address.getGroup()) {
			case 1:
				btn = data.Button.GROUP1_OFF;
				break;
			case 2:
				btn = data.Button.GROUP2_OFF;
				break;
			case 3:
				btn = data.Button.GROUP3_OFF;
				break;
			case 4:
				btn = data.Button.GROUP4_OFF;
				break;
			}
			Command cmd = new Command(new State(btn), new Bulb(address));
			try {
				GuiClient.getInstance().getServer().update(cmd);
			}
			catch (RemoteException e1) {
				LOG.error(e);
			}
		}
	}

// @FXML
// private void saveBulb(ActionEvent e) {
// LOG.info("Adding Bulb to Server");
// if (address != null) {
// Bulb bulb = new Bulb(address, idField.getText());
// try {
// GuiClient.getInstance().getServer().addBulbToList(bulb);
// }
// catch (RemoteException e1) {
// LOG.error(e1);
// }
// }
// ((Stage) removeBulb.getScene().getWindow()).close();
// }
}
