package com.ecomproj.store.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CheckoutForm {
    @NotBlank(message = "Enter your name.")
    private String fullName;

    @NotBlank(message = "Enter your email address.")
    @Email(message = "Enter a valid email address.")
    private String email;

    @NotBlank(message = "Enter a delivery address.")
    private String address;

    @NotBlank(message = "Enter your city.")
    private String city;

    @NotBlank(message = "Enter a postal code.")
    private String postalCode;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}