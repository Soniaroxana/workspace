package cz.vutbr.fit.airlines;

public class Customer {

	public final String dest;
	public final String name;
	public final String surName;
	
	private FlightTicket ft = null;
	
	public Customer(String dest, String name, String surName) {
		super();
		this.dest = dest;
		this.name = name;
		this.surName = surName;
	}

	public String getName(){
		return name;
	}

	public String getSurName(){
		return surName;
	}


	public String getDestination() {
		return dest;
	}
	
	public synchronized void setTicket(FlightTicket t){
		ft = t;
	}
	
	public synchronized FlightTicket getTicket(){
		return ft;
	}
}
