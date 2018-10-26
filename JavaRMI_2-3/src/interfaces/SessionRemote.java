package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * common interface for sessions, provides an interface for closing sessions
 * and retrieving the id of the current session
 */
public interface SessionRemote extends Remote {
    long getId() throws RemoteException;
    void close() throws RemoteException;
}
