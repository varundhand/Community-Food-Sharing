package models;

public class User {
    private String name;
    private String email;
    private String phone;
    private String address;
    private String postalCode;
    private UserType userType; // Donor/Recipient

    public User(String name, String email, String phone, String address, String postalCode, UserType userType) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.postalCode = postalCode;
        this.userType = userType;
    }

    // Right-click -> Generate -> Constructor / Getters / Setters

    public String getName() {
        return name;
    }

    public UserType getUserType() { return userType; }

    public void setName(String name) {
        this.name = name;
    }

}
