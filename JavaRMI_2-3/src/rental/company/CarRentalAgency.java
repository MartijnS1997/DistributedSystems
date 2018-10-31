package rental.company;

import interfaces.CarRentalCompanyRemote;
import interfaces.ManagerSessionRemote;
import interfaces.RentalSessionRemote;
import rental.session.RentalSession;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;

public class CarRentalAgency {

    //Note all the methods are synchronized to prevent that the registered companies are changed during
    //a lookup, causing race conditions

    private Map<String,CarRentalCompanyRemote> registeredCompanies = new HashMap<>();

    /**
     * Getters and setters
     */

    private  Map<String, CarRentalCompanyRemote> getRegisteredCompanies() {
        // Shallow copy to prevent the map from changing when iterating the elements (e.g. in getReservationCount)
        return registeredCompanies;
    }


    /**
     * Constructors
     */

    public CarRentalAgency(){
        // no initialisation needed
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
    public synchronized void registerCompany(CarRentalCompanyRemote company) throws RemoteException {
        this.getRegisteredCompanies().put(company.getName(), company);
    }

    /**
     * removes a car rental company from the registered companies
     * @param companyName the company to remove
     * @return the car rental company that was removed
     */
    public synchronized CarRentalCompanyRemote unregisterCompany(String companyName) {
        return this.getRegisteredCompanies().remove(companyName); //use the instance variable because the getter returns a copy
    }

    /**
     * Lookup for a car rental company
     * @param companyName the name used for the lookup
     * @return a stub to a registered company
     * @implNote lookup is used to find registered companies
     *           if a client needs a certain company it first requests all the available companies
     *           and then it asks for a lookup. Note that because the method is synchronized, it is safe
     *           to retrieve a company. However it is possible that the company is removed afterwards.
     *           (The client still has the stub to the company so as long as the company server is not
     *            shut down the client can continue it's interaction)
     */
    public synchronized CarRentalCompanyRemote lookupRentalCompany(String companyName) throws RemoteException {
        CarRentalCompanyRemote remoteCompany = getRegisteredCompanies().get(companyName);
        if(remoteCompany == null) { throw new RemoteException();}
        return remoteCompany;
    }

    /**
     * @return a collection of all the registered company names. This call is used by the client to get an
     *         overview of all the available rental companies
     */
    public synchronized Collection<String> registeredCompanyNames(){
        return new HashSet<>(getRegisteredCompanies().keySet()); //HashMap$HashSet is not serializable, need to convert it first to a serializable interface!
    }

}
