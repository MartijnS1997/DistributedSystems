package rental.session;

import interfaces.CarRentalCompanyRemote;
import interfaces.ManagerSessionRemote;
import rental.company.CarRentalAgency;
import rental.company.CarType;
import util.Pair;

import java.beans.Customizer;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    public Collection<CarRentalCompanyRemote> getRegisteredCompanies() throws RemoteException {
        return getRentalAgency().getAllRegisterdCompanies();
    }

    @Override
    public int getReservationCount(String carType) throws RemoteException {
        int accumulator = 0;
        for(CarRentalCompanyRemote rentalCompany: getRentalAgency().getAllRegisterdCompanies()){
           accumulator += rentalCompany.getCarTypeReservationCount(carType);
        }

        return accumulator;
    }

    @Override
    public Collection<Pair<String, Collection<CarType>>> getCarTypesPerCompany() throws RemoteException {
        //TODO implement
        return null;
    }

    @Override
    public CarType mostWanted(Date calendarYear) throws  RemoteException {
        //TODO: implement the most wanted method
        return null;
    }

    @Override
    public String bestCustomer() throws RemoteException{
        Map<String, Long> reservationsByCustomers = getAllReservationsPerCustomer();
        return determineBestCustomer(reservationsByCustomers);


    }

    private Map<String, Long> getAllReservationsPerCustomer() throws RemoteException {
        Collection<String> allCompanies = getRentalAgency().getAllCompanyNames();
        Map<String, Long> reservationsPerCustomer = new HashMap<>();

        //first get the total per customer
        for(String company: allCompanies){
            CarRentalCompanyRemote currentCompany = getRentalAgency().lookupRentalCompany(company);
            //sumCustomerReservations(reservationsPerCustomer, currentCompany);
            currentCompany.getReservationsByCustomer().forEach((customer, reservations) -> reservationsPerCustomer.merge(customer, reservations, Long::sum));
        }

        return reservationsPerCustomer;
    }


// replaced by the merge lambda
//    private void sumCustomerReservations(Map<String, Long> currentSumMap, CarRentalCompanyRemote companyToAdd){
//        Map<String, Long> mapToAdd = companyToAdd.getReservationsByCustomer();
//        for(String customerToAdd: mapToAdd.keySet()) {
//            Long currentTotal = currentSumMap.get(customerToAdd);
//            if (currentTotal == null) {
//                //in case there is no entry yet, add the total
//                currentSumMap.put(customerToAdd, mapToAdd.get(customerToAdd));
//            } else {
//                currentSumMap.put(customerToAdd, currentTotal + mapToAdd.get(customerToAdd));
//            }
//        }
//    }

    private String determineBestCustomer(Map<String, Long> customerReservations){
        String bestCustomer = "";
        long bestCount = 0;
        for(String currentCustomer: customerReservations.keySet()){
            long currentCount = customerReservations.get(currentCustomer);
            if(currentCount > bestCount){
                bestCount = currentCount;
                bestCustomer = currentCustomer;
            }
        }

        return bestCustomer;
    }
}
