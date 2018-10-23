package rental.session;

import interfaces.RentalSessionRemote;
import rental.company.CarRentalAgency;

public class RentalSession extends Session implements RentalSessionRemote {


    protected RentalSession(CarRentalAgency agency, String sessionid, SessionManager manager) {
        super(agency, sessionid, manager);
    }
}
