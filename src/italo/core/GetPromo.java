package italo.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.MatchResult;

public class GetPromo {	
	private int data[] = null;
	private MatchResult anno;
	private HttpURLConnection con;
	
	public GetPromo() {}
	
	public int[] getData() {
		return data;
	}
	public MatchResult getAnno(){
		return anno;
	}
	public int connection(String url) throws IOException{
		URL obj = new URL(url);
		con = (HttpURLConnection) obj.openConnection();
		con.setConnectTimeout(15 * 1000);
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

			return searchHTML(response.toString());
	}
	
	private boolean searchHTML(String buff){
		boolean found = false;
		boolean foundDiv = false;
		Scanner s = new Scanner (buff);
		// espressione regolare per identificare i separatori
		s.useDelimiter("[\\s'.’]+");
		while (s.hasNext()) {
			//la data è scritta sempre dopo la parola entro Ex: da comprare entro lunedi 11 ottobre
			String temp = s.next();
			if(temp.contains("informativa-image")&&found){
				s.close();
				return found;
			}
		    if (temp.contains("box-informativa"))
		    	foundDiv = true;
		    if(foundDiv){
		    	if (temp.equalsIgnoreCase("entro")) {
			        found = true;
			        try {	        		        
			        	data = interpreter(s); //if it doest find the date if gives exception
			        	boolean ret =searchYear(s);
			        	s.close();
			        	return ret;
					} catch (Exception e) {
						return false;
					}
		    	}
		    }
		}
		s.close();
		return found;
		
	}
	
	private boolean searchYear(Scanner s) {
		s.findInLine("(201\\d)");
		MatchResult tmp = s.match();
		if(tmp!=null) {
			anno = tmp;
			return true;
		}
		return false;
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
