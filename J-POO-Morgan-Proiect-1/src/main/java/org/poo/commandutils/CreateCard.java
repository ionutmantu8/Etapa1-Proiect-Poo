package org.poo.commandutils;

import org.poo.banking.Transcation;
import org.poo.fileio.CommandInput;
import org.poo.userutils.Account;
import org.poo.userutils.Card;
import org.poo.userutils.User;
import org.poo.utils.Utils;

import java.util.ArrayList;

public class CreateCard {


    public static void createCard(ArrayList<User> userList, CommandInput command) {
        String IBAN = command.getAccount();
        String email = command.getEmail();
        User user = CommandHelper.findUserByEmail(userList, email);
        if (user != null) {
            Account account = CommandHelper.findAccountByIban(user, IBAN);
            if (account != null) {
                Card newCard = new Card();
                newCard.setOneTime(false);
                String cardNumber = Utils.generateCardNumber();
                newCard.setCardNumber(cardNumber);
                newCard.setTimeStamp(command.getTimestamp());
                newCard.setActive(true);
                account.getCards().add(newCard);
                Transcation transcation = new Transcation();
                transcation.setTimestamp(command.getTimestamp());
                transcation.setDescription("New card created");
                transcation.setCard(cardNumber);
                transcation.setCardHolder(user.getEmail());
                transcation.setAccount(IBAN);
                user.getTranscations().add(transcation);
            }
        }


    }
}
