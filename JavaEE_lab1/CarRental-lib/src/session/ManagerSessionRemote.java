package session;

import java.util.Set;
import javax.ejb.Remote;

/**
 *
 * @author Clara De Smet
 * @author Martijn Sauwens
 */
@Remote
public interface ManagerSessionRemote {
    
    Set<String> getAllCarTypes(String companyName);
    
    int getReservationCount(String carType, String companyName);
    
    String getBestCustomer(String companyName);
    
    int getNumberOfReservationsBy(String clientName);
    
}
