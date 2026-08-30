package org.dreambot.settings.ui.equipmentpicker.trie;

import java.util.LinkedList;
import java.util.List;

public class Trie {
    TrieNode root = new TrieNode(' ');

    public void insert(String word) {
        TrieNode node = root;
        for (char ch : word.toCharArray()) {
            if (!node.children.containsKey(ch)) node.children.put(ch, new TrieNode(ch));
            node = node.children.get(ch);
        }
        node.isEnd = true;
    }

    public LinkedList<String> autocomplete(String prefix) {
        LinkedList<String> res = new LinkedList<String>();
        TrieNode node = root;
        for (char ch : prefix.toCharArray()) {
            if (node.children.containsKey(ch)) {
                node = node.children.get(ch);
            } else {
                return res;
            }
        }
        helper(node, res, prefix.substring(0, prefix.length() - 1));
        return res;
    }

    void helper(TrieNode node, List<String> res, String prefix) {
        if (node.isEnd) res.add(prefix + node.data);
        for (Character ch : node.children.keySet()) {
            helper(node.children.get(ch), res, prefix + node.data);
        }
    }
}
