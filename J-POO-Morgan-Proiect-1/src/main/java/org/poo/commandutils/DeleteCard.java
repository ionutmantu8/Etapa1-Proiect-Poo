package org.poo.commandutils;

import org.poo.fileio.CommandInput;
import org.poo.userutils.Account;
import org.poo.userutils.Card;
import org.poo.userutils.User;

import java.util.ArrayList;

public class DeleteCard {
    public static void deleteCard(ArrayList<User> users, CommandInput command) {
        String email = command.getEmail();
        String cardNumber = command.getCardNumber();
        User user = CommandHelper.findUserByEmail(users, email);
        if (user != null) {
            for (Account account : user.getAccounts()) {
                Card cardToRemove = null;
                for (Card card : account.getCards()) {
                    if (card.getCardNumber().equals(cardNumber)) {
                        cardToRemove = card;
                        break;
                    }
                }
                if (cardToRemove != null) {
                    account.getCards().remove(cardToRemove);
                    break;
                }
            }
        }
    }

}
