package client;

import client.CompanyLoader.CrcData;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.naming.InitialContext;
import rental.CarType;
import rental.Reservation;
import rental.ReservationConstraints;
import session.CarRentalSessionRemote;
import session.ManagerSessionRemote;

public class Main extends AbstractTestManagement<CarRentalSessionRemote, ManagerSessionRemote> {

    public Main(String scriptFile) {
        super(scriptFile);
    }

    public static void main(String[] args) throws Exception {
        // TODO: use updated manager interface to load cars into companies
        Main main = new Main("trips");
        main.loadCompanies();
        main.printAllCarTypes();
        System.out.println("number of reservations for Economy: " + main.getNumberOfReservationsForCarType(main.getNewManagerSession(null, null), "Hertz", "Economy"));
        System.out.println("get all rental companies: " + main.getNewManagerSession(null, null).getAllRentalCompanies());
        main.run();
    }
    
    private void loadCompanies() throws Exception{
        List<CrcData> companyData = CompanyLoader.loadAllData("hertz.csv", "dockx.csv");
        ManagerSessionRemote ms = getNewManagerSession(" ", " ");
        for(CrcData data : companyData){
            ms.addRentalCompany(data.name, data.regions, data.cars);
        }
    }
    
    private void printAllCarTypes() throws Exception{
        ManagerSessionRemote ms = getNewManagerSession(" ", " ");
        Set<CarType> types = ms.getCarTypes("Hertz");
        
        for(CarType type : types){
            System.out.println(type.getName());
        }
    }

    @Override
    protected Set<String> getBestClients(ManagerSessionRemote ms) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String getCheapestCarType(CarRentalSessionRemote session, Date start, Date end, String region) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected CarType getMostPopularCarTypeIn(ManagerSessionRemote ms, String carRentalCompanyName, int year) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected CarRentalSessionRemote getNewReservationSession(String name) throws Exception {
        InitialContext context = new InitialContext();
        CarRentalSessionRemote session = (CarRentalSessionRemote) context.lookup(CarRentalSessionRemote.class.getName());
        session.setRenterName(name);
        return session;
    }

    @Override
    protected ManagerSessionRemote getNewManagerSession(String name, String carRentalName) throws Exception {
        InitialContext context = new InitialContext();
        return (ManagerSessionRemote) context.lookup(ManagerSessionRemote.class.getName());
    }

    @Override
    protected void checkForAvailableCarTypes(CarRentalSessionRemote session, Date start, Date end) throws Exception {
        System.out.println("Available car types");
        Collection<CarType> types = session.getAvailableCarTypes(start, end);
        for(CarType type : types){
            System.out.println(type.getName());
        }
    }

    @Override
    protected void addQuoteToSession(CarRentalSessionRemote session, String name, Date start, Date end, String carType, String region) throws Exception {
        ReservationConstraints constraint = new ReservationConstraints(start, end, carType, region);
        session.createQuote(constraint);
    }

    @Override
    protected List<Reservation> confirmQuotes(CarRentalSessionRemote session, String name) throws Exception {
        return session.confirmQuotes();
    }

    @Override
    protected int getNumberOfReservationsBy(ManagerSessionRemote ms, String clientName) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected int getNumberOfReservationsForCarType(ManagerSessionRemote ms, String carRentalName, String carType) throws Exception {
        return ms.getNumberOfReservations(carRentalName, carType);
    }
}