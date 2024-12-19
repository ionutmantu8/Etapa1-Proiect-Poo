package org.poo.userutils;

import lombok.Getter;
import lombok.Setter;
import org.poo.banking.Commerciants;
import org.poo.banking.Transcation;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class User {
    private String firstName;
    private String lastName;
    private String email;
    private List<Account> accounts;
    private List<Transcation> transcations;
    private List<Commerciants> commerciants;
    public User() {
        accounts = new ArrayList<>();
        transcations = new ArrayList<>();
        commerciants = new ArrayList<>();
    }


}
