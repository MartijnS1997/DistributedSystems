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
import javax.persistence.TemporalType;
import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;
import rental.RentalStore;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

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
        return ((Long)em.createQuery("SELECT COUNT(r) FROM CarRentalCompany company, IN(company.cars) c, IN(c.reservations) r WHERE c.type.name = :ctype")
                .setParameter("ctype", type).getSingleResult()).intValue();
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
        
        List<Reservation> reservations = em.createQuery("SELECT r FROM Reservation r ORDER BY r.carRenter").getResultList();
        
        int best = getMostReservations(reservations);
        System.out.println("Highest Count = " + best);
        
//        List<String> renters =em.createQuery("SELECT r.carRenter, Count(*) as c FROM Reservation r GROUP BY r.carRenter ORDER BY Count(r.reservationID) DESC").getResultList();
//        /**List<String> renters = em.createQuery("SELECT carRenter FROM Reservation GROUP BY carRenter "
//                + "HAVING COUNT(*)="
//                + "(SELECT TOP 1 COUNT(*) FROM Reservation GROUP BY carRenter ORDER BY COUNT(*) DESC)").getResultList(); */
        return this.getCustomersWithReservationCount(best, reservations);
    
    }
    
   
    private Set<String> getCustomersWithReservationCount(int reservationCount, List<Reservation> reservations){
        Set<String> customers = new HashSet<>();
        String currentCustomer = "";
        int currentCount = -1; // all the reservations will be larger than -1
        for(Reservation reservation : reservations){
            System.out.println("Current Renter: " + reservation.getCarRenter());
            //we're swapping customers, check now for the equality
            if(!reservation.getCarRenter().equals(currentCustomer)){
                if(currentCount == reservationCount){
                    customers.add(currentCustomer);
                }
                
                currentCount = 0;
            }
            
            currentCustomer = reservation.getCarRenter();
            currentCount++;
        }
        
        return customers;
    }

    private int getMostReservations(List<Reservation> reservations) {
        int bestResult = 0;
        int current = 0;
        String prevCust = "";
        for(Reservation reservation : reservations){
            // we may do this because the result is ordered alphabetically
            current = prevCust.equals(reservation.getCarRenter()) ? current + 1 : 1;
            bestResult = bestResult < current ? current : bestResult;
            prevCust = reservation.getCarRenter();
        }
        
        return bestResult;
    }
    

    @Override
    public CarType getMostPopular(String companyName, int year) {
        return (CarType) em.createQuery("SELECT cType FROM CarType cType, IN(cType.cars) c, IN(c.reservations) res WHERE "
                + "cType.company.name = :companyName and res.startDate BETWEEN :start AND :end GROUP BY cType ORDER BY COUNT(res) DESC ")
                .setParameter("companyName", companyName)
                .setParameter("start", new Date(year - 1900, 0, 1), TemporalType.DATE)
                .setParameter("end", new Date(year - 1899, 0, 1), TemporalType.DATE)
                .setMaxResults(1)
                .getSingleResult();

    }

    @Override
    public CarType getCheapestCarType(ReservationConstraints constraints){
        try{
            return (CarType) em.createNamedQuery("orderAvailableCarsByPrice")
                    .setParameter("start", constraints.getStartDate(), TemporalType.DATE)
                    .setParameter("end", constraints.getEndDate(), TemporalType.DATE)
                    .setParameter("region", constraints.getRegion())
                    .setMaxResults(1) //only select one result
                    .getSingleResult(); //get the single result
        }catch(Exception e){
                return null;
        }
    }
    
    @Override
    public int getNumberOfReservationsBy(String clientName) {
       return em.createQuery("SELECT Count(r) FROM Reservation r WHERE r.carRenter = :clientName" )
               .setParameter("clientName", clientName).getFirstResult();

    }

}