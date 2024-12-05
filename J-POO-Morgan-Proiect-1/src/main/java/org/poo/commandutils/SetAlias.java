package org.poo.commandutils;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;
import org.poo.userutils.User;

import java.util.ArrayList;
@Getter
@Setter
public class SetAlias extends AddAccount implements Visitable {


    public SetAlias(final CommandInput commandInput, final ArrayList<User> users) {
        super(commandInput, users);
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
