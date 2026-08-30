package com.ccscripts.cballs.framework;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;

// node that includes scriptnode and branches
public class Node {
    @Getter
    private final List<Node> children = new ArrayList<>();
    final BooleanSupplier acceptCondition;

    public Node(BooleanSupplier acceptCondition) {
        this.acceptCondition = acceptCondition;
    }

    public Node() {
        this.acceptCondition = () -> true;
    }

    boolean isValid() {
        return acceptCondition.getAsBoolean();
    }

    public ScriptNode validChild() {
        if (children.isEmpty()) return null;
        for (Node child : children) {
            if (child.isValid()) {
                // script node overrides this with returning self
                return child.validChild();
            }
        }
        return null;
    }

    public Node addChildren(Node... children) {
        Collections.addAll(this.children, children);
        return this;
    }
}
