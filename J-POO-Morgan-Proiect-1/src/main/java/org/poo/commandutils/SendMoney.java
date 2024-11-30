package org.poo.commandutils;

import org.poo.banking.ExchangeRate;
import org.poo.fileio.CommandInput;
import org.poo.userutils.Account;
import org.poo.userutils.User;

import java.util.ArrayList;

public class SendMoney {
    public static void sendMoney(final ArrayList<User> users, final CommandInput command,
                                 final ArrayList<ExchangeRate> exchangeRates) {
        String sendingIBAN = command.getAccount();
        String receiverIBAN = command.getReceiver();
        double amount = command.getAmount();
        String email = command.getEmail();

        User user = CommandHelper.findUserByEmail(users, email);

        if (user != null) {
            Account sending = CommandHelper.findAccountByIban(user, sendingIBAN);
            Account receiver = CommandHelper.findAccountByIBANWithoutEmail(users, receiverIBAN);

            if (sending != null && receiver != null) {
                if (sending.getBalance() < amount) {
                    return;
                }

                if (sending.getCurrency().equals(receiver.getCurrency())) {
                    sending.setBalance(sending.getBalance() - amount);
                    receiver.setBalance(receiver.getBalance() + amount);
                } else {
                    double convertedAmount = CommandHelper.convertCurrency(amount, sending.getCurrency(), receiver.getCurrency(), exchangeRates);

                    if (sending.getBalance() < amount) {
                        return;
                    }
                    sending.setBalance(sending.getBalance() - amount);
                    receiver.setBalance(receiver.getBalance() + convertedAmount);
                }
            }
        }
    }
}
