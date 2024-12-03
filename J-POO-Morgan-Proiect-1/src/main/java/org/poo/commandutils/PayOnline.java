package org.poo.commandutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.banking.ExchangeRate;
import org.poo.banking.Transcation;
import org.poo.fileio.CommandInput;
import org.poo.userutils.Account;
import org.poo.userutils.Card;
import org.poo.userutils.User;

import java.util.*;

@Getter
@Setter
public class PayOnline implements Visitable {
    private final ArrayList<User> users;
    private final ObjectNode node;
    private final ObjectMapper mapper;
    private final ArrayNode output;
    private final CommandInput command;
    private final ArrayList<ExchangeRate> exchangeRates;

    public PayOnline(ArrayList<User> users, ObjectNode node,
                     ObjectMapper mapper, ArrayNode output,
                     CommandInput command, ArrayList<ExchangeRate> exchangeRates) {
        this.users = users;
        this.node = node;
        this.mapper = mapper;
        this.output = output;
        this.command = command;
        this.exchangeRates = exchangeRates;
    }


    @Override
    public void accept(CommandVisitor visitor) {
        visitor.visit(this);
    }
}
