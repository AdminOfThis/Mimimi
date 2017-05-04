package gui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class MainFrameController implements Initializable {

    private static final Logger LOG = Logger.getLogger(MainFrameController.class);
    private static final String GUI_FOLDER = "../gui/";
    @FXML
    private ListView<String> moduleList;
    @FXML
    private BorderPane content;
    private HashMap<String, String> modules = new HashMap<>();
    private HashMap<String, Node> loadedModules = new HashMap<>();

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
	    FXMLLoader loader = new FXMLLoader(getClass().getResource(GUI_FOLDER + modules.get(newValue)));
	    Node node = null;
	    try {
		node = loader.load();
		loadedModules.put(newValue, node);
		AnchorPane.setLeftAnchor(node, 0.0);
		AnchorPane.setTopAnchor(node, 0.0);
		AnchorPane.setRightAnchor(node, 0.0);
		AnchorPane.setBottomAnchor(node, 0.0);
	    } catch (IOException e) {
		LOG.error("Unable to load Module", e);
	    }
	    return node;
	}

    }

}
