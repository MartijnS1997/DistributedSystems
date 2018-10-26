package rental.servers;

import rental.company.CarRentalAgency;
import rental.session.SessionManager;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * The agency server should register instances of sessions and
 * serve the customers, as well as the managers
 */
public class AgencyServer {
    public static void main(String[] args){
        try{
            Registry registry = LocateRegistry.getRegistry();
            CarRentalAgency agency = new CarRentalAgency();
            SessionManager.createSessionManager(agency, registry, PORT);
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }

    private final static int PORT = 0;
}
