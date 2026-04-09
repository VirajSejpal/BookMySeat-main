package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("Home_Viewing_Package")
public class HomeViewingPackageInstance extends Purchase {

    @OneToOne
    private Movie movie;

    private LocalDateTime activationDate;

    private String link;

    private boolean linkActive;

    public HomeViewingPackageInstance() {
    }

    public HomeViewingPackageInstance(LocalDateTime purchaseDate, RegisteredUser owner, String purchaseValidation, Movie movie, LocalDateTime viewingDate, boolean isActive, String link, int pricePaid) {
        super(purchaseDate, owner, purchaseValidation, isActive,pricePaid);
        this.movie = movie;
        this.activationDate = viewingDate;
        this.link = link;
        this.linkActive = false; // Link is inactive by default
    }

    // Getters and setters

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public LocalDateTime getViewingDate() {
        return activationDate;
    }

    public void setViewingDate(LocalDateTime viewingDate) {
        this.activationDate = viewingDate;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLink() {
        return this.link;
    }

    public LocalDateTime getActivationDate() {
        return this.activationDate;
    }

    public void setActivationDate(LocalDateTime dateTime) {
        this.activationDate = dateTime;
    }

    public boolean isLinkActive() {
        return linkActive;
    }


    public void activateLink() {
        this.linkActive = true;
    }

    public void deactivateLink() {
        this.linkActive = false;
    }

    @Override
    protected String getPurchaseType() {
        return "HomeViewingPackageInstance";
    }
}