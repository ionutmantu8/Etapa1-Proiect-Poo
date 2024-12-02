package org.poo.commandutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banking.ExchangeRate;
import org.poo.banking.Transcation;
import org.poo.fileio.CommandInput;
import org.poo.userutils.Account;
import org.poo.userutils.Card;
import org.poo.userutils.User;

import java.util.*;

public class PayOnline {

    public static void payOnline(final ArrayList<User> users, final CommandInput command,
                                 final ArrayList<ExchangeRate> exchangeRates,
                                 final ObjectNode node, final ArrayNode output,
                                 final ObjectMapper mapper) {
        String cardNumber = command.getCardNumber();
        double amount = command.getAmount();
        String currency = command.getCurrency();
        String email = command.getEmail();
        User user = CommandHelper.findUserByEmail(users, email);

        if (user != null) {
            Card card = CommandHelper.findCardByNumber(user, cardNumber);
            if (card == null) {
                node.put("command", command.getCommand());
                ObjectNode messageNode = mapper.createObjectNode();
                messageNode.put("timestamp", command.getTimestamp());
                messageNode.put("description", "Card not found");
                node.set("output", messageNode);
                node.put("timestamp", command.getTimestamp());
                output.add(node);
                return;
            }
        }

        if (user != null) {
            for (Account account : user.getAccounts()) {
                for (Card card : account.getCards()) {
                    if (card.getCardNumber().equals(cardNumber) && card.isActive()) {
                        double amountInAccountCurrency = CommandHelper.convertCurrency(amount, currency, account.getCurrency(), exchangeRates);
                        if (account.getBalance() >= amountInAccountCurrency) {
                            account.setBalance(account.getBalance() - amountInAccountCurrency);
                            Transcation transcation = new Transcation();
                            transcation.setTimestamp(command.getTimestamp());
                            transcation.setDescription("Card payment");
                            transcation.setAmountNotStr(amountInAccountCurrency);
                            transcation.setCommeriant(command.getCommerciant());
                            user.getTranscations().add(transcation);
                            return;
                        } else {
                            Transcation transcation = new Transcation();
                            transcation.setTimestamp(command.getTimestamp());
                            transcation.setDescription("Insufficient funds");
                            user.getTranscations().add(transcation);

                        }
                    }
                }
            }
        }
    }


}
