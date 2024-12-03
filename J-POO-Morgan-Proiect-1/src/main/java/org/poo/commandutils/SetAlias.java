package org.poo.commandutils;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.poo.fileio.CommandInput;
import org.poo.userutils.Account;
import org.poo.userutils.User;

import java.util.ArrayList;

public class SetAlias {
    public static void setAlias(ArrayList<User> users, CommandInput command) {
        String email = command.getEmail();
        String IBAN = command.getAccount();
        String alias = command.getAlias();
        User user = CommandHelper.findUserByEmail(users, email);
        if (user != null) {
            Account account = CommandHelper.findAccountByIban(user, IBAN);
            if (account != null)
                account.setAlias(alias);
        }
    }
}
