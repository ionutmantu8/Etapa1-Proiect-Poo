    package org.poo.paymentutils;

    import lombok.Getter;
    import lombok.Setter;
    import org.poo.banking.ExchangeRate;
    import org.poo.commandutils.CommandVisitor;
    import org.poo.commandutils.Visitable;
    import org.poo.fileio.CommandInput;
    import org.poo.userutils.User;

    import java.util.ArrayList;
@Getter
@Setter
    public class SendMoney implements Visitable {
        private final ArrayList<User> users;
        private final CommandInput command;
        private final ArrayList<ExchangeRate> exchangeRates;

        public SendMoney(final ArrayList<User> users, final ArrayList<ExchangeRate> exchangeRates,
                         final CommandInput command) {
            this.users = users;
            this.command = command;
            this.exchangeRates = exchangeRates;
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

