package org.poo.commandutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.userutils.User;

import java.util.ArrayList;

public class SpendingsReport extends Report implements Visitable{

    public SpendingsReport(final ArrayList<User> users, final CommandInput commandInput,
                           final ObjectNode node, final ObjectMapper mapper,
                           final ArrayNode output) {
        super(users, mapper, output, node, commandInput);
    }


    @Override
    public void accept(final CommandVisitor visitor) {
        visitor.visit(this);
    }
}