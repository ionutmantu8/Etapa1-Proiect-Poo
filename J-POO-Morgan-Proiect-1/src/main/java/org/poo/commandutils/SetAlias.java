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
public class SetAlias implements Visitable {
    private final CommandInput commandInput;
    private final ArrayList<User> users;

    public SetAlias(final CommandInput commandInput, final ArrayList<User> users) {
        this.commandInput = commandInput;
        this.users = users;
    }

    public static void setAlias(ArrayList<User> users, CommandInput command){
        for(User user : users){
            for(Account account : user.getAccounts()){
                if(account.getIBAN().equals(command.getAccount())){
                    account.setAlias(command.getAlias());
                    break;
                }
            }
        }

    }

    @Override
    public void accept(CommandVisitor visitor) {
        visitor.visit(this);
    }

}
