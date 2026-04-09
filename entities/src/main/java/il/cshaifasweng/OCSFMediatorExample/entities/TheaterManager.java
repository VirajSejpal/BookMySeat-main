package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;

@Entity
@Table(name = "theater_managers")
@Inheritance(strategy = InheritanceType.JOINED)
public class TheaterManager extends Employee {

    @OneToOne
    @JoinColumn(name = "theater")
    private Theater theater;

    public TheaterManager() {
    }

    // Getters and Setters

    public TheaterManager(String id_number, String name, boolean isOnline, String password, EmployeeType employeeType, Theater theater) {
        super(id_number,name, isOnline, password, employeeType);
        this.theater = theater;
    }

    public Theater getTheater() {
        return theater;
    }

    public void setTheater(Theater theater) {
        this.theater = theater;
    }


}