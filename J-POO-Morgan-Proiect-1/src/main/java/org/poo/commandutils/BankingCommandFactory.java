package org.poo.commandutils;

import org.poo.accountandcardutils.*;
import org.poo.banking.ExchangeRate;
import org.poo.fileio.CommandInput;
import org.poo.paymentutils.PayOnline;
import org.poo.paymentutils.SendMoney;
import org.poo.paymentutils.SplitPayment;
import org.poo.userutils.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.printingutils.PrintTransactions;
import org.poo.printingutils.PrintUsers;
import org.poo.printingutils.Report;
import org.poo.printingutils.SpendingsReport;

import java.util.List;

public class BankingCommandFactory extends CommandFactory {


    private static BankingCommandFactory instance;
    private BankingCommandFactory() {
    }
    public static BankingCommandFactory getSingletonInstance() {
        if (instance == null) {
            synchronized (BankingCommandFactory.class) {
                if (instance == null) {
                    instance = new BankingCommandFactory();
                }
            }
        }
        return instance;
    }
    /**
     * Creates a command based on the command input.
     * @param commandInput the command input
     * @param usersList the list of users
     * @param exchangeRates the list of exchange rates
     * @param mapper the object mapper
     * @param output the output array node
     * @param node the object node
     * @return the created command
     */
    @Override
    public Visitable createCommand(final CommandInput commandInput, final List<User> usersList,
                                   final List<ExchangeRate> exchangeRates, final ObjectMapper mapper,
                                   final ArrayNode output, final ObjectNode node) {
        return switch (commandInput.getCommand()) {
            case "printUsers" ->
                    new PrintUsers(usersList, node, mapper, output, commandInput);
            case "addAccount" ->
                    new AddAccount(commandInput, usersList);
            case "createCard" ->
                    new CreateCard(commandInput, usersList);
            case "addFunds" ->
                    new AddFunds(commandInput, usersList);
            case "deleteAccount" ->
                    new DeleteAccount(usersList, node, mapper, output, commandInput);
            case "createOneTimeCard" ->
                    new CreateOneTimeCard(commandInput, usersList);
            case "deleteCard" ->
                    new DeleteCard(commandInput, usersList);
            case "payOnline" ->
                    new PayOnline(usersList, node, mapper, output, commandInput, exchangeRates);
            case "sendMoney" ->
                    new SendMoney(usersList, exchangeRates, commandInput);
            case "setAlias" ->
                    new SetAlias(commandInput, usersList);
            case "printTransactions" ->
                    new PrintTransactions(usersList, node, mapper, output, commandInput);
            case "setMinimumBalance" ->
                    new SetMinBalance(commandInput, usersList);
            case "checkCardStatus" ->
                    new CheckCardStatus(usersList, node, mapper, output, commandInput);
            case "changeInterestRate" ->
                    new ChangeInterestRate(usersList, commandInput, mapper, node, output);
            case "splitPayment" ->
                    new SplitPayment(commandInput, usersList, exchangeRates);
            case "report" ->
                    new Report(usersList, mapper, output, node, commandInput);
            case "spendingsReport" ->
                    new SpendingsReport(usersList, commandInput, node, mapper, output);
            case "addInterest" ->
                    new AddInterest(usersList, commandInput, node, mapper, output);
            default ->
                    throw new IllegalArgumentException(
                            "Unknown command: " + commandInput.getCommand());
        };
    }

}

