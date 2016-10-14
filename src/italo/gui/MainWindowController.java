package italo.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import italo.core.GetPromo;
import italo.core.Util;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

public class MainWindowController implements Initializable {
	@FXML private ListView<String> valid;
	@FXML private ListView<String> expired;
	@FXML private ListView<String> unkown;
	private Service<Void> backgroundThread;
	Thread myTaskThread;
	MyTask myTask;
	
	public void test(){
		
//		 myTask = new MyTask();
//		 valid.accessibleTextProperty().bind(myTask.messageProperty());
//		 myTaskThread = new Thread(myTask);
//         myTaskThread.start();
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
											data.add(util.getUrl());
											valid.setItems(data);	
											//updateMessage(data.toString()); // non so perchè funziona al posto di 
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
		
		valid.accessibleTextProperty().bind(backgroundThread.messageProperty());
//		expired.accessibleTextProperty().bind(backgroundThread.messageProperty());
		
		backgroundThread.start();
	}
	
	class MyTask extends Task<Void>{
        
        @Override
        protected Void call() throws Exception {
        	for(int i=0; i<100; i++)
                updateMessage("cirri"+i);
            return null;
        }
         
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
	

}
