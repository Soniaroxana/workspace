package cz.vutbr.fit.airlines;

import java.util.HashSet;
import java.util.LinkedList;

public class InternetSeller implements Runnable {

	private Integer sold = new Integer(0);

	private LinkedList<Customer> c = new LinkedList<Customer>();

	private volatile boolean end;
	
	public synchronized int getSold() {
		return sold;
	}

	@Override
	public void run() {
		while (!end) {
			waitForCustomer();
			while(sellTicket() != null){
				// operation is performed in sellTicket
			}

		}

	}

	public synchronized void newCustomer(Customer ct) {
		if (ct != null) {
			c.add(ct);
			this.notify();
		}
	}
	
	public synchronized void finishSelling(){
		end = true;
		this.notify();
	}

	private synchronized void waitForCustomer() {
		while (c == null) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				// ignore
			}
		}
	}

	private FlightTicket sellTicket() {
		Customer cus = null;
		synchronized(this){
			if (c.isEmpty())
				return null;
			else
				cus = c.removeFirst();
		}
		
		String dst = cus.getDestination();
		HashSet<Flight> fs = FlightsTable.getFlightsForDestination(dst);
		if (!fs.isEmpty()) {
			for (Flight f : fs) {
				for (FlightTicket ft : f.getFreeSeats()) {
					if (ft.isFree()) {
						// atomicity violation isFree and buy should be atomic
						ft.buy(cus.name, cus.surName);
						cus.setTicket(ft);
						synchronized(sold){
							sold++;
						}
						return ft;
					}
					// else try another
				}
			}
		}

		return null;
	}

	
	public int getNumSolds(){
		return sold.intValue();
	}
}
