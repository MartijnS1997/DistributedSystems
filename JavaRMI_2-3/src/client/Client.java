package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;
import java.util.List;
import java.util.Set;

import interfaces.CarRentalCompanyRemote;
import rental.company.CarType;
import rental.company.Quote;
import rental.company.Reservation;
import rental.company.ReservationConstraints;

public class Client extends AbstractTestBooking {

	/************
	 * PRIVATES *
	 ************/

	private CarRentalCompanyRemote rentalCompany;

	private CarRentalCompanyRemote getRemoteCarRentalCompany() {
		return rentalCompany;
	}

	/********
	 * MAIN *
	 ********/
	
	public static void main(String[] args) throws Exception {
		
		String carRentalCompanyName = "Hertz";
		
		// An example reservation scenario on car rental company 'Hertz' would be...
		Client client = new Client("simpleTrips", carRentalCompanyName);
		client.run();
	}
	
	/***************
	 * CONSTRUCTOR *
	 ***************/
	
	public Client(String scriptFile, String carRentalCompanyName) throws RemoteException, NotBoundException {
		super(scriptFile);
		loadCarRentalCompany(carRentalCompanyName);
	}

	public void loadCarRentalCompany(String carRentalCompanyName) throws RemoteException, NotBoundException {
		System.setSecurityManager(null);
		Registry registry = LocateRegistry.getRegistry();
		CarRentalCompanyRemote stub = (CarRentalCompanyRemote) registry.lookup(carRentalCompanyName);
		this.rentalCompany = stub;
	}



	/**
	 * Check which car types are available in the given period
	 * and print this list of car types.
	 *
	 * @param 	start
	 * 			start time of the period
	 * @param 	end
	 * 			end time of the period
	 * @throws 	Exception
	 * 			if things go wrong, throw exception
	 */
	@Override
	protected void checkForAvailableCarTypes(Date start, Date end) throws Exception {
		Set<CarType> allCarTypes = getRemoteCarRentalCompany().getAvailableCarTypes(start,end);
		for (CarType carType: allCarTypes) {
			System.out.println(carType.toString());
		}
	}



	/**
	 * Retrieve a quote for a given car type (tentative reservation).
	 * 
	 * @param	clientName 
	 * 			name of the client 
	 * @param 	start 
	 * 			start time for the quote
	 * @param 	end 
	 * 			end time for the quote
	 * @param 	carType 
	 * 			type of car to be reserved
	 * @param 	region
	 * 			region in which car must be available
	 * @return	the newly created quote
	 *  
	 * @throws 	Exception
	 * 			if things go wrong, throw exception
	 */
	@Override
	protected Quote createQuote(String clientName, Date start, Date end,
                                String carType, String region) throws Exception {
		ReservationConstraints reservationConstraints = new ReservationConstraints(start,end,carType,region);
		return getRemoteCarRentalCompany().createQuote(reservationConstraints, clientName);
	}

	/**
	 * Confirm the given quote to receive a final reservation of a car.
	 * 
	 * @param 	quote 
	 * 			the quote to be confirmed
	 * @return	the final reservation of a car
	 * 
	 * @throws 	Exception
	 * 			if things go wrong, throw exception
	 */
	@Override
	protected Reservation confirmQuote(Quote quote) throws Exception {
		return getRemoteCarRentalCompany().confirmQuote(quote);
	}
	
	/**
	 * Get all reservations made by the given client.
	 *
	 * @param 	clientName
	 * 			name of the client
	 * @return	the list of reservations of the given client
	 * 
	 * @throws 	Exception
	 * 			if things go wrong, throw exception
	 */
	@Override
	protected List<Reservation> getReservationsByRenter(String clientName) throws Exception {
		List<Reservation> reservations = getRemoteCarRentalCompany().getYourReservations(clientName);
		StringBuilder bldr = new StringBuilder();
		System.out.println("\n_____RESERVATIONS_____");

		for (Reservation res: reservations) {
			bldr.append("CarType: " + res.getCarType() + "\n");
			bldr.append("CarId: " + res.getCarId() + "\n");
			bldr.append("Start date: " + res.getStartDate().toString() + "\n");
			bldr.append("End date: " + res.getEndDate().toString() + "\n");
			bldr.append("Price: " + res.getRentalPrice() + "\n");
			bldr.append("_____________________ \n");
			System.out.println(bldr.toString());
		}
		return reservations;
	}

	/**
	 * Get the number of reservations for a particular car type.
	 * 
	 * @param 	carType 
	 * 			name of the car type
	 * @return 	number of reservations for the given car type
	 * 
	 * @throws 	Exception
	 * 			if things go wrong, throw exception
	 */
	@Override
	protected int getNumberOfReservationsForCarType(String carType) throws Exception {
		// We are always the manager
		return getRemoteCarRentalCompany().getCarTypeReservationCount(carType,true);
	}
}