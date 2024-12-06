package org.poo.commandutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banking.Commerciants;
import org.poo.banking.ExchangeRate;
import org.poo.banking.Transcation;
import org.poo.fileio.CommandInput;
import org.poo.userutils.Account;
import org.poo.userutils.Card;
import org.poo.userutils.User;
import org.poo.utils.Utils;

import java.util.*;

public class BankingCommandVisitor implements CommandVisitor {
    /**
     *
     */
    @Override
    public void visit(final AddAccount command) {
        CommandInput commandInput = command.getCommandInput();
        ArrayList<User> usersList = command.getUsers();
        String accountEmail = commandInput.getEmail();
        User user = CommandHelper.findUserByEmail(usersList, accountEmail);
        if (user != null) {
            Account newAccount = new Account();
            newAccount.setTimpeStamp(commandInput.getTimestamp());
            newAccount.setAccountType(commandInput.getAccountType());
            newAccount.setCurrency(commandInput.getCurrency());
            newAccount.setBalance(0);
            newAccount.setIBAN(Utils.generateIBAN());
            newAccount.setInterestRate(commandInput.getInterestRate());
            user.getAccounts().add(newAccount);
            Transcation transcation = new Transcation();
            transcation.setTimestamp(commandInput.getTimestamp());
            transcation.setDescription("New account created");
            user.getTranscations().add(transcation);
        }
    }
    /**
     *
     */
    @Override
    public void visit(final AddFunds command) {
        ArrayList<User> users = command.getUsers();
        CommandInput commandInput = command.getCommandInput();

        for (User user : users) {
            for (Account account : user.getAccounts()) {
                if (account.getIBAN().equals(commandInput.getAccount())) {
                    double ammount = commandInput.getAmount();
                    account.setBalance(account.getBalance() + ammount);
                    break;
                }
            }
        }

    }
    /**
     *
     */
    @Override
    public void visit(final CreateCard command) {
        CommandInput commandInput = command.getCommandInput();
        ArrayList<User> userList = command.getUsers();
        String IBAN = commandInput.getAccount();
        String email = commandInput.getEmail();
        User user = CommandHelper.findUserByEmail(userList, email);
        if (user != null) {
            Account account = CommandHelper.findAccountByIban(user, IBAN);
            if (account != null) {
                Card newCard = new Card();
                newCard.setOneTime(false);
                String cardNumber = Utils.generateCardNumber();
                newCard.setCardNumber(cardNumber);
                newCard.setTimeStamp(commandInput.getTimestamp());
                newCard.setActive(true);
                account.getCards().add(newCard);
                Transcation transcation = new Transcation();
                transcation.setTimestamp(commandInput.getTimestamp());
                transcation.setDescription("New card created");
                transcation.setCard(cardNumber);
                transcation.setCardHolder(user.getEmail());
                transcation.setAccount(IBAN);
                user.getTranscations().add(transcation);
            }
        }
    }
    /**
     *
     */
    @Override
    public void visit(final PrintUsers command) {
        ObjectNode node = command.getNode();
        CommandInput commandInput = command.getCommand();
        ArrayList<User> users = command.getUsers();
        ArrayNode output = command.getOutput();
        ObjectMapper mapper = command.getMapper();

        node.put("command", commandInput.getCommand());
        ArrayNode usersArray = mapper.createArrayNode();

        for (User user : users) {
            ObjectNode userNode = mapper.createObjectNode();
            userNode.put("firstName", user.getFirstName());
            userNode.put("lastName", user.getLastName());
            userNode.put("email", user.getEmail());

            ArrayNode accountsArray = mapper.createArrayNode();
            for (Account account : user.getAccounts()) {
                ObjectNode accountNode = mapper.createObjectNode();
                accountNode.put("IBAN", account.getIBAN());
                accountNode.put("balance", account.getBalance());
                accountNode.put("currency", account.getCurrency());
                accountNode.put("type", account.getAccountType());

                ArrayNode cardsArray = mapper.createArrayNode();
                for (Card card : account.getCards()) {
                    ObjectNode cardNode = mapper.createObjectNode();
                    cardNode.put("cardNumber", card.getCardNumber());
                    cardNode.put("status", card.isActive() ? "active" : "frozen");
                    cardsArray.add(cardNode);
                }
                accountNode.set("cards", cardsArray);
                accountsArray.add(accountNode);
            }
            userNode.set("accounts", accountsArray);
            usersArray.add(userNode);
        }

        node.set("output", usersArray);
        node.put("timestamp", commandInput.getTimestamp());

        output.add(node);
    }
    /**
     *
     */
    @Override
    public void visit(final DeleteAccount command) {
        CommandInput commandInput = command.getCommand();
        ArrayList<User> users = command.getUsers();
        ObjectNode node = command.getNode();
        ArrayNode output = command.getOutput();

        String email = commandInput.getEmail();
        String accountIBAN = commandInput.getAccount();
        User user = CommandHelper.findUserByEmail(users, email);
        Account accountToRemove = null;
        if (user == null) {
            node.put("command", commandInput.getCommand());
            ObjectNode outputNode = node.putObject("output");
            outputNode
                    .put("error",
                            "Account couldn't be deleted - see org.poo.transactions for details");
            outputNode.put("timestamp", commandInput.getTimestamp());
            node.put("timestamp", commandInput.getTimestamp());
            output.add(node);
            return;
        }

        for (Account account : user.getAccounts()) {
            if (account.getIBAN().equals(accountIBAN)) {
                accountToRemove = account;
                break;
            }

        }
        if (accountToRemove != null && accountToRemove.getBalance() != 0) {
            node.put("command", commandInput.getCommand());
            ObjectNode outputNode = node.putObject("output");
            outputNode.put("error",
                    "Account couldn't be deleted - see org.poo.transactions for details");
            outputNode.put("timestamp", commandInput.getTimestamp());
            node.put("timestamp", commandInput.getTimestamp());
            output.add(node);
            Transcation transcation = new Transcation();
            transcation.setTimestamp(commandInput.getTimestamp());
            transcation.setDescription("Account couldn't be deleted - there are funds remaining");
            user.getTranscations().add(transcation);
            return;
        }

        user.getAccounts().remove(accountToRemove);


        node.put("command", commandInput.getCommand());
        ObjectNode outputNode = node.putObject("output");
        if (accountToRemove != null) {
            outputNode.put("success", "Account deleted");
        } else {
            outputNode.put("error",
                    "Account couldn't be deleted - see org.poo.transactions for details");
        }
        outputNode.put("timestamp", commandInput.getTimestamp());
        node.put("timestamp", commandInput.getTimestamp());

        output.add(node);
    }
    /**
     *
     */
    @Override
    public void visit(final CreateOneTimeCard command) {
        ArrayList<User> users = command.getUsers();
        CommandInput commandInput = command.getCommandInput();
        String IBAN = commandInput.getAccount();
        String email = commandInput.getEmail();
        User user = CommandHelper.findUserByEmail(users, email);
        if (user != null) {
            Account account = CommandHelper.findAccountByIban(user, IBAN);
            if (account != null) {
                Card newCard = new Card();
                newCard.setOneTime(true);
                String cardNumber = Utils.generateCardNumber();
                newCard.setCardNumber(cardNumber);
                newCard.setTimeStamp(commandInput.getTimestamp());
                newCard.setActive(true);
                account.getCards().add(newCard);
                Transcation transcation = new Transcation();
                transcation.setTimestamp(commandInput.getTimestamp());
                transcation.setDescription("New card created");
                transcation.setCard(cardNumber);
                transcation.setCardHolder(user.getEmail());
                transcation.setAccount(account.getIBAN());
                user.getTranscations().add(transcation);
            }
        }

    }
    /**
     *
     */
    @Override
    public void visit(final DeleteCard command) {
        CommandInput commandInput = command.getCommandInput();
        ArrayList<User> users = command.getUsers();
        String email = commandInput.getEmail();
        String cardNumber = commandInput.getCardNumber();
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
                    Transcation transcation = new Transcation();
                    transcation.setTimestamp(commandInput.getTimestamp());
                    transcation.setDescription("The card has been destroyed");
                    transcation.setCard(cardNumber);
                    transcation.setCardHolder(user.getEmail());
                    transcation.setAccount(account.getIBAN());
                    user.getTranscations().add(transcation);
                    break;
                }


            }
        }
    }

    /**
     *
     */
    public void visit(final PayOnline command) {
        ArrayList<User> users = command.getUsers();
        ArrayList<ExchangeRate> exchangeRates = command.getExchangeRates();
        ObjectNode node = command.getNode();
        ObjectMapper mapper = command.getMapper();
        ArrayNode output = command.getOutput();
        CommandInput commandInput = command.getCommand();
        String cardNumber = commandInput.getCardNumber();
        double amount = commandInput.getAmount();
        String currency = commandInput.getCurrency();
        String email = commandInput.getEmail();

        User user = CommandHelper.findUserByEmail(users, email);
        if (user == null) {
            return;
        }

        Card card = CommandHelper.findCardByNumber(user, cardNumber);
        if (card == null) {
            node.put("command", commandInput.getCommand());
            ObjectNode messageNode = mapper.createObjectNode();
            messageNode.put("timestamp", commandInput.getTimestamp());
            messageNode.put("description", "Card not found");
            node.set("output", messageNode);
            node.put("timestamp", commandInput.getTimestamp());
            output.add(node);
            return;
        }

        for (Account account : user.getAccounts()) {
            for (Card card2 : account.getCards()) {
                if (card2.getCardNumber().equals(cardNumber)) {

                    if (!card2.isActive()) {
                        Transcation transcation = new Transcation();
                        transcation.setTimestamp(commandInput.getTimestamp());
                        transcation.setDescription("The card is frozen");
                        user.getTranscations().add(transcation);
                        return;
                    }

                    if (account.getBalance() < account.getMinBalance()) {
                        card2.setActive(false);
                        Transcation transcation = new Transcation();
                        transcation.setTimestamp(commandInput.getTimestamp());
                        transcation.setDescription("The card is frozen");
                        user.getTranscations().add(transcation);
                        return;
                    }
                    double amountInAccountCurrency = CommandHelper
                                                    .convertCurrency(amount, currency,
                                                            account.getCurrency(), exchangeRates);
                    if (account.getBalance() >= amountInAccountCurrency
                            && account.getBalance() - amountInAccountCurrency
                            >= account.getMinBalance()) {
                        account.setBalance(account.getBalance() - amountInAccountCurrency);
                        Transcation transcation = new Transcation();
                        transcation.setTimestamp(commandInput.getTimestamp());
                        transcation.setDescription("Card payment");
                        transcation.setAmountNotStr(amountInAccountCurrency);
                        transcation.setCommeriant(commandInput.getCommerciant());
                        transcation.setAccountThatMadeTheTranscation(account.getIBAN());
                        user.getTranscations().add(transcation);
                        if (card2.isOneTime()){
                            account.getCards().remove(card2);
                            Card newOneTimeCard = new Card();
                            newOneTimeCard.setActive(true);
                            newOneTimeCard.setOneTime(true);
                            newOneTimeCard.setCardNumber(Utils.generateCardNumber());
                            newOneTimeCard.setTimeStamp(commandInput.getTimestamp());
                            account.getCards().add(newOneTimeCard);
                        }
                        Commerciants commerciant = new Commerciants();
                        commerciant.setCommerciantName(commandInput.getCommerciant());
                        commerciant.setTotal(amountInAccountCurrency);
                        commerciant.setAccountThatPayedTheCommerciant(account.getIBAN());
                        boolean found = false;
                        for (Commerciants comm : user.getCommerciants()) {
                            if (comm.getCommerciantName().equals(commandInput.getCommerciant())) {
                                comm.setTotal(comm.getTotal() + amountInAccountCurrency);
                                found = true;
                            }
                        }
                        if (!found) {
                            user.getCommerciants().add(commerciant);
                        }
//                        if (card2.isOneTime()) {
//                            card2.setActive(false);
//                        }
                        return;
                    } else if (account.getBalance() < amountInAccountCurrency) {
                        Transcation transcation = new Transcation();
                        transcation.setTimestamp(commandInput.getTimestamp());
                        transcation.setDescription("Insufficient funds");
                        user.getTranscations().add(transcation);
                    } else {
                        card2.setActive(false);
                        Transcation transcation = new Transcation();
                        transcation.setTimestamp(commandInput.getTimestamp());
                        transcation.setDescription("The card is frozen");
                        user.getTranscations().add(transcation);
                    }


                }
            }
        }
    }

    /**
     *
     */
    public void visit(final SendMoney command) {
        CommandInput commandInput = command.getCommand();
        ArrayList<User> users = command.getUsers();
        ArrayList<ExchangeRate> exchangeRates = command.getExchangeRates();

        String senderIdentifier = commandInput.getAccount();
        String receiverIdentifier = commandInput.getReceiver();
        double amount = commandInput.getAmount();
        String email = commandInput.getEmail();

        User senderUser = CommandHelper.findUserByEmail(users, email);
        User receiverUser = CommandHelper.findUserByIBANOrAlias(users, receiverIdentifier);
        if (receiverUser == null) {
            return;
        }

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
                Transcation transcation = new Transcation();
                transcation.setTimestamp(commandInput.getTimestamp());
                transcation.setDescription("Insufficient funds");
                senderUser.getTranscations().add(transcation);
                return;
            }

            if (sender.getCurrency().equals(receiver.getCurrency())) {
                sender.setBalance(sender.getBalance() - amount);
                receiver.setBalance(receiver.getBalance() + amount);
                Transcation transcation2 = new Transcation();
                transcation2.setTimestamp(commandInput.getTimestamp());
                transcation2.setDescription(commandInput.getDescription());
                transcation2.setSenderIBAN(sender.getIBAN());
                transcation2.setReceiverIBAN(receiver.getIBAN());
                String formattedAmount2 = String
                        .format("%.1f %s", commandInput.getAmount(), receiver.getCurrency());
                transcation2.setAmount(formattedAmount2);
                transcation2.setTransferType("received");
                receiverUser.getTranscations().add(transcation2);
            } else {
                double convertedAmount =
                        CommandHelper
                                .convertCurrency(amount, sender.getCurrency(),
                                        receiver.getCurrency(), exchangeRates);
                sender.setBalance(sender.getBalance() - amount);
                receiver.setBalance(receiver.getBalance() + convertedAmount);
                Transcation transcation2 = new Transcation();
                transcation2.setTimestamp(commandInput.getTimestamp());
                transcation2.setDescription(commandInput.getDescription());
                transcation2.setSenderIBAN(sender.getIBAN());
                transcation2.setReceiverIBAN(receiver.getIBAN());
                String formattedAmount2;
                if (convertedAmount * 100 % 10 == 0) {
                    formattedAmount2 = String
                            .format("%.1f %s", convertedAmount, receiver.getCurrency());
                } else if (convertedAmount * 1000 % 10 == 0) {
                    formattedAmount2 = String
                            .format("%.2f %s", convertedAmount, receiver.getCurrency());
                } else {
                    formattedAmount2 = String
                            .format("%.14f %s", convertedAmount, receiver.getCurrency());
                }

                transcation2.setAmount(formattedAmount2);
                transcation2.setTransferType("received");
                receiverUser.getTranscations().add(transcation2);
            }
            Transcation transcation = new Transcation();
            transcation.setTimestamp(commandInput.getTimestamp());
            transcation.setDescription(commandInput.getDescription());
            transcation.setSenderIBAN(sender.getIBAN());
            transcation.setReceiverIBAN(receiver.getIBAN());
            String formattedAmount = String
                    .format("%.1f %s", commandInput.getAmount(), sender.getCurrency());
            transcation.setAmount(formattedAmount);
            transcation.setTransferType("sent");
            senderUser.getTranscations().add(transcation);




        }
    }
    /**
     *
     */
    public void visit(final SetAlias command) {
        ArrayList<User> users = command.getUsers();
        CommandInput commandInput = command.getCommandInput();
        String email = commandInput.getEmail();
        String IBAN = commandInput.getAccount();
        String alias = commandInput.getAlias();
        User user = CommandHelper.findUserByEmail(users, email);
        if (user != null) {
            Account account = CommandHelper.findAccountByIban(user, IBAN);
            if (account != null) {
                account.setAlias(alias);
            }
        }
    }
    /**
     *
     */
    public void visit(final PrintTransactions command) {
        ArrayList<User> users = command.getUsers();
        ObjectMapper mapper = command.getMapper();
        ArrayNode output = command.getOutput();
        ObjectNode node = command.getNode();
        CommandInput commandInput = command.getCommand();
        node.put("command", commandInput.getCommand());
        String email = commandInput.getEmail();
        User user = CommandHelper.findUserByEmail(users, email);

        if (user != null) {
            ArrayList<Transcation> transactions = user.getTranscations();
            transactions.sort(Comparator.comparingInt(Transcation::getTimestamp));

            ArrayNode transactionsArray = mapper.createArrayNode();
            for (Transcation transaction : transactions) {
                ObjectNode transactionNode = mapper.createObjectNode();

                transactionNode.put("timestamp", transaction.getTimestamp());
                transactionNode.put("description", transaction.getDescription());
                if (transaction.getCurrency() != null) {
                    transactionNode.put("currency", transaction.getCurrency());
                }
                if (transaction.getAmountNotStr() != 0) {
                    transactionNode.put("amount", transaction.getAmountNotStr());
                }
                if (transaction.getCommeriant() != null) {
                    transactionNode.put("commerciant", transaction.getCommeriant());
                }
                if (transaction.getCard() != null) {
                    transactionNode.put("card", transaction.getCard());
                }
                if (transaction.getCardHolder() != null) {
                    transactionNode.put("cardHolder", transaction.getCardHolder());
                }
                if (transaction.getAccount() != null) {
                    transactionNode.put("account", transaction.getAccount());
                }
                if (transaction.getSenderIBAN() != null) {
                    transactionNode.put("senderIBAN", transaction.getSenderIBAN());
                }
                if (transaction.getReceiverIBAN() != null) {
                    transactionNode.put("receiverIBAN", transaction.getReceiverIBAN());
                }
                if (transaction.getAmount() != null) {
                    transactionNode.put("amount", transaction.getAmount());
                }
                if (transaction.getTransferType() != null) {
                    transactionNode.put("transferType", transaction.getTransferType());
                }
                if (transaction.getInvolvedAccounts() != null) {
                    ArrayNode involvedAccountsArray = transactionNode.putArray("involvedAccounts");
                    for (String account : transaction.getInvolvedAccounts()) {
                        involvedAccountsArray.add(account);
                    }
                }
                if (transaction.getError() != null) {
                    transactionNode.put("error", transaction.getError());
                }
                transactionsArray.add(transactionNode);
            }

            node.set("output", transactionsArray);
            node.put("timestamp", commandInput.getTimestamp());

            output.add(node);
        }
    }
    /**
     *
     */
    public void visit(final SetMinBalance command) {
        CommandInput commandInput = command.getCommandInput();
        ArrayList<User> users = command.getUsers();
        Account account = CommandHelper
                .findAccountByIBANWithoutEmail(users, commandInput.getAccount());
        if (account == null) {
            return;
        }
        account.setMinBalance(commandInput.getAmount());

    }
    /**
     *
     */
    public void visit(final CheckCardStatus commnad) {
        CommandInput commandInput = commnad.getCommand();
        ObjectNode node = commnad.getNode();
        ArrayNode output = commnad.getOutput();
        ObjectMapper mapper = commnad.getMapper();
        ArrayList<User> users = commnad.getUsers();
        Account account = CommandHelper
                        .findAccountByCardNumberWithoutEmail(users, commandInput.getCardNumber());
        User user = CommandHelper
                    .findUserByCardNumberWithoutEmail(users, commandInput.getCardNumber());

        Card card = CommandHelper
                    .findCardByNumberWithoutEmail(users, commandInput.getCardNumber());
        if (card == null) {
            node.put("command", commandInput.getCommand());
            ObjectNode messageNode = mapper.createObjectNode();
            messageNode.put("timestamp", commandInput.getTimestamp());
            messageNode.put("description", "Card not found");
            node.set("output", messageNode);
            node.put("timestamp", commandInput.getTimestamp());
            output.add(node);
            return;
        }
        if (user == null) {
            return;
        }
        if (account == null) {
            return;
        }

        if (account.getBalance() == account.getMinBalance()
                && account.getBalance() == 0
                && account.getMinBalance() == 0) {
            String mess = "You have reached the minimum amount of funds, the card will be frozen";
            Transcation transcation = new Transcation();
            transcation.setTimestamp(commandInput.getTimestamp());
            transcation.setDescription(mess);
            user.getTranscations().add(transcation);
        }


    }
    /**
     *
     */
    public void visit(final ChangeInterestRate command) {
        CommandInput commandInput = command.getCommand();
        ArrayList<User> users = command.getUsers();
        ObjectNode node = command.getNode();
        ObjectMapper mapper = command.getMapper();
        ArrayNode output = command.getOutput();
        Account account = CommandHelper
                        .findAccountByIBANWithoutEmail(users, commandInput.getAccount());
        if (account == null) {
            return;
        }
        if (!account.getAccountType().equals("savings")) {
            node.put("command", commandInput.getCommand());
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("timestamp", commandInput.getTimestamp());
            outputNode.put("description", "This is not a savings account");
            node.set("output", outputNode);
            node.put("timestamp", commandInput.getTimestamp());
            output.add(node);
            return;
        }
        account.setInterestRate(commandInput.getInterestRate());
    }
    /**
     *
     */
    public void visit(final SplitPayment command) {
        CommandInput commandInput = command.getCommand();
        ArrayList<User> users = command.getUsers();
        ArrayList<ExchangeRate> exchangeRates = command.getExchangeRates();
        List<String> accountIdentifiers = commandInput.getAccounts();
        double totalAmount = commandInput.getAmount();
        String baseCurrency = commandInput.getCurrency();
        int timestamp = commandInput.getTimestamp();

        ArrayList<Account> accounts = new ArrayList<>();

        for (String accountIdentifier : accountIdentifiers) {
            Account account = CommandHelper.findAccountByIBANWithoutEmail(users, accountIdentifier);
            if (account == null) {
                return;
            }
            accounts.add(account);
        }
        ArrayList<User> usersInSplit = new ArrayList<>();
        for (Account account : accounts) {
            User user = CommandHelper.findUserByIBAN(users, account.getIBAN());
            if (user == null) {
                return;
            }
            usersInSplit.add(user);
        }

        double splitAmount = totalAmount / accounts.size();

        for (Account account : accounts) {
            double amountInAccountCurrency = splitAmount;

            if (!account.getCurrency().equals(baseCurrency)) {
                amountInAccountCurrency = CommandHelper.convertCurrency(
                        splitAmount, baseCurrency, account.getCurrency(), exchangeRates);
            }

            if (account.getBalance() < amountInAccountCurrency) {
                Transcation transaction = new Transcation();
                transaction.setTimestamp(timestamp);
                transaction
                        .setDescription("Split payment of "
                                + String.format("%.2f", totalAmount)
                                + " "
                                + baseCurrency);
                transaction.setCurrency(commandInput.getCurrency());
                transaction
                        .setAmountNotStr(commandInput.getAmount()
                                / commandInput.getAccounts().size());
                transaction.setInvolvedAccounts(new ArrayList<>(accountIdentifiers));
                transaction.setError("Account" + " " + account.getIBAN()
                                    + " " + "has insufficient funds for a split payment.");
                for (User user : usersInSplit) {
                   user.getTranscations().add(transaction);
                    }


                return;
            }
        }

        for (Account account : accounts) {
            double amountInAccountCurrency = splitAmount;

            if (!account.getCurrency().equals(baseCurrency)) {
                amountInAccountCurrency = CommandHelper.convertCurrency(
                        splitAmount, baseCurrency, account.getCurrency(), exchangeRates);
            }

            account.setBalance(account.getBalance() - amountInAccountCurrency);

            Transcation transaction = new Transcation();
            transaction.setTimestamp(timestamp);
            transaction
                    .setDescription("Split payment of "
                                    + String.format("%.2f", totalAmount)
                                    + " "
                                    + baseCurrency);
            transaction.setCurrency(commandInput.getCurrency());
            transaction
                    .setAmountNotStr(commandInput.getAmount() / commandInput.getAccounts().size());
            transaction.setInvolvedAccounts(new ArrayList<>(accountIdentifiers));

            for (User user : users) {
                for (Account userAccount : user.getAccounts()) {
                    if (userAccount.getIBAN().equals(account.getIBAN())) {
                        user.getTranscations().add(transaction);
                        break;
                    }
                }
            }
        }
    }
    /**
     *
     */
    public void visit(final Report command) {
        CommandInput commandInput = command.getCommand();
        ArrayList<User> users = command.getUsers();
        ObjectNode node = command.getNode();
        ObjectMapper mapper = command.getMapper();
        ArrayNode output = command.getOutput();

        String IBAN = commandInput.getAccount();
        User user = CommandHelper.findUserByIBAN(users, IBAN);

        if (user == null) {
            node.put("command", commandInput.getCommand());
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("timestamp", commandInput.getTimestamp());
            outputNode.put("description", "Account not found");
            node.set("output", outputNode);
            node.put("timestamp", commandInput.getTimestamp());
            output.add(node);
            return;
        }

        Account account = CommandHelper.findAccountByIban(user, IBAN);
        if (account == null) {
            node.put("command", commandInput.getCommand());
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("timestamp", commandInput.getTimestamp());
            outputNode.put("description", "Account not found");
            node.set("output", outputNode);
            node.put("timestamp", commandInput.getTimestamp());
            output.add(node);
            return;
        }


        int startTimestamp = commandInput.getStartTimestamp();
        int endTimestamp = commandInput.getEndTimestamp();

        ArrayList<Transcation> transactions = user.getTranscations();
        transactions.sort(Comparator.comparingInt(Transcation::getTimestamp));

        ArrayNode transactionsArray = mapper.createArrayNode();
        for (Transcation transaction : transactions) {
            if (transaction.getTimestamp() >= startTimestamp
                    && transaction.getTimestamp() <= endTimestamp) {
                ObjectNode transactionNode = mapper.createObjectNode();
                transactionNode.put("timestamp", transaction.getTimestamp());
                transactionNode.put("description", transaction.getDescription());
                if (transaction.getCurrency() != null) {
                    transactionNode.put("currency", transaction.getCurrency());
                }
                if (transaction.getAmountNotStr() != 0) {
                    transactionNode.put("amount", transaction.getAmountNotStr());
                }
                if (transaction.getCommeriant() != null) {
                    transactionNode.put("commerciant", transaction.getCommeriant());
                }
                if (transaction.getCard() != null) {
                    transactionNode.put("card", transaction.getCard());
                }
                if (transaction.getCardHolder() != null) {
                    transactionNode.put("cardHolder", transaction.getCardHolder());
                }
                if (transaction.getAccount() != null) {
                    transactionNode.put("account", transaction.getAccount());
                }
                if (transaction.getSenderIBAN() != null) {
                    transactionNode.put("senderIBAN", transaction.getSenderIBAN());
                }
                if (transaction.getReceiverIBAN() != null) {
                    transactionNode.put("receiverIBAN", transaction.getReceiverIBAN());
                }
                if (transaction.getAmount() != null) {
                    transactionNode.put("amount", transaction.getAmount());
                }
                if (transaction.getTransferType() != null) {
                    transactionNode.put("transferType", transaction.getTransferType());
                }
                if (transaction.getInvolvedAccounts() != null) {
                    ArrayNode involvedAccountsArray = transactionNode.putArray("involvedAccounts");
                    for (String acc : transaction.getInvolvedAccounts()) {
                        involvedAccountsArray.add(acc);
                    }
                }
                if (transaction.getError() != null) {
                    transactionNode.put("error", transaction.getError());
                }
                transactionsArray.add(transactionNode);
            }
        }

        ObjectNode outputNode = mapper.createObjectNode();
        outputNode.put("IBAN", IBAN);
        outputNode.put("balance", account.getBalance());
        outputNode.put("currency", account.getCurrency());
        outputNode.set("transactions", transactionsArray);

        node.put("command", "report");
        node.set("output", outputNode);
        node.put("timestamp", commandInput.getTimestamp());

        output.add(node);
    }
    /**
     *
     */
    public void visit(final SpendingsReport command) {
        CommandInput commandInput = command.getCommand();
        ArrayList<User> users = command.getUsers();
        ObjectNode node = command.getNode();
        ObjectMapper mapper = command.getMapper();
        ArrayNode output = command.getOutput();

        String IBAN = commandInput.getAccount();
        User user = CommandHelper.findUserByIBAN(users, IBAN);
        if (user == null) {
            node.put("command", commandInput.getCommand());
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("timestamp", commandInput.getTimestamp());
            outputNode.put("description", "Account not found");
            node.set("output", outputNode);
            node.put("timestamp", commandInput.getTimestamp());
            output.add(node);
            return;
        }
        Account account = CommandHelper.findAccountByIban(user, IBAN);
        if (account == null) {
            node.put("command", commandInput.getCommand());
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("timestamp", commandInput.getTimestamp());
            outputNode.put("description", "Account not found");
            node.set("output", outputNode);
            node.put("timestamp", commandInput.getTimestamp());
            output.add(node);
            return;
        }

        int startTimestamp = commandInput.getStartTimestamp();
        int endTimestamp = commandInput.getEndTimestamp();

        node.put("command", commandInput.getCommand());
        ObjectNode outputNode = mapper.createObjectNode();

        outputNode.put("IBAN", IBAN);
        outputNode.put("balance", account.getBalance());
        outputNode.put("currency", account.getCurrency());

        ArrayNode transactionsArray = mapper.createArrayNode();
        ArrayList<Commerciants> reportCommerciants = new ArrayList<>();

        for (Transcation transaction : user.getTranscations()) {
            if ((transaction.getDescription().equals("Card payment")
                    && transaction.getAccountThatMadeTheTranscation().equals(IBAN))
                    && transaction.getTimestamp() >= startTimestamp
                    && transaction.getTimestamp() <= endTimestamp) {

                ObjectNode transactionNode = mapper.createObjectNode();
                transactionNode.put("timestamp", transaction.getTimestamp());
                transactionNode.put("description", transaction.getDescription());
                transactionNode.put("amount", transaction.getAmountNotStr());
                transactionNode.put("commerciant", transaction.getCommeriant());
                transactionsArray.add(transactionNode);

                boolean found = false;
                for (Commerciants commerciant : reportCommerciants) {
                    if (commerciant.getCommerciantName().equals(transaction.getCommeriant())) {
                        commerciant.setTotal(commerciant.getTotal() + transaction.getAmountNotStr());
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    Commerciants newCommerciant = new Commerciants();
                    newCommerciant.setCommerciantName(transaction.getCommeriant());
                    newCommerciant.setTotal(transaction.getAmountNotStr());
                    newCommerciant.setAccountThatPayedTheCommerciant(IBAN);
                    reportCommerciants.add(newCommerciant);
                }
            }
        }

        outputNode.set("transactions", transactionsArray);

        ArrayNode commerciantsArray = mapper.createArrayNode();
        reportCommerciants.sort(Comparator.comparing(Commerciants::getCommerciantName));
        for (Commerciants commerciant : reportCommerciants) {
            if (!commerciant.getAccountThatPayedTheCommerciant().equals(IBAN)) {
                continue;
            }
            ObjectNode commerciantNode = mapper.createObjectNode();
            commerciantNode.put("commerciant", commerciant.getCommerciantName());
            commerciantNode.put("total", commerciant.getTotal());
            commerciantsArray.add(commerciantNode);
        }

        outputNode.set("commerciants", commerciantsArray);

        node.set("output", outputNode);
        node.put("timestamp", commandInput.getTimestamp());

        output.add(node);
    }
    /**
     *
     */
    public void visit(final AddInterest command) {
        CommandInput commandInput = command.getCommand();
        ArrayList<User> users = command.getUsers();
        ObjectNode node = command.getNode();
        ObjectMapper mapper = command.getMapper();
        ArrayNode output = command.getOutput();

        String IBAN = commandInput.getAccount();
        User user = CommandHelper.findUserByIBAN(users, IBAN);
        if (user == null) {
            return;
        }
        Account account = CommandHelper.findAccountByIban(user, IBAN);
        if (account == null) {
            return;
        }
        if (!account.getAccountType().equals("savings")) {
            node.put("command", commandInput.getCommand());
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("timestamp", commandInput.getTimestamp());
            outputNode.put("description", "This is not a savings account");
            node.set("output", outputNode);
            node.put("timestamp", commandInput.getTimestamp());
            output.add(node);
            return;
        }
        double interest = account.getInterestRate() * account.getBalance();
        account.setBalance(account.getBalance() + interest);



    }





}



