package rental.session;

import interfaces.*;
import rental.company.CarRentalAgency;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

/**
 * This class does lifecycle management of sessions, so that CarRentalAgency does not need to be concerned with that.
 * The class is a singleton class since there should not be multiple session managers active on the same server
 */
public class SessionManager implements SessionManagerRemote {

    static SessionManager singletonManager = null;

    /**
     * Extra name Identifier
     */
    private final static String SESSION_STRING = "session";

    /**
     * The name used to find the manager in the registry
     */
    private final static String MANAGER_NAME = "SessionManager";

//    /**
//     * map that stores the sessions created, based on the ID of the session
//     */
//    private Map<String,Session> sessions = new HashMap<>();

    /**
     * counter to give each session a unique ID
     */
    private long idCounter = 1;


    /**
     * Every SessionManager belongs to one CarRentalCompany and vice versa.
     */
    private final CarRentalAgency rentalAgency;

    /**
     * The registry wherein the newly created sessions are stored
     */
    private final Registry serverRegistry;


    /**
     * the port on which to publish the results
     */
    private final int serverPort;

    /**
     * Getters and setters
     */

//    private Map<String, Session> getSessions() {
//        return sessions;
//    }

    private CarRentalAgency getRentalAgency() {
        return rentalAgency;
    }

    private Registry getServerRegistry() {
        return serverRegistry;
    }

    private int getServerPort() {
        return serverPort;
    }

    //TODO Can this be private as according to suggestion?
    public static String getManagerName() {
        return MANAGER_NAME;
    }

    /**
     * Constructor
     */

    /**
     * creator method for the session manager. This method is called to create a session manager.
     * If the manager already exists the instance created earlier is returned.
     * The created manager can be found in the registry with the method: getManagerName();
     * @param agency the car rental agency to create sessions for
     * @param registry the registry used to register the manager and the sessions created by the manager
     * @param port the port at which to communicate with the server
     * @return because the session manager is a singleton class it can only return one instance of the manager
     * @throws RemoteException if the registration of the manager goes wrong
     *
     */
    public static SessionManager createSessionManager(CarRentalAgency agency, Registry registry, int port) throws RemoteException {
        return singletonManager == null ? new SessionManager(agency, registry, port) : singletonManager;
    }

    /**
     * See creator method for the semantics
     */
    private SessionManager(CarRentalAgency agency, Registry registry, int port) throws RemoteException{
        rentalAgency = agency;
        serverRegistry = registry;
        serverPort = port;
        registerManager();
    }

    /**
     * Register this session manager so it can be found in the registry
     * @throws RemoteException
     */
    private void registerManager() throws RemoteException{
        //generate the stub
        SessionManagerRemote remoteManager = this;
        SessionManagerRemote managerStub = (SessionManagerRemote) UnicastRemoteObject.exportObject(remoteManager, getServerPort());
        //then bind the manager to the registry
        getServerRegistry().rebind(getManagerName(), managerStub);
    }

    /**
     * Implementation of remote interface methods
     */

    /**
     * Creates a new rental session
     * @param clientName the name of the client for the session
     * @return the newly created stub (such that no extra lookup is needed)
     * @throws RemoteException if something goes wrong during registring and/or creating
     * NOTE: check if we may return the remote object or that we must return the name of the created
     *
     */
    @Override
    public RentalSessionRemote createRentalSession(String clientName) throws RemoteException{
        //first create a new rental session instance
        long sessionId = generateSessionId();
        RentalSessionRemote rentalSession = new RentalSession(clientName, getRentalAgency(), sessionId, this);
        //then create a stub for the rental session
        return addSessionToRegistry(rentalSession, sessionId);
    }
    @Override
    public ManagerSessionRemote createManagerSession() throws RemoteException {
        //first create a new manager session instance
        long sessionId = generateSessionId();
        ManagerSessionRemote managerSession = new ManagerSession(getRentalAgency(), sessionId, this);
        //then add the stub to the registry
        return addSessionToRegistry(managerSession, sessionId);
    }

    @Override
    public void closeSession(long sessionId) throws RemoteException{
        //remove the session from the registry
        try{
            getServerRegistry().unbind(Long.toString(sessionId));
        }catch(NotBoundException e){
            //let it fly
        }
    }

    /**
     * Increment the id counter and return that to the current session
     * @return
     */
    private long generateSessionId(){
        return idCounter++;
    }

    /**
     * Generic method used to add a remote session to the registry
     * @param session the session to add to the registry
     * @param identifier identifier for the session to put into the registry
     * @param <S> the type of the session. Must be a subtype of the SessionRemote interface
     * @return the created stub
     * @throws RemoteException if something went wrong during the creation of the stub
     */
    private <S extends SessionRemote> S addSessionToRegistry(S session, long identifier) throws RemoteException {
        //create the stub from the session
        S sessionStub = (S) UnicastRemoteObject.exportObject(session, getServerPort()); //ignore the warning, will work
        //add the session to the registry
        getServerRegistry().rebind(getUniqueSessionName(identifier), sessionStub);
        return sessionStub;

    }

    /**
     * converts the session id into a more 'unique' reference name to avoid conflicts
     * @param sessionID the id to convert
     * @return the name of the session created. Used to get a unique name in the RMI registry
     */
    private static String getUniqueSessionName(long sessionID){
        return SESSION_STRING + '/' + sessionID;
    }
}
