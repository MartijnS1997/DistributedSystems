package interfaces;

import rental.company.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;


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
     * Get the reservations each client has made
     * @return a map of (client, nb_res)
     * @throws RemoteException
     */
    Map<String, Long> getReservationsByCustomer() throws RemoteException;

}
