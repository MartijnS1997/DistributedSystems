package rental.company;

import interfaces.CarRentalCompanyRemote;

import java.rmi.RemoteException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CarRentalCompany implements CarRentalCompanyRemote {

	private static Logger logger = Logger.getLogger(CarRentalCompany.class.getName());
	
	private List<String> regions;
	private String name;
	private List<Car> cars;
	private Map<String,CarType> carTypes = new HashMap<String, CarType>();
	private Set<String> customers = new HashSet<>();

	/***************
	 * CONSTRUCTOR *
	 ***************/

	public CarRentalCompany(String name, List<String> regions, List<Car> cars) {
		logger.log(Level.INFO, "<{0}> Car Rental Company {0} starting up...", name);
		setName(name);
		this.cars = cars;
		setRegions(regions);
		for(Car car:cars)
			carTypes.put(car.getType().getName(), car.getType());
		logger.log(Level.INFO, this.toString());
	}

	/********
	 * NAME *
	 ********/

	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

    /***********
     * Regions *
     **********/
    private void setRegions(List<String> regions) {
        this.regions = regions;
    }

    @Override
    public List<String> getRegions() {
        return this.regions;
    }
    
    public boolean hasRegion(String region) {
        return this.regions.contains(region);
    }
	
	/*************
	 * CAR TYPES *
	 *************/

	public synchronized Collection<CarType> getAllCarTypes() {
		return carTypes.values();
	}
	
	public synchronized CarType getCarType(String carTypeName) {
		if(carTypes.containsKey(carTypeName))
			return carTypes.get(carTypeName);
		throw new IllegalArgumentException("<" + carTypeName + "> No car type of name " + carTypeName);
	}
	
	// mark
	public synchronized boolean isAvailable(String carTypeName, Date start, Date end) {
		logger.log(Level.INFO, "<{0}> Checking availability for car type {1}", new Object[]{name, carTypeName});
		if(carTypes.containsKey(carTypeName)) {
			return getAvailableCarTypes(start, end).contains(carTypes.get(carTypeName));
		} else {
			throw new IllegalArgumentException("<" + carTypeName + "> No car type of name " + carTypeName);
		}
	}

	@Override
	public synchronized Set<CarType> getAvailableCarTypes(Date start, Date end) {
		Set<CarType> availableCarTypes = new HashSet<CarType>();
		for (Car car : cars) {
			if (car.isAvailable(start, end)) {
				availableCarTypes.add(car.getType());
			}
		}
		return availableCarTypes;
	}

	@Override
	public CarType getCheapestCarType(Date start, Date end) {
		return getAvailableCarTypes(start,end).stream()
				.reduce((cheapest,current) -> cheapest = current.getRentalPricePerDay() < cheapest.getRentalPricePerDay()
				? current : cheapest).get();
	}
	
	/*********
	 * CARS *
	 *********/
	
	private Car getCar(int uid) {
		for (Car car : cars) {
			if (car.getId() == uid)
				return car;
		}
		throw new IllegalArgumentException("<" + name + "> No car with uid " + uid);
	}
	
	 private List<Car> getAvailableCars(String carType, Date start, Date end) {
		List<Car> availableCars = new LinkedList<Car>();
		for (Car car : cars) {
			if (car.getType().getName().equals(carType) && car.isAvailable(start, end)) {
				availableCars.add(car);
			}
		}
		return availableCars;
	}



    /****************
	 * RESERVATIONS *
	 ****************/

    /**
     * getter for the set of customers. This set is used to get the best all time customer
     */
    private Set<String> getCustomers() {
        return customers;
    }

    @Override
	public synchronized Quote createQuote(ReservationConstraints constraints, String client)
			throws ReservationException {
		logger.log(Level.INFO, "<{0}> Creating tentative reservation for {1} with constraints {2}", 
                        new Object[]{name, client, constraints.toString()});
		
				
		if(!regions.contains(constraints.getRegion()) || !isAvailable(constraints.getCarType(), constraints.getStartDate(), constraints.getEndDate()))
			throw new ReservationException("<" + name
				+ "> No cars available to satisfy the given constraints.");

		CarType type = getCarType(constraints.getCarType());
		
		double price = calculateRentalPrice(type.getRentalPricePerDay(),constraints.getStartDate(), constraints.getEndDate());
		
		return new Quote(client, constraints.getStartDate(), constraints.getEndDate(), getName(), constraints.getCarType(), price);
	}

	// Implementation can be subject to different pricing strategies
	private double calculateRentalPrice(double rentalPricePerDay, Date start, Date end) {
		return rentalPricePerDay * Math.ceil((end.getTime() - start.getTime())
						/ (1000 * 60 * 60 * 24D));
	}
	 @Override
	 public synchronized Reservation confirmQuote(Quote quote) throws ReservationException {

		 logger.log(Level.INFO, "<{0}> Reservation of {1}", new Object[]{name, quote.toString()});
		List<Car> availableCars = getAvailableCars(quote.getCarType(), quote.getStartDate(), quote.getEndDate());
		if(availableCars.isEmpty())
			throw new ReservationException("Reservation failed, all cars of type " + quote.getCarType()
	                + " are unavailable from " + quote.getStartDate() + " to " + quote.getEndDate());
		Car car = availableCars.get((int)(Math.random()*availableCars.size()));
		
		Reservation res = new Reservation(quote, car.getId());
		car.addReservation(res);
		getCustomers().add(quote.getCarRenter()); //save the customer of this purchase
		return res;
	}

	@Override
	public synchronized List<Reservation> getYourReservations(String clientName) throws RemoteException {
		List<Reservation> yourReservations = new ArrayList<>();
		for (Car car: cars) {
			for (Reservation res: car.getAllReservations()) {
				if (res.getCarRenter().equals(clientName)) {
					yourReservations.add(res);
				}
			}
		}
		return yourReservations;
	}

	@Override
	public synchronized int getCarTypeReservationCount(String carType) {
		int resCount = 0;
		CarType type;

		try{
			type = getCarType(carType);
		}catch(IllegalArgumentException e){
			return 0; // the car type does not exist
		}

		for (Car car: cars) {
			if (car.getType().equals(type)) {
				resCount += car.getAllReservations().size();
			}
		}
		return resCount;
	}

	@Override
	public String getBestCustomer() throws RemoteException {
    	String bestCustomer ="";
    	int mostReservations = 0;
    	for(String customer : getCustomers()) {
			int currentReservations = getYourReservations(customer).size();
    		if (currentReservations > mostReservations) {
    			mostReservations = currentReservations;
    			bestCustomer = customer;
			}
		}
		return bestCustomer;
	}

	/**
	 * Gets the number of reservations for each client
	 * @return a map with for each customer the number of reservations made by that customer
	 */
	@Override
	public Map<String, Integer> reservationsPerCustomer() throws RemoteException {

		Map<String, Integer> reservationCount = new HashMap<>();

    	for(String customer : getCustomers()){
    		reservationCount.put(customer, getYourReservations(customer).size());
		}

		return reservationCount;
	}

//  we could have replaced the inner loop with the following lambda...
//    long reservationCount = cars.stream().
//                    map(car->car.getAllReservations().stream().
//                    filter(reservation -> reservation.getCarRenter().equals(customer)).count())
//                    .reduce(0L, Long::sum);

    @Override
    public synchronized void cancelReservation(Reservation res) {
		logger.log(Level.INFO, "<{0}> Cancelling reservation {1}", new Object[]{name, res.toString()});
		getCar(res.getCarId()).removeReservation(res);
	}
	
	@Override
	public synchronized String toString() {
		return String.format("<%s> CRC is active in regions %s and serving with %d car types", name, listToString(regions), carTypes.size());
	}
	
	private static String listToString(List<? extends Object> input) {
		StringBuilder out = new StringBuilder();
		for (int i=0; i < input.size(); i++) {
			if (i == input.size()-1) {
				out.append(input.get(i).toString());
			} else {
				out.append(input.get(i).toString()+", ");
			}
		}
		return out.toString();
	}

    @Override
    public synchronized CarType mostWanted(int year) throws RemoteException {
        //drop the rest of the date
        CarType mostWanted = null;
        long mostReservations = 0;

        for(CarType carType: carTypes.values()){
            //get all the cars of the particular type
            Collection<Car> carsOfType = cars.stream().filter(car-> car.getType().equals(carType)).collect(Collectors.toList());
            long currentReservations = getNumberOfReservationsInYear(carsOfType, year);
            if(currentReservations > mostReservations){
                mostWanted = carType;
                mostReservations = currentReservations;
            }
        }
        return  mostWanted;
    }

    /**
     * counts the number of reservations for a list of cars in a given year
     * @param cars the cars to count the reservations for
     * @param year the year for which the reservations are counted
     * @return the number  of reservations for that year
     */
    private long getNumberOfReservationsInYear(Collection<Car> cars, int year){
        int total = 0;
        for(Car car: cars){
            total += car.getAllReservations().stream().filter(reservation -> {
            	return reservation.getStartDate().getYear() + 1900 == year; //for some arcane reason java decided that christ was not born in the year 0 but in 1900
            }).count();
        }
        return total;
    }

}