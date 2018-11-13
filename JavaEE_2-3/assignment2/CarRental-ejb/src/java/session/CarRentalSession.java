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

    @Override
    public List<String> getAllRentalCompanies() {
        return em.createNamedQuery("allCompanies").getResultList();
    }
    
    //TODO Maybe optimise if we have time left
    @Override
    public List<CarType> getAvailableCarTypes(Date start, Date end) {
        List <CarType> out = new ArrayList<>();
        for (String companyName : getAllRentalCompanies()) {
            out.addAll(em.find(CarRentalCompany.class, companyName).getAvailableCarTypes(start, end));
        }
        return out;
    }

    @Override
    public Quote createQuote(ReservationConstraints constraints) throws ReservationException {
        Quote q = null;
        for (String companyName : getAllRentalCompanies()) {
            try {
                q = em.find(CarRentalCompany.class,companyName).createQuote(constraints, getRenter());
            } catch (Exception e) {
                // Continue searching for available cars
            }
            if (q != null) {break;}
        }
        
        if(q == null){
            throw new ReservationException("No car type available for: " + constraints.toString());
        }
        quotes.add(q);
        return q;
    }

    @Override
    public List<Quote> getCurrentQuotes() {
        return quotes;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<Reservation> confirmQuotes() throws ReservationException {
        // TODO optimise by grouping quotes for a specific company together (less queries)
        List<Reservation> reservations = new ArrayList<>();
        System.out.println(getCurrentQuotes());
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

    @Override
    public void setRenterName(String name) {
        if (renter != null) {
            throw new IllegalStateException("name already set");
        }
        renter = name;
    }

    @Override
    public CarType getCheapestCarType(Date start, Date end,String region) throws ReservationException {
        try{
            return (CarType) em.createNamedQuery("orderAvailableCarsByPrice")
                    .setParameter("start", start, TemporalType.DATE)
                    .setParameter("end", end, TemporalType.DATE)
                    .setParameter("region", region)
                    .setMaxResults(1) //only select one result
                    .getSingleResult(); //get the single result
        }catch(Exception e){
                throw new ReservationException("No cars available for the given date");
        }
    }
}