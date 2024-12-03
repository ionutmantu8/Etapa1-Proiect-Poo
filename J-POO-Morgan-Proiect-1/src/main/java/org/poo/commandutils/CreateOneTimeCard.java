package org.poo.commandutils;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;
import org.poo.userutils.Account;
import org.poo.userutils.Card;
import org.poo.userutils.User;
import org.poo.utils.Utils;

import java.util.ArrayList;

@Getter
@Setter
public class CreateOneTimeCard implements Visitable {
    private final CommandInput commandInput;
    private final ArrayList<User> users;

    public CreateOneTimeCard(CommandInput commandInput, ArrayList<User> users) {
        this.commandInput = commandInput;
        this.users = users;
    }


    @Override
    public void accept(CommandVisitor visitor) {
        visitor.visit(this);
    }
}
