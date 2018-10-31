package client;

import client.provided.AbstractTestManagement;
import interfaces.*;
import rental.company.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

public class ClientMain extends AbstractTestManagement<RentalSessionRemote, ManagerSessionRemote> {

    private SessionManagerRemote sessionManager;

    public SessionManagerRemote getSessionManager() {
        return sessionManager;
    }

    public static void main(String[] args) throws Exception {
        ClientMain main = new ClientMain();
        //option: manager registers the company
        if(args.length > 0 && args[0].equals("MR")){
            main.registerAllCompanies();
        }
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
        return ms.getBestCustomers();
    }

    @Override
    protected String getCheapestCarType(RentalSessionRemote rentalSessionRemote, Date start, Date end, String region) throws Exception {
        return rentalSessionRemote.getCheapestCarType(start,end,region).getName();
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
        Quote quote = null;
        for (String company: rentalSessionRemote.getAllCompanies()) {
            try {
                quote = rentalSessionRemote.createQuote(new ReservationConstraints(start,end,carType,region,company));
                if (quote != null) {
                    break;
                }
            } catch (ReservationException exc) {
                // let it go
            }
        }

        if(quote == null){
            throw new ReservationException("could not find an available car for you");
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

    private void registerAllCompanies() throws Exception {
        String companyFiles[] = {"hertz.csv", "dockx.csv"};
        ManagerSessionRemote ms = getNewManagerSession("", "");
        for(String companyFile: companyFiles){
            CarRentalCompanyRemote company = CompanyCreator.createCompany(companyFile);
            ms.registerRentalCompany(company);
        }

        ms.close(); //close the session
    }
}
