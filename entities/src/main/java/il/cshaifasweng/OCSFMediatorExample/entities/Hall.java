package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "halls")
public class Hall implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Seat> seats;

    @Column(nullable = false)
    private int capacity;

    @ManyToOne
    private Theater theater;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "hall")
    private List<MovieInstance> movieInstances;

    public Hall() {
    }

    public Hall(int id, List<Seat> seats, int capacity, Theater theater, List<MovieInstance> movieInstances, String name) {
        this.id = id;
        this.seats = seats;
        this.capacity = capacity;
        this.theater = theater;
        this.movieInstances = movieInstances;
        this.name = name;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Theater getTheater() {
        return theater;
    }

    public void setTheater(Theater theater) {
        this.theater = theater;
    }

    public List<MovieInstance> getMovieInstances() {
        return movieInstances;
    }

    public void setMovieInstances(List<MovieInstance> movieInstances) {
        this.movieInstances = movieInstances;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return this.getName();
    }
}
