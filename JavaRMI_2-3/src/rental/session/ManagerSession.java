package rental.session;

import interfaces.CarRentalCompanyRemote;
import interfaces.ManagerSessionRemote;
import rental.company.*;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ManagerSession extends Session implements ManagerSessionRemote {

    /**
     * Constructor
     */
    protected ManagerSession(CarRentalAgency agency, long sessionId, SessionManager manager) {
        super(agency, sessionId, manager);
    }

    private static void companyNullCheck(CarRentalCompanyRemote carRentalCompany) throws ReservationException {
        if (carRentalCompany == null) {
            throw new ReservationException("Company doesn't exist!");
        }
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
        for (CarRentalCompanyRemote rentalCompany : getRentalAgency().getAllRegisteredCompanies()) {
            accumulator += rentalCompany.getCarTypeReservationCount(carType);
        }

        return accumulator;
    }

    @Override
    public Collection<CarType> getCarTypesPerCompany(String company) throws RemoteException, ReservationException {
        CarRentalCompanyRemote carRentalCompany = getRentalAgency().lookupRentalCompany(company);
        companyNullCheck(carRentalCompany);
        return carRentalCompany.getAllCarTypes();
    }

    @Override
    public CarType mostWanted(String companyName, int calendarYear) throws RemoteException, ReservationException {
        CarRentalCompanyRemote company = getRentalAgency().lookupRentalCompany(companyName);
        companyNullCheck(company);
        return company.mostWanted(calendarYear);
    }

    @Override
    public String bestCustomer(String companyName) throws RemoteException, ReservationException {
        CarRentalCompanyRemote company = getRentalAgency().lookupRentalCompany(companyName);
        companyNullCheck(company);

        long mostReservations = 0;
        String bestCustomerName = "";

        Map<String, Long> reservationCountMap = company.getReservationCountPerCustomer();
        for (String customer : reservationCountMap.keySet()) {
            long currentCount = reservationCountMap.get(customer);
            if (currentCount > mostReservations) {
                mostReservations = currentCount;
                bestCustomerName = customer;
            }
        }

        return bestCustomerName;
    }

    @Override
    public int getReservationsBy(String client) throws RemoteException, ReservationException {
        Long nb_res = getAllReservationsPerCustomer().get(client);
        if (nb_res == null){
            throw new ReservationException("Customer doesn't exist!");
        }
        return nb_res.intValue();
    }

    private Map<String, Long> getAllReservationsPerCustomer() throws RemoteException, ReservationException {
        Collection<String> allCompanies = getRentalAgency().getAllCompanyNames();
        Map<String, Long> reservationsPerCustomer = new HashMap<>();

        //first get the total per customer
        for (String company : allCompanies) {
            CarRentalCompanyRemote currentCompany = getRentalAgency().lookupRentalCompany(company);
            companyNullCheck(currentCompany);
            //sumCustomerReservations(reservationsPerCustomer, currentCompany);
            currentCompany.getReservationCountPerCustomer()
                    .forEach((customer, reservations) -> reservationsPerCustomer
                            .merge(customer, reservations, Long::sum));
        }

        return reservationsPerCustomer;
    }
}