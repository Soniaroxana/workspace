package cz.vutbr.fit.airlines;

import java.util.Random;

public class Simulator{

	static Thread[] sellersThreads;

	static Thread[] buyersThreads;

	// control the number of threads in conflict
	static int numThreads = 4;
	static int numCThreads = 4;
	static int numCustom = 4;

	static int numLoop = 100;

	static Flight flight;

	static long currentTime;

	public static void main(String args[]) {


		if (args.length < 3) {
			System.out.println(" Simulator X Y Z (X=num_selling_threads, Y=num_custom_threads, Z=num_customers_in_1_thread)");
			return;
		}
		// get info from args
		try {
			numThreads = Integer.parseInt(args[0]);
			numCThreads = Integer.parseInt(args[1]);
			numCustom = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			System.err.println(" Argument must be an integer.");
			return;
		}

		Random r = new Random(System.currentTimeMillis());
/*		numThreads = r.nextInt(5)+1;
		numThreads = r.nextInt(5)+1;
		numCustom = r.nextInt(10)+1;
*/		
		sellersThreads = new Thread[numThreads];
		buyersThreads = new Thread[numCThreads];
		
		currentTime = System.currentTimeMillis();


		
		Airlines[] airlines = new Airlines[r.nextInt(10)+5];
		for (int i=0; i<airlines.length; i++){
			airlines[i] = new Airlines("air_"+i);
			airlines[i].generateFlights();
		}
		
		InternetSeller[] sellers = new InternetSeller[numThreads];
		// prepare sellers
		for (int i = 0; i < numThreads; i++) {
			sellers[i] = new InternetSeller();
			sellersThreads[i] = new Thread(sellers[i]);
			sellersThreads[i].start();
		}

		CustomerGenerator[] buyers = new CustomerGenerator[numCThreads];
		// prepare customer simulators
		for (int i = 0; i < numCThreads; i++) {
			buyers[i] = new CustomerGenerator(numCustom, sellers);
			buyersThreads[i] = new Thread(buyers[i]);
			buyersThreads[i].start();
		}

		// wait for sellers
		for (int i = 0; i < numCThreads; i++) {			
			try {
				buyersThreads[i].join();
			} catch (InterruptedException e) {
				System.out.printf("grrrr\n");
			}
		}
		
		
		// wait for sellers
		for (int i = 0; i < numThreads; i++) {			
			sellers[i].finishSelling();
		}
		for (int i = 0; i < numThreads; i++) {			
			try {
				sellersThreads[i].join();
			} catch (Exception e) {
				System.out.printf("grrrr\n");
			}
		}

		// give info concerning time
		System.out.println(" TIME="
				+ (System.currentTimeMillis() - currentTime));


		// compare statistics
		int statC = 0;
		int statS = 0;
		for (CustomerGenerator g : buyers)
			statC += g.howManyGetOnBoard();
		
		for (InternetSeller s : sellers)
			statS += s.getNumSolds();
		
		if (statC != statS){
			RuntimeException e = new RuntimeException(" Statistics do not match: sellers("+statS+") buyers("+statC+")");
			//cz.vutbr.fit.coverage.ExceptionCover.reportException(e);
			throw e;
		}
	}


}
