package org.poo.userutils;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class Account {
   private String IBAN;
   private String currency;
   private String accountType;
   private double balance;
   private ArrayList<Card> cards;
   private int timpeStamp;
   private String alias;
   private double minBalance;
   private double interestRate;
   public Account() {
         cards = new ArrayList<>();
   }
}
