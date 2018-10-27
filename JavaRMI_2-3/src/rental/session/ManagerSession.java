package rental.session;

import interfaces.CarRentalCompanyRemote;
import interfaces.ManagerSessionRemote;
import rental.company.CarRentalAgency;
import rental.company.CarType;
import util.Pair;

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
    public Collection<String> getRegisteredCompanies() throws RemoteException {
        return getRentalAgency().getAllCompanyNames();
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
        // Clara: Volgens mij moet er nog een extra methode aangemaakt worden in CarRentalCompanyRemote
        // We kunnen momenteel enkel de available car types opvragen
        return null;
    }

    @Override
    public CarType mostWanted(String companyName, int calendarYear) throws  RemoteException {
        //TODO: do something in case the company we looked up does not exist? Nullptr exception
        // Same as getCarTypesPerCompany
        return getRentalAgency().lookupRentalCompany(companyName).mostWanted(calendarYear);
    }

    @Override
    public String bestCustomer(String companyName) throws RemoteException {
        CarRentalCompanyRemote  company = getRentalAgency().lookupRentalCompany(companyName);

        long mostReservations = 0;
        String bestCustomerName = "";

        Map<String, Long> reservationCountMap = company.getReservationCountPerCustomer();
        for(String customer:reservationCountMap.keySet()){
            long currentCount = reservationCountMap.get(customer);
            if(currentCount > mostReservations){
                mostReservations = currentCount;
                bestCustomerName = customer;
            }
        }

        return bestCustomerName;
    }

    @Override
    public int getReservationsBy(String client) throws RemoteException {
        return getAllReservationsPerCustomer().get(client).intValue();
    }

    private Map<String, Long> getAllReservationsPerCustomer() throws RemoteException {
        Collection<String> allCompanies = getRentalAgency().getAllCompanyNames();
        Map<String, Long> reservationsPerCustomer = new HashMap<>();

        //first get the total per customer
        for(String company: allCompanies){
            CarRentalCompanyRemote currentCompany = getRentalAgency().lookupRentalCompany(company);
            //sumCustomerReservations(reservationsPerCustomer, currentCompany);
            currentCompany.getReservationCountPerCustomer().forEach((customer, reservations) -> reservationsPerCustomer.merge(customer, reservations, Long::sum));
        }

        return reservationsPerCustomer;
    }


//    @Override
//    public String bestCustomer(CarRentalCompanyRemote company) throws RemoteException{
//        //TODO This has to return a Set<String> of best customers
//        Map<String, Long> reservationsByCustomers = getAllReservationsPerCustomer();
//        return determineBestCustomer(reservationsByCustomers);
//
//
//    }
//
//
//
//    private String determineBestCustomer(Map<String, Long> customerReservations){
//        String bestCustomer = "";
//        long bestCount = 0;
//        for(String currentCustomer: customerReservations.keySet()){
//            long currentCount = customerReservations.get(currentCustomer);
//            if(currentCount > bestCount){
//                bestCount = currentCount;
//                bestCustomer = currentCustomer;
//            }
//        }
//
//        return bestCustomer;
//    }
}
