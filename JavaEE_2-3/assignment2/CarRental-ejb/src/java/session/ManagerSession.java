package session;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;
import rental.RentalStore;
import rental.Reservation;
import rental.ReservationConstraints;

/**
 * Important note: A manager cannot create a new CarType unless a CarRentalCompany already 
 * exists. The same goes for Car and CarType.
 * @author Clara De Smet
 * @author Martijn Sauwens
 */
@Stateless
public class ManagerSession implements ManagerSessionRemote {
    
    //the persistence manager used to add the new companies, cars and car types
    @PersistenceContext
    EntityManager entityManager;
    
    @Override
    public Set<CarType> getCarTypes(String company) {
        try {
            return new HashSet<CarType>(RentalStore.getRental(company).getAllTypes());
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Set<Car> getCars(String company, String type) {
        Set<Car> out = new HashSet<Car>();
        try {
            for(Car c: RentalStore.getRental(company).getCars(type)){
                out.add(c);
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return out;
    }

    @Override
    public int getNumberOfReservations(String company, String type, int id) {
        try {
            return RentalStore.getRental(company).getCar(id).getReservations().size();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    @Override
    public int getNumberOfReservations(String company, String type) {
        Set<Reservation> out = new HashSet<Reservation>();
        try {
            for(Car c: RentalStore.getRental(company).getCars(type)){
                out.addAll(c.getReservations());
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
        return out.size();
    }

    @Override
    public Set<String> getAllRentalCompanies() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    
     @Override
    public void addRentalCompany(String companyName, List<String> regions) {
        CarRentalCompany company = new CarRentalCompany(companyName, regions);
        entityManager.persist(company);
    }
    
    @Override
    public void addCar(String companyName, Car car) {
        //get the company to add the car to
        CarRentalCompany company = entityManager.find(CarRentalCompany.class, companyName);
        car.carRentalCompany = company;
        CarType carType = car.type;
        CarType carTypeInDatabase = entityManager.find(CarType.class, carType.name);
        
        if(carTypeInDatabase != null){
            car.type = carTypeInDatabase;
        }else{
            entityManager.persist(carType);
        }
        entityManager.persist(car);
    }

    @Override
    public void addCarRentalCompany(String companyName, List<String> regions, List<Car> cars) {
        CarRentalCompany company = new CarRentalCompany(companyName, regions, cars);
        entityManager.persist(company);
    }
    
    
}