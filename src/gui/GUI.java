package gui;


import java.io.File;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import function.Sum;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GUI extends Application {
	
	// String to store the OS
	private static String os;
	
	// Flag for change in algorithm or file to determine if the checksum has to be recalculated
	private static boolean unchanged = false;

	// Determine the OS
	// Error message and termination if it fails
	public static void main(String[] args) {
		os = System.getProperty("os.name").toLowerCase();
		if(os.startsWith("win")) {
			os = "WIN";
		} else if(os.startsWith("mac")) {
			os = "OSX";
		} else if(os.contains("linux")) {
			os = "LINUX";
		} else {
			JOptionPane.showMessageDialog(null, "OS not supported!", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		launch();
	}

	@Override
	public void start(Stage window) throws Exception {
		window.setTitle("SumCheck");
		
		// Hidden Label to get the text width to resize the window and initialize the dropDown width
		Label hiddenLabel = new Label("Choose Algorithm");
		VBox hiddenBox = new VBox(10);
		hiddenBox.getChildren().add(hiddenLabel);
		Scene hiddenScene = new Scene(hiddenBox, 10000,1);
		hiddenScene.getStylesheets().add(GUI.class.getResource("Style.css").toExternalForm());
		hiddenBox.applyCss();
		hiddenBox.layout();
		
		// Top of BorderPane
		HBox top = new HBox(50);
		top.setAlignment(Pos.CENTER);
		
		// Drop down menu for algorithm selection
		ComboBox<String> dropDown = new ComboBox<String>();
		dropDown.getItems().addAll("MD5", "SHA1", "SHA256", "SHA384", "SHA512");
		dropDown.setPromptText("Choose Algorithm");
		dropDown.setMinWidth(hiddenLabel.widthProperty().getValue() + 80);
		
		// If change in algorithm set flag to recalculate checksum
		dropDown.setOnAction(e -> {
			unchanged = false;
		});
		
		// Label and TextField for file path from center of BorderPane (needed for fileDrop)
		Label filePathLabel = new Label("File Path");
		TextField filePathField = new TextField();
		
		// fileDrop is a pane where files are dragged and dropped
		Label fileDropLabel = new Label("Drop file here!");
		fileDropLabel.setId("fileDrop");
		
		StackPane fileDrop = new StackPane();
		fileDrop.getChildren().add(fileDropLabel);
		fileDrop.setBorder(new Border(new BorderStroke(Color.WHITE , BorderStrokeStyle.DASHED, new CornerRadii(10), new BorderWidths(2))));
		fileDrop.prefWidthProperty().bind(top.widthProperty());
		fileDrop.setMinHeight(100);
		fileDrop.setAlignment(Pos.CENTER);
		
		// Drag and drop for fileDrop
		fileDrop.setOnDragOver(e -> {
			if (e.getDragboard().hasFiles()) {
	            e.acceptTransferModes(TransferMode.LINK);
	        }
		});
		fileDrop.setOnDragDropped(e -> {
			String filePath = e.getDragboard().getFiles().stream().limit(1).map(File::getAbsolutePath).collect(Collectors.joining("\n"));
			
			filePathField.setText(filePath);
			e.setDropCompleted(true);
		});
		
		top.getChildren().addAll(dropDown, fileDrop);
		
		
		// center of BorderPane
		Label sumLabel = new Label("Checksum");
		TextField sumField = new TextField();
		sumField.setEditable(false);
		
		Label expSumLabel = new Label("Expected Checksum");
		TextField expSumField = new TextField();
		
		// Listeners for resizing the window on change	
		filePathField.textProperty().addListener((v, oldVal, newVal) -> {
			unchanged = false;
			if(newVal.length() > hiddenLabel.getText().length()) {
				hiddenLabel.setText(newVal);
				resize(window, hiddenBox, hiddenLabel);
			}
		});

		expSumField.textProperty().addListener((v, oldVal, newVal) -> {
			if(newVal.length() > hiddenLabel.getText().length()) {
				hiddenLabel.setText(newVal);
				resize(window, hiddenBox, hiddenLabel);
			}
		});
		
		VBox center = new VBox(10);
		center.getChildren().addAll(filePathLabel, filePathField, sumLabel, sumField, expSumLabel, expSumField);
		center.setMaxHeight(center.getHeight());
		
		// bottom of BorderPane
		Button sumBtn = new Button("Get Sum");
		Button checkBtn = new Button("Check");
		
		// Listeners for button press
		sumBtn.setOnAction(e -> {
			sumField.setText(getSum(dropDown, filePathField));
			if(sumField.getText() != null && sumField.getText().length() > hiddenLabel.getText().length()) {
				hiddenLabel.setText(sumField.getText());
				resize(window, hiddenBox, hiddenLabel);
			}
		});
		
		checkBtn.setOnAction(e -> {
			checkSum(dropDown, filePathField, sumField, expSumField);
			if(sumField.getText() != null && sumField.getText().length() > hiddenLabel.getText().length()) {
				hiddenLabel.setText(sumField.getText());
				resize(window, hiddenBox, hiddenLabel);
			}
		});
		
		HBox bottom = new HBox(100);
		bottom.setAlignment(Pos.TOP_CENTER);
		bottom.getChildren().addAll(sumBtn, checkBtn);
		
		
		// BorderPane and Scene
		BorderPane layout = new BorderPane();
		layout.setTop(top);
		BorderPane.setMargin(top, new Insets(10,10,10,10));
		layout.setCenter(center);
		BorderPane.setMargin(center, new Insets(10,10,10,10));
		layout.setBottom(bottom);
		BorderPane.setMargin(bottom, new Insets(10,10,25,10));
		
		Scene scene = new Scene(layout, 500, 380);
		scene.getStylesheets().add(GUI.class.getResource("Style.css").toExternalForm());
		
		// Make fileDrop size fit the window size
		fileDrop.prefHeightProperty().bind(window.heightProperty().subtract(bottom.heightProperty().add(center.heightProperty().add(120))));
		window.setScene(scene);
		window.setMinWidth(420);
		window.setMinHeight(420);
		window.centerOnScreen();
		window.show();
	}
	
	// Checks if input is correct and returns the checksum
	private String getSum(ComboBox<String> dropDown, TextField filePathField) {
		String algo = dropDown.getValue();
		
		if(algo == null) {
			MessageBox.display("Alert!", "No Algorithm selected");
			return null;
		}
		
		String filePath = filePathField.getText();
		if(filePath == null || filePath.isEmpty()) {
			MessageBox.display("Alert!", "No Filepath");
			return null;
		}
		
		File file = new File(filePath);
		if(!file.isFile()) {
			MessageBox.display("Alert!", "Not a file");
			return null;
		}
		
		unchanged = true;
		return Sum.getSum(algo, filePath, os);
	}
	
	// Gets checksum if needed and compares it to the expected value
	private void checkSum(ComboBox<String> dropDown, TextField filePathField, TextField sumField, TextField expSumField) {
		String expSum = expSumField.getText();
		String sum = "";
		
		if(unchanged) {
			sum = sumField.getText();
		} else {
			sum = getSum(dropDown, filePathField);
			sumField.setText(sum);
		}
		
		// If there is no sum, the alerts are already handled by getSum()
		if(sum == null || sum.isEmpty()) {
			return;
		}
		
		// Alert if there is no expected sum
		if(expSum == null || expSum.isEmpty()) {
			MessageBox.display("Alert!", "No expected checksum");
			return;
		}
		
		// Check if sums match and show answer
		if(sum.equals(expSum)) {
			MessageBox.display("Answer", "Match");
		} else {
			MessageBox.display("Answer", "Sums don't match");
		}
	}
	
	// Increase window width to fit the whole filePath, sum, expSum
	private void resize(Stage window, VBox hiddenBox, Label hiddenLabel) {
		hiddenBox.applyCss();
		hiddenBox.layout();
		if(hiddenLabel.widthProperty().getValue() + 80 > window.getWidth()) {
			window.setWidth(hiddenLabel.widthProperty().getValue() + 80);
		}
		window.centerOnScreen();
	}
}
