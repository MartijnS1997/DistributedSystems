package client.provided;

import interfaces.CarRentalCompanyRemote;
import interfaces.ManagerSessionRemote;
import interfaces.RentalSessionRemote;
import interfaces.SessionManagerRemote;
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
        this.sessionManager = (SessionManagerRemote)registry.lookup(SessionManager.getManagerName());
    }


    @Override
    protected Set<String> getBestClients(ManagerSessionRemote ms) throws Exception {
        //TODO a set of best clients?? So one from each company or do we have to compare and pick the top X clients?
       return ms.getRegisteredCompanies().stream().map(companyName -> {
           try {
               return ms.bestCustomer(companyName);
           } catch (RemoteException e) {
               e.printStackTrace();
               return "An error Occurred";
           }
       }).collect(Collectors.toSet());
    }

    @Override
    protected String getCheapestCarType(RentalSessionRemote rentalSessionRemote, Date start, Date end, String region) throws Exception {
        //TODO Is it ok to use toString() here ? Yes :) carType implements to string
        return rentalSessionRemote.getCheapestCarType(start,end,region).toString();
    }

    @Override
    protected CarType getMostPopularCarTypeIn(ManagerSessionRemote ms, String carRentalCompanyName, int year) throws Exception {
        return ms.mostWanted(carRentalCompanyName, year);
    }

    @Override
    protected RentalSessionRemote getNewReservationSession(String name) throws Exception {
        //TODO Do we set up a session manager here? If so sessionManager.createRentalSession(name);
        return sessionManager.createRentalSession(name);
    }

    @Override
    protected ManagerSessionRemote getNewManagerSession(String name, String carRentalName) throws Exception {
        //TODO Same as getNewReservationSession
        return sessionManager.createManagerSession();
    }

    @Override
    protected void checkForAvailableCarTypes(RentalSessionRemote rentalSessionRemote, Date start, Date end) throws Exception {
        List<CarType> availableCarTypes = new ArrayList<>();
        for (String company : rentalSessionRemote.getAllCompanies()) {
            Collection<CarType> carTypes = rentalSessionRemote.getAvailableCarTypes(start,end,company);
            availableCarTypes.addAll(carTypes);
        }

        availableCarTypes.forEach(carType -> System.out.println(carType.toString()));
        //TODO print availableCarTypes?
    }

    @Override
    protected void addQuoteToSession(RentalSessionRemote rentalSessionRemote, String name, Date start, Date end, String carType, String region) throws Exception {
        //TODO We're missing a company name... Iterate all companies and pick the first that suffices? M: Yes i think so
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
