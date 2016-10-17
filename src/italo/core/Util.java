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
		numbers[1] = 0;
		numbers[2] = 0;
	}
	
	public Util(char l, int[] n){
		letters = l;
		numbers = n;
	}
	
	public String getUrl() {
		return GET_URL + letters + numbers[0] + numbers[1] + numbers[2];
	}
	public String getUrlCode() {
		return "" + numbers[0] + numbers[1] + numbers[2];
	}
	 public double getProgress() {
		 return numbers[2] + numbers[1]*10 + numbers[0]*100;
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
	
	// return true at 999
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
			return false;
		}
		return true;
	}
}
