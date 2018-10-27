package client.provided;

import interfaces.CarRentalCompanyRemote;
import interfaces.ManagerSessionRemote;
import interfaces.RentalSessionRemote;
import rental.company.*;

import java.util.*;

public class ClientMain extends AbstractTestManagement<RentalSessionRemote, ManagerSessionRemote> {

    public ClientMain(String scriptFile) {
        super(scriptFile);
    }

    @Override
    protected Set<String> getBestClients(ManagerSessionRemote ms) throws Exception {
        //TODO a set of best clients?? So one from each company or do we have to compare and pick the top X clients?
        //ms.bestCustomer();
        return null;
    }

    @Override
    protected String getCheapestCarType(RentalSessionRemote rentalSessionRemote, Date start, Date end, String region) throws Exception {
        //TODO Is it ok to use toString() here ?
        return rentalSessionRemote.getCheapestCarType(start,end,region).toString();
    }

    @Override
    protected CarType getMostPopularCarTypeIn(ManagerSessionRemote ms, String carRentalCompanyName, int year) throws Exception {
        return ms.mostWanted(carRentalCompanyName,new Date(year,1,1));
    }

    @Override
    protected RentalSessionRemote getNewReservationSession(String name) throws Exception {
        //TODO Do we set up a session manager here? If so sessionManager.createRentalSession(name);
        return null;
    }

    @Override
    protected ManagerSessionRemote getNewManagerSession(String name, String carRentalName) throws Exception {
        //TODO Same as getNewReservationSession
        return null;
    }

    @Override
    protected void checkForAvailableCarTypes(RentalSessionRemote rentalSessionRemote, Date start, Date end) throws Exception {
        List<CarType> availableCarTypes = new ArrayList<>();
        for (String company : rentalSessionRemote.getAllCompanies()) {
            Collection<CarType> carTypes = rentalSessionRemote.getAvailableCarTypes(start,end,company);
            for (CarType carType : carTypes) {
                availableCarTypes.add(carType);
            }
        }
        //TODO print availableCarTypes?
    }

    @Override
    protected void addQuoteToSession(RentalSessionRemote rentalSessionRemote, String name, Date start, Date end, String carType, String region) throws Exception {
        //TODO We're missing a company name... Iterate all companies and pick the first that suffices?
        for (String company: rentalSessionRemote.getAllCompanies()) {
            Quote quote = rentalSessionRemote.createQuote(new ReservationConstraints(start,end,carType,region,company));
            if (quote != null) {
                break;
            }
        }

    }

    @Override
    protected List<Reservation> confirmQuotes(RentalSessionRemote rentalSessionRemote, String name) throws Exception {
        return new ArrayList<Reservation>(rentalSessionRemote.confirmQuotes());
    }

    @Override
    protected int getNumberOfReservationsBy(ManagerSessionRemote ms, String clientName) throws Exception {
        return ms.getReservationsBy(clientName);
    }

    @Override
    protected int getNumberOfReservationsForCarType(ManagerSessionRemote ms, String carRentalName, String carType) throws Exception {
        //TODO carRentalName isn't necessary in my opinion?
        return ms.getReservationCount(carType);
    }
}
