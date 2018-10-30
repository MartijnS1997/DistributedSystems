package rental.session;

import interfaces.RentalSessionRemote;
import interfaces.CarRentalCompanyRemote;
import rental.company.*;

import java.rmi.RemoteException;
import java.util.*;

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
        CarRentalCompanyRemote carRentalCompany = getRentalAgency().lookupRentalCompany(constraints.getCompanyName());
        if(carRentalCompany == null) { throw new ReservationException("Company doesn't exist"); }
        return carRentalCompany.createQuote(constraints,getClientName());
    }

    @Override
    public Collection<Quote> getCurrentQuotes() {
        return sessionQuotes;
    }

    @Override
    public Collection<Reservation> confirmQuotes() throws RemoteException {
        //TODO Need to rewrite because of ReservationExceptions
        List<Reservation> reservations = new LinkedList<>(); //linked list for fast addition of new reservations
        for (Quote quote : getCurrentQuotes()) {
            String company = quote.getRentalCompany();
            CarRentalCompanyRemote rentalCompany;
            try{
                rentalCompany = getRentalAgency().lookupRentalCompany(company);
                Reservation reservation = getRentalAgency().lookupRentalCompany(company).confirmQuote(quote);
                reservations.add(reservation);
            }catch(ReservationException e){
                reservations.forEach(reservation -> {
                    try {
                        getRentalAgency().lookupRentalCompany(reservation.getRentalCompany()).cancelReservation(reservation);
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                        //let the remote exception fly
                    }
                });
            }
        }
        return reservations;
    }

    @Override
    public CarType getCheapestCarType(Date start, Date end, String region) throws RemoteException {
        CarType cheapest = null;
        // Iterate all companies
        for (CarRentalCompanyRemote company : getRentalAgency().getAllRegisterdCompanies()) {
            if (company.getRegions().contains(region)) {
                CarType currentType = company.getCheapestCarType(start,end);
                if (cheapest == null) {
                    cheapest = currentType;
                }
                else if (cheapest.getRentalPricePerDay() > currentType.getRentalPricePerDay()) {
                    cheapest = currentType;
                }
            }
        }
        return cheapest;
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
