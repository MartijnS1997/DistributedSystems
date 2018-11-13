package session;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;
import rental.RentalStore;
import rental.Reservation;
import rental.ReservationConstraints;

@Stateless
public class ManagerSession implements ManagerSessionRemote {
    
    @PersistenceContext
    EntityManager em;
    
    @Override
    public Set<CarType> getCarTypes(String companyName) {
        CarRentalCompany company = em.find(CarRentalCompany.class, companyName);
        Set<CarType> types = company.getAllTypes();
        return types;
    }

    @Override
    public Set<Integer> getCarIds(String company, String type) {
        Set<Integer> out = new HashSet<Integer>();
        try {
            for(Car c: RentalStore.getRental(company).getCars(type)){
                out.add(c.getId());
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return out;
    }

    @Override
    public int getNumberOfReservations(String company, String type) {
        CarRentalCompany rentalCompany = em.find(CarRentalCompany.class, company);
        return em.createQuery("SELECT COUNT(r) FROM company, IN(company.cars) car, IN(car.reservations) r WHERE car.type = :ctype")
                .setParameter("ctype", type).getFirstResult();
    }


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW) // Make a new transaction when adding a company
    public void addRentalCompany(String companyName, List<String> regions, List<Car> cars) {
        em.persist(new CarRentalCompany(companyName, regions, cars));
    }

    @Override
    public List<String> getAllRentalCompanies() {
        return em.createQuery("SELECT company.id FROM CarRentalCompany").getResultList();
    }

    @Override
    public Set<String> bestClients() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CarType getMostPopular(String company, Date year) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CarType getCheapestCarType(ReservationConstraints constraints) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}