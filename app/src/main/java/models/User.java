package models;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class User {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String postalCode;
    private UserType userType; // Donor/Recipient
    private String imageKey;

    public User(int id, String name, String email, String phone, String address, String postalCode, UserType userType, String imageKey) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.postalCode = postalCode;
        this.userType = userType;
        this.imageKey = imageKey;
    }

    // Right-click -> Generate -> Constructor / Getters / Setters

    public int getId() { return id; }

    public String getName() {
        return name;
    }

    public UserType getUserType() { return userType; }

    public String getImageKey() { return imageKey; }

    public String getPhone() { return phone; }

    public String getPostalAddress() { return address; }

    public String getPostalCode() { return postalCode; }

    public void setName(String name) {
        this.name = name;
    }

}
