package client;

import interfaces.SessionManagerRemote;

import java.rmi.RemoteException;

public class ManagerClient extends AbstractClient {

    //TODO implement typical manager client methods

    public ManagerClient(String clientName) {
        super(clientName);
    }

    @Override
    protected void afterInit(SessionManagerRemote sessionManager) throws RemoteException {
        sessionManager.createRentalSession(getClientName());
    }
}
