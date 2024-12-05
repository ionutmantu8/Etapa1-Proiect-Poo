package org.poo.commandutils;

import org.poo.banking.ExchangeRate;
import org.poo.fileio.CommandInput;
import org.poo.userutils.User;

import java.util.ArrayList;

public class SplitPayment extends SendMoney implements Visitable {
    public SplitPayment(final  CommandInput commandInput, final ArrayList<User> usersList,
                        final ArrayList<ExchangeRate> exchangeRates) {
        super(usersList, exchangeRates, commandInput);
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
