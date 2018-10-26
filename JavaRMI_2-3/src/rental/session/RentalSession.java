package rental.session;

import interfaces.RentalSessionRemote;
import rental.company.*;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class RentalSession extends Session implements RentalSessionRemote {


    /**
     * Private instances
     */
    private Collection<Quote> sessionQuotes = new HashSet<>();

    private String clientName;

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
    public Quote createQuote(ReservationConstraints constraints) throws ReservationException {
        return null;
    }

    @Override
    public Collection<Quote> getCurrentQuotes() {
        return sessionQuotes;
    }

    @Override
    public Collection<Reservation> confirmQuotes() throws ReservationException {
        return null;
    }

    @Override
    public CarType getCheapestCarType(Date start, Date end, String region) {
        return new CarType("hello", 1, 2.5f, 656.0, false);
    }

    @Override
    public Collection<CarType> getAvailableCarTypes(Date start, Date end, String companyName) {
        return null;
    }

    @Override
    public Collection<String> getAllCompanies() {
        return null;
    }

}
