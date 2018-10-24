package rental.session;

import interfaces.RentalSessionRemote;
import rental.company.*;

import java.util.Collection;
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

    protected RentalSession(CarRentalAgency agency, String sessionid, SessionManager manager) {
        super(agency, sessionid, manager);
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
    public CarType getCheapestCarType() {
        return null;
    }

    @Override
    public Collection<CarType> getAvailableCarTypes() {
        return null;
    }

    @Override
    public Collection<String> getAllCompanies() {
        return null;
    }

    @Override
    public void setCurrentRentalCompany(String companyName) {
        currentRentalCompany = companyName;
    }

    @Override
    public String getCurrentRentalCompany() {
        return currentRentalCompany;
    }
}
