package client;

import interfaces.RentalSessionRemote;
import interfaces.SessionManagerRemote;
import rental.company.CarType;
import rental.company.TestServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Use as you wish... place to test stuff if you want to make sure it works
 */
public class TestClient {
    public static void main(String[] args) throws RemoteException, NotBoundException {
        System.setSecurityManager(null);
        String managerRef = TestServer.SESSION_MANAGER;
        Registry registry = LocateRegistry.getRegistry();
        SessionManagerRemote manager = (SessionManagerRemote) registry.lookup(managerRef);
        //then create a new session
        RentalSessionRemote rentalSession = manager.createRentalSession("newClient");
        CarType carType = rentalSession.getCheapestCarType(null, null, null);
        System.out.println(carType.toString());
    }
}
