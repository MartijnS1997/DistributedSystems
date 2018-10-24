package rental.session;

import interfaces.CarRentalCompanyRemote;
import interfaces.ManagerSessionRemote;
import rental.company.CarRentalAgency;
import rental.company.CarType;

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

    protected ManagerSession(CarRentalAgency agency, String sessionid, SessionManager manager) {
        super(agency, sessionid, manager);
    }

    /**
     * implementation of remote interface
     */

    @Override
    public void registerRentalCompany(CarRentalCompanyRemote company) throws RemoteException {

    }

    @Override
    public void unregisterRentalCompany(String companyName) throws RemoteException {

    }

    @Override
    public Collection<CarRentalCompanyRemote> getRegisteredCompanies() throws RemoteException {
        return null;
    }

    @Override
    public int getReservationCount(String carType) throws RemoteException {
        return 0;
    }

    @Override
    public CarType mostWanted(Date calendarYear) throws  RemoteException {
        return null;
    }

    @Override
    public String bestCustomer() throws RemoteException{
        return null;
    }
}
