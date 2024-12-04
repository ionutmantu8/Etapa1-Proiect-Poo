package org.poo.commandutils;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;
import org.poo.userutils.User;

import java.util.ArrayList;

@Getter
@Setter
public class SetMinBalance extends AddAccount implements Visitable {
    public SetMinBalance(final CommandInput commandInput, final ArrayList<User> users) {
        super(commandInput, users);
    }

    @Override
    public void accept(CommandVisitor visitor) {
        visitor.visit(this);
    }


}
