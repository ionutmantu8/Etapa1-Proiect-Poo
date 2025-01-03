package org.poo.accountandcardutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.commandutils.CommandVisitor;
import org.poo.commandutils.Visitable;
import org.poo.fileio.CommandInput;
import org.poo.userutils.User;
import org.poo.printingutils.Report;

import java.util.List;

public class AddInterest extends Report implements Visitable {

    public AddInterest(final List<User> users, final CommandInput commandInput,
                       final ObjectNode node, final ObjectMapper mapper,
                       final ArrayNode output) {
        super(users, mapper, output, node, commandInput);
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
