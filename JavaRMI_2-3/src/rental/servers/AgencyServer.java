package rental.servers;

import interfaces.CarRentalCompanyRemote;
import interfaces.RentalSessionRemote;
import rental.company.CarRentalAgency;
import rental.session.SessionManager;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collection;

/**
 * The agency server should register instances of sessions and
 * serve the customers, as well as the managers
 */
public class AgencyServer {
    public static void init(Collection<CarRentalCompanyRemote> companies) {
        try {
            Registry registry = LocateRegistry.getRegistry();
            //create the agency
            CarRentalAgency agency = new CarRentalAgency();

            //before launch add all the companies
            for(CarRentalCompanyRemote company : companies){
                agency.registerCompany(company);
            }

            //then launch the manager
            SessionManager.createSessionManager(agency, registry, PORT);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private final static int PORT = 0;
}
