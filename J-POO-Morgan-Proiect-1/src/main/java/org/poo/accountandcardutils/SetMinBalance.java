package org.poo.accountandcardutils;

import lombok.Getter;
import lombok.Setter;
import org.poo.commandutils.CommandVisitor;
import org.poo.commandutils.Visitable;
import org.poo.fileio.CommandInput;
import org.poo.userutils.User;

import java.util.List;

@Getter
@Setter
public class SetMinBalance extends AddAccount implements Visitable {
    public SetMinBalance(final CommandInput commandInput, final List<User> users) {
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
