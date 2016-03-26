package cz.vutbr.fit.airlines;

import java.util.HashSet;
import java.util.Random;

public class CustomerGenerator implements Runnable {

	private int numCustom;
	
	private Customer[] customs;
	private InternetSeller[] avalSellers;
	
	public CustomerGenerator (int numCust, InternetSeller[] sel){
		numCustom = numCust;
		avalSellers = sel;
		customs = new Customer[numCust];
	}
	
	@Override
	public void run() {
		
		String name = Thread.currentThread().getName();
		Random r = new Random(System.currentTimeMillis());
		
		for (int i=0; i<numCustom; i++){
			
			String dst = Airlines.DESTINATIONS[r.nextInt(Airlines.DESTINATIONS.length)];
			
			customs[i] = new Customer(dst, name, Integer.toString(i));
			
			// randomly choose a seller
			InternetSeller is = avalSellers[r.nextInt(avalSellers.length)];
			
			// try to get on board :-)
			is.newCustomer(customs[i]);
			
			try {
				Thread.sleep(0, r.nextInt(2000));
			} catch (InterruptedException e) {
				// ignore
			}
			
			
		}
	}

	
	public int howManyGetOnBoard(){
		int ret = 0;
		
		HashSet< FlightTicket > ticketSet = new HashSet< FlightTicket >();
		
		for (Customer c : customs)
			if (c.getTicket() != null)
			  if (!ticketSet.contains(c.getTicket()))
			  {
				  ret++;
				  ticketSet.add(c.getTicket());
			  }
		
		return ret;
	}
}
