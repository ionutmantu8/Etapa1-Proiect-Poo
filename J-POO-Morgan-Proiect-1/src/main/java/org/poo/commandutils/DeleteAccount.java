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
        User user = CommandHelper.findUserByEmail(users, email);
        Account accountToRemove = null;
        if (user == null) {
            node.put("command", command.getCommand());
            ObjectNode outputNode = node.putObject("output");
            outputNode.put("error", "Account couldn't be deleted - see org.poo.transactions for details");
            outputNode.put("timestamp", command.getTimestamp());
            node.put("timestamp", command.getTimestamp());
            output.add(node);
            return;
        }

        for (Account account : user.getAccounts()) {
            if (account.getIBAN().equals(accountIBAN)) {
                accountToRemove = account;
                break;
            }

        }
        if (accountToRemove != null && accountToRemove.getBalance() != 0) {
            node.put("command", command.getCommand());
            ObjectNode outputNode = node.putObject("output");
            outputNode.put("error", "Account couldn't be deleted - see org.poo.transactions for details");
            outputNode.put("timestamp", command.getTimestamp());
            node.put("timestamp", command.getTimestamp());
            output.add(node);
            return;
        }

        user.getAccounts().remove(accountToRemove);


        node.put("command", command.getCommand());
        ObjectNode outputNode = node.putObject("output");
        if (accountToRemove != null) {
            outputNode.put("success", "Account deleted");
        } else {
            outputNode.put("error", "Account couldn't be deleted - see org.poo.transactions for details");
        }
        outputNode.put("timestamp", command.getTimestamp());
        node.put("timestamp", command.getTimestamp());

        output.add(node);
    }
}
