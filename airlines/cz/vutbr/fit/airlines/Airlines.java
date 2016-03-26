package cz.vutbr.fit.airlines;

import java.util.HashSet;
import java.util.Random;

/**
 * 
 * 
 * @author zdenek
 * 
 */

public class Airlines {
	
	public static final String[] DESTINATIONS = {"PRG", "STL", "VIE"};
		
	public final String name;
	
	private HashSet<Flight> ourFlights = new HashSet<Flight>();
	
	public Airlines(String name){
		this.name = name;
	}
	
	public void generateFlights(){
		Random r = new Random();
		for (int i=0; i < DESTINATIONS.length; i++){
			// each destination
			// capacity 2-100 seats
			Flight f = new Flight("name"+i,DESTINATIONS[i],DESTINATIONS[r.nextInt(DESTINATIONS.length)], r.nextInt(98)+2);
			FlightsTable.addFlight(f);
			ourFlights.add(f);
		}
		
	}


}
