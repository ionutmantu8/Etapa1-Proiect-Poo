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
     * Visit method for payOnline command
     */
    void visit(PayOnline command);
    /**
     * Visit method for sendMoney command
     */
    void visit(SendMoney command);
    /**
     * Visit method for setAlias command
     */
    void visit(SetAlias command);
    /**
     * Visit method for printTransactions command
     */
    void visit(PrintTransactions command);
    /**
     * Visit method for setMinBalance command
     */
    void visit(SetMinBalance command);
    /**
     * Visit method for checkCardStatus command
     */
    void visit(CheckCardStatus command);
    /**
     * Visit method for changeInterestRate command
     */
    void visit(ChangeInterestRate command);
    /**
     * Visit method for splitPayment command
     */
    void visit(SplitPayment command);
    /**
     * Visit method for report command
     */
    void visit(Report command);
    /**
     * Visit method for spendingsReport command
     */
    void visit(SpendingsReport command);
    /**
     * Visit method for addInterest command
     */
    void visit(AddInterest command);
}
