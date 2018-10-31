package rental.servers;

import org.omg.PortableServer.POA;
import rental.company.CarRentalAgency;
import rental.session.SessionManager;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class EmptyAgencyServer {
    public static void main(String args[]) throws RemoteException {
        Registry registry = LocateRegistry.getRegistry();
        CarRentalAgency agency = new CarRentalAgency();
        SessionManager.createSessionManager(agency, registry, PORT);
    }

    private final static int PORT = 0;
}
