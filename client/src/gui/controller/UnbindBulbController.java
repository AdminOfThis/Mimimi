package gui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;

public class UnbindBulbController implements Initializable {

	private static final Logger	LOG		= Logger.getLogger(UnbindBulbController.class);
	private static final double	WAIT	= 2000;
	@FXML
	private Spinner				spinner;
	@FXML
	private Button				sendBtn;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	@FXML
	private void send(ActionEvent e) {

	}
}
