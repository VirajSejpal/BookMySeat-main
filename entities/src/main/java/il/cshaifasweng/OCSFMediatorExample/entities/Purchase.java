package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


@Entity
@Table(name = "purchases")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)

public abstract class Purchase implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDateTime purchaseDate;

    @ManyToOne
    private RegisteredUser owner;

    @Column
    private String purchaseValidation;

    @Column
    private boolean isActive;

    @Column
    private int pricePaid;


    public Purchase(LocalDateTime purchaseDate, RegisteredUser owner, String purchaseValidation, boolean isActive, int pricePaid) {
        this.purchaseDate = purchaseDate;
        this.purchaseValidation = purchaseValidation;
        this.owner = owner;
        this.isActive= isActive;
        this.pricePaid = pricePaid;
    }


    public Purchase() {
    }

    // Getters and setters

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public RegisteredUser getOwner() {
        return owner;
    }

    public void setOwner(RegisteredUser owner) {
        this.owner = owner;
    }

    public String getPurchaseValidation() {
        return purchaseValidation;
    }

    public void setPurchaseValidation(String purchaseValidation) {
        this.purchaseValidation = purchaseValidation;
    }

    public int getId(){return this.id;}

    public void setId(int id) {
        this.id = id;
    }

    public void setisActive(boolean isActive)
    {
        this.isActive=isActive;
    }

    public boolean getIsActive ()
    {
        return this.isActive;
    }

    protected abstract String getPurchaseType();

    public int getPricePaid() {
        return pricePaid;
    }

    public void setPricePaid(int pricePaid) {
        this.pricePaid = pricePaid;
    }

    @Override
    public String toString() {
        return "Purchase{" +
                "id=" + id +
                ", purchaseDate=" + purchaseDate +
                ", owner=" + (owner != null ? owner.toString() : "null") +
                ", purchaseValidation='" + purchaseValidation + '\'' +
                '}';
    }


}