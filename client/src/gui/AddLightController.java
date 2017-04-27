package gui;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import data.Address;
import data.LightBulb;
import data.LightState;
import gui.controller.ControlController;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.GuiClient;

public class AddLightController implements Initializable {

    private static final Logger LOG = Logger.getLogger(AddLightController.class);
    private static final double WAIT = 10000;
    @FXML
    private ProgressBar bar;
    @FXML
    private Label text;
    @FXML
    private Button btnOn, btnOff, saveBulb;
    @FXML
    private TextField nameField;
    Address address;
    private long start = System.currentTimeMillis();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

	btnOn.setVisible(false);
	btnOff.setVisible(false);
	btnOn.setManaged(false);
	btnOff.setManaged(false);
	saveBulb.setManaged(false);
	btnOff.setManaged(false);
	nameField.setManaged(false);
	nameField.setManaged(false);
	saveBulb.disableProperty().bind(nameField.textProperty().isEmpty());
	AnimationTimer timer = new AnimationTimer() {

	    @Override
	    public void handle(long now) {
		if (start + WAIT >= System.currentTimeMillis()) {
		    text.setText(
			    "Wait: " + Math.round((WAIT - (System.currentTimeMillis() - start) + 800) / 1000) + "s");
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
			    address = GuiClient.getInstance().getServer().connectLightBulb();
			} catch (RemoteException e) {
			    LOG.error(e);
			}
			btnOn.setVisible(true);
			btnOff.setVisible(true);
			btnOn.setManaged(true);
			btnOff.setManaged(true);
			saveBulb.setManaged(true);
			btnOff.setManaged(true);
			nameField.setManaged(true);
			nameField.setManaged(true);
			stop();
		    }
		}
	    }
	};
	timer.start();

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
	    LightState state = new LightState(btn);
	    state.setAddress(address);
	    try {
		GuiClient.getInstance().getServer().update(state);
	    } catch (RemoteException e1) {
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
	    LightState state = new LightState(btn);
	    state.setAddress(address);
	    try {
		GuiClient.getInstance().getServer().update(state);
	    } catch (RemoteException e1) {
		LOG.error(e);
	    }
	}
    }

    @FXML
    private void saveBulb(ActionEvent e) {
	LOG.info("Adding Bulb to Server");
	if (address != null) {
	    LightBulb bulb = new LightBulb(nameField.getText(), address);
	    try {
		GuiClient.getInstance().getServer().addLightBulbToList(bulb);
	    } catch (RemoteException e1) {
		LOG.error(e1);
	    }
	}
	((Stage) saveBulb.getScene().getWindow()).close();
    }
}
