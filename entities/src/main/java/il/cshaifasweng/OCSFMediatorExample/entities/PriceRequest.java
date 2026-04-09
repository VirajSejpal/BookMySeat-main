package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "price_requests")
public class PriceRequest implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private int newPrice; // Changed from String to int for better representation

    @ManyToOne
    private Movie movie;

    private Movie.StreamingType type; //what price to change

    public PriceRequest() {
    }

    public PriceRequest(int newPrice, Movie movie, Movie.StreamingType type) {
        this.newPrice = newPrice;
        this.movie = movie;
        this.type = type;
    }


    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNewPrice() {
        return newPrice;
    }

    public String getEnglishName() {
        return movie.getEnglishName();
    }

    public Movie.StreamingType getStreamingType() {
        return type;
    }

    public void setNewPrice(int newPrice) {
        this.newPrice = newPrice;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Movie.StreamingType getType() {return type;}

    public void setType(Movie.StreamingType type) {this.type = type;}
}