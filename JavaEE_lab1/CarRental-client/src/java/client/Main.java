package client;

import com.sun.org.apache.xpath.internal.axes.SelfIteratorNoPredicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.naming.InitialContext;
import rental.Reservation;
import rental.ReservationConstraints;
import session.CarRentalSessionRemote;
import session.ManagerSessionRemote;

public class Main extends AbstractTestAgency<CarRentalSessionRemote,ManagerSessionRemote> {
    
    private CarRentalSessionRemote clientSession;
    private ManagerSessionRemote managerSession;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //System.out.println("found rental companies: "+clientSession.getAllRentalCompanies());
    }

    public Main(String scriptFile) {
        super(scriptFile);
    }

    @Override
    protected CarRentalSessionRemote getNewReservationSession(String name) throws Exception {
        InitialContext context = new InitialContext();
        CarRentalSessionRemote session = (CarRentalSessionRemote) context.lookup(CarRentalSessionRemote.class.getName());
        // We set the client name for this session.
        session.setCurrentClient(name);
        return session;
    }

    // Hieronder mogen alle String names weg want client name wordt opgeslagen in de session bean
    /**
     * These arguments are not used since it is a stateless session bean.
     * @param name
     * @param carRentalName
     * @return
     * @throws Exception 
     */
    @Override
    protected ManagerSessionRemote getNewManagerSession(String name, String carRentalName) throws Exception {
        InitialContext context = new InitialContext();
        return (ManagerSessionRemote) context.lookup(ManagerSessionRemote.class.getName());
    }

    @Override
    protected void checkForAvailableCarTypes(CarRentalSessionRemote session, Date start, Date end) throws Exception {
        for (String carTypeString: session.getAvailableCarTypes(start, end)) {
            System.out.println(carTypeString);
        }
    }

    @Override
    protected void addQuoteToSession(CarRentalSessionRemote session, String name, Date start, Date end, String carType, String region) throws Exception {
        session.createQuote(constraintFactory(start, end, carType, region));
    }

    @Override
    protected List<Reservation> confirmQuotes(CarRentalSessionRemote session, String name) throws Exception {
        return new ArrayList<Reservation>(session.confirmQuotes());
    }

    @Override
    protected int getNumberOfReservationsBy(ManagerSessionRemote ms, String clientName) throws Exception {
        //Since no car rental company is given, we interprete this as all the reservations over all car rental companies. 
        // (Because the assignment only specifies we may delete/ignore redundant paramaters, but doesn't mention 
        // anything about adding parameters)
        
        return ms.getNumberOfReservationsBy(clientName);
    }

    @Override
    protected int getNumberOfReservationsForCarType(ManagerSessionRemote ms, String carRentalName, String carType) throws Exception {
        return ms.getReservationCount(carType, carRentalName);
    }

    private static ReservationConstraints constraintFactory(Date start, Date end, String carType, String region) {
        return new ReservationConstraints(start, end, carType, region);
    }

}
