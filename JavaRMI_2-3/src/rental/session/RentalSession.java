package rental.session;

import interfaces.RentalSessionRemote;
import interfaces.CarRentalCompanyRemote;
import rental.company.*;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class RentalSession extends Session implements RentalSessionRemote {


    /**
     * Private instances
     */

    /**
     * A collection of all quotes made in the current session.
     */
    private Collection<Quote> sessionQuotes = new HashSet<>();

    /**
     * The client name, used to identify a client with a rental company.
     */
    private String clientName;

    /**
     * The rental company where the client is currently making quotes.
     */
    private String currentRentalCompany;

    /**
     * Getters and setters that are not part of interface
     */
    private String getClientName() {
        return clientName;
    }

    private void setClientName(String clientName) {
        this.clientName = clientName;
    }

    /**
     * constructor
     */
    protected RentalSession( String clientName, CarRentalAgency agency, long sessionId, SessionManager manager) {
        super(agency, sessionId, manager);
        setClientName(clientName);
    }

    @Override
    public Quote createQuote(ReservationConstraints constraints) throws ReservationException, RemoteException {
        CarRentalCompanyRemote carRentalCompany = getRentalAgency().lookupRentalCompany(currentRentalCompany);
        return carRentalCompany.createQuote(constraints,getClientName());
    }

    @Override
    public Collection<Quote> getCurrentQuotes() {
        return sessionQuotes;
    }

    @Override
    public Collection<Reservation> confirmQuotes() throws ReservationException, RemoteException {
        //TODO If exception thrown, cancel all reservations.
        for (Quote quote : getCurrentQuotes()) {
            String company = quote.getRentalCompany();
            Reservation reservation = getRentalAgency().lookupRentalCompany(company).confirmQuote(quote);
        }
        return null;
    }

    @Override
    public CarType getCheapestCarType(Date start, Date end, String region) {
        //TODO this is a dummy implementation i suppose? :p
        return new CarType("hello", 1, 2.5f, 656.0, false);
    }

    @Override
    public Collection<CarType> getAvailableCarTypes(Date start, Date end, String companyName) throws RemoteException {
        CarRentalCompanyRemote carRentalCompany = getRentalAgency().lookupRentalCompany(companyName);
        return carRentalCompany.getAvailableCarTypes(start, end);
    }

    @Override
    public Collection<String> getAllCompanies() {
        return getRentalAgency().getAllCompanyNames();
    }

}
