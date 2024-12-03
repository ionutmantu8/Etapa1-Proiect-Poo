package org.poo.commandutils;

import org.poo.fileio.CommandInput;
import org.poo.userutils.Account;
import org.poo.userutils.Card;
import org.poo.userutils.User;
import org.poo.utils.Utils;

import java.util.ArrayList;

public class CreateOneTimeCard {


    public static void createOneTimeCard(ArrayList<User> users, CommandInput command) {
        String IBAN = command.getAccount();
        String email = command.getEmail();
        User user = CommandHelper.findUserByEmail(users, email);
        if (user != null) {
            Account account = CommandHelper.findAccountByIban(user, IBAN);
            if (account != null) {
                Card newCard = new Card();
                newCard.setOneTime(true);
                newCard.setCardNumber(Utils.generateCardNumber());
                newCard.setTimeStamp(command.getTimestamp());
                newCard.setActive(true);
                account.getCards().add(newCard);
            }
        }
    }
}
