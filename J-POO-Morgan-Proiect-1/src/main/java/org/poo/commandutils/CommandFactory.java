package org.poo.commandutils;

import org.poo.banking.ExchangeRate;
import org.poo.fileio.CommandInput;
import org.poo.userutils.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

public abstract class CommandFactory {
    /**
     * Abstract factory for creating commands
     */
    public abstract Visitable createCommand(
            CommandInput commandInput,
            ArrayList<User> usersList,
            ArrayList<ExchangeRate> exchangeRates,
            ObjectMapper mapper,
            ArrayNode output,
            ObjectNode node);
}
