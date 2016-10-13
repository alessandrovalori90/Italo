package italo.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class GetPromo {	
	private int data[] = null;
	private HttpURLConnection con;
	
	public GetPromo() {}
	
	public int[] getData() {
		return data;
	}
	public int coonection(String url) throws IOException{
		System.out.println(url);
		URL obj = new URL(url);
		con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");		
		int responseCode = con.getResponseCode();
		return responseCode;
				
	}
	
	// stabilisce la connessione all'url richiesto e trova la data di scadenza
	// Encoding necessariamente in UTF8
	// IOException due to the connection
	public boolean search() throws IOException {		
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream(),"UTF8"));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			return searchSentence(response.toString());
	}
	
	// cerca la frase che contiene la data all'interno della pagina
	private boolean searchSentence(String buff) {
		data = null;
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
		        	data = interpreter(s);
				} catch (Exception e) {
					return false;
				}
		        break;
		    }
		}		
		return found;
	}
	
	// interpreta il testo per trovare la data
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


}
