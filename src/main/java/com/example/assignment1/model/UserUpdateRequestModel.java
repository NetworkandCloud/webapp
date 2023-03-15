package com.example.assignment1.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserUpdateRequestModel {

    @JsonProperty("first_name")
    @NotEmpty(message = "First Name cannot be null/empty")
    private String firstName;

    @JsonProperty("last_name")
    @NotEmpty(message = "Last Name cannot be null/empty")
    private String lastName;

    @JsonProperty("password")
    @NotEmpty(message = "Password cannot be null/empty")
    private String password;

    @JsonProperty("username")
    @NotEmpty(message = "Username cannot be null/empty")
    private String username;


}
