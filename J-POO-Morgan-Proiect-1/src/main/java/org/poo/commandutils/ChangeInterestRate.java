package org.poo.commandutils;

import org.poo.fileio.CommandInput;
import org.poo.userutils.User;

import java.util.ArrayList;

public class ChangeInterestRate extends AddAccount implements Visitable {
    public ChangeInterestRate(final ArrayList<User> users, final CommandInput commandInput) {
        super(commandInput, users);
    }

    /**
     * Accept method for the visitor pattern.
     * @param commandVisitor the visitor
     */
    public void accept(final CommandVisitor commandVisitor) {
        commandVisitor.visit(this);
    }
}
