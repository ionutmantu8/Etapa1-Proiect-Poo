package org.poo.commandutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.banking.Transcation;
import org.poo.fileio.CommandInput;
import org.poo.userutils.User;

import java.util.ArrayList;
import java.util.Comparator;

@Getter
@Setter
public class PrintTransactions extends PrintUsers implements Visitable {


    public PrintTransactions(ArrayList<User> users, ObjectNode node,
                             ObjectMapper mapper, ArrayNode output,
                             CommandInput command) {
      super(users, node, mapper, output, command);
    }


    @Override
    public void accept(CommandVisitor visitor) {
        visitor.visit(this);
    }
}
