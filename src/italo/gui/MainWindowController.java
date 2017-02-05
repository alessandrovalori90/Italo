package italo.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import italo.core.GetPromo;
import italo.core.Util;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;

public class MainWindowController implements Initializable{
	@FXML private ListView<Hyperlink> valid;
	@FXML private ListView<Hyperlink> expired;
	@FXML private ListView<Hyperlink> unknown;
	@FXML private Button button1;
	@FXML private ProgressBar progressBar;
	@FXML private Label label;
	
	private final String labelStart = "Scann progress...";
	private final String listPlaceholder= "Empty List";
	
	private HostServices service = null;
	private static Service<Void> backgroundThread;
	//minimum progress to show in order to show that the scan started. otherwise the bar would be empty
	private double minprog = 0.04; 
	public void setApp(HostServices s){
		service = s;
	}
	
	public void test(){
		if(backgroundThread != null) {
			backgroundThread.cancel();
			backgroundThread = null;
			button1.setText("Start");
			progressBar.setProgress(0);
		
		} else {
			ObservableList<Hyperlink> unknownLinks = FXCollections.observableArrayList();
			ObservableList<Hyperlink> validLinks = FXCollections.observableArrayList();
	        ObservableList<Hyperlink> expiredLinks = FXCollections.observableArrayList();
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
								if(isCancelled()) {
									Platform.runLater(new Runnable() {											
										@Override
										public void run() {
												progressBar.setProgress(0);
												label.setText(labelStart);
										}
									});
									return null;
								}
									
								try {
									// we MUST save the result of getUrl before the execution of run later. because we dont know 
									//when the thread is gona be scehduled and the url might already been incremented in this thread.
									String tmp = util.getUrl(); 
									int responce = promo.connection(tmp);
									if(responce == 200) { //OK
										if(promo.search()) { //promotion expire date found
											if(util.checkExpired(promo.getData(), promo.getAnno())) {
												Platform.runLater(new Runnable() {											
													@Override
													public void run() {
														Hyperlink link = new Hyperlink();
														link.setOnMouseClicked(new EventHandler<Event>() {
															@Override
															public void handle(Event event) {
																service.showDocument(tmp);
															}
														});
														link.setText(tmp);
														expiredLinks.add(link);
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
														Hyperlink link = new Hyperlink();
														link.setOnMouseClicked(new EventHandler<Event>() {
															@Override
															public void handle(Event event) {
																service.showDocument(tmp);
															}
														});
														link.setText(tmp);
														validLinks.add(link);
														valid.setItems(validLinks);	
														
													}
												});
											}
										} else { //expire date not found
											Platform.runLater(new Runnable() {											
												@Override
												public void run() {
													Hyperlink link = new Hyperlink();
													link.setOnMouseClicked(new EventHandler<Event>() {
														@Override
														public void handle(Event event) {
															service.showDocument(tmp);
														}
													});
													link.setText(tmp);
													unknownLinks.add(link);
													unknown.setItems(unknownLinks);									
												}
											});
										}
									}

										end = util.add();
										Platform.runLater(new Runnable() {											
											@Override
											public void run() {
												
												double prog = util.getProgress()/1000;
												label.setText("Page: L" + util.getUrlCode());
												if (prog<minprog)
													progressBar.setProgress(-1);
												else
													progressBar.setProgress(prog);
											}
										});
								} catch (IOException e) { //in case there is no connection							
									Platform.runLater(new Runnable() {											
										@Override
										public void run() {									
											label.setText("Check connection");

										}
									});
								}
								
							}
							// scann finished
							Platform.runLater(new Runnable() {											
								@Override
								public void run() {
									button1.setText("Start");
									progressBar.setProgress(0);	
									label.setText(labelStart);
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
		valid.setPlaceholder(new Label(listPlaceholder));
		unknown.setPlaceholder(new Label(listPlaceholder));
		expired.setPlaceholder(new Label(listPlaceholder));
	}

}
