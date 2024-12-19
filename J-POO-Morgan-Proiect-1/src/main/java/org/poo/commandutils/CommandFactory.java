package org.poo.commandutils;

import org.poo.banking.ExchangeRate;
import org.poo.fileio.CommandInput;
import org.poo.userutils.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public abstract class CommandFactory {
    /**
     * Abstract factory for creating commands
     */
    public abstract Visitable createCommand(
            CommandInput commandInput,
            List<User> usersList,
            List<ExchangeRate> exchangeRates,
            ObjectMapper mapper,
            ArrayNode output,
            ObjectNode node);
}
