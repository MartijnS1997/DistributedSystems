package interfaces;

import rental.company.CarType;
import util.Pair;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

/**
 * The manager session is responsible for managing the activities
 * for a manager. This is the 'bean' used for the manager
 */
public interface ManagerSessionRemote extends SessionRemote {


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
     * @return a collection of all registered company names
     * Martijn: changed the return type since it is a requirement that managers cannot access reservations direcly
     *          and they could if this method returned remote companies
     */
    Collection<String> getRegisteredCompanies() throws RemoteException;

    /**
     * @return a collection of all the companies and their car types
     * @throws RemoteException if something went wrong
     * TODO: Check if this method is really needed... seems a bit "overkill"
     * Clara: Dit kan met een for lus toch gewoon? Laat voorlopig nog staan. Als we hem veel nodig hebben, kan dit handig zijn.
     */
    Collection<Pair<String, Collection<CarType>>> getCarTypesPerCompany() throws RemoteException;

    /**
     * counts all the reservations for a specific car type
     * @param carType the name of the car type
     * @return the number of reservations for a specific car type
     */
    int getReservationCount(String carType) throws RemoteException;

    /**
     * gets the most wanted car type (the most number of reservations) for the given year
     * starting at the provided date and the provided company
     * @param calendarYear the year in which the success will be counted
     * @param companyName the car rental company we want the most wanted car type from
     * @return the specs of the most wanted car type of the given calendar year
     *
     * note: returns the type instead of a string such that the manager may inspect the
     *       particularities of the car type
     */
    CarType mostWanted(String companyName, int calendarYear) throws RemoteException;

    /**
     * gets the best customer of the company (no date range specified)
     * @return the name of the best customer
     * @param companyName the  name of the company to get the best customer for
     */
    String bestCustomer(String companyName) throws RemoteException;

    /**
     * gets the number of reservations done by a client
     * @param client the client that has done the reservations
     * @return the number of reservations
     * @throws RemoteException
     */
    int getReservationsBy(String client) throws RemoteException;

}
