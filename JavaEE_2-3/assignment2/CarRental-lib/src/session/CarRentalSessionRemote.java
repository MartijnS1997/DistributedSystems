package session;

import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.Remote;
import rental.CarType;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

@Remote
public interface CarRentalSessionRemote {
    
    public void setRenterName(String name);
    
    /**
     * 
     * @return 
     */
    public List<String> getAllRentalCompanies();
    
    public List<CarType> getAvailableCarTypes(Date start, Date end);
    
    /**
     * Iterate all companies until we find an available car
     * @param constraints The constraints for our reservation
     * @return the quote we made
     * @throws ReservationException 
     */
    public Quote createQuote(ReservationConstraints constraints) throws ReservationException;
    
    /**
     * Get the quotes the client made during this session
     * @return the list of quotes
     */
    public List<Quote> getCurrentQuotes();
    
    public List<Reservation> confirmQuotes() throws ReservationException;
    
    /**
     * Retrieve the cheapest car type for the given interval
     * @param start the start date
     * @param end the end date
     * @param region the region in which the car will be rented
     * @return the cheapest car type available in the given date
     */
    public CarType getCheapestCarType(Date start, Date end, String region) throws ReservationException;
    
}