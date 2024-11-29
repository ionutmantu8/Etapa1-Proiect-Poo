package org.poo.commandutils;

import org.poo.fileio.CommandInput;
import org.poo.userutils.Account;
import org.poo.userutils.Card;
import org.poo.userutils.User;
import org.poo.utils.Utils;

import java.util.ArrayList;

public class CreateCard {


    public static Account findAccountByIban(User user, String IBAN){
      for(Account account : user.getAccounts()){
          if(account.getIBAN().equals(IBAN))
              return account;
      }
        return null;
    }
    public static void createCard(ArrayList<User> userList, CommandInput command) {
        String IBAN = command.getAccount();
        String email = command.getEmail();
        User user = AddAccount.findUserByEmail(userList, email);
        if(user != null) {
            Account account = findAccountByIban(user, IBAN);
            if(account != null){
                Card newCard = new Card();
                newCard.setCardNumber(Utils.generateCardNumber());
                newCard.setTimeStamp(command.getTimestamp());
                newCard.setActive(true);
                account.getCards().add(newCard);
            }
        }




    }
}
