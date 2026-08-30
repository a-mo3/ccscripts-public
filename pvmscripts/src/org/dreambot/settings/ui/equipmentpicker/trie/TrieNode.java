package org.dreambot.settings.ui.equipmentpicker.trie;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class TrieNode {
    char data;
    HashMap<Character, TrieNode> children = new HashMap<>();
    boolean isEnd = false;

    TrieNode(char c) {
        this.data = c;
    }
}
