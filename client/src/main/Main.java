package main;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import gui.GUIController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

	private static Logger LOG;
	public static final boolean DEBUG = true;
	public static final String LOG_CONFIG_FILE = "./log4j.ini";

	public static void main(String[] args) {
		try {
			PropertyConfigurator.configure(LOG_CONFIG_FILE);
			LOG = Logger.getLogger(Main.class);
			LOG.info("Starting GUI");
			launch(args);
		}
		catch (Exception e) {
			LOG.fatal("Unexpected error during startup", e);
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(GUIController.MAIN_NAME));
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
