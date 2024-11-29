package org.poo.commandutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banking.ExchangeRate;
import org.poo.fileio.CommandInput;
import org.poo.userutils.Account;
import org.poo.userutils.Card;
import org.poo.userutils.User;

import java.util.ArrayList;

public class PayOnline {
    public static double convertCurrency(double amount, String from, String to, ArrayList<ExchangeRate> exchangeRates) {
        if (from.equals(to)) {
            return amount; // Dacă monedele sunt identice, suma rămâne neschimbată
        }

        for (ExchangeRate rate : exchangeRates) {
            if (rate.getFromCurrency().equals(from) && rate.getToCurrency().equals(to)) {
                return amount * rate.getRate();
            }
        }

        for (ExchangeRate rate1 : exchangeRates) {
            if (rate1.getFromCurrency().equals(from)) {
                String intermediateCurrency = rate1.getToCurrency();
                for (ExchangeRate rate2 : exchangeRates) {
                    if (rate2.getFromCurrency().equals(intermediateCurrency) && rate2.getToCurrency().equals(to)) {
                        return amount * rate1.getRate() * rate2.getRate();
                    }
                }
            }
        }


        return 0;
    }


    public static Card findCardByNumber(User user, String cardNumber){
        for(Account account : user.getAccounts()){
            for(Card card : account.getCards()){
                if(card.getCardNumber().equals(cardNumber))
                    return card;
            }
        }
        return null;
    }
    public static void payOnline(final ArrayList<User> users, final CommandInput command,
                                 final ArrayList<ExchangeRate> exchangeRates,
                                 final ObjectNode node, final ArrayNode output,
                                 final ObjectMapper mapper) {
        String cardNumber = command.getCardNumber();
        double amount = command.getAmount();
        String currency = command.getCurrency();
        String email = command.getEmail();
        User user = AddAccount.findUserByEmail(users, email);
        if(user != null) {
            Card card = findCardByNumber(user, cardNumber);
            if(card == null){
                node.put("command",command.getCommand());
                ObjectNode messageNode = mapper.createObjectNode();
                messageNode.put("timestamp",command.getTimestamp());
                messageNode.put("description","Card not found");
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
                        double amountInAccountCurrency = convertCurrency(amount, currency, account.getCurrency(), exchangeRates);
                        if (account.getBalance() >= amountInAccountCurrency) {
                            account.setBalance(account.getBalance() - amountInAccountCurrency);
                            return;
                        }
                    }
                }
            }
        }
    }
}
