package italo.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import com.sun.javafx.application.PlatformImpl;

import italo.core.GetPromo;
import italo.core.Util;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.util.Duration;

public class MainWindowController {
	@FXML private ListView<String> valid;
	@FXML private ListView<String> expired;
	@FXML private ListView<String> unkown;
	private Service<Void> backgroundThread;
	
	public void test(){
         ObservableList<String> data = FXCollections.observableArrayList();	
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
							try {
								int responce = promo.coonection(util.getUrl());
								if(responce == 200) { //OK
									if(promo.search()) { //promotion expire date found
										if(util.checkExpired(promo.getData())) {
											end = util.add();
										} else {
											Platform.runLater(new Runnable() {
												
												@Override
												public void run() {
													data.add(util.getUrl());
//													System.out.println(Platform.isFxApplicationThread());
													valid.setItems(data);
													
												}
											});
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
						
						return null;
					}
				};
			}
		};
		
		backgroundThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {		
			@Override
			public void handle(WorkerStateEvent event) {
				valid.accessibleTextProperty().unbind();			
			}
		});
		
		backgroundThread.restart();
	}
	
 public void cose( ObservableList<String> data){
	 
		
 }
}
