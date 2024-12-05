package org.poo.commandutils;

public interface CommandVisitor {
    /**
     * Visit method for addAccount command
     */
    void visit(AddAccount command);
    /**
     * Visit method for addFunds command
     */
    void visit(AddFunds command);
    /**
     * Visit method for createCard command
     */
    void visit(CreateCard command);
    /**
     * Visit method for printUsers command
     */
    void visit(PrintUsers command);
    /**
     * Visit method for deleteAccount command
     */
    void visit(DeleteAccount command);
    /**
     * Visit method for createOneTimeCard command
     */
    void visit(CreateOneTimeCard command);
    /**
     * Visit method for deleteCard command
     */
    void visit(DeleteCard command);
    /**
     * Visit method for addAccount command
     */
    void visit(PayOnline command);
    /**
     * Visit method for addAccount command
     */
    void visit(SendMoney command);
    /**
     * Visit method for addAccount command
     */
    void visit(SetAlias command);
    /**
     * Visit method for addAccount command
     */
    void visit(PrintTransactions command);
    /**
     * Visit method for addAccount command
     */
    void visit(SetMinBalance command);
    /**
     * Visit method for addAccount command
     */
    void visit(CheckCardStatus command);
    /**
     * Visit method for addAccount command
     */
    void visit(ChangeInterestRate command);
    /**
     * Visit method for addAccount command
     */
    void visit(SplitPayment command);
}
