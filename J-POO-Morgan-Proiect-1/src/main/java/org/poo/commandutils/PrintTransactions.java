package org.poo.commandutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banking.Transcation;
import org.poo.fileio.CommandInput;
import org.poo.userutils.User;

import java.util.ArrayList;
import java.util.Comparator;


public class PrintTransactions {
    public static void printTransactions(final ArrayList<User> users, final CommandInput command,
                                         final ObjectMapper mapper, final ObjectNode node,
                                         final ArrayNode output) {
        node.put("command", command.getCommand());
        String email = command.getEmail();
        User user = CommandHelper.findUserByEmail(users, email);

        if (user != null) {
            ArrayList<Transcation> transactions = user.getTranscations();
            transactions.sort(Comparator.comparingInt(Transcation::getTimestamp));

            ArrayNode transactionsArray = mapper.createArrayNode();
            for (Transcation transaction : transactions) {
                ObjectNode transactionNode = mapper.createObjectNode();

                transactionNode.put("timestamp", transaction.getTimestamp());
                transactionNode.put("description", transaction.getDescription());
                if (transaction.getAmountNotStr() != 0) {
                    transactionNode.put("amount", transaction.getAmountNotStr());
                }
                if (transaction.getCommeriant() != null) {
                    transactionNode.put("commerciant", transaction.getCommeriant());
                }
                if (transaction.getCard() != null) {
                    transactionNode.put("card", transaction.getCard());
                }
                if (transaction.getCardHolder() != null) {
                    transactionNode.put("cardHolder", transaction.getCardHolder());
                }
                if (transaction.getAccount() != null) {
                    transactionNode.put("account", transaction.getAccount());
                }
                if (transaction.getSenderIBAN() != null) {
                    transactionNode.put("senderIBAN", transaction.getSenderIBAN());
                }
                if (transaction.getReceiverIBAN() != null) {
                    transactionNode.put("receiverIBAN", transaction.getReceiverIBAN());
                }
                if (transaction.getAmount() != null) {
                    transactionNode.put("amount", transaction.getAmount());
                }
                if (transaction.getTransferType() != null) {
                    transactionNode.put("transferType", transaction.getTransferType());
                }

                transactionsArray.add(transactionNode);
            }

            node.set("output", transactionsArray);
            node.put("timestamp", command.getTimestamp());

            output.add(node);
        }
    }
}
