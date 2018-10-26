package client.provided;

import interfaces.ManagerSessionRemote;
import interfaces.RentalSessionRemote;
import rental.company.CarType;
import rental.company.Reservation;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class ClientMain extends AbstractTestManagement<RentalSessionRemote, ManagerSessionRemote> {

    public ClientMain(String scriptFile) {
        super(scriptFile);
    }

    @Override
    protected Set<String> getBestClients(ManagerSessionRemote ms) throws Exception {
        return null;
    }

    @Override
    protected String getCheapestCarType(RentalSessionRemote rentalSessionRemote, Date start, Date end, String region) throws Exception {
        return null;
    }

    @Override
    protected CarType getMostPopularCarTypeIn(ManagerSessionRemote ms, String carRentalCompanyName, int year) throws Exception {
        return null;
    }

    @Override
    protected RentalSessionRemote getNewReservationSession(String name) throws Exception {
        return null;
    }

    @Override
    protected ManagerSessionRemote getNewManagerSession(String name, String carRentalName) throws Exception {
        return null;
    }

    @Override
    protected void checkForAvailableCarTypes(RentalSessionRemote rentalSessionRemote, Date start, Date end) throws Exception {

    }

    @Override
    protected void addQuoteToSession(RentalSessionRemote rentalSessionRemote, String name, Date start, Date end, String carType, String region) throws Exception {

    }

    @Override
    protected List<Reservation> confirmQuotes(RentalSessionRemote rentalSessionRemote, String name) throws Exception {
        return null;
    }

    @Override
    protected int getNumberOfReservationsBy(ManagerSessionRemote ms, String clientName) throws Exception {
        return 0;
    }

    @Override
    protected int getNumberOfReservationsForCarType(ManagerSessionRemote ms, String carRentalName, String carType) throws Exception {
        return 0;
    }
}
