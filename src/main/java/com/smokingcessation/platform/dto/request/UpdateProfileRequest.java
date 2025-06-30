package com.smokingcessation.platform.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdateProfileRequest {

    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "First name can only contain letters and spaces")
    private String firstName;

    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "Last name can only contain letters and spaces")
    private String lastName;

    @Size(max = 15, message = "Phone number must not exceed 15 characters")
    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]*$", message = "Phone number format is invalid")
    private String phoneNumber;

    @Size(max = 500, message = "Bio must not exceed 500 characters")
    private String bio;

    // Constructors
    public UpdateProfileRequest() {}

    public UpdateProfileRequest(String firstName, String lastName, String phoneNumber, String bio) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.bio = bio;
    }

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    @Override
    public String toString() {
        return "UpdateProfileRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", bio='" + bio + '\'' +
                '}';
    }
}