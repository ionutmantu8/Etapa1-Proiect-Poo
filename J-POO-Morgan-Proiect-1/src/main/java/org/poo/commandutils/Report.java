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
public class Report extends PrintUsers implements Visitable {
   public Report(final ArrayList<User> users, final ObjectMapper mapper,
                 final ArrayNode output, final ObjectNode node,
                 final CommandInput commandInput) {
        super(users, node, mapper, output, commandInput);
    }
    /**
     *
     */
    @Override
    public void accept(final CommandVisitor visitor) {
        visitor.visit(this);
    }
}