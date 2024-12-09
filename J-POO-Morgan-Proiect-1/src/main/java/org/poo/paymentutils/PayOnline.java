package org.poo.paymentutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.banking.ExchangeRate;
import org.poo.commandutils.CommandVisitor;
import org.poo.commandutils.Visitable;
import org.poo.fileio.CommandInput;
import org.poo.userutils.User;

import java.util.ArrayList;

@Getter
@Setter
public class PayOnline implements Visitable {
    private final ArrayList<User> users;
    private final ObjectNode node;
    private final ObjectMapper mapper;
    private final ArrayNode output;
    private final CommandInput command;
    private final ArrayList<ExchangeRate> exchangeRates;

    public PayOnline(final ArrayList<User> users, final ObjectNode node,
                     final ObjectMapper mapper, final ArrayNode output,
                     final CommandInput command, final ArrayList<ExchangeRate> exchangeRates) {
        this.users = users;
        this.node = node;
        this.mapper = mapper;
        this.output = output;
        this.command = command;
        this.exchangeRates = exchangeRates;
    }

    /**
     * Accept method for the visitor pattern.
     * @param visitor the visitor
     */
    @Override
    public void accept(final CommandVisitor visitor) {
        visitor.visit(this);
    }
}
