package gui;

import data.Message;
import gui.controller.ControlController;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

public class MessageCell extends ListCell<Message> {

	@Override
	protected void updateItem(Message item, boolean empty) {
		super.updateItem(item, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			setText(item.getText());
			Node graphic = null;
			switch (item.getType()) {
			case LIGHT_COLOR:
				Circle circle = new Circle(10);
				circle.setStyle("-fx-fill: " + ControlController.parseToColor((int) item.getValue()));
				graphic = circle;
				break;
			case LIGHT_BRIGHTNESS:
				graphic = new ImageView(getClass().getResource("./icons/Message_Light-32.png").toString());
				break;
			case WIFI:
				graphic = new ImageView(getClass().getResource("./icons/Message_WiFi-32.png").toString());
				break;
			case ERROR:
				graphic = new ImageView(getClass().getResource("./icons/Message_Error-32.png").toString());
				break;
			default:
				break;
			}
			setGraphic(graphic);
		}
	}
}
