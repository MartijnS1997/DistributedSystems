package rental.company;

import interfaces.ManagerSessionRemote;
import interfaces.RentalSessionRemote;
import interfaces.SessionManagerRemote;
import rental.session.SessionManager;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class TestServer {
    public static void main (String[] args) throws RemoteException {
        System.setSecurityManager(null);
        //lookup the registry
        Registry registry = LocateRegistry.getRegistry();
        //set a port to listen on
        int port = 0;
        SessionManager.createSessionManager(null, registry, port);
    }

    public final static String SESSION_MANAGER = "SessionManager";
}
