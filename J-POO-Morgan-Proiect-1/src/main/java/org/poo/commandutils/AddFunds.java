package org.poo.commandutils;

import org.poo.fileio.CommandInput;
import org.poo.userutils.Account;
import org.poo.userutils.User;

import java.util.ArrayList;

public class AddFunds {
    public static void addFunds(ArrayList<User> users, CommandInput command){
        for(User user : users){
            for(Account account : user.getAccounts()){
                if(account.getIBAN().equals(command.getAccount())){
                    double ammount = command.getAmount();
                    account.setBalance(account.getBalance() + ammount);
                    break;
                }
            }
        }

    }

}
