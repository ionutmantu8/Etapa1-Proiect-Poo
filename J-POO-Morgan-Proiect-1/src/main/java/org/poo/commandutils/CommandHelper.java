package org.poo.commandutils;

import org.poo.banking.ExchangeRate;
import org.poo.userutils.Account;
import org.poo.userutils.Card;
import org.poo.userutils.User;

import java.util.List;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public final class CommandHelper {
    private CommandHelper() {
        /**
         * for coding style purposes
         */
    }
    /**
     * Finds a user by their email address.
     *
     * @param usersList the list of users
     * @param accountEmail the email address of the user
     * @return the user with the specified email address, or null if not found
     */
    public static User findUserByEmail(final List<User> usersList,
                                       final String accountEmail) {
        for (User user : usersList) {
            if (user.getEmail().equals(accountEmail)) {
                return user;
            }
        }
        return null;
    }


    /**
     * Finds an account by its IBAN.
     *
     * @param user the user who owns the account
     * @param IBAN the IBAN of the account
     * @return the account with the specified IBAN, or null if not found
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
     * Finds a card by its number.
     *
     * @param user the user who owns the card
     * @param cardNumber the number of the card
     * @return the card with the specified number, or null if not found
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
     * Builds a graph of exchange rates.
     *
     * @param exchangeRates the list of exchange rates
     * @return a map representing the graph of exchange rates
     */
    public static Map<String, Map<String, Double>> buildGraph(
            final List<ExchangeRate> exchangeRates) {
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
     * Finds the best exchange rate between two currencies.
     *
     * @param from the source currency
     * @param to the target currency
     * @param graph the graph of exchange rates
     * @return the best exchange rate, or 0.0 if no rate is found
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
     * Converts an amount from one currency to another.
     *
     * @param amount the amount to convert
     * @param from the source currency
     * @param to the target currency
     * @param exchangeRates the list of exchange rates
     * @return the converted amount
     */
    public static double convertCurrency(final double amount, final String from, final String to,
                                         final List<ExchangeRate> exchangeRates) {
        Map<String, Map<String, Double>> graph = buildGraph(exchangeRates);
        double rate = findBestRate(from, to, graph);
        return amount * rate;
    }


    /**
     * Finds an account by its IBAN without requiring the user's email.
     *
     * @param users the list of users
     * @param IBAN the IBAN of the account
     * @return the account with the specified IBAN, or null if not found
     */
    public static Account findAccountByIBANWithoutEmail(final List<User> users,
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
     * Finds an account by its IBAN or alias.
     *
     * @param users the list of users
     * @param identifier the IBAN or alias of the account
     * @return the account with the specified IBAN or alias, or null if not found
     */
    public static Account findAccountByIBANOrAlias(final List<User> users,
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
     * Finds a card by its number without requiring the user's email.
     *
     * @param users the list of users
     * @param cardNumber the number of the card
     * @return the card with the specified number, or null if not found
     */
    public static Card findCardByNumberWithoutEmail(final List<User> users,
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
     * Finds an account by a card number without requiring the user's email.
     *
     * @param users the list of users
     * @param cardNumber the number of the card
     * @return the account associated with the specified card number, or null if not found
     */
    public static Account findAccountByCardNumberWithoutEmail(final List<User> users,
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
     * Finds a user by a card number without requiring the user's email.
     *
     * @param users the list of users
     * @param cardNumber the number of the card
     * @return the user associated with the specified card number, or null if not found
     */
    public static User findUserByCardNumberWithoutEmail(final List<User> users,
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

    /**
     * Finds a user by an account's IBAN.
     *
     * @param users the list of users
     * @param IBAN the IBAN of the account
     * @return the user associated with the specified IBAN, or null if not found
     */
    public static User findUserByIBAN(final List<User> users,
                                                        final String IBAN) {
        for (User user : users) {
            for (Account account : user.getAccounts()) {
                if (account.getIBAN().equals(IBAN)) {
                        return user;
                    }
            }
        }
        return null;
    }
    /**
     * Finds a user by an account's IBAN or alias.
     *
     * @param users the list of users
     * @param identifier the IBAN or alias of the account
     * @return the user associated with the specified IBAN or alias, or null if not found
     */
    public static User findUserByIBANOrAlias(final List<User> users,
                                      final String identifier) {
        for (User user : users) {
            for (Account account : user.getAccounts()) {
                if (account.getIBAN().equals(identifier)
                        || (account.getAlias() != null
                        && account.getAlias().equals(identifier))) {
                    return user;
                }
            }
        }
        return null;
    }
}
