package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "seats")
public class Seat implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "row_num", nullable = false) // Avoid reserved keyword
    int row;

    @Column(nullable = false)
    int col;

    @ManyToOne
    Hall hall;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "taken_seats", joinColumns = @JoinColumn(name = "entity_id"))
    @Column(name = "seat_number")
    List<Integer> taken;

    public Seat() {
    }

    public Seat(int row, int col) {
        this.row = row;
        this.col = col;
        taken = new ArrayList<>();
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int rowNumber) {
        this.row = rowNumber;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public List<Integer> getMoviesIds() {
        return taken;
    }

    public void addMovieInstanceId(MovieInstance movie) {
        this.taken.add(movie.getId());
    }
    public void deleteMovieInstance(MovieInstance movieInstance) {
        Integer movieInstanceId = movieInstance.getId();
        this.taken.remove(movieInstanceId);
    }


    public void setHall(Hall hall) {
        this.hall = hall;
    }

    public Hall getHall() {
        return hall;
    }
}
