package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("movie_ticket")
public class MovieTicket extends Purchase{

    @OneToOne
    private MovieInstance movieInstance;

    @OneToOne
    private Seat seat;

    public MovieTicket() {
    }

    public MovieTicket(LocalDateTime purchaseDate, RegisteredUser owner, String purchaseValidation, MovieInstance movieInstance, Seat seat, boolean isActive, int pricePaid) {
        super(purchaseDate, owner, purchaseValidation, isActive, pricePaid);
        this.movieInstance = movieInstance;
        this.seat = seat;
    }

    // Getters and setters

    public MovieInstance getMovieInstance() {
        return movieInstance;
    }

    public void setMovieInstance(MovieInstance movieInstance) {
        this.movieInstance = movieInstance;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    @Override
    protected String getPurchaseType() {
        return "MovieTicket";
    }

}