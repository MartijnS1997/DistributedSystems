package client;

import interfaces.SessionManagerRemote;

import java.rmi.RemoteException;

public abstract class AbstractClient {


    private SessionManagerRemote sessionManager;

    private final String clientName;

    private String currentCompany;

    public String getClientName() {
        return clientName;
    }

    private SessionManagerRemote getSessionManager() {
        return sessionManager;
    }

    AbstractClient(String clientName){
        this.clientName = clientName;
        initSessionManager();
    }

    //TODO add operations to switch between companies


    /**
     * Initializes the session manager, retrieves the stub from the rmi registry
     */
    private void initSessionManager(){
        //TODO implement the method to retrieve the session manager
    }

    public void init() throws RemoteException {
        initSessionManager();
        afterInit(getSessionManager()); //do callback
    }

    public void terminate(){

    }

    /**
     * callback method to be called after the session manager is initialized
     * this creates the possibility to create a specific session from the session manager
     */
    protected abstract void afterInit(SessionManagerRemote sessionManager) throws RemoteException;
}
