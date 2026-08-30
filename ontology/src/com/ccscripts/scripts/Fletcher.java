package com.ccscripts.scripts;

import com.ccscripts.cballs.framework.Node;
import com.ccscripts.cballs.framework.QuickNode;

public class Fletcher {
    Node script = new Node();
    public Fletcher() {
        script.addChildren(
                QuickNode.builder()
//                        .acceptCondition(() -> )
                        .identifier("DepositProducts")
//                        .expectedNextState()
                        .build()
        );
    }
}
