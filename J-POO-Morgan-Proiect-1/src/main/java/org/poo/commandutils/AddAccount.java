package org.poo.commandutils;

import org.poo.fileio.CommandInput;
import org.poo.userutils.Account;
import org.poo.userutils.User;
import org.poo.utils.Utils;

import java.util.ArrayList;

public class AddAccount {
    public static User findUserByEmail(ArrayList<User> usersList,String accountEmail){
        for(User user : usersList){
            if(user.getEmail().equals(accountEmail))
                return user;
        }
        return null;
    }
    public static void addAccount(final ArrayList<User> usersList, final CommandInput command){
        String accountEmail = command.getEmail();
        User user = findUserByEmail(usersList, accountEmail);
        if (user != null) {
            Account newAccount = new Account();
            newAccount.setTimpeStamp(command.getTimestamp());
            newAccount.setAccountType(command.getAccountType());
            newAccount.setCurrency(command.getCurrency());
            newAccount.setBalance(0);
            newAccount.setIBAN(Utils.generateIBAN());
            user.getAccounts().add(newAccount);
        }



    }
}
