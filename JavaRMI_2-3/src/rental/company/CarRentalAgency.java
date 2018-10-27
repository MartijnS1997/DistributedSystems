package rental.company;

import interfaces.CarRentalCompanyRemote;
import interfaces.ManagerSessionRemote;
import interfaces.RentalSessionRemote;
import rental.session.RentalSession;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CarRentalAgency {

    //TODO synchronization: What if we remove and lookup a company at the same time???

    private Map<String,CarRentalCompanyRemote> registeredCompanies = new HashMap<>();

    /**
     * Getters and setters
     */

    private Map<String, CarRentalCompanyRemote> getRegisteredCompanies() {
        return registeredCompanies;
    }


    /**
     * Constructors
     */

    public CarRentalAgency(){

    }

    /**
     * Company management
     */

    /**
     * Registers a new company
     * @param company the company to register
     * @throws RemoteException in case the provided company could not be accessed to provide its name
     * @implNote used the remote version of the company only (instead of two parameters one for the company and one for
     *           the name) this has as a consequence that remote exceptions can be thrown from a non remote class
     */
    public void registerCompany(CarRentalCompanyRemote company) throws RemoteException {
        Map<String, CarRentalCompanyRemote> registeredCompanies = getRegisteredCompanies();
        registeredCompanies.put(company.getName(), company);
    }

    /**
     * removes a car rental company from the registered companies
     * @param companyName the company to remove
     * @return the car rental company that was removed
     */
    public CarRentalCompanyRemote unregisterCompany(String companyName) {
        return getRegisteredCompanies().remove(companyName);
    }

    /**
     * Lookup for a car rental company
     * @param companyName the name used for the lookup
     * @return a stub to a registered company
     */
    public CarRentalCompanyRemote lookupRentalCompany(String companyName) {
        return getRegisteredCompanies().get(companyName);
    }

    /**
     * @return a collection of all the registered company names. This call is used by the client to get an
     *         overview of all the available rental companies
     */
    public Collection<String> getAllCompanyNames(){
        return new HashSet<>(getRegisteredCompanies().keySet()); //HashMap$HashSet is not serializable, need to convert it first to a serializable interface!
    }

    /**
     * getter for all the registered companies in the car rental agency
     * @return all the registered companies
     */
    public Collection<CarRentalCompanyRemote> getAllRegisterdCompanies(){
        return getRegisteredCompanies().values();
    }

}
