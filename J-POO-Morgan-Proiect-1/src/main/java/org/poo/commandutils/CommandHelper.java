package org.poo.commandutils;

import org.poo.banking.ExchangeRate;
import org.poo.userutils.Account;
import org.poo.userutils.Card;
import org.poo.userutils.User;

import java.util.*;

public final class CommandHelper {
    private CommandHelper() {
        /**
         * for coding style purposes
         */
    }
    /**
     *
     *
     */
    public static User findUserByEmail(final ArrayList<User> usersList,
                                       final String accountEmail) {
        for (User user : usersList) {
            if (user.getEmail().equals(accountEmail)) {
                return user;
            }
        }
        return null;
    }

    /**
     *
     */
    public static Account findAccountByIban(final User user, final String IBAN) {
        for (Account account : user.getAccounts()) {
            if (account.getIBAN().equals(IBAN)) {
                return account;
            }
        }
        return null;
    }
    /**
     *
     */
    public static Card findCardByNumber(final User user, final String cardNumber) {
        for (Account account : user.getAccounts()) {
            for (Card card : account.getCards()) {
                if (card.getCardNumber().equals(cardNumber)) {
                    return card;
                }
            }
        }
        return null;
    }

    public static class Pair<T, U> {
        private final  T currency;
        private final  U rate;

        Pair(final T currency, final U rate) {
            this.currency = currency;
            this.rate = rate;
        }
    }
    /**
     *
     */
    public static Map<String, Map<String, Double>> buildGraph(
            final ArrayList<ExchangeRate> exchangeRates) {
        Map<String, Map<String, Double>> graph = new HashMap<>();

        for (ExchangeRate rate : exchangeRates) {
            graph.putIfAbsent(rate.getFromCurrency(), new HashMap<>());
            graph.putIfAbsent(rate.getToCurrency(), new HashMap<>());

            graph.get(rate.getFromCurrency()).put(rate.getToCurrency(), rate.getRate());

            graph.get(rate.getToCurrency()).put(rate.getFromCurrency(), 1.0 / rate.getRate());
        }

        return graph;
    }
    /**
     *
     */
    public static double findBestRate(final String from, final String to,
                                      final Map<String, Map<String, Double>> graph) {
        if (from.equals(to)) {
            return 1.0;
        }
        PriorityQueue<Pair<String, Double>> pq =
                new PriorityQueue<>(Comparator.comparingDouble(a -> -a.rate));
        Set<String> visited = new HashSet<>();
        pq.add(new Pair<>(from, 1.0));

        while (!pq.isEmpty()) {
            Pair<String, Double> current = pq.poll();
            String currency = current.currency;
            double rate = current.rate;

            if (currency.equals(to)) {
                return rate;
            }

            if (visited.contains(currency)) {
                continue;
            }
            visited.add(currency);

            if (graph.containsKey(currency)) {
                for (Map.Entry<String, Double> neighbor : graph.get(currency).entrySet()) {
                    if (!visited.contains(neighbor.getKey())) {
                        pq.add(new Pair<>(neighbor.getKey(), rate * neighbor.getValue()));
                    }
                }
            }
        }

        return 0.0;
    }
    /**
     *
     */
    public static double convertCurrency(final double amount, final String from, final String to,
                                         final ArrayList<ExchangeRate> exchangeRates) {
        Map<String, Map<String, Double>> graph = buildGraph(exchangeRates);
        double rate = findBestRate(from, to, graph);
        return amount * rate;
    }
    /**
     *
     */
    public static Account findAccountByIBANWithoutEmail(final ArrayList<User> users,
                                                        final String IBAN) {
        for (User user : users) {
            for (Account account : user.getAccounts()) {
                if (account.getIBAN().equals(IBAN)) {
                    return account;
                }
            }
        }
        return null;

    }
    /**
     *
     */
    public static Account findAccountByIBANOrAlias(final ArrayList<User> users,
                                                   final String identifier) {
       for (User user : users) {
           for (Account account : user.getAccounts()) {
               if (account.getIBAN().equals(identifier)
                       || (account.getAlias() != null
                       && account.getAlias().equals(identifier))) {
                   return account;
               }
           }
       }
        return null;

    }
    /**
     *
     */
    public static Card findCardByNumberWithoutEmail(final ArrayList<User> users,
                                                     final String cardNumber) {
        for (User user : users) {
            for (Account account : user.getAccounts()) {
                for (Card card : account.getCards()) {
                    if (card.getCardNumber().equals(cardNumber)) {
                        return card;
                    }
                }
            }
        }
        return null;
    }
    /**
     *
     */
    public static Account findAccountByCardNumberWithoutEmail(final ArrayList<User> users,
                                                               final String cardNumber) {
        for (User user : users) {
            for (Account account : user.getAccounts()) {
                for (Card card : account.getCards()) {
                    if (card.getCardNumber().equals(cardNumber)) {
                        return account;
                    }
                }
            }
        }
        return null;
    }
    /**
     *
     */
    public static User findUserByCardNumberWithoutEmail(final ArrayList<User> users,
                                                         final String cardNumber) {
        for (User user : users) {
            for (Account account : user.getAccounts()) {
                for (Card card : account.getCards()) {
                    if (card.getCardNumber().equals(cardNumber)) {
                        return user;
                    }
                }
            }
        }
        return null;
    }
}
