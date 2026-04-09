package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "movies")
public class Movie implements Serializable {

    public enum StreamingType {
        HOME_VIEWING {
            @Override
            public String toString() {
                return "Home Viewing";
            }
        },
        THEATER_VIEWING {
            @Override
            public String toString() {
                return "Theater Viewing";
            }
        },
        BOTH {
            @Override
            public String toString() {
                return "Home and Theater Viewing";
            }
        }
    }

    public enum Availability {
        NOT_AVAILABLE {
            @Override
            public String toString() {
                return "Not Available";
            }
        },
        AVAILABLE {
            @Override
            public String toString() {
                return "Available";
            }
        },
        COMING_SOON {
            @Override
            public String toString() {
                return "Coming Soon";
            }
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String hebrewName;

    @Column(nullable = false)
    private String info;

    @Column(nullable = false)
    private String producer;

    @Column(nullable = false)
    private String englishName;

    @Column
    private String mainActors;

    @Column(nullable = false)
    private String image;


    @Lob
    @Column(nullable = true)
    private byte[] imageBytes;

    @Column(nullable = false)
    private StreamingType streamingType;

    @Column(nullable = false)
    private int duration;

    @Column(nullable = false)
    private int homeViewingPrice;

    @Column(nullable = false)
    private int theaterPrice;

    @Column(nullable = false)
    private String genre;

    @Column(nullable = false)
    private Availability available;

    @Column(nullable = false)
    private boolean notificationSent;

    public Movie()
    {

    }

    public Movie(String hebrewName, String info, String producer, String englishName, String mainActors, String image, StreamingType streamingType, int duration, int theaterPrice, int homeViewingPrice, String genre, Availability available, boolean notificationSent, byte[] imageBytes) {
        this.hebrewName = hebrewName;
        this.info = info;
        this.producer = producer;
        this.englishName = englishName;
        this.mainActors = mainActors;
        this.image = image;
        this.streamingType = streamingType;
        this.duration = duration;
        this.homeViewingPrice = homeViewingPrice;
        this.theaterPrice = theaterPrice;
        this.genre = genre;
        this.available = available;
        this.notificationSent = notificationSent;
        this.imageBytes = imageBytes;
    }


    public Movie(String hebrewName, String info, String producer, String englishName, String mainActors, String image, StreamingType streamingType, int duration, int theaterPrice, int homeViewingPrice, String genre, Availability available, boolean notificationSent) {
        this(hebrewName, info, producer, englishName, mainActors, image, streamingType, duration, theaterPrice, homeViewingPrice, genre, available, notificationSent, null);
    }

    // Getters and setters

    public String getHebrewName() {
        return hebrewName;
    }

    public void setHebrewName(String hebrewName) {
        this.hebrewName = hebrewName;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }


    public List<String> getMainActors()
    {
        return Arrays.stream(mainActors.split("_")).toList();
    }

    public void setMainActors(String mainActors) {
        this.mainActors = mainActors;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }


    public StreamingType getStreamingType() {
        return streamingType;
    }

    public void setStreamingType(StreamingType streamingType) {
        this.streamingType = streamingType;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHomeViewingPrice() {
        return homeViewingPrice;
    }

    public void setHomeViewingPrice(int homeViewingPrice) {
        this.homeViewingPrice = homeViewingPrice;
    }

    public int getTheaterPrice() {
        return theaterPrice;
    }

    public void setTheaterPrice(int theaterPrice) {
        this.theaterPrice = theaterPrice;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Availability getAvailability() {return available;}

    public void setNotificationSent(boolean notificationSent) {
        this.notificationSent = notificationSent;
    }

    public boolean getNotificationSent ()
    {
        return this.notificationSent;
    }

    public void setActive(Availability available) {this.available=available;}

    @Override
    public String toString() {
        return this.getEnglishName();
    }

}