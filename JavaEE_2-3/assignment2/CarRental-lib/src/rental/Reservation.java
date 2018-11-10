package rental;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Reservation extends Quote {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int reservationID;

    @ManyToOne
    private Car car;
    
    /***************
     * CONSTRUCTOR *
     ***************/
    
    /**
     * Default constructor for JPA
     */
    protected Reservation() {  }

    public Reservation(Quote quote, Car car) {
    	super(quote.getCarRenter(), quote.getStartDate(), quote.getEndDate(), 
    		quote.getRentalCompany(), quote.getCarType(), quote.getRentalPrice());
        this.car = car;
    }
    
    /******
     * ID *
     ******/
    
    public int getCarId() {
    	return car.getId();
    }
    
    /*************
     * TO STRING *
     *************/
    
    @Override
    public String toString() {
        return String.format("Reservation for %s from %s to %s at %s\nCar type: %s\tCar: %s\nTotal price: %.2f", 
                getCarRenter(), getStartDate(), getEndDate(), getRentalCompany(), getCarType(), getCarId(), getRentalPrice());
    }	
}