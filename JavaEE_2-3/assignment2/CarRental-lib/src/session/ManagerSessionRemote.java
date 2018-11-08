package session;

import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.Remote;
import rental.Car;
import rental.CarType;
import rental.ReservationConstraints;

@Remote
public interface ManagerSessionRemote {
    
    public Set<CarType> getCarTypes(String company);
    
    public Set<Car> getCars(String company,String type);
    
    /**
     * Get the number of reservations for a particular car
     * @param company
     * @param type
     * @param carId
     * @return 
     */
    public int getNumberOfReservations(String company, String type, int carId);

    /**
     * Get the number of reservations for a particular car type
     * @param company
     * @param type
     * @return 
     */
    public int getNumberOfReservations(String company, String type);
    
    /**
     * Look up all car rental companies
     * @return 
     */
    public Set<String> getAllRentalCompanies();
    
    /**
     * Get the best client(s) across all car rental companies (has to be a set in case of a tie)
     * @return a set of the best clients
     */
    public Set<String> bestClients();
    
    /**
     * Get the most popular car type of a car rental company for a given calendar year
     * @param company
     * @param year
     * @return the car type with the most reservations
     */
    public CarType getMostPopular(String company, Date year);
    
    /**
     * Get the cheapest car type available given the constraints
     * @param constraints
     * @return the cheapest car type
     */
    public CarType getCheapestCarType(ReservationConstraints constraints);
    
     /**
     * Add a rental company, the company entity gets
     * constructed based on the provided values
     * @param company the name of the company to add
     * @param regions the list of regions the company is active in
     */
    public void addRentalCompany(String company, List<String> regions);
    
//    /**
//     * Add a CarType. The CarRentalCompany should already exist.
//     * @param company
//     * @param carType 
//     */
//    public void addCarType(String company, CarType carType);
//    
    /**
     * Adds a car to the rental company.
     * If the car type to be added already exists, the old car type is 
     * updated
     * @param company the name of the company to add the car to
     * @param car the car to add to the company
     */
    public void addCar(String company, Car car);
    
    /**
     * creates a new company with existing cars
     * @param company the name of the company to add
     * @param regions the regions in which the company operates
     * @param cars the cars that are rented by the company
     */
    public void addCarRentalCompany(String company, List<String> regions, List<Car> cars);
      
}