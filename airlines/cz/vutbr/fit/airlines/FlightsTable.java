package cz.vutbr.fit.airlines;

import java.util.HashMap;
import java.util.HashSet;

public class FlightsTable {
	
	private static HashMap<String, Flight> flights = new HashMap<String, Flight>();
	
	public static synchronized void addFlight(Flight f){
		flights.put(f.id,f);
		
		return;
	}
	
	public static synchronized HashSet<Flight> getFlightsForDestination(String dest){
		HashSet<Flight> sf = new HashSet<Flight>();
		
		for (Flight f : flights.values())
			if (f.destination == dest)
				sf.add(f);
		
		return sf;
	}
}
