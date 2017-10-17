package gui.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.GuiClient;

public class MainFrameController implements Initializable {

	private static final Logger		LOG				= Logger.getLogger(MainFrameController.class);
	private static final String		GUI_FOLDER		= "../gui/";
	@FXML
	private ListView<String>		moduleList;
	@FXML
	private BorderPane				content;
	@FXML
	private ToggleButton			wiFiToggle;

	private HashMap<String, String>	modules			= new HashMap<>();
	private HashMap<String, Node>	loadedModules	= new HashMap<>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOG.info("Loading MainFrame");
		modules.put("Control", "Control.fxml");
		modules.put("Alarms", "Alarm.fxml");

		// list actionListener
		moduleList.getItems().setAll(modules.keySet());
		moduleList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

				content.setCenter(null);
				if (newValue != null) {
					Node node = loadModule(newValue);
					if (node != null) {
						content.setCenter(loadModule(newValue));
					}
				}
			}
		});

	}

	private Node loadModule(String newValue) {
		if (loadedModules.containsKey(newValue)) {
			return loadedModules.get(newValue);
		} else {
			Node module = loadScene(modules.get(newValue));
			loadedModules.put(newValue, module);
			return module;

		}
	}

	private Node loadScene(String fileName) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(GUI_FOLDER + fileName));
		Node node = null;
		try {
			node = loader.load();
			AnchorPane.setLeftAnchor(node, 0.0);
			AnchorPane.setTopAnchor(node, 0.0);
			AnchorPane.setRightAnchor(node, 0.0);
			AnchorPane.setBottomAnchor(node, 0.0);
		}
		catch (IOException e) {
			LOG.error("Unable to load Module", e);
		}
		return node;
	}

	@FXML
	public void addBulb(ActionEvent e) {
		Stage stage2 = new Stage();
		stage2.initModality(Modality.WINDOW_MODAL);
		Parent p = (Parent) loadScene("AddBulb.fxml");
		stage2.setScene(new Scene(p));
		stage2.showAndWait();
	}

	@FXML
	public void removeBulb(ActionEvent e) {
		Stage stage2 = new Stage();
		stage2.initModality(Modality.WINDOW_MODAL);
		Parent p = (Parent) loadScene("RemoveBulb.fxml");
		stage2.setScene(new Scene(p));
		stage2.showAndWait();
	}

	@FXML
	public void close(ActionEvent e) {
		LOG.info("Close requested");
		// TODO unregister from server
		Platform.exit();
		LOG.info("=== Application closed ===");
		System.exit(0);
	}

	@FXML
	private void wiFiToggle(ActionEvent e) {
		LOG.info("Toggle Wifi Detection on server");
		try {
			GuiClient.getInstance().getServer().turnWiFiDetectorOn(wiFiToggle.isSelected());
		}
		catch (RemoteException e1) {
			LOG.warn("Unable to change Status of WiFi Scanner", e1);
		}
	}

	@FXML
	private void updateServer(ActionEvent e) {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Please choose new server jar");
		chooser.setInitialDirectory(new File("."));
		ArrayList<String> extensionList = new ArrayList<>();
		extensionList.add("*.jar");
		chooser.setSelectedExtensionFilter(new ExtensionFilter("Jar", extensionList));
		File jar = chooser.showOpenDialog(content.getScene().getWindow());
		if (jar != null && jar.exists() && jar.getName().endsWith(".jar")) {
			LOG.info("Updating server: " + jar.getPath());
			try {
				FileInputStream in = new FileInputStream(jar);
				byte[] mydata = new byte[1024 * 1024];
				int mylen = in.read(mydata);
				while (mylen > 0) {
					GuiClient.getInstance().getServer().sendData("mimimi_new.jar", mydata, mylen);
					mylen = in.read(mydata);
				}
			}
			catch (Exception ex) {
				LOG.error("Unable to send file", ex);
			}
			try {
				GuiClient.getInstance().getServer().restartServer();
			}
			catch (RemoteException e1) {
				LOG.error("Unable to update server", e1);
			}
		}
	}
}
