package org.poo.banking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.commandutils.BankingCommandFactory;
import org.poo.commandutils.BankingCommandVisitor;
import org.poo.commandutils.CommandFactory;
import org.poo.commandutils.CommandVisitor;
import org.poo.commandutils.Visitable;
import org.poo.fileio.CommandInput;
import org.poo.fileio.ExchangeInput;
import org.poo.fileio.ObjectInput;
import org.poo.fileio.UserInput;
import org.poo.userutils.User;
import org.poo.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Bank {
    /**
     * Class that represents the bank.
     */

    public final ArrayNode startBanking(final ObjectInput inputData) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode output = mapper.createArrayNode();

        UserInput[] users = inputData.getUsers();
        List<User> usersList = new ArrayList<>();
        Utils.resetRandom();
        for (int i = 0; i < users.length; i++) {
            usersList.add(new User());
            usersList.get(i).setFirstName(users[i].getFirstName());
            usersList.get(i).setLastName(users[i].getLastName());
            usersList.get(i).setEmail(users[i].getEmail());
        }

        List<ExchangeRate> exchangeRates = getExchangeRates(inputData);

        CommandVisitor visitor = new BankingCommandVisitor();
        CommandFactory factory = BankingCommandFactory.getSingletonInstance();
        CommandInput[] commandInputs = inputData.getCommands();
        for (CommandInput commandInput : commandInputs) {
            ObjectNode node = mapper.createObjectNode();
            Visitable command = factory.createCommand(
                    commandInput, usersList, exchangeRates, mapper, output, node);
            if (command != null) {
                command.accept(visitor);
            }
        }

        return output;
    }

    private static List<ExchangeRate> getExchangeRates(ObjectInput inputData) {
        ExchangeInput[] exchangeInputs = inputData.getExchangeRates();
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        for (ExchangeInput input : exchangeInputs) {
            ExchangeRate directRate = new ExchangeRate();
            directRate.setFromCurrency(input.getFrom());
            directRate.setToCurrency(input.getTo());
            directRate.setRate(input.getRate());
            exchangeRates.add(directRate);

            ExchangeRate reverseRate = new ExchangeRate();
            reverseRate.setFromCurrency(input.getTo());
            reverseRate.setToCurrency(input.getFrom());
            reverseRate.setRate(1 / input.getRate());
            exchangeRates.add(reverseRate);
        }
        return exchangeRates;
    }
}
