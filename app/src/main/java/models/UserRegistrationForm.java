package models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserRegistrationForm {
    // Class for containing values and doing validations as necessary
    private final UserType userType;
    private final String name, email, password, phone, postalCode, postalAddress, imageKey;

    public UserRegistrationForm(String name, UserType usertype,
                                String email, String password,
                                String phone, String postalCode,
                                String postalAddress, String imageKey) {
        this.name = name;
        this.userType = usertype;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.postalCode = postalCode;
        this.postalAddress = postalAddress;
        this.imageKey = imageKey;
    }

    public boolean isValid() {
        // TODO: implement/use more proper validator
        Pattern emailRegex =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

        return !name.isEmpty() &&
                userType != null &&
                emailRegex.matcher(email).matches() &&
                !password.isEmpty() &&
                !phone.isEmpty() &&
                !postalCode.isEmpty() &&
                !postalAddress.isEmpty();
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
