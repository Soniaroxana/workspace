package cz.vutbr.fit.airlines;

public class FlightTicket {

	private String name = null;
	private String surName = null;
	
	public final int position;
	
	public FlightTicket (int seat){
		position = seat;
	}
	
	public synchronized void buy(String n, String sn){
		name = n;
		surName = sn;
	}

	public synchronized String getName(){
		return name;
	}

	public synchronized String getSurName(){
		return surName;
	}

	public synchronized boolean isFree(){
		return (name == null);
	}
}
