package org.poo.commandutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.userutils.User;

import java.util.ArrayList;


public class PrintTransactions {
    public static void printTransactions(final ArrayList<User> users, final CommandInput command,
                                         final ObjectMapper mapper, final ObjectNode node,
                                         final ArrayNode output){
        String email = command.getEmail();
        User user = CommandHelper.findUserByEmail(users, email);

    }
}
