package org.poo.commandutils;

import org.poo.banking.ExchangeRate;
import org.poo.banking.Transcation;
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
            Account sender = CommandHelper.findAccountByIBANWithoutEmail(users, senderIdentifier);
            Account receiver = CommandHelper.findAccountByIBANOrAlias(users, receiverIdentifier);

            if (sender == null) {
                return;
            }

            if (receiver == null) {
                return;
            }

            if ("savings".equals(sender.getAccountType())) {
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
            Transcation transcation = new Transcation();
            transcation.setTimestamp(command.getTimestamp());
            transcation.setDescription(command.getDescription());
            transcation.setSenderIBAN(sender.getIBAN());
            transcation.setReceiverIBAN(receiver.getIBAN());
            String formattedAmount = String.format("%.1f %s", command.getAmount(), sender.getCurrency());
            transcation.setAmount(formattedAmount);
            transcation.setTransferType("sent");
            senderUser.getTranscations().add(transcation);

        }
    }
}

