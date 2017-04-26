package main;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.sun.security.ntlm.Client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.ServerFinder;
import net.ServerInterface;

public class Main extends Application {

    private static Logger LOG;
    public static final String LOG_CONFIG_FILE = "./log4j.ini";

    public static void main(String[] args) {
	try {
	    PropertyConfigurator.configure(LOG_CONFIG_FILE);
	    LOG = Logger.getLogger(Main.class);
	} catch (Exception e) {
	    LOG.fatal("Unexpected error while initializing logging", e);
	}
	if (GuiClient.getInstance().isConnected()) {
	    LOG.info("Starting GUI");
	    launch(args);
	} else {
	    LOG.warn("No Server found, will exit");
	    Platform.exit();
	    System.exit(0);
	}

    }

    private static void findSever() {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
	FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/gui/MainFrame.fxml"));
	Parent p = loader.load();
	Scene scene = new Scene(p);
	primaryStage.setScene(scene);
	primaryStage.centerOnScreen();
	primaryStage.setTitle("Mimimi");
	primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

	    @Override
	    public void handle(WindowEvent event) {
		LOG.info("Terminating client");
		Platform.exit();
		System.exit(0);
	    }
	});
	// primaryStage.setResizable(false);
	primaryStage.show();
    }
}
