package org.poo.commandutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accountandcardutils.*;
import org.poo.banking.Commerciants;
import org.poo.banking.ExchangeRate;
import org.poo.banking.Transcation;
import org.poo.fileio.CommandInput;
import org.poo.paymentutils.PayOnline;
import org.poo.paymentutils.SendMoney;
import org.poo.paymentutils.SplitPayment;
import org.poo.userutils.Account;
import org.poo.userutils.Card;
import org.poo.userutils.User;
import org.poo.utils.Utils;
import org.poo.printingutils.PrintTransactions;
import org.poo.printingutils.PrintUsers;
import org.poo.printingutils.Report;
import org.poo.printingutils.SpendingsReport;

import java.util.*;
import java.util.List;


public class BankingCommandVisitor implements CommandVisitor {
    /**
     * Adds a new account for a user.
     *
     * @param command the command containing the account details
     */
    @Override
    public void visit(final AddAccount command) {
        CommandInput commandInput = command.getCommandInput();
        List<User> usersList = command.getUsers();
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
     * Adds funds to an account.
     *
     * @param command the command containing the account and amount details
     */
    @Override
    public void visit(final AddFunds command) {
        List<User> users = command.getUsers();
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
     * Creates a new card for an account.
     *
     * @param command the command containing the card details
     */
    @Override
    public void visit(final CreateCard command) {
        CommandInput commandInput = command.getCommandInput();
        List<User> userList = command.getUsers();
        String IBAN = commandInput.getAccount();
        String email = commandInput.getEmail();
        User user = CommandHelper.findUserByEmail(userList, email);
        if (user == null) {
            return;
        }
        Account account = CommandHelper.findAccountByIban(user, IBAN);
        if (account == null) {
            return;
        }
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
    /**
     * Prints the list of users aside with all their accounts and cards.
     *
     * @param command the command containing the user details
     */
    @Override
    public void visit(final PrintUsers command) {
        ObjectNode node = command.getNode();
        CommandInput commandInput = command.getCommand();
        List<User> users = command.getUsers();
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
     * Deletes an account.
     *
     * @param command the command containing the account details
     */
    @Override
    public void visit(final DeleteAccount command) {
        CommandInput commandInput = command.getCommand();
        List<User> users = command.getUsers();
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
     * Creates a one-time use card for an account.
     *
     * @param command the command containing the card details
     */
    @Override
    public void visit(final CreateOneTimeCard command) {
        List<User> users = command.getUsers();
        CommandInput commandInput = command.getCommandInput();
        String IBAN = commandInput.getAccount();
        String email = commandInput.getEmail();
        User user = CommandHelper.findUserByEmail(users, email);
        if (user == null) {
            return;
        }
        Account account = CommandHelper.findAccountByIban(user, IBAN);
        if (account == null) {
            return;
        }
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
    /**
     * Deletes a card.
     *
     * @param command the command containing the card details
     */
    @Override
    public void visit(final DeleteCard command) {
        CommandInput commandInput = command.getCommandInput();
        List<User> users = command.getUsers();
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
     * Processes an online payment.
     *
     * @param command the command containing the payment details
     */
    public void visit(final PayOnline command) {
        List<User> users = command.getUsers();
        List<ExchangeRate> exchangeRates = command.getExchangeRates();
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
                        if (card2.isOneTime()) {
                            Transcation deleteTranscation = new Transcation();
                            deleteTranscation.setTimestamp(commandInput.getTimestamp());
                            deleteTranscation.setDescription("The card has been destroyed");
                            deleteTranscation.setCard(cardNumber);
                            deleteTranscation.setCardHolder(user.getEmail());
                            deleteTranscation.setAccount(account.getIBAN());
                            user.getTranscations().add(deleteTranscation);
                            account.getCards().remove(card2);
                            Card newOneTimeCard = new Card();
                            newOneTimeCard.setActive(true);
                            newOneTimeCard.setOneTime(true);
                            newOneTimeCard.setCardNumber(Utils.generateCardNumber());
                            newOneTimeCard.setTimeStamp(commandInput.getTimestamp());
                            account.getCards().add(newOneTimeCard);
                            Transcation addNewCardTransaciton = new Transcation();
                            addNewCardTransaciton.setTimestamp(commandInput.getTimestamp());
                            addNewCardTransaciton.setDescription("New card created");
                            addNewCardTransaciton.setCard(newOneTimeCard.getCardNumber());
                            addNewCardTransaciton.setCardHolder(user.getEmail());
                            addNewCardTransaciton.setAccount(account.getIBAN());
                            user.getTranscations().add(addNewCardTransaciton);
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
     * Sends money from one account to another.
     *
     * @param command the command containing the transfer details
     */
    public void visit(final SendMoney command) {
        CommandInput commandInput = command.getCommand();
        List<User> users = command.getUsers();
        List<ExchangeRate> exchangeRates = command.getExchangeRates();

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


            if (sender.getBalance() < amount) {
                Transcation transcation = new Transcation();
                transcation.setTimestamp(commandInput.getTimestamp());
                transcation.setDescription("Insufficient funds");
                senderUser.getTranscations().add(transcation);
                return;
            }



            if (sender.getCurrency().equals(receiver.getCurrency())) {
                Transcation transcation = getTranscation(commandInput, sender, receiver, "sent");
                senderUser.getTranscations().add(transcation);
                sender.setBalance(sender.getBalance() - amount);
                receiver.setBalance(receiver.getBalance() + amount);
                Transcation transcation2 = getTranscation(commandInput, sender, receiver, "received");
                receiverUser.getTranscations().add(transcation2);
            } else {
                double convertedAmount =
                        CommandHelper
                                .convertCurrency(amount, sender.getCurrency(),
                                        receiver.getCurrency(), exchangeRates);
                sender.setBalance(sender.getBalance() - amount);
                Transcation transcation = getTranscation(commandInput, sender, receiver, "sent");
                senderUser.getTranscations().add(transcation);
                receiver.setBalance(receiver.getBalance() + convertedAmount);
                Transcation transcation2 = new Transcation();
                transcation2.setTimestamp(commandInput.getTimestamp());
                transcation2.setDescription(commandInput.getDescription());
                transcation2.setSenderIBAN(sender.getIBAN());
                transcation2.setReceiverIBAN(receiver.getIBAN());
                String formattedAmount2;
                    formattedAmount2 = String
                            .format("%.14f %s", convertedAmount, receiver.getCurrency());
                transcation2.setAmount(formattedAmount2);
                transcation2.setTransferType("received");
                receiverUser.getTranscations().add(transcation2);
            }
        }
    }

    private static Transcation getTranscation(final CommandInput commandInput, final Account sender,
                                              final Account receiver, final String sent) {
        Transcation transcation = new Transcation();
        transcation.setTimestamp(commandInput.getTimestamp());
        transcation.setDescription(commandInput.getDescription());
        transcation.setSenderIBAN(sender.getIBAN());
        transcation.setReceiverIBAN(receiver.getIBAN());
        String formattedAmount = String
                .format("%.1f %s", commandInput.getAmount(), sender.getCurrency());
        transcation.setAmount(formattedAmount);
        transcation.setTransferType(sent);
        return transcation;
    }

    /**
     * Sets an alias for an account.
     *
     * @param command the command containing the alias details
     */
    public void visit(final SetAlias command) {
        List<User> users = command.getUsers();
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
     * Prints the list of transactions for a user.
     *
     * @param command the command containing the transaction details
     */
    public void visit(final PrintTransactions command) {
        List<User> users = command.getUsers();
        ObjectMapper mapper = command.getMapper();
        ArrayNode output = command.getOutput();
        ObjectNode node = command.getNode();
        CommandInput commandInput = command.getCommand();
        node.put("command", commandInput.getCommand());
        String email = commandInput.getEmail();
        User user = CommandHelper.findUserByEmail(users, email);

        if (user != null) {
            List<Transcation> transactions = user.getTranscations();
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
     * Sets the minimum balance for an account.
     *
     * @param command the command containing the balance details
     */
    public void visit(final SetMinBalance command) {
        CommandInput commandInput = command.getCommandInput();
        List<User> users = command.getUsers();
        Account account = CommandHelper
                .findAccountByIBANWithoutEmail(users, commandInput.getAccount());
        if (account == null) {
            return;
        }
        account.setMinBalance(commandInput.getAmount());

    }
    /**
     * Checks the status of a card.
     *
     * @param command the command containing the card details
     */
    public void visit(final CheckCardStatus command) {
        CommandInput commandInput = command.getCommand();
        ObjectNode node = command.getNode();
        ArrayNode output = command.getOutput();
        ObjectMapper mapper = command.getMapper();
        List<User> users = command.getUsers();
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
     * Changes the interest rate of a savings account.
     *
     * @param command the command containing the interest rate details
     */
    public void visit(final ChangeInterestRate command) {
        CommandInput commandInput = command.getCommand();
        List<User> users = command.getUsers();
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
        Transcation transcation = new Transcation();
        transcation.setTimestamp(commandInput.getTimestamp());
        transcation.setDescription("Interest rate of the account changed to "
                                    + commandInput.getInterestRate());
        User user = CommandHelper.findUserByIBAN(users, commandInput.getAccount());
        if (user == null) {
            return;
        }
        user.getTranscations().add(transcation);

    }

    /**
     * Processes a split payment among multiple accounts.
     *
     * @param command the command containing the split payment details
     */
    public void visit(final SplitPayment command) {
        CommandInput commandInput = command.getCommand();
        List<User> users = command.getUsers();
        List<ExchangeRate> exchangeRates = command.getExchangeRates();
        double totalAmount = commandInput.getAmount();
        String baseCurrency = commandInput.getCurrency();
        List<String> accountIdentifiers = commandInput.getAccounts();
        int timestamp = commandInput.getTimestamp();

        List<Account> accounts = new ArrayList<>();
        List<User> usersInSplit = new ArrayList<>();
        String insufficientFundsError = null;
        for (String iban : accountIdentifiers) {
            Account account = CommandHelper.findAccountByIBANWithoutEmail(users, iban);
            User user = CommandHelper.findUserByIBAN(users, iban);

            if (account == null || user == null) {
                return;
            }

            accounts.add(account);
            usersInSplit.add(user);
        }

        double splitAmount = totalAmount / accounts.size();
        List<Double> convertedAmounts = new ArrayList<>();

        for (Account account : accounts) {
            double amountInAccountCurrency = splitAmount;

            if (!account.getCurrency().equals(baseCurrency)) {
                amountInAccountCurrency = CommandHelper.convertCurrency(
                        splitAmount, baseCurrency, account.getCurrency(), exchangeRates);
            }

            if (account.getBalance() < amountInAccountCurrency) {
                insufficientFundsError = "Account " + account.getIBAN()
                        + " has insufficient funds for a split payment.";
            }

            convertedAmounts.add(amountInAccountCurrency);
        }

        if (insufficientFundsError != null) {
            Transcation transaction = new Transcation();
            transaction.setTimestamp(timestamp);
            transaction.setDescription("Split payment of "
                    + String.format("%.2f", totalAmount)
                    + " "
                    + baseCurrency);
            transaction.setCurrency(baseCurrency);
            transaction.setAmountNotStr(splitAmount);
            transaction.setInvolvedAccounts(accountIdentifiers);
            transaction.setError(insufficientFundsError);

            for (User user : usersInSplit) {
                user.getTranscations().add(transaction);
            }

            return;
        }

        for (int i = 0; i < accounts.size(); i++) {
            Account account = accounts.get(i);
            User user = usersInSplit.get(i);

            double amountInAccountCurrency = convertedAmounts.get(i);
            account.setBalance(account.getBalance() - amountInAccountCurrency);

            Transcation transaction = new Transcation();
            transaction.setTimestamp(timestamp);
            transaction.setDescription("Split payment of "
                    + String.format("%.2f", totalAmount)
                    + " "
                    + baseCurrency);
            transaction.setCurrency(baseCurrency);
            transaction.setAmountNotStr(splitAmount);
            transaction.setInvolvedAccounts(accountIdentifiers);

            user.getTranscations().add(transaction);
        }
    }

    /**
     * Generates a report for an account.
     *
     * @param command the command containing the report details
     */
    public void visit(final Report command) {
        CommandInput commandInput = command.getCommand();
        List<User> users = command.getUsers();
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

        TreeSet<Transcation> uniqueTransactions = new TreeSet<>(
                Comparator.comparingInt(Transcation::getTimestamp)
                        .thenComparing(Transcation::getDescription)
                        .thenComparing(Transcation::getAmountNotStr)
        );
        uniqueTransactions.addAll(user.getTranscations());

        List<Transcation> transactions = new ArrayList<>(uniqueTransactions);
        transactions.sort(Comparator.comparingInt(Transcation::getTimestamp));

        ArrayNode transactionsArray = mapper.createArrayNode();
        for (Transcation transaction : transactions) {
            if (transaction.getTimestamp() >= startTimestamp
                    && transaction.getTimestamp() <= endTimestamp) {
                ObjectNode transactionNode = mapper.createObjectNode();
                if (transaction.getAccount() != null && !transaction.getAccount().equals(IBAN)) {
                   continue;
                }
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
     * Generates a spendings report for an account.
     *
     * @param command the command containing the spendings report details
     */
    public void visit(final SpendingsReport command) {
        CommandInput commandInput = command.getCommand();
        List<User> users = command.getUsers();
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
        if (account.getAccountType().equals("savings")) {
            node.put("command", commandInput.getCommand());
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("error", "This kind of report is not supported for a saving account");
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
        List<Commerciants> reportCommerciants = new ArrayList<>();

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
                    if (commerciant.getCommerciantName()
                            .equals(transaction.getCommeriant())) {
                        commerciant.setTotal(commerciant.getTotal()
                                            + transaction.getAmountNotStr());
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
     * Adds interest to a savings account.
     *
     * @param command the command containing the interest details
     */
    public void visit(final AddInterest command) {
        CommandInput commandInput = command.getCommand();
        List<User> users = command.getUsers();
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



