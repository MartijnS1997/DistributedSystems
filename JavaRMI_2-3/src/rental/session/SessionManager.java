package rental.session;

import interfaces.CarRentalCompanyRemote;
import rental.company.CarRentalAgency;
import rental.company.CarRentalCompany;

import java.util.HashMap;

/**
 * This class does lifecycle management of sessions, so that CarRentalAgency does not need to be concerned with that.
 */
public class SessionManager {

    private HashMap<String,Session> sessions;

    /**
     * Every SessionManager belongs to one CarRentalCompany and vice versa.
     */
    private CarRentalAgency rentalAngency;

    private long idCounter = 1;

    RentalSession createRentalSession(){
        //TODO
        return null;
    }

    ManagerSession createManagerSession(){
        //TODO
        return null;
    }

    void closeSession(String id) {
        //TODO
    }
}
