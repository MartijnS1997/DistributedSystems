package rental.servers;

import interfaces.CarRentalCompanyRemote;
import rental.company.CarRentalCompany;
import rental.company.ReservationException;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

public class BootServersMain {
    public static void main(String[] args) throws IOException, ReservationException {
        Collection<CarRentalCompany> companies = CarRentalCompanyServer.createCompanies();
        CarRentalCompanyServer.init(companies);
        System.out.println(companies.toString());
        AgencyServer.init(new HashSet<>(companies));

    }
}
