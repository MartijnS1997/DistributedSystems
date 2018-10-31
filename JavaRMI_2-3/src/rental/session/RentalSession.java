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
        try{
             Quote quote = carRentalCompany.createQuote(constraints,getClientName());
             sessionQuotes.add(quote);
             return quote;
        }catch (Exception e){
            throw new ReservationException("Something went wrong when creating a quote");
        }
    }

    @Override
    public Collection<Quote> getCurrentQuotes() {
        return sessionQuotes;
    }

    @Override
    public Collection<Reservation> confirmQuotes() throws RemoteException, ReservationException {
        List<Reservation> reservations = new LinkedList<>();
        for(Quote quote : sessionQuotes){ //use the local variable instead of the getter -> no remote exception
            String companyName = quote.getRentalCompany();
            CarRentalCompanyRemote company = getRentalAgency().lookupRentalCompany(companyName);
            if(company == null){ rollBack(reservations);  }

            try {
                Reservation reservation = company.confirmQuote(quote); //ignore will (hopefully) work
                reservations.add(reservation);
            } catch (ReservationException e) {
                rollBack(reservations);
            }

        }

        return reservations;
    }

    private void rollBack(Collection<Reservation> reservations) throws ReservationException {

        for(Reservation reservation : reservations){

            CarRentalCompanyRemote company;
            try{
                company = getRentalAgency().lookupRentalCompany(reservation.getRentalCompany());

            }catch(RemoteException e){
                continue; //we've lost the company we cannot undo it
            }
            try {
                company.cancelReservation(reservation);
            } catch (RemoteException e) {
                //just ignore it
            }
        }

        throw new ReservationException("Error during confirming quotes");
    }

    @Override
    public CarType getCheapestCarType(Date start, Date end, String region) throws RemoteException {
        CarType cheapest = null;
        // Iterate all companies

        for (String companyName : getRentalAgency().registeredCompanyNames()) {
            CarRentalCompanyRemote company = getRentalAgency().lookupRentalCompany(companyName);
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
    public Collection<CarType> getAvailableCarTypes(Date start, Date end, String companyName) throws RemoteException, ReservationException {
        CarRentalCompanyRemote carRentalCompany = getRentalAgency().lookupRentalCompany(companyName);
        return carRentalCompany.getAvailableCarTypes(start, end);
    }

    @Override
    public Collection<String> getAllCompanies() {
        return getRentalAgency().registeredCompanyNames();
    }

}
