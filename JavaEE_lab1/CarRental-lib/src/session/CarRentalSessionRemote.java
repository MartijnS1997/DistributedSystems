package session;

import java.util.Set;
import javax.ejb.Remote;
import rental.*;

@Remote
public interface CarRentalSessionRemote {

    void setCurrentClient(String clientName);
    
    Set<String> getAllRentalCompanies();
    
    Quote createQuote(ReservationConstraints constraints) throws ReservationException;
    
    Set<Quote> getCurrentQuotes();
    
    Set<Reservation> confirmQuotes() throws ReservationException;
    
}
