package org.poo.commandutils;

import lombok.Getter;
import org.poo.fileio.CommandInput;
import org.poo.userutils.User;

import java.util.ArrayList;
@Getter
public class AddAccount implements Visitable {
    private final CommandInput commandInput;
    private final ArrayList<User> users;

    public AddAccount(final CommandInput commandInput, final ArrayList<User> users) {
        this.commandInput = commandInput;
        this.users = users;
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
