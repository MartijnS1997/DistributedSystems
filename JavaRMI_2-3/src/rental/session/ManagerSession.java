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
            try{
                accumulator += rentalCompany.getCarTypeReservationCount(carType);
            } catch(IllegalArgumentException e){
                //let it fly
            }
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
    public Set<String> bestCustomers() throws RemoteException, ReservationException {
        Set<String> bestCustomers = new HashSet<>();
        for(String companyName : getRentalAgency().getAllCompanyNames()) {
            CarRentalCompanyRemote company = getRentalAgency().lookupRentalCompany(companyName);
            companyNullCheck(company);
            bestCustomers.add(company.getBestCustomer());
        }
        return bestCustomers;
    }

    @Override
    public int getReservationsBy(String client) throws RemoteException, ReservationException {
        int accumulator = 0;
        for (String company : getRentalAgency().getAllCompanyNames()) {
            CarRentalCompanyRemote rentalCompany = getRentalAgency().lookupRentalCompany(company);
            companyNullCheck(rentalCompany);
            accumulator += rentalCompany.getYourReservations(client).size();
        }
        return accumulator;
    }

}