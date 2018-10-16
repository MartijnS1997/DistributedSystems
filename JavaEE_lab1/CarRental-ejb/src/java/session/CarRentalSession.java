package session;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.ejb.Stateful;
import rental.*;

@Stateful
public class CarRentalSession implements CarRentalSessionRemote {

    private String client;
    private Set<Quote> currentQuotes = new HashSet<Quote>();
    
    @Override
    public void setCurrentClient(String clientName) {
        this.client = clientName;
    }
    
    public String getCurrentClient() {
        return this.client;
    }
    
        @Override
    public Set<Quote> getCurrentQuotes() {
        return currentQuotes;
    }

    @Override
    public Set<String> getAllRentalCompanies()  {
        return new HashSet<String>(RentalStore.getRentals().keySet());
    }

    /**
     * Search for a company that can give a quote to our client
     * @param constraints
     * @return
     * @throws ReservationException 
     */
    @Override
    public Quote createQuote(ReservationConstraints constraints) throws ReservationException {
        Quote newQuote = null;
        // Iterate all rental companies until we find one that can fulfill our constraints
        for (String rentalCompanyName: this.getAllRentalCompanies()) {
            CarRentalCompany rentalCompany = RentalStore.getRentals().get(rentalCompanyName);
            try {
                newQuote = rentalCompany.createQuote(constraints, getCurrentClient());
                break;
            } catch(ReservationException resExc) {
                // Do nothing
            }
            
        }
        if (newQuote == null) {
            throw new ReservationException(getCurrentClient());
        }
        getCurrentQuotes().add(newQuote);
        return newQuote;
        
    }


    /**
     * Confirm the quotes stored in the session bean
     * @return  A set with all confirmed quotes
     * @throws ReservationException
     *                  If one of the quotes cannot be confirmed
     */
    @Override
    public Set<Reservation> confirmQuotes() throws ReservationException {
        Map<String,CarRentalCompany> rentalCompanyMap = RentalStore.getRentals();
        Set<Reservation> reservations = new HashSet<Reservation>();
        // Try to confirm all quotes
        try {
            for (Quote currentQuote: getCurrentQuotes()) {
                CarRentalCompany currentCompany = rentalCompanyMap.get(currentQuote.getRentalCompany());
                reservations.add(currentCompany.confirmQuote(currentQuote));
            }
            // If one fails, cancel everything
        } catch (ReservationException resExc) {
            cancelAllReservations(reservations, rentalCompanyMap);
            throw new ReservationException(getCurrentClient());
        }
        return reservations;
    }
    
    /**
     * Cancels all reservations made in this session bean
     * @param reservations
     * @param rentalCompanyMap 
     */
    private static void cancelAllReservations(Set<Reservation>reservations, Map<String,CarRentalCompany> rentalCompanyMap) {
        for (Reservation currentReservation: reservations) {
            CarRentalCompany rentalCompany = rentalCompanyMap.get(currentReservation.getRentalCompany());
            rentalCompany.cancelReservation(currentReservation);
        }
    }

    @Override
    public Set<String> getAvailableCarTypes(Date start, Date end) {
        Set<String> allCarTypes = new HashSet<String>();
        for (String rentalCompanyName: getAllRentalCompanies()) {
            CarRentalCompany rentalCompany = RentalStore.getRentals().get(rentalCompanyName);
            for (CarType carType : rentalCompany.getAvailableCarTypes(start, end)) {
                allCarTypes.add(carType.getName());
            }
        }
        return allCarTypes;
    }

    
    
}
