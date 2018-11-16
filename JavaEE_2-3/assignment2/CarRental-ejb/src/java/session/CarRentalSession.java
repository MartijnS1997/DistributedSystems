package session;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Stateful
public class CarRentalSession implements CarRentalSessionRemote {
    
    @PersistenceContext
    EntityManager em;
    
    @Resource
    private SessionContext context;

    private String renter;
    private List<Quote> quotes = new LinkedList<Quote>();
    
    
    /**
     * Get the renter's name
     * @return the renter's name
     */
    private String getRenter() {
        return renter;
    }

    //no need for transaction since read only
    @Override
    public List<String> getAllRentalCompanies() {
        return em.createNamedQuery("allCompanies").getResultList();
    }
    
    //no need to have transactional behavior since only queries data
    @Override
    public List<CarType> getAvailableCarTypes(Date start, Date end) {
        return em.createQuery("SELECT DISTINCT cType FROM CarRentalCompany company, IN(company.carTypes) ctype, IN(cType.cars) c WHERE "
                + "NOT EXISTS (SELECT res FROM IN(c.reservations) res WHERE (:start BETWEEN res.startDate AND res.endDate) OR (:end BETWEEN res.startDate AND res.endDate))")
                .setParameter("start", start, TemporalType.DATE)
                .setParameter("end", end, TemporalType.DATE)
                .getResultList();
    }
    
    //No need for transaction support since read only
    @Override
    public Quote createQuote(ReservationConstraints constraints) throws ReservationException {
        Quote q = null;
        for (String companyName : getAllRentalCompanies()) {
            try {
                q = em.find(CarRentalCompany.class,companyName).createQuote(constraints, getRenter()); // try to create a quote
            } catch (Exception e) { // if not possible
                // Continue searching for available cars
            }
            if (q != null) {break;} // creating quote succeeded
        }
        
        if(q == null){
            throw new ReservationException("No car type available for: " + constraints.toString());
        }
        quotes.add(q);
        return q;
    }

    //also no transactional support needed since no database is touched
    @Override
    public List<Quote> getCurrentQuotes() {
        return quotes;
    }

    @Override
    //we need to start a transaction because we will update the database
    //we require a NEW transaction since this method gets never called from another bean
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<Reservation> confirmQuotes() throws ReservationException {
        List<Reservation> reservations = new ArrayList<>();
        for(Quote quote : getCurrentQuotes()) {
            CarRentalCompany company = em.find(CarRentalCompany.class, quote.getRentalCompany());
            try {
                Reservation res = company.confirmQuote(quote);
                reservations.add(res);
                em.persist(res); // Persist this reservation in the database
            } catch (ReservationException e) {
                context.setRollbackOnly(); // Rollback all quotes
                //throw the exception after the rollback... it will reach the user to signal the transaction went wrong
                throw new ReservationException("Unable to confirm quote: " + e.toString());
            }       
        }
        return reservations;
    }

    //also no need for transaction support since no data is touched
    @Override
    public void setRenterName(String name) {
        if (renter != null) {
            throw new IllegalStateException("name already set");
        }
        renter = name;
    }

    //no need for transactional support since query
    @Override
    public CarType getCheapestCarType(Date start, Date end, String region) throws ReservationException {
        try{

            return (CarType) em.createNamedQuery("orderAvailableCarsByPrice")
                    .setParameter("start", start, TemporalType.DATE)
                    .setParameter("end", end, TemporalType.DATE)
                    .setParameter("region", region)
                    .setMaxResults(1) //only select one result
                    .getSingleResult(); //get the single result
        }catch(Exception e){
                e.printStackTrace();
                throw new ReservationException("No cars available for the given date");
        }
    }
}