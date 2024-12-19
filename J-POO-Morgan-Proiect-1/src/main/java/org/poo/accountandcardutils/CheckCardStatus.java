package org.poo.accountandcardutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.commandutils.CommandVisitor;
import org.poo.commandutils.Visitable;
import org.poo.fileio.CommandInput;
import org.poo.userutils.User;
import org.poo.printingutils.PrintUsers;

import java.util.List;


public class CheckCardStatus extends PrintUsers implements Visitable {
    public CheckCardStatus(final List<User> users, final ObjectNode node,
                           final ObjectMapper mapper, final ArrayNode output,
                           final CommandInput command) {
        super(users, node, mapper, output, command);
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
