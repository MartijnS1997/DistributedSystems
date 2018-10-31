package rental.session;

import interfaces.CarRentalCompanyRemote;
import interfaces.ManagerSessionRemote;
import rental.company.*;

import java.rmi.RemoteException;
import java.util.*;

public class ManagerSession extends Session implements ManagerSessionRemote {

    /**
     * Constructor
     */
    protected ManagerSession(CarRentalAgency agency, long sessionId, SessionManager manager) {
        super(agency, sessionId, manager);
    }

    /**
     * implementation of remote interface
     */

    @Override
    public void registerRentalCompany(CarRentalCompanyRemote company) throws RemoteException {
        getRentalAgency().registerCompany(company);
    }

    @Override
    public void unregisterRentalCompany(String companyName) throws RemoteException {
        getRentalAgency().unregisterCompany(companyName);
    }

    @Override
    public Collection<String> getRegisteredCompanies() throws RemoteException {
        return getRentalAgency().registeredCompanyNames();
    }

    @Override
    public int getReservationCount(String carType) throws RemoteException {
        int accumulator = 0;
        Collection<String> companies = getRentalAgency().registeredCompanyNames();
        for(String companyName : companies){
            CarRentalCompanyRemote company = getRentalAgency().lookupRentalCompany(companyName);
            try{
                accumulator += company.getCarTypeReservationCount(carType);
            } catch (IllegalArgumentException e){
                // means the company does not supply the car type
            }
        }

        return accumulator;
    }

    @Override
    public Collection<CarType> getCarTypesPerCompany(String company) throws RemoteException {
        CarRentalCompanyRemote carRentalCompany = getRentalAgency().lookupRentalCompany(company);
        return carRentalCompany.getAllCarTypes();
    }

    @Override
    public CarType mostWanted(String companyName, int calendarYear) throws RemoteException {
        CarRentalCompanyRemote company = getRentalAgency().lookupRentalCompany(companyName);
        return company.mostWanted(calendarYear);
    }

    @Override
    public Set<String> bestCustomersPerCompany() throws RemoteException {
        Set<String> bestCustomers = new HashSet<>();

        for(String companyName : getRentalAgency().registeredCompanyNames()) {
            CarRentalCompanyRemote company = getRentalAgency().lookupRentalCompany(companyName);
            bestCustomers.add(company.getBestCustomer());
        }
        return bestCustomers;
    }

    @Override
    public int getReservationsBy(String client) throws RemoteException {
        int accumulator = 0;
        for (String company : getRentalAgency().registeredCompanyNames()) {
            CarRentalCompanyRemote rentalCompany = getRentalAgency().lookupRentalCompany(company);
            accumulator += rentalCompany.getYourReservations(client).size();
        }
        return accumulator;
    }

    @Override
    public Set<String> getBestCustomers() throws RemoteException {
        Map<String, Integer> customerReservations = mergeCompanyCustomerRankings();
        return getBestCustomers(customerReservations);
    }

    private Set<String> getBestCustomers(Map<String, Integer> customerReservations){
        int maxRents = getMaxMapValue(customerReservations);
        Set<String> bestCustomers = new HashSet<>();
        for(String customer : customerReservations.keySet()){
            if(customerReservations.get(customer) == maxRents){
                bestCustomers.add(customer);
            }
        }

        return bestCustomers;
    }

    private Integer getMaxMapValue(Map<String, Integer> customerReservations){
        return customerReservations.values().stream().max(Integer::compareTo).get();
    }

    /**
     * merges for all the companies the number of reservations for each customer to create a map
     * that contains the number of reservations for all the companies
     * @throws RemoteException
     */
    private Map<String, Integer> mergeCompanyCustomerRankings() throws RemoteException {
        Collection<String> companies = getRentalAgency().registeredCompanyNames();
        Map<String, Integer> customerReservations = new HashMap<>();

        for(String companyName: companies){
            CarRentalCompanyRemote company = getRentalAgency().lookupRentalCompany(companyName);
            Map<String, Integer> currentCountPerCustomer = company.reservationsPerCustomer();
            mergeIntValueMap(customerReservations, currentCountPerCustomer);
        }

        return customerReservations;
    }


    /**
     * Merges the two provided maps of integers and a given key into one map summing the integers if
     * both keys are present in the map
     * @param target the map to merge the result into
     * @param toMerge the map to merge with the target
     * @param <Key> the type of the key used in the map to merge
     */
    private static <Key> void mergeIntValueMap(Map<Key, Integer> target, Map<Key, Integer> toMerge){
        for(Key toMergeKey : toMerge.keySet()){

            //if the map contains the key
            if(target.containsKey(toMergeKey)){
                //sum the value
                Integer merged = target.get(toMergeKey) + toMerge.get(toMergeKey);
                target.put(toMergeKey, merged);
            //if there is no such key, just put the to merge value in the map
            }else{
                target.put(toMergeKey, toMerge.get(toMergeKey));
            }
        }

        //we've merged the set
    }



}