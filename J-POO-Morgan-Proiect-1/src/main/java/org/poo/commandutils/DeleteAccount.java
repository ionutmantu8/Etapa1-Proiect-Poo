package org.poo.commandutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.userutils.Account;
import org.poo.userutils.User;

import java.util.ArrayList;

public class DeleteAccount {

    public static void deleteAccount(ArrayList<User> users, CommandInput command, ObjectNode node, ArrayNode output) {
        String email = command.getEmail();
        String accountIBAN = command.getAccount();
        boolean accountDeleted = false;
        User user = AddAccount.findUserByEmail(users, email);
        if (user != null) {
            Account accountToRemove = null;
            for (Account account : user.getAccounts()) {
                if (account.getIBAN().equals(accountIBAN)) {
                    accountToRemove = account;
                    break;
                }
            }
            if (accountToRemove != null) {
                user.getAccounts().remove(accountToRemove);
                accountDeleted = true;
            }
        }

        node.put("command", command.getCommand());
        ObjectNode outputNode = node.putObject("output");
        if (accountDeleted) {
            outputNode.put("success", "Account deleted");
        } else {
            outputNode.put("error", "Account not found");
        }
        outputNode.put("timestamp", command.getTimestamp());
        node.put("timestamp", command.getTimestamp());

        output.add(node);
    }
}
