package org.poo.commandutils;

public interface CommandVisitor {
    void visit (AddAccount command);
    void visit (AddFunds command);
    void visit (CreateCard command);
    void visit (PrintUsers command);
    void visit (DeleteAccount command);
    void visit (CreateOneTimeCard command);
    void visit (DeleteCard command);
    void visit (PayOnline command);
    void visit (SendMoney command);
    void visit (SetAlias command);
    void visit (PrintTransactions command);
}
