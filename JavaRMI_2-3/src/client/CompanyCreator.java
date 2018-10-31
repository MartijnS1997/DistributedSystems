package client;

import interfaces.CarRentalCompanyRemote;
import rental.company.Car;
import rental.company.CarRentalCompany;
import rental.company.CarType;
import rental.company.ReservationException;
import rental.servers.CarRentalCompanyServer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class CompanyCreator {
    public  static CarRentalCompanyRemote createCompany(String csvFileName) throws IOException, ReservationException {
        CrcData companyData = loadData(csvFileName);
        CarRentalCompanyRemote company = new CarRentalCompany(companyData.name, companyData.regions, companyData.cars);
        Registry registry = LocateRegistry.getRegistry();
        CarRentalCompanyRemote stub = (CarRentalCompanyRemote) UnicastRemoteObject.exportObject(company, PORT);
        registry.rebind(company.getName(), stub);
        return stub;
    }

    private static CrcData loadData(String datafile)
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

    private final static int PORT = 0;

}
