package org.poo.commandutils;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;
import org.poo.userutils.Account;
import org.poo.userutils.User;

import java.util.ArrayList;
@Getter
@Setter
public class AddFunds implements Visitable {
    private final CommandInput commandInput;
    private final ArrayList<User> users;

    public AddFunds(CommandInput commandInput, ArrayList<User> users) {
        this.commandInput = commandInput;
        this.users = users;
    }



    @Override
    public void accept(CommandVisitor visitor) {
        visitor.visit(this);
    }



}
