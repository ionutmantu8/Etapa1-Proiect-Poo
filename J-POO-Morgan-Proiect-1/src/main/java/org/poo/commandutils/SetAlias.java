package org.poo.commandutils;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;
import org.poo.userutils.Account;
import org.poo.userutils.User;

import java.util.ArrayList;
@Getter
@Setter
public class SetAlias extends AddAccount implements Visitable {


    public SetAlias(final CommandInput commandInput, final ArrayList<User> users) {
        super(commandInput, users);
    }

    @Override
    public void accept(CommandVisitor visitor) {
        visitor.visit(this);
    }

}
