package interfaces;

import rental.company.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;


public interface CarRentalCompanyRemote extends Remote {
    /**
     * The name of the company
     * @return the name of the company. Used to register the remote car rental company
     * @throws RemoteException
     */
    String getName() throws RemoteException;

    /**
     * Get all car types in this company
     * @param start the start date
     * @param end the end date
     * @return a set of car types
     * @throws RemoteException
     */
    Set<CarType> getAvailableCarTypes(Date start, Date end) throws RemoteException;

    /**
     * Get the cheapest car type for this company
     * @param start the start date
     * @param end the end date
     * @return a car type that is the cheapest
     */
    CarType getCheapestCarType(Date start, Date end) throws  RemoteException;

    /**
     * Create a quote on behalf of a client
     * @param constraints the constraints for the quote, like region and start/end date
     * @param client the client making the quote
     * @return the quote made
     * @throws RemoteException
     * @throws ReservationException
     */
    Quote createQuote(ReservationConstraints constraints, String client) throws RemoteException, ReservationException;

    /**
     * Confirm a quote
     * @param quote the quote to be confirmed
     * @return the reservation that has been made
     * @implNote Should happen atomically!
     * @throws RemoteException
     * @throws ReservationException
     */
    Reservation confirmQuote(Quote quote) throws RemoteException, ReservationException;

    /**
     * Get all reservations for a client
     * @param clientName the client to be searched for
     * @return a list of reservations by the client
     * @throws RemoteException
     */
    List<Reservation> getYourReservations(String clientName) throws RemoteException;

    /**
     * Get the number of times a car type has been reserved
     * @param carTypes a string of a car type
     * @return the number of reservations (int)
     * @throws RemoteException
     */
    int getCarTypeReservationCount(String carTypes) throws RemoteException;

    /**
     * Getter for the  most wanted car type in the specified date
     * @param calendarYear integer representing the year in which to look for reservations
     * @return the most wanted car type
     * @throws RemoteException
     */
    CarType mostWanted(int calendarYear) throws  RemoteException;

    /**
     * Cancels the provided reservation
     * @param reservation the reservation to be cancelled
     * @throws RemoteException
     */
    void cancelReservation(Reservation reservation) throws RemoteException;

    /**
     * Get the regions where the company is active
     * @return a list of regions
     */
    List<String> getRegions() throws RemoteException;

    /**
     * A getter for all car types
     * @return a collection of all registered car types
     */
    Collection<CarType> getAllCarTypes() throws RemoteException;

    public String getBestCustomer() throws RemoteException;

}
