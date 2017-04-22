package gui;

import java.text.SimpleDateFormat;
import java.util.Date;

import data.Alarm;
import javafx.application.Platform;
import javafx.scene.control.ListCell;

public class AlarmCell extends ListCell<Alarm> {

    @Override
    protected void updateItem(Alarm item, boolean empty) {
	super.updateItem(item, empty);
	Platform.runLater(new Runnable() {

	    @Override
	    public void run() {
		if (!empty) {
		    Date date = item.getDate().getTime();
		    SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		    setText(formatter.format(date) + " (" + item.getMode().toString() + ")");
		} else {
		    setGraphic(null);
		    setText(null);
		}
	    }
	});
    }
}
