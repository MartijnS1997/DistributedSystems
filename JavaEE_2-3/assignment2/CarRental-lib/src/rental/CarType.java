package rental;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class CarType implements Serializable {
    
    //note that we have to generate an unique id because each company
    //has to define their own car types, if we would choose the name of the
    //car type to be the primary key. No two companies would be able to offer 
    //the same car type
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    
    /**
     * Because we do not use the ToOne side of the relationship
     * between company and car type often, we do not need to load it
     * every time a car type is queried (this side of the relationship is 
     * only in place because the company should gain access to car types)
     */
    @ManyToOne(fetch=FetchType.LAZY)
    private CarRentalCompany company;
    
    /**
     * Added for convenience during querying (requires no extra work in other cases
     * because of lazy loading of the "ToMany" side of the relationship
     */
    @OneToMany(mappedBy ="type")
    private Collection<Car> cars;
    
    private String name;
    private int nbOfSeats;
    private boolean smokingAllowed;
    private double rentalPricePerDay;
    //trunk space in liters
    private float trunkSpace;
    
    /***************
     * CONSTRUCTOR *
     ***************/
    
    /**
     * Default constructor needed for JPA
     */
    protected CarType(){ }
    
    public CarType(String name, int nbOfSeats, float trunkSpace, double rentalPricePerDay, boolean smokingAllowed) {
        this.name = name;
        this.nbOfSeats = nbOfSeats;
        this.trunkSpace = trunkSpace;
        this.rentalPricePerDay = rentalPricePerDay;
        this.smokingAllowed = smokingAllowed;
    }
    
    protected void setCompany(CarRentalCompany company){
        this.company = company;
    }

    public String getName() {
    	return name;
    }
    
    public int getNbOfSeats() {
        return nbOfSeats;
    }
    
    public boolean isSmokingAllowed() {
        return smokingAllowed;
    }

    public double getRentalPricePerDay() {
        return rentalPricePerDay;
    }
    
    public float getTrunkSpace() {
    	return trunkSpace;
    }
    
    /*************
     * TO STRING *
     *************/
    
    @Override
    public String toString() {
    	return String.format("Car type: %s \t[seats: %d, price: %.2f, smoking: %b, trunk: %.0fl]" , 
                getName(), getNbOfSeats(), getRentalPricePerDay(), isSmokingAllowed(), getTrunkSpace());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
	int result = 1;
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
	if (obj == null)
            return false;
	if (getClass() != obj.getClass())
            return false;
	CarType other = (CarType) obj;
	if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
	return true;
    }
}