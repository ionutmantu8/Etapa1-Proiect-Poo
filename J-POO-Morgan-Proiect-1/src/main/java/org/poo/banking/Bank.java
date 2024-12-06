package org.poo.banking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.commandutils.*;
import org.poo.fileio.CommandInput;
import org.poo.fileio.ExchangeInput;
import org.poo.fileio.ObjectInput;
import org.poo.fileio.UserInput;
import org.poo.userutils.User;
import org.poo.utils.Utils;

import java.util.ArrayList;



public class Bank {
    /**
     * Class that represents the bank.
     */
    public final ArrayNode startBanking(final ObjectInput inputData) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode output = mapper.createArrayNode();
        UserInput[] users = inputData.getUsers();
        ArrayList<User> usersList = new ArrayList<>();
        Utils.resetRandom();
        for (int i = 0; i < users.length; i++) {
            usersList.add(new User());
            usersList
                    .get(i)
                    .setFirstName(users[i].getFirstName());
            usersList
                    .get(i)
                    .setLastName(users[i].getLastName());
            usersList
                    .get(i)
                    .setEmail(users[i].getEmail());
        }
        ExchangeInput[] exchangeInputs = inputData.getExchangeRates();
        ArrayList<ExchangeRate> exchangeRates = new ArrayList<>();
        for (int i = 0; i < exchangeInputs.length; i++) {
            exchangeRates.add(new ExchangeRate());
            exchangeRates
                    .get(i)
                    .setFromCurrency(exchangeInputs[i].getFrom());
            exchangeRates
                    .get(i)
                    .setToCurrency(exchangeInputs[i].getTo());
            exchangeRates
                    .get(i)
                    .setRate(exchangeInputs[i].getRate());
        }
        CommandVisitor visitor = new BankingCommandVisitor();
        CommandInput[] commandInputs = inputData.getCommands();
        for (CommandInput commandInput : commandInputs) {
            ObjectNode node = mapper.createObjectNode();
            switch (commandInput.getCommand()) {
                case "printUsers" -> {
                    PrintUsers printUsersCommand =
                            new PrintUsers(usersList, node, mapper, output, commandInput);
                    printUsersCommand.accept(visitor);
                }
                case "addAccount" -> {
                    AddAccount addAccountCommand = new AddAccount(commandInput, usersList);
                    addAccountCommand.accept(visitor);
                }
                case "createCard" -> {
                    CreateCard createCardCommand = new CreateCard(commandInput, usersList);
                    createCardCommand.accept(visitor);
                }
                case "addFunds" -> {
                    AddFunds addFundsCommand = new AddFunds(commandInput, usersList);
                    addFundsCommand.accept(visitor);
                }
                case "deleteAccount" -> {
                    DeleteAccount deleteAccountCommand =
                            new DeleteAccount(usersList, node, mapper, output, commandInput);
                    deleteAccountCommand.accept(visitor);
                }
                case "createOneTimeCard" -> {
                    CreateOneTimeCard createOneTimeCardCommand =
                            new CreateOneTimeCard(commandInput, usersList);
                    createOneTimeCardCommand.accept(visitor);
                }
                case "deleteCard" -> {
                    DeleteCard deleteCardCommand = new DeleteCard(commandInput, usersList);
                    deleteCardCommand.accept(visitor);
                }
                case "payOnline" -> {
                    PayOnline payOnlineCommand = new PayOnline(usersList, node, mapper, output,
                            commandInput, exchangeRates);
                    payOnlineCommand.accept(visitor);
                }
                case "sendMoney" -> {
                    SendMoney sendMoneyCommand =
                            new SendMoney(usersList, exchangeRates, commandInput);
                    sendMoneyCommand.accept(visitor);
                }
                case "setAlias" -> {
                    SetAlias setAliasCommand = new SetAlias(commandInput, usersList);
                    setAliasCommand.accept(visitor);
                }
                case "printTransactions" -> {
                    PrintTransactions printTransactionsCommand =
                            new PrintTransactions(usersList, node, mapper, output, commandInput);
                    printTransactionsCommand.accept(visitor);
                }
                case "setMinimumBalance" -> {
                    SetMinBalance setMinBalanceCommand =
                            new SetMinBalance(commandInput, usersList);
                    setMinBalanceCommand.accept(visitor);
                }
                case "checkCardStatus" -> {
                    CheckCardStatus checkCardStatus =
                            new CheckCardStatus(usersList, node, mapper, output, commandInput);
                    checkCardStatus.accept(visitor);
                }
                case "changeInterestRate" -> {
                    ChangeInterestRate changeInterestRate =
                            new ChangeInterestRate(usersList, commandInput, mapper, node, output);
                    changeInterestRate.accept(visitor);
                }
                case "splitPayment" -> {
                    SplitPayment splitPayment =
                            new SplitPayment(commandInput, usersList, exchangeRates);
                    splitPayment.accept(visitor);
                }
                case "report" -> {
                    Report report = new Report(usersList, mapper, output, node, commandInput);
                    report.accept(visitor);
                }
                case "spendingsReport" -> {
                    SpendingsReport spendingsReport =
                            new SpendingsReport(usersList, commandInput, node, mapper, output);
                    spendingsReport.accept(visitor);
                }
                case "addInterest" -> {
                    AddInterest addInterest = new AddInterest(usersList, commandInput, node, mapper, output);
                    addInterest.accept(visitor);
                }

            }
        }


        return output;
    }

}
