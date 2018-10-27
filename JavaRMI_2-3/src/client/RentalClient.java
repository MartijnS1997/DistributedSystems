package client;

import interfaces.SessionManagerRemote;

import java.rmi.RemoteException;

public class RentalClient extends AbstractClient{

    //TODO implement typical rental client functionality

    RentalClient(String clientName) {
        super(clientName);
    }

    @Override
    protected void afterInit(SessionManagerRemote sessionManager) throws RemoteException {
        sessionManager.createRentalSession(getClientName());
    }
}
