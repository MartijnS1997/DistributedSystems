package rental.session;

import interfaces.ManagerSessionRemote;
import rental.company.CarRentalAgency;

public class ManagerSession extends Session implements ManagerSessionRemote {

    protected ManagerSession(CarRentalAgency agency, String sessionid, SessionManager manager) {
        super(agency, sessionid, manager);
    }
}
