package org.poo.commandutils;

import org.poo.banking.ExchangeRate;
import org.poo.fileio.CommandInput;
import org.poo.userutils.Account;
import org.poo.userutils.User;

import java.util.ArrayList;

public class SendMoney {
    public static void sendMoney(final ArrayList<User> users, final CommandInput command,
                                 final ArrayList<ExchangeRate> exchangeRates) {
        String senderIdentifier = command.getAccount();
        String receiverIdentifier = command.getReceiver();
        double amount = command.getAmount();
        String email = command.getEmail();

        User senderUser = CommandHelper.findUserByEmail(users, email);

        if (senderUser != null) {
            Account sender = CommandHelper.findAccountByIBANOrAlias(users, senderIdentifier);
            Account receiver = CommandHelper.findAccountByIBANOrAlias(users, receiverIdentifier);

            if (sender == null) {
                System.out.println("Sender account not found!");
                return;
            }

            if (receiver == null) {
                System.out.println("Receiver account not found!");
                return;
            }

            if ("savings".equals(sender.getAccountType())) {
                System.out.println("Cannot send money directly to a savings account.");
                return;
            }

            if (sender.getBalance() < amount) {
                return;
            }

            if (sender.getCurrency().equals(receiver.getCurrency())) {
                sender.setBalance(sender.getBalance() - amount);
                receiver.setBalance(receiver.getBalance() + amount);
            } else {
                double convertedAmount = CommandHelper.convertCurrency(amount, sender.getCurrency(), receiver.getCurrency(), exchangeRates);
                sender.setBalance(sender.getBalance() - amount);
                receiver.setBalance(receiver.getBalance() + convertedAmount);
            }
        }
    }
}

