package org.poo.userutils;

import lombok.Getter;
import lombok.Setter;
import org.poo.banking.Commerciants;
import org.poo.banking.Transcation;

import java.util.ArrayList;

@Setter
@Getter
public class User {
    private String firstName;
    private String lastName;
    private String email;
    private ArrayList<Account> accounts;
    private ArrayList<Transcation> transcations;
    private ArrayList <Commerciants> commerciants;
    public User() {
        accounts = new ArrayList<>();
        transcations = new ArrayList<>();
        commerciants = new ArrayList<>();
    }


}
