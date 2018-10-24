package interfaces;

import rental.company.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Set;


public interface CarRentalCompanyRemote extends Remote {
    /**
     * The name of the company
     * @return the name of the company. Used to register the remote car rental company
     * @throws RemoteException
     */
    String getName() throws RemoteException;

    Set<CarType> getAvailableCarTypes(Date start, Date end) throws RemoteException;

    Quote createQuote(ReservationConstraints constraints, String client) throws RemoteException, ReservationException;

    Reservation confirmQuote(Quote quote) throws RemoteException, ReservationException;

    List<Reservation> getYourReservations(String clientName) throws RemoteException;

    int getCarTypeReservationCount(String carTypes, boolean IAmTheManager) throws IllegalAccessException, RemoteException;
}
