package models;

public class UserRegistrationForm {
    // Class for containing values and doing validations as necessary
    private final UserType userType;
    private final String name, email, password, phone, postalCode, postalAddress;

    public UserRegistrationForm(String name, UserType usertype, String email, String password, String phone, String postalCode, String postalAddress) {
        this.name = name;
        this.userType = usertype;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.postalCode = postalCode;
        this.postalAddress = postalAddress;
    }

    public String getName() {
        return name;
    }

    public UserType getUserType() {
        return userType;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getPostalAddress() {
        return postalAddress;
    }
}
