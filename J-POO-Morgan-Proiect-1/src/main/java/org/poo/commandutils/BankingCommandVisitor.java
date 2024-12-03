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
import org.poo.utils.Utils;

import java.util.ArrayList;
import java.util.Comparator;

public class BankingCommandVisitor implements CommandVisitor {

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
            user.getAccounts().add(newAccount);
            Transcation transcation = new Transcation();
            transcation.setTimestamp(commandInput.getTimestamp());
            transcation.setDescription("New account created");
            user.getTranscations().add(transcation);
        }
    }

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
            outputNode.put("error", "Account couldn't be deleted - see org.poo.transactions for details");
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
            outputNode.put("error", "Account couldn't be deleted - see org.poo.transactions for details");
            outputNode.put("timestamp", commandInput.getTimestamp());
            node.put("timestamp", commandInput.getTimestamp());
            output.add(node);
            return;
        }

        user.getAccounts().remove(accountToRemove);


        node.put("command", commandInput.getCommand());
        ObjectNode outputNode = node.putObject("output");
        if (accountToRemove != null) {
            outputNode.put("success", "Account deleted");
        } else {
            outputNode.put("error", "Account couldn't be deleted - see org.poo.transactions for details");
        }
        outputNode.put("timestamp", commandInput.getTimestamp());
        node.put("timestamp", commandInput.getTimestamp());

        output.add(node);
    }

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
                newCard.setCardNumber(Utils.generateCardNumber());
                newCard.setTimeStamp(commandInput.getTimestamp());
                newCard.setActive(true);
                account.getCards().add(newCard);
            }
        }

    }

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

        if (user != null) {
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
        }

        if (user != null) {
            for (Account account : user.getAccounts()) {
                for (Card card : account.getCards()) {
                    if (card.getCardNumber().equals(cardNumber) && card.isActive()) {
                        double amountInAccountCurrency = CommandHelper.convertCurrency(amount, currency, account.getCurrency(), exchangeRates);
                        if (account.getBalance() >= amountInAccountCurrency) {
                            account.setBalance(account.getBalance() - amountInAccountCurrency);
                            Transcation transcation = new Transcation();
                            transcation.setTimestamp(commandInput.getTimestamp());
                            transcation.setDescription("Card payment");
                            transcation.setAmountNotStr(amountInAccountCurrency);
                            transcation.setCommeriant(commandInput.getCommerciant());
                            user.getTranscations().add(transcation);
                            return;
                        } else {
                            Transcation transcation = new Transcation();
                            transcation.setTimestamp(commandInput.getTimestamp());
                            transcation.setDescription("Insufficient funds");
                            user.getTranscations().add(transcation);

                        }
                    }
                }
            }
        }
    }

    public void visit(final SendMoney command) {
        CommandInput commandInput = command.getCommand();
        ArrayList<User> users = command.getUsers();
        ArrayList<ExchangeRate> exchangeRates = command.getExchangeRates();

        String senderIdentifier = commandInput.getAccount();
        String receiverIdentifier = commandInput.getReceiver();
        double amount = commandInput.getAmount();
        String email = commandInput.getEmail();

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
            transcation.setTimestamp(commandInput.getTimestamp());
            transcation.setDescription(commandInput.getDescription());
            transcation.setSenderIBAN(sender.getIBAN());
            transcation.setReceiverIBAN(receiver.getIBAN());
            String formattedAmount = String.format("%.1f %s", commandInput.getAmount(), sender.getCurrency());
            transcation.setAmount(formattedAmount);
            transcation.setTransferType("sent");
            senderUser.getTranscations().add(transcation);

        }
    }

    public void visit(final SetAlias command) {
        ArrayList<User> users = command.getUsers();
        CommandInput commandInput = command.getCommandInput();
        String email = commandInput.getEmail();
        String IBAN = commandInput.getAccount();
        String alias = commandInput.getAlias();
        User user = CommandHelper.findUserByEmail(users, email);
        if (user != null) {
            Account account = CommandHelper.findAccountByIban(user, IBAN);
            if (account != null)
                account.setAlias(alias);
        }
    }

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

                transactionsArray.add(transactionNode);
            }

            node.set("output", transactionsArray);
            node.put("timestamp", commandInput.getTimestamp());

            output.add(node);
        }
    }

}


