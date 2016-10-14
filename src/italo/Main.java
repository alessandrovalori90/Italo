package italo;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application{

	public static void main(String[] args) throws IOException {
		Application.launch();
		//launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/italo/gui/MainWindow.fxml"));
        Scene scene = new Scene(root);   
        stage.setTitle("Italo Promo Scanner");
        stage.setScene(scene);
        stage.show();
	}
	
}
