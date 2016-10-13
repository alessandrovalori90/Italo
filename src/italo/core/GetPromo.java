package italo.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class GetPromo {	
	private static final String GET_URL = "http://www.italotreno.it/it/programma-fedelta-italo-piu/Promozioni/";
	private static char letters = 'L';
	private static int  numbers [] = {0,0,0};
	public GetPromo() {}

	// stabilisce la connessione a tutte le pagine possibili
	// Encoding necessariamente in UTF8
	// IOException due to the connection
	public void sendGetRequest(String url) throws IOException {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");		
		int responseCode = con.getResponseCode();
		System.out.println("url: "+ url +" GET Response Code: " + responseCode);
		
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream(),"UTF8"));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			boolean found = false;
			found = search(response.toString());
			System.out.println(found);
		} else {
			System.out.println("GET request not worked");
		}		

	}
	
	// cerca la frase che contiene la data all'interno della pagina
	private boolean search(String buff) {
		Scanner s = new Scanner (buff);
		// espressione regolare per identificare i separatori
		s.useDelimiter("[\\s'.’]+");
		boolean found = false;
		while (s.hasNext()) {
			//la data è scritta sempre dopo la parola entro Ex: da comprare entro lunedi 11 ottobre
			String temp = s.next();
		    if (temp.equalsIgnoreCase("entro")) {
		        found = true;
		        try {
		        	int data[];		        
		        	data = interpreter(s);
		        	System.out.println(data[0]+ "/" + data[1]);
				} catch (Exception e) {
					return false;
				}
		        break;
		    }
		}		
		return found;
	}
	
	// interpreta il testo per trovare la data
	// prendi le 3 parole dopo la parola chiave "entro" e salto la prima delle 3 per trovare la data di scadenza della promozione
	private int[] interpreter(Scanner s) throws Exception {
		String retString[] = new String[2];
		int retInt[] = new int[2];
		s.next(); //salto una parola (il, l', nome_giorno)
		retString[0] = s.next();
		if(!retString[0].matches("\\d{1,2}")) 
			throw new Exception();
		else
			retInt[0] = Integer.parseInt(retString[0]);
		retString[1] = s.next();
		if(retString[1].matches("\\d{1,2}")) 
			retInt[1] = Integer.parseInt(retString[1]);
		else 
			retInt[1] = monthToInt(retString[1]);
		return retInt;
	}
	
	private int monthToInt(String string) throws Exception {
		String months[] = {
				"gennaio",
				"febbraio",
				"marzo",
				"aprile",
				"maggio",
				"giugno",
				"luglio",
				"agosto",
				"settembre",
				"ottobre",
				"novembre",
				"dicembre"
		};
		for (int i = 0; i < months.length; i++) {
			if(string.matches(months[i]))
				return ++i;
		}
		throw new Exception();
	}
	
	// incrementa le cifre finali dell'url 
	private boolean add() {
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
	
	public void startFullScann() {
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


}
