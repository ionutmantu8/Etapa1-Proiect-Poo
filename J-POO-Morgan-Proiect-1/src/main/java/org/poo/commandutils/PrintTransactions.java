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
public class PrintTransactions extends PrintUsers implements Visitable {


    public PrintTransactions(final ArrayList<User> users, final ObjectNode node,
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
