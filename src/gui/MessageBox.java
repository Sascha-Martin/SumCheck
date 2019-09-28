package gui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

// Window to display messages
public class MessageBox {
	
	public static void display(String type, String message) {		
		Stage window = new Stage();
		window.setTitle(type);
		window.initModality(Modality.APPLICATION_MODAL);
		
		Label msg = new Label();
		msg.setText(message);
		
		Button closeBtn = new Button();
		closeBtn.setText("Close");
		closeBtn.setOnAction(e -> window.close());
		
		VBox layout = new VBox(25);
		layout.getChildren().addAll(msg, closeBtn);
		layout.setAlignment(Pos.CENTER);
				
		Scene scene = new Scene(layout, 100, 100);
		scene.getStylesheets().add(GUI.class.getResource("Style.css").toExternalForm());
		window.setScene(scene);
		window.setMinWidth(100);
		window.setResizable(false);	
		layout.applyCss();
		layout.layout();
		window.setWidth(msg.widthProperty().getValue() + 80);
		window.setHeight(msg.heightProperty().getValue() + 120 + closeBtn.heightProperty().getValue());
		
		window.showAndWait();
	}
}
