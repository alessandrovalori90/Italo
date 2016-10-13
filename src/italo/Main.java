package italo;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import core.GetPromo;

public class Main {
	private static final String GET_URL = "http://www.italotreno.it/it/programma-fedelta-italo-piu/Promozioni/";
	private static char letters = 'L';
	private static int  numbers [] = {0,0,0};
	
	public static void main(String[] args) throws IOException {
		boolean end = true;
		while (end){
			String url_temp = GET_URL + letters + numbers[0] + numbers[1] + numbers[2];
			GetPromo promo = new GetPromo();
			promo.sendGetRequest(url_temp);
			end = add();
			try {
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
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
}
