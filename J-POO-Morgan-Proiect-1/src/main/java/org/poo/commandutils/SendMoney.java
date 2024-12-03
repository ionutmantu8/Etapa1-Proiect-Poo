    package org.poo.commandutils;

    import com.fasterxml.jackson.databind.ObjectMapper;
    import com.fasterxml.jackson.databind.node.ArrayNode;
    import com.fasterxml.jackson.databind.node.ObjectNode;
    import lombok.Getter;
    import lombok.Setter;
    import org.poo.banking.ExchangeRate;
    import org.poo.banking.Transcation;
    import org.poo.fileio.CommandInput;
    import org.poo.userutils.Account;
    import org.poo.userutils.User;

    import java.util.ArrayList;
@Getter
@Setter
    public class SendMoney implements Visitable {
        private final ArrayList<User> users;
        private final CommandInput command;
        private final ArrayList<ExchangeRate> exchangeRates;

        public SendMoney(final ArrayList<User> users, final ArrayList<ExchangeRate> exchangeRates,final CommandInput command) {
            this.users = users;
            this.command = command;
            this.exchangeRates = exchangeRates;
        }


        @Override
        public void accept(CommandVisitor visitor) {
            visitor.visit(this);
        }

    }

