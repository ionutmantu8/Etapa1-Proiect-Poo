package org.poo.commandutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.userutils.Account;
import org.poo.userutils.User;

import java.util.ArrayList;

public class DeleteAccount implements Visitable {
    private final ArrayList<User> users;
    private final ObjectNode node;
    private final ObjectMapper mapper;
    private final ArrayNode output;
    private final CommandInput command;

    public DeleteAccount(ArrayList<User> users, ObjectNode node, ObjectMapper mapper, ArrayNode output, CommandInput command) {
        this.users = users;
        this.node = node;
        this.mapper = mapper;
        this.output = output;
        this.command = command;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public ObjectNode getNode() {
        return node;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public ArrayNode getOutput() {
        return output;
    }

    public CommandInput getCommand() {
        return command;
    }

    @Override
    public void accept(CommandVisitor visitor) {
        visitor.visit(this);
    }
}
