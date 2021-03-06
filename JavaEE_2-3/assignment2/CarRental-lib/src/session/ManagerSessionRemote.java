package session;

import java.util.List;
import java.util.Set;
import javax.ejb.Remote;
import rental.Car;
import rental.CarType;
import rental.ReservationConstraints;

@Remote
public interface ManagerSessionRemote {
    
    public Set<CarType> getCarTypes(String company);
    
        /**
     * Get the number of reservations for a particular car type
     * @param company
     * @param type
     * @return 
     */
    public int getNumberOfReservations(String company, String type);
      
    /**
     * Adds a new car rental company entity in the database in the backend
     * @param companyName the name of the company, will be used as the primary key
     * @param regions the regions in which the car rental company will be active
     * @param cars the cars that belong to the car rental company
     */
    public void addRentalCompany(String companyName, List<String> regions, List<Car> cars);
    
     /**
     * Look up all car rental companies
     * @return 
     */
    public List<String> getAllRentalCompanies();
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
    public CarType getMostPopular(String company, int year);
    
    /**
     * Get the cheapest car type available given the constraints
     * @param constraints
     * @return the cheapest car type
     */
    public CarType getCheapestCarType(ReservationConstraints constraints);
    
    public int getNumberOfReservationsBy(String clientName);
}