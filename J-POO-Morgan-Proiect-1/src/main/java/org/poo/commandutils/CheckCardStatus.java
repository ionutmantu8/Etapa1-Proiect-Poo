package org.poo.commandutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.userutils.User;

import java.util.ArrayList;

public class CheckCardStatus extends PrintUsers implements Visitable {
    public CheckCardStatus(ArrayList<User> users, ObjectNode node, ObjectMapper mapper, ArrayNode output, CommandInput command) {
        super(users, node, mapper, output, command);
    }

    @Override
    public void accept(CommandVisitor visitor) {
        visitor.visit(this);
    }

}
