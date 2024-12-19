package org.poo.accountandcardutils;

import lombok.Getter;
import org.poo.commandutils.CommandVisitor;
import org.poo.commandutils.Visitable;
import org.poo.fileio.CommandInput;
import org.poo.userutils.User;

import java.util.List;

@Getter
public class AddAccount implements Visitable {
    private final CommandInput commandInput;
    private final List<User> users;

    public AddAccount(final CommandInput commandInput, final List<User> users) {
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
