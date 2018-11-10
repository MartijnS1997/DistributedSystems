package rental;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Car implements Serializable{

    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO) <-- already generated in the constructor
    public int id;
    
    // Unidirectional relation between Car and CarType (so Car knows about its CarType, but CarType doesn't
    // know how many Cars it has)
    @ManyToOne
    public CarType type;
    
    // One car can have many reservations, a reservation can have only one car
    // If a car is removed, the reservations should also be removed -> REMOVE
    // If a car is added and already has some reservations, cascade -> PERSIST
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "car")
    public Set<Reservation> reservations;
   
    @ManyToOne
    public CarRentalCompany carRentalCompany;


    /***************
     * CONSTRUCTOR *
     ***************/
    
    public Car(int uid, CarType type) {
    	this.id = uid;
        this.type = type;
        this.reservations = new HashSet<Reservation>();
    }
    
    /************
     * CAR TYPE *
     ************/
    
    public CarType getType() {
        return type;
    }
	
	public void setType(CarType type) {
		this.type = type;
	}
    /****************
     * RESERVATIONS *
     ****************/

    public boolean isAvailable(Date start, Date end) {
        if(!start.before(end))
            throw new IllegalArgumentException("Illegal given period");

        for(Reservation reservation : reservations) {
            if(reservation.getEndDate().before(start) || reservation.getStartDate().after(end))
                continue;
            return false;
        }
        return true;
    }
    
    public void addReservation(Reservation res) {
        reservations.add(res);
    }
    
    public void removeReservation(Reservation reservation) {
        // equals-method for Reservation is required!
        reservations.remove(reservation);
    }

    public Set<Reservation> getReservations() {
        return reservations;
    }
}