package cz.vutbr.fit.airlines;

import java.util.HashSet;

public class Flight {

	public final String id;

	public final String source;
	public final String destination;

	private final FlightTicket[] tickets;

	public Flight(String id, String source, String destination, int capacity) {
		super();
		this.id = id;
		this.source = source;
		this.destination = destination;

		tickets = new FlightTicket[capacity];

		for (int i = 0; i < capacity; i++) {
			tickets[i] = new FlightTicket(i);
		}
	}

	/**
	 * @return the source
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @return the destination
	 */
	public String getDestination() {
		return destination;
	}

	public HashSet<FlightTicket> getFreeSeats() {
		HashSet<FlightTicket> tmp = new HashSet<FlightTicket>();

		for (FlightTicket ft : tickets)
			if (ft.isFree())
				tmp.add(ft);

		return tmp;
	}

	public String listOfPassangers() {
		StringBuilder sb = new StringBuilder();

		sb.append(" Flight id:");
		sb.append(id);
		sb.append(" from ");
		sb.append(source);
		sb.append(" to ");
		sb.append(destination);
		sb.append("\n");

		for (FlightTicket ft : tickets) {
			sb.append(ft.position);
			sb.append(" ");
			if (ft.isFree())
				sb.append("free");
			else {
				sb.append(ft.getName());
				sb.append(" ");
				sb.append(ft.getSurName());
				sb.append("\n");
			}
		}
		
		return sb.toString();
	}
}
