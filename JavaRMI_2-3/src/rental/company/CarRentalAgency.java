package rental.company;

import interfaces.CarRentalCompanyRemote;
import interfaces.ManagerSessionRemote;
import interfaces.RentalSessionRemote;
import rental.session.RentalSession;

import java.util.Map;

public class CarRentalAgency {

    private Map<String,CarRentalCompanyRemote> registeredCompanies;

    /**
     * Getters and setters
     */

    private Map<String, CarRentalCompanyRemote> getRegisteredCompanies() {
        return registeredCompanies;
    }
    
    /**
     * Constructors
     */

    CarRentalAgency(){

    }

    void registerCompany(String companyName) {
        //TODO getCompany from registry
    }

    void unregisterCompany(String companyName) {
        //TODO remove company from local list
    }

    CarRentalCompanyRemote lookupRentalCompany(String companyName) {
        //TODO lookup in local list
        return null;
    }

}
