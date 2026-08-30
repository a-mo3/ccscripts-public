package org.dreambot.pktrie;

import java.util.HashMap;

public class PKTrieNode {
    public HashMap<Character, PKTrieNode> map = new HashMap<>();
    public boolean isEnd;
}
