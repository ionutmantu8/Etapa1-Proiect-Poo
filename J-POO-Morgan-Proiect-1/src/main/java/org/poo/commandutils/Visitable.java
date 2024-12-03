package org.poo.commandutils;

public interface Visitable {
    void accept(CommandVisitor visitor);
}
