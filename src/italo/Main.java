package italo;

import java.io.IOException;

import italo.gui.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application{

	public static void main(String[] args) throws IOException {
		Application.launch();
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/italo/gui/MainWindow.fxml"));
		Parent root = loader.load();
		MainWindowController controller = loader.getController();
		controller.setApp(getHostServices());
        Scene scene = new Scene(root);  
        stage.setTitle("Italo Promo Scanner");
        stage.setScene(scene);
        stage.show();
	}
	
}
