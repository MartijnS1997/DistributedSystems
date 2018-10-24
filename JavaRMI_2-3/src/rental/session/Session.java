package rental.session;

import rental.company.*;

public abstract class Session {

    /**
     * Private instances
     */

    private final CarRentalAgency rentalAgency;

    private final String id;

    private final SessionManager sessionManager;



    /**
     * Constructor
     */
    protected Session(CarRentalAgency agency, String sessionid, SessionManager manager) {
        rentalAgency = agency;
        id = sessionid;
        sessionManager = manager;
    }

    public CarRentalAgency getRentalAgency() {
        return rentalAgency;
    }

    public String getId() {
        return id;
    }

    public void close() {
        //TODO
    }
}
