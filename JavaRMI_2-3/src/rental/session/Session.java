package rental.session;

import rental.company.*;

public abstract class Session {

    private final CarRentalAgency rentalAgency;

    private final String id;

    private final SessionManager sessionManager;

    public CarRentalAgency getRentalAgency() {
        return rentalAgency;
    }

    public String getId() {
        return id;
    }

    protected Session(CarRentalAgency agency, String sessionid, SessionManager manager) {
        rentalAgency = agency;
        id = sessionid;
        sessionManager = manager;
    }

    public void close() {
        //TODO
    }
}
