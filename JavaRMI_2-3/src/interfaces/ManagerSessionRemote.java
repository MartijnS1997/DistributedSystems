package interfaces;

import rental.company.CarType;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

/**
 * The manager session is responsible for managing the activities
 * for a manager. This is the 'bean' used for the manager
 */
public interface ManagerSessionRemote extends Remote {


    /**
     * Registers a car rental company with the associated car rental agency
     * @param company the company to register
     */
    void registerRentalCompany(CarRentalCompanyRemote company) throws RemoteException;
    /**
     * unregisters the rental company with the associated car rental agency
     * @param companyName the name of the company to remove
     */
    void unregisterRentalCompany(String companyName) throws RemoteException;

    /**
     * getter for all the registered car rental companies in the car rental agency
     * @return a collection of all registered companies
     */
    Collection<CarRentalCompanyRemote> getRegisteredCompanies() throws RemoteException;

    /**
     * counts all the reservations for a specific car type
     * @param carType the name of the car type
     * @return the number of reservations for a specific car type
     */
    int getReservationCount(String carType) throws RemoteException;

    /**
     * gets the most wanted car type (the most number of reservations) for the given year
     * starting at the provided date
     * @param calendarYear the year in which the success will be counted
     * @return the specs of the most wanted car type of the given calendar year
     *
     * note: returns the type instead of a string such that the manager may inspect the
     *       particularities of the car type
     */
    CarType mostWanted(Date calendarYear) throws RemoteException;

    /**
     * gets the best customer of the company (no date range specified)
     * @return the name of the best customer
     */
    String bestCustomer() throws RemoteException;

}
