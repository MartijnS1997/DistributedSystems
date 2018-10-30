package interfaces;

import rental.company.*;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

public interface RentalSessionRemote extends SessionRemote {

    /**
     * creates a quote, given the provided constraints
     * @param constraints the constraints used in creating a quote
     * @return a copy of the quote that was created
     * @throws ReservationException in case that the quote was not satisfiable
     */
    Quote createQuote(ReservationConstraints constraints) throws ReservationException, RemoteException;

    /**
     * getter for the quotes made by the user during this session
     * @return a collection of quotes made by the current user
     */
    Collection<Quote> getCurrentQuotes() throws RemoteException;

    /**
     * Confirms all the quotes that are currently made by the session
     * @return the reservations corresponding to the quotes
     * @throws ReservationException in case the quotes could not be satisfied
     *         If one quote could not be satisfied, the entire transaction is rolled back and no quotes are confirmed
     */
    Collection<Reservation> confirmQuotes() throws ReservationException, RemoteException;

    /**
     * getter for the cheapest car type
     * @param start start date for the availability
     * @param end end date for the availability
     * @param region the region to look in for the cheapest car type
     * @return the car type that is the cheapest
     * note: returns the carType instead of the string such that the customer can inspect the properties of the car
     */
    CarType getCheapestCarType(Date start, Date end , String region) throws RemoteException;

    /**
     * Getter for all the available car types
     * @return a collection of all the car types that are available
     */
    Collection<CarType> getAvailableCarTypes(Date start, Date end, String companyName) throws RemoteException, ReservationException;

    /**
     * Getter for all the rental companies that are managed by the agency
     * @return a collection of the company names provided by the rental agency
     */
    Collection<String> getAllCompanies() throws RemoteException;
}
