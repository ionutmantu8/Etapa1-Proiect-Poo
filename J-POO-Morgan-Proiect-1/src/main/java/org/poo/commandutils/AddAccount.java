package org.poo.commandutils;

import lombok.Getter;
import org.poo.fileio.CommandInput;
import org.poo.userutils.User;

import java.util.ArrayList;
@Getter
public class AddAccount implements Visitable {
    private final CommandInput commandInput;
    private final ArrayList<User> users;

    public AddAccount(CommandInput commandInput, ArrayList<User> users) {
        this.commandInput = commandInput;
        this.users = users;
    }



    @Override
    public void accept(CommandVisitor visitor) {
        visitor.visit(this);
    }
}
