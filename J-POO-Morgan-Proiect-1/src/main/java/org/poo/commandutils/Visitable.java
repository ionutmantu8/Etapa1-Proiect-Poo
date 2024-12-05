package org.poo.commandutils;

public interface Visitable {
    /**
     * Accept method for the visitor pattern.
     * @param visitor the visitor
     */
    void accept(CommandVisitor visitor);
}
