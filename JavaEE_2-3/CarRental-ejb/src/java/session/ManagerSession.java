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
     * Get all car types in String form so that CarType doesn't have to be serialized.
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
     * Get the number of reservations for a specific car type of a specific
     * CarRentalCompany
     * @param carType
     * @param companyName
     * @return 
     */
    @Override
    public int getReservationCount(String carType, String companyName) {
         CarRentalCompany rentalCompany = RentalStore.getRentals().get(companyName);
         int reservationAccumulator = 0;
         for (Car car: rentalCompany.getCars()) {
             if(car.getType().getName().equals(carType)){
                 for (Reservation reservation: car.getAllReservations()){
                    reservationAccumulator++;
                 }
             }
         }
        return reservationAccumulator;
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

    @Override
    public int getNumberOfReservationsBy(String clientName) {
        int allReservations = 0;
        for (String rentalCompany: RentalStore.getRentals().keySet()) {
            allReservations += RentalStore.getRental(rentalCompany).getReservationsBy(clientName).size();
        }
        return allReservations;
    }

}
