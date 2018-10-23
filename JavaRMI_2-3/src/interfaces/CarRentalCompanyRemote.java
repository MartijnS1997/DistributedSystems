package interfaces;

import rental.company.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Set;


public interface CarRentalCompanyRemote extends Remote {

    static final String REMOTE_COMPANY_NAME = "Hertz";

    Set<CarType> getAvailableCarTypes(Date start, Date end) throws RemoteException;

    Quote createQuote(ReservationConstraints constraints, String client) throws RemoteException, ReservationException;

    Reservation confirmQuote(Quote quote) throws RemoteException, ReservationException;

    List<Reservation> getYourReservations(String clientName) throws RemoteException;

    int getCarTypeReservationCount(String carTypes, boolean IAmTheManager) throws IllegalAccessException, RemoteException;
}
