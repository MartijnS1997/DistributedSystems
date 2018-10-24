package rental.session;

import interfaces.CarRentalCompanyRemote;
import interfaces.ManagerSessionRemote;
import interfaces.RentalSessionRemote;
import interfaces.SessionManagerRemote;
import rental.company.CarRentalAgency;
import rental.company.CarRentalCompany;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class does lifecycle management of sessions, so that CarRentalAgency does not need to be concerned with that.
 */
public class SessionManager implements SessionManagerRemote {

    /**
     * map that stores the sessions created, based on the ID of the session
     */
    private Map<String,Session> sessions = new HashMap<>();

    /**
     * counter to give each session a unique ID
     */
    private long idCounter = 1;


    /**
     * Every SessionManager belongs to one CarRentalCompany and vice versa.
     */
    private final CarRentalAgency rentalAngency;

    /**
     * Getters and setters
     */

    private Map<String, Session> getSessions() {
        return sessions;
    }


    /**
     * Constructor
     */

    SessionManager(CarRentalAgency agency){
        rentalAngency = agency;
    }

    /**
     * Implementation of remote interface methods
     */

    @Override
    public RentalSessionRemote createRentalSession(String clientName) throws RemoteException{
        //TODO
        return null;
    }
    @Override
    public ManagerSessionRemote createManagerSession() throws RemoteException {
        //TODO
        return null;
    }

    @Override
    public void closeSession(String sessionId) throws RemoteException{
        //TODO
    }
}
