package italo;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import italo.core.GetPromo;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{
	private static final String GET_URL = "http://www.italotreno.it/it/programma-fedelta-italo-piu/Promozioni/";
	private static char letters = 'L';
	private static int  numbers [] = {0,0,0};
	
	public static void main(String[] args) throws IOException {
		launch(args);
		startScann();	
		System.out.println("GET DONE");
	}
	
	// incrementa le cifre finali dell'url 
	private static boolean add() {
		if(numbers[2] < 9 ) {
			numbers[2]++;
			return true;
		} else if (numbers[1] < 9 ){
			numbers[2] = 0;
			numbers[1]++;
			return true;
		} else if (numbers[0] < 9 ){
			numbers[2] = 0;
			numbers[1] = 0;
			numbers[0]++;
			return true;
		}
		return false;
	}
	
	private static void startScann() {
		boolean end = true;
		while (end){
			String url_temp = GET_URL + letters + numbers[0] + numbers[1] + numbers[2];
			GetPromo promo = new GetPromo();			
			try {
				promo.sendGetRequest(url_temp);
				end = add();
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	public void start(Stage stage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/gui/MainWindow.fxml"));
        Scene scene = new Scene(root, 300, 275);   
        stage.setTitle("FXML Welcome");
        stage.setScene(scene);
        stage.show();
	}
}
