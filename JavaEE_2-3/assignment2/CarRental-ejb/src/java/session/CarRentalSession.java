package session;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NamedQuery;
import javax.persistence.PersistenceContext;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Quote;
import rental.RentalStore;
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
        for(Quote quote : getCurrentQuotes()) {
            CarRentalCompany company = em.find(CarRentalCompany.class, quote.getRentalCompany());
            try {
                Reservation res = company.confirmQuote(quote);
                reservations.add(res);
                em.persist(res); // Persist this reservation in the database
            } catch (ReservationException e) {
                context.setRollbackOnly(); // Rollback all quotes
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
}