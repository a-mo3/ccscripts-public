package org.dreambot.behaviour.woodcutting;

import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.wrappers.interactive.GameObject;

public enum TreeType {
    NORMAL(x -> x.getName().equals("Tree") || x.getName().equals("Evergreen tree")),
//    OAK,
    WILLOW(x -> x.getName().contains("Willow")),
//    WILLOW,
    YEW(x -> x.getName().equals("Yew tree")),
    ;

    final Filter<GameObject> treeFilter;

    TreeType(Filter<GameObject> treeFilter) {
        this.treeFilter = treeFilter;
    }
}
