package interfaces;

import rental.session.Session;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SessionManagerRemote extends Remote {

    /**
     * Creates a remote session for a manager
     * @return the remote manager
     * @throws RemoteException something goes wrong with the connection
     */
    ManagerSessionRemote createManagerSession() throws RemoteException;

    /**
     * Creates a remote session to rent some cars in the agency
     * @return the remote rental session
     * @throws RemoteException something went wrong with the connection
     */
    RentalSessionRemote createRentalSession(String clientName) throws RemoteException;

    /**
     * closes the given session
     * @param sessionId the session to close
     * @throws RemoteException if something with the connection goes wrong
     * TODO check if it is feasable to set the currentSession as the argument, since now other sessions
     * can close different sessions than themselves
     * Clara: Good question... How would you do it otherwise?
     */
    void closeSession(long sessionId) throws RemoteException;
}
