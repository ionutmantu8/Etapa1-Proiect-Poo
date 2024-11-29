package org.poo.userutils;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Setter
@Getter
public class User {
    private String firstName;
    private String lastName;
    private String email;
    private ArrayList<Account> accounts;
    public User() {
        accounts = new ArrayList<>();
    }


}
