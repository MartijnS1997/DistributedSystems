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

    /**
     * See page 734 in Java EE 5 tutorial for IN semantics
     * @param company
     * @param type
     * @return 
     */
    @Override
    public int getNumberOfReservations(String company, String type) {
        CarRentalCompany rentalCompany = em.find(CarRentalCompany.class, company);
        return em.createQuery("SELECT COUNT(r) FROM CarRentalCompany company, IN(company.cars) c, IN(c.reservations) r WHERE c.type.name = :ctype")
                .setParameter("ctype", type).getFirstResult();
    }


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW) // Make a new transaction when adding a company
    public void addRentalCompany(String companyName, List<String> regions, List<Car> cars) {
        em.persist(new CarRentalCompany(companyName, regions, cars));
    }

    @Override
    public List<String> getAllRentalCompanies() {
        return em.createNamedQuery("allCompanies").getResultList();
    }

    //TODO fix, is broken (multiple car renters must be returned if they have equal )
    @Override
    public Set<String> bestClients() {
        List<String> out = em.createQuery("SELECT r.carRenter FROM Reservation r GROUP BY r.carRenter ORDER BY Count(r.reservationID) DESC").getResultList();
        Set<String> outset =new HashSet<>();
        outset.add(out.get(0));
        return outset;
    
    }

    //TODO not forgetti
    @Override
    public CarType getMostPopular(String company, Date year) {
        //return em.createQuery("Select cType FROM Reservation r, IN(r.car) c,  FROM ")
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

    }

    //TODO also keep in mind the constraints (currently not done)
    @Override
    public CarType getCheapestCarType(ReservationConstraints constraints) {
        return (CarType) em.createQuery("SELECT cType FROM CarType cType ORDER BY cType.rentalPricePerDay DESC").getResultList().get(0);
    }
    
    @Override
    public int getNumberOfReservationsBy(String clientName) {
       return em.createQuery("SELECT Count(r) FROM Reservation r WHERE r.carRenter = :clientName" )
               .setParameter("clientName", clientName).getFirstResult();

    }

}