package fr.tldr.eta;

import fr.tldr.eta.controler.MainWindow;
import fr.tldr.eta.data.SettingsData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	



	public static void main(String[] args) {
		launch(args);
		//SettingsData settingsData = new SettingsData();

		//Main.starter(args);

	}
	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/assets/fxmlFiles/MainWindow.fxml"));
		Parent root = loader.load();
		primaryStage.setTitle("Hello World");
		primaryStage.setScene(new Scene(root, 800, 600));
		primaryStage.setMinHeight(100); // set the minimum Height value
		primaryStage.setMinWidth(100); //set the minimum Width value
		primaryStage.show();
		
		MainWindow controller = loader.getController();
		controller.setPrimaryStage(primaryStage);


	}
}
