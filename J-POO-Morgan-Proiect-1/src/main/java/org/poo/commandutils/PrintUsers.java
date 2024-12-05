package org.poo.commandutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;
import org.poo.userutils.User;

import java.util.ArrayList;

@Getter
@Setter
public class PrintUsers implements Visitable {
    private final ArrayList<User> users;
    private final ObjectNode node;
    private final ObjectMapper mapper;
    private final ArrayNode output;
    private final CommandInput command;

    public PrintUsers(final ArrayList<User> users, final ObjectNode node,
                      final ObjectMapper mapper, final ArrayNode output,
                      final CommandInput command) {
        this.users = users;
        this.node = node;
        this.mapper = mapper;
        this.output = output;
        this.command = command;
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




