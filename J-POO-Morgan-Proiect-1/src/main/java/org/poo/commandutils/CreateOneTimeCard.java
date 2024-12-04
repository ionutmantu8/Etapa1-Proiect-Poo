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
public class CreateOneTimeCard extends AddAccount implements Visitable {
    public CreateOneTimeCard(CommandInput commandInput, ArrayList<User> users) {
       super(commandInput, users);
    }


    @Override
    public void accept(CommandVisitor visitor) {
        visitor.visit(this);
    }
}
