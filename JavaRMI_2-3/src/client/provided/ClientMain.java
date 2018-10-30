package client.provided;

import interfaces.*;
import rental.company.*;
import rental.session.SessionManager;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.stream.Collectors;

public class ClientMain extends AbstractTestManagement<RentalSessionRemote, ManagerSessionRemote> {

    private SessionManagerRemote sessionManager;

    public SessionManagerRemote getSessionManager() {
        return sessionManager;
    }

    public static void main(String[] args) throws Exception {
        ClientMain main = new ClientMain();
        main.run();
    }

    public ClientMain() throws RemoteException, NotBoundException {
        super("trips");
        //do the lookup
        initSessionManager();
    }

    private void initSessionManager() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry();
        this.sessionManager = (SessionManagerRemote)registry.lookup(Constants.MANAGER_NAME);
    }


    @Override
    protected Set<String> getBestClients(ManagerSessionRemote ms) throws Exception {
        // Get Companies
       return ms.getRegisteredCompanies().stream().map(companyName -> {
           try {
               // Map to best customer
               return ms.bestCustomer(companyName);
           } catch (RemoteException e) {
               e.printStackTrace();
               return "An error Occurred";
           }
       }).collect(Collectors.toSet());
    }

    @Override
    protected String getCheapestCarType(RentalSessionRemote rentalSessionRemote, Date start, Date end, String region) throws Exception {
        return rentalSessionRemote.getCheapestCarType(start,end,region).toString();
    }

    @Override
    protected CarType getMostPopularCarTypeIn(ManagerSessionRemote ms, String carRentalCompanyName, int year) throws Exception {
        return ms.mostWanted(carRentalCompanyName, year);
    }

    @Override
    protected RentalSessionRemote getNewReservationSession(String name) throws Exception {
        return getSessionManager().createRentalSession(name);
    }

    @Override
    protected ManagerSessionRemote getNewManagerSession(String name, String carRentalName) throws Exception {
        return getSessionManager().createManagerSession();
    }

    @Override
    protected void checkForAvailableCarTypes(RentalSessionRemote rentalSessionRemote, Date start, Date end) throws Exception {
        List<CarType> availableCarTypes = new ArrayList<>();
        //Get companies
        for (String company : rentalSessionRemote.getAllCompanies()) {
            // Available car types
            Collection<CarType> carTypes = rentalSessionRemote.getAvailableCarTypes(start,end,company);
            availableCarTypes.addAll(carTypes);
        }

        // Print
        availableCarTypes.forEach(carType -> System.out.println(carType.toString()));
    }

    @Override
    protected void addQuoteToSession(RentalSessionRemote rentalSessionRemote, String name, Date start, Date end, String carType, String region) throws Exception {
        for (String company: rentalSessionRemote.getAllCompanies()) {
            Quote quote = rentalSessionRemote.createQuote(new ReservationConstraints(start,end,carType,region,company));
            if (quote != null) {
                break;
            }
        }
    }

    @Override
    protected List<Reservation> confirmQuotes(RentalSessionRemote rentalSessionRemote, String name) throws Exception {
        return new ArrayList<>(rentalSessionRemote.confirmQuotes());
    }

    @Override
    protected int getNumberOfReservationsBy(ManagerSessionRemote ms, String clientName) throws Exception {
        return ms.getReservationsBy(clientName);
    }

    @Override
    protected int getNumberOfReservationsForCarType(ManagerSessionRemote ms, String carRentalName, String carType) throws Exception {
        //TODO carRentalName isn't necessary in my opinion? M: lets see what their tests return
        return ms.getReservationCount(carType);
    }
}
