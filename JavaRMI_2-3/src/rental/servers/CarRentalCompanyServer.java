package rental.servers;

import interfaces.CarRentalCompanyRemote;
import rental.company.Car;
import rental.company.CarRentalCompany;
import rental.company.CarType;
import rental.company.ReservationException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * The server used by the car rental companies
 * will store the information about every car rental company
 * --> each rental company should get a different server in principle
 */
public class CarRentalCompanyServer {
    public static void init( Collection<CarRentalCompany> companies) throws ReservationException, IOException {
        System.setSecurityManager(null);

        try{
            Registry registry = LocateRegistry.getRegistry();
            //then add all the companies to the registers
            registerAllCompanies(companies, registry);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private static void registerAllCompanies(Collection<CarRentalCompany> companies, Registry registry) throws RemoteException {
        for(CarRentalCompanyRemote company: companies){
            System.out.println("currently registering: " + company.getName());
            CarRentalCompanyRemote stub = (CarRentalCompanyRemote) UnicastRemoteObject.exportObject(company, PORT);
            registry.rebind(company.getName(), stub);
        }
    }

    public static Collection<CarRentalCompany> createCompanies() throws IOException, ReservationException {
        CrcData hertzData = loadData(HERTZ);
        CrcData dockxData = loadData(DOCKX);
        CarRentalCompany hertz = new CarRentalCompany(hertzData.name, hertzData.regions, hertzData.cars);
        CarRentalCompany dockx = new CarRentalCompany(dockxData.name, dockxData.regions, dockxData.cars);

        List<CarRentalCompany> companyList = new ArrayList<>();
        companyList.add(hertz);
        companyList.add(dockx);

        return companyList;

    }

    public static CrcData loadData(String datafile)
            throws ReservationException, NumberFormatException, IOException {

        CrcData out = new CrcData();
        int nextuid = 0;

        // open file
        BufferedReader in = new BufferedReader(new FileReader(datafile));
        StringTokenizer csvReader;

        try {
            // while next line exists
            while (in.ready()) {
                String line = in.readLine();

                if (line.startsWith("#")) {
                    // comment -> skip
                } else if (line.startsWith("-")) {
                    csvReader = new StringTokenizer(line.substring(1), ",");
                    out.name = csvReader.nextToken();
                    out.regions = Arrays.asList(csvReader.nextToken().split(":"));
                } else {
                    // tokenize on ,
                    csvReader = new StringTokenizer(line, ",");
                    // create new car type from first 5 fields
                    CarType type = new CarType(csvReader.nextToken(),
                            Integer.parseInt(csvReader.nextToken()),
                            Float.parseFloat(csvReader.nextToken()),
                            Double.parseDouble(csvReader.nextToken()),
                            Boolean.parseBoolean(csvReader.nextToken()));
                    System.out.println(type);
                    // create N new cars with given type, where N is the 5th field
                    for (int i = Integer.parseInt(csvReader.nextToken()); i > 0; i--) {
                        out.cars.add(new Car(nextuid++, type));
                    }
                }
            }
        } finally {
            in.close();
        }

        return out;
    }

    static class CrcData {
        public List<Car> cars = new LinkedList<Car>();
        public String name;
        public List<String> regions =  new LinkedList<String>();
    }

    private final static String HERTZ = "hertz.csv";
    private final static String DOCKX = "dockx.csv";

    private final static int PORT = 0;
}
