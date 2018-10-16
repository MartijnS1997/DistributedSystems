package session;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.ejb.Stateless;
import rental.CarType;
import rental.*;

/**
 *
 * @author Clara De Smet
 * @author Martijn Sauwens
 */
@Stateless
public class ManagerSession implements ManagerSessionRemote {

    /**
     * Get all car types in String form so that CarType doesn't have to be serialised.
     * @param companyName
     * @return 
     * Note: We only return the CarType as a string (name). If we wanted to give the manager the ability to
     * change the CarType, we should add another method to the interface that takes a CarType by value and
     * change this method to a return by value (CarType must implement Serializable).
     */
    @Override
    public Set<String> getAllCarTypes(String companyName) {
        Collection<CarType> cartypes = RentalStore.getRentals().get(companyName).getCarTypes();
        // Convert car types to string
        Set<String> carTypesInStrings = new HashSet<String>();
        for (CarType carType: cartypes) {
            carTypesInStrings.add(carType.toString());
        }
        return carTypesInStrings;
    }

    /**
     * Get the number of reservations for a given company
     * @param CarType
     * @param companyName
     * @return 
     */
    @Override
    public int getReservationCount(String CarType, String companyName) {
         CarRentalCompany rentalCompany = RentalStore.getRentals().get(companyName);
         int accumulator = 0;
         for (Car car: rentalCompany.getCars()) {
             for (Reservation reservation: car.getAllReservations())
                 accumulator++;
         }
        return accumulator;
    }

    /**
     * 
     * @param companyName
     * @return 
     */
    @Override
    public String getBestCustomer(String companyName) {
        CarRentalCompany rentalCompany = RentalStore.getRentals().get(companyName);
        String bestCustomer ="";
        int maxReservations = 0;
        for (String customer: rentalCompany.getAllCustomers()) {
            int currentReservationCount = rentalCompany.getReservationsBy(customer).size();
            if ( currentReservationCount > maxReservations) {
                bestCustomer = customer;
                maxReservations = currentReservationCount;
            }
        }
        return bestCustomer;
    }

}
