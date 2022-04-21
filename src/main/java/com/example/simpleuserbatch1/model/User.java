package com.example.simpleuserbatch1.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private long id;
    private String username;
    private String firstName;
    private String lastName;
    private long importJobId = 0;

    public User(String username) {
        this.username = username;
    }
}
