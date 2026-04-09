package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "theaters")
public class Theater implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String location;

    @OneToMany(mappedBy = "theater")
    private List<Hall> halls;

    @OneToOne
    private TheaterManager manager;

    public Theater() {
    }

    public Theater(int id, String location, List<Hall> halls, TheaterManager manager) {
        this.id = id;
        this.location = location;
        this.halls = halls;
        this.manager = manager;
    }

    // Getters and Setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<Hall> getHalls() {
        return halls;
    }

    public void setHalls(List<Hall> halls) {
        this.halls = halls;
    }

    public TheaterManager getManager() {
        return manager;
    }

    public void setManager(TheaterManager manager) {
        this.manager = manager;
    }

    @Override
    public String toString() {
        return this.getLocation();
    }

}