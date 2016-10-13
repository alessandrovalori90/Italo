package italo.gui;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import italo.core.GetPromo;
import italo.core.Util;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class MainWindowController {
	@FXML
	private ListView<String> valid;
	private Service<Void> backgroundThread;
	
	public void test(){
		
		backgroundThread = new Service<Void>() {
			
			@Override
			protected Task<Void> createTask() {
				// TODO Auto-generated method stub
				return new Task<Void>() {
					
					@Override
					protected Void call() throws Exception {
						background();
						return null;
					}
				};
			}
		};
		
		valid.accessibleTextProperty().bind(backgroundThread.messageProperty());
		backgroundThread.restart();
	}
	
	private void background(){
		ObservableList<String> data = FXCollections.observableArrayList();	
		Util util = new Util();
		GetPromo promo = new GetPromo();
		boolean end = false;	
		while(!end){
			try {
				int responce = promo.coonection(util.getUrl());
				if(responce == 200) { //OK
					if(promo.search()) { //promotion expire date found
						if(util.checkExpired(promo.getData())) {
							end = util.add();
						} else {
							data.add(util.getUrl());
							valid.setItems(data);							
						}
					}
				}
				end = util.add();
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Connection failed");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	

}
