package models;

public class User {
    private String name;
    private String email;
    private String phone;
    private String address;
    private UserType userType; // Donor/Recipient

    // Right-click -> Generate -> Constructor / Getters / Setters

    public String getName() {
        return name;
    }

    public UserType getUserType() { return userType; }

    public void setName(String name) {
        this.name = name;
    }

}
