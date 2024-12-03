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
        CommandInput[] commandInputs = inputData.getCommands();
        for (CommandInput commandInput : commandInputs) {
            ObjectNode node = mapper.createObjectNode();
            switch (commandInput.getCommand()) {
                case "printUsers" -> PrintUsers
                                    .printUsers(usersList, node, mapper, output, commandInput);
                case "addAccount" -> AddAccount
                                    .addAccount(usersList, commandInput);
                case "createCard" -> CreateCard
                                    .createCard(usersList, commandInput);
                case "addFunds" -> AddFunds
                                    .addFunds(usersList, commandInput);
                case "deleteAccount" -> DeleteAccount
                                        .deleteAccount(usersList, commandInput, node, output);
                case "createOneTimeCard" -> CreateOneTimeCard
                                            .createOneTimeCard(usersList, commandInput);
                case "deleteCard" -> DeleteCard
                                    .deleteCard(usersList, commandInput);
                case "payOnline" -> PayOnline
                                    .payOnline(usersList, commandInput,
                                                exchangeRates, node, output, mapper);
                case "sendMoney" -> SendMoney
                                    .sendMoney(usersList, commandInput, exchangeRates);
                case "setAlias" -> SetAlias
                                    .setAlias(usersList, commandInput);
                case "printTransactions" -> PrintTransactions
                                            .printTransactions(usersList, commandInput,
                                                                mapper, node, output);

            }
        }


        return output;
    }

}
