package italo.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import italo.core.GetPromo;
import italo.core.Util;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;

public class MainWindowController implements Initializable{
	@FXML private ListView<String> valid;
	@FXML private ListView<String> expired;
	@FXML private ListView<String> unkown;
	@FXML private Button button1;
	@FXML private ProgressBar progressBar;
	private static Service<Void> backgroundThread;
	
	public void test(){
		if(backgroundThread != null) {
			backgroundThread.cancel();
			backgroundThread = null;
			button1.setText("Start");
			progressBar.setProgress(0);
		
		} else {
			ObservableList<String> unknownLinks = FXCollections.observableArrayList();
			ObservableList<String> validLinks = FXCollections.observableArrayList();
	        ObservableList<String> expiredLinks = FXCollections.observableArrayList();
			backgroundThread = new Service<Void>() {	// background processing to avoid blocking the GUI			
				@Override
				protected Task<Void> createTask() {
					return new Task<Void>() {				
						@Override
						protected Void call() throws Exception {						
							Util util = new Util();
							GetPromo promo = new GetPromo();
							boolean end = false;	
							while(!end){
								if(isCancelled()) // check if is canceled
									return null;
								try {
									System.out.println(util.getUrl());
									// we MUST save the result of getUrl before the execution of run later. because we dont know 
									//when the thread is gona be scehduled and the url might already been incremented in this thread.
									String tmp = util.getUrl(); 
									int responce = promo.coonection(tmp);
									if(responce == 200) { //OK
										if(promo.search()) { //promotion expire date found
											if(util.checkExpired(promo.getData())) {
												Platform.runLater(new Runnable() {											
													@Override
													public void run() {
														expiredLinks.add(tmp);
														expired.setItems(expiredLinks);
													}
												});
												end = util.add();
											} else {	
												
												Platform.runLater(new Runnable() {											
													@Override
													public void run() {
														// we MUST save the result of getUrl before the execution of run later. because we dont know 
														//when the thread is gona be scehduled and the url might already been incremented in this thread.
														// that's why we use tmp instead of util.getUrl()
														validLinks.add(tmp);
														valid.setItems(validLinks);	
														
													}
												});
											}
										} else { //expire date not found
											Platform.runLater(new Runnable() {											
												@Override
												public void run() {
													unknownLinks.add(tmp);
													valid.setItems(unknownLinks);											
												}
											});
										}
									}
									//Thread.sleep(100);

										end = util.add();
									
								} catch (IOException e) {
									// TODO Auto-generated catch block
									System.out.println("Connection failed");
								}
								
							}
							Platform.runLater(new Runnable() {											
								@Override
								public void run() {
									button1.setText("Start");
									progressBar.setProgress(0);										
								}
							});
							return null;
						}
					};
				}
			};
			progressBar.setProgress(-1);
			button1.setText("Stop");
			backgroundThread.start();
		}
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		assert button1!= null : "fx:id=\"start\" was not injected: check your FXML file 'MainWindow.fxml'.";
	}

}
