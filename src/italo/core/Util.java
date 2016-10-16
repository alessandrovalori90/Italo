package italo.core;

import java.util.Calendar;

public class Util {
	private final String GET_URL = "http://www.italotreno.it/it/programma-fedelta-italo-piu/Promozioni/";
	private char letters;
	private int  numbers [];
	
	public Util(){
		letters = 'L';
		numbers = new int[3];
		numbers[0] = 0;
		numbers[1] = 1;
		numbers[2] = 8;
	}
	
	public Util(char l, int[] n){
		letters = l;
		numbers = n;
	}
	
	public String getUrl() {
		return GET_URL + letters + numbers[0] + numbers[1] + numbers[2];
	}
	
	//return true if expired
	public boolean checkExpired(int[] date) {
		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH)+1;
		if(month>date[1])
			return true;
		else if(month<date[1])
			return false;
		else {
			int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
			return dayOfMonth>date[0];
		}
	}
	
	public boolean add() {
		if(numbers[2] < 9 ) {
			numbers[2]++;
			return false;
		} else if (numbers[1] < 9 ){
			numbers[2] = 0;
			numbers[1]++;
			return false;
		} else if (numbers[0] < 9 ){
			numbers[2] = 0;
			numbers[1] = 0;
			numbers[0]++;
			return true;
		}
		return true;
	}
}
