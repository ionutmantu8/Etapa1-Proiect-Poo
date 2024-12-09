package org.poo.paymentutils;

import org.poo.banking.ExchangeRate;
import org.poo.commandutils.CommandVisitor;
import org.poo.commandutils.Visitable;
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
