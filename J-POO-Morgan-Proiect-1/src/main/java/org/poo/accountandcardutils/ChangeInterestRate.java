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

public class ChangeInterestRate extends PrintUsers implements Visitable {
    public ChangeInterestRate(final List<User> users, final CommandInput commandInput,
                              final ObjectMapper mapper, final ObjectNode node,
                              final ArrayNode output) {
        super(users, node, mapper, output, commandInput);
    }

    /**
     * Accept method for the visitor pattern.
     * @param commandVisitor the visitor
     */
    public void accept(final CommandVisitor commandVisitor) {
        commandVisitor.visit(this);
    }
}
