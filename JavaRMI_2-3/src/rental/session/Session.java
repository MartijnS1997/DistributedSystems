package rental.session;

import rental.company.*;

import java.rmi.RemoteException;

/**
 * Note does not implement SessionRemote because this is an abstract class. This class is only in
 * place to provide common functionality for the different kinds of sessions
 */
public abstract class Session {

    /**
     * Private instances
     */

    /**
     * The rental agency to retrieve the different car rental companies from. This
     * is the central 'database'
     */
    private final CarRentalAgency rentalAgency;

    /**
     * The id of the session, used to unregister with the session manager
     */
    private final long id;

    /**
     * The session manager that is used to log-off the current session
     */
    private final SessionManager sessionManager;



    /**
     * Constructor
     */
    protected Session(CarRentalAgency agency, long sessionId, SessionManager manager) {
        rentalAgency = agency;
        id = sessionId;
        sessionManager = manager;
    }

    public CarRentalAgency getRentalAgency() {
        return rentalAgency;
    }

    public long getId() {
        return id;
    }

    public void close() throws RemoteException {
        sessionManager.closeSession(this.getId());
    }
}
