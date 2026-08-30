package org.dreambot.behaviour.quests.icegloves;

import org.apache.log4j.Logger;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.obstacle.impl.DestructableObstacle;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;
import org.dreambot.api.methods.walking.web.node.impl.EntranceWebNode;

public class IceGloveWebnodes {
    private static final Logger log = Logger.getLogger(IceGloveWebnodes.class);
    private static IceGloveWebnodes instance;

    public static void init() {
        if (instance == null) instance = new IceGloveWebnodes();
    }

    private IceGloveWebnodes() {
        WebFinder wf = WebFinder.getWebFinder();
        LocalPathFinder.getLocalPathFinder().addObstacle(new DestructableObstacle("Rock slide", "Mine"));

        EntranceWebNode firstLadder = new EntranceWebNode(2848, 3513, 0, "Ladder", "Climb-down");
        wf.getNearest(new BasicWebNode(2845, 3518)).addDualConnections(firstLadder);

        EntranceWebNode bottomFirstLadder = new EntranceWebNode(2848, 9913, 0, "Ladder", "Climb-up");
        bottomFirstLadder.addDualConnections(firstLadder);

        AbstractWebNode webNode0 = new BasicWebNode(2844, 9913, 0);
        bottomFirstLadder.addDualConnections(webNode0);
        wf.addWebNode(firstLadder);

        // from 1st to second ladder
        AbstractWebNode webNode1 = new BasicWebNode(2839, 9911, 0);
        AbstractWebNode webNode2 = new BasicWebNode(2836, 9905, 0);
        AbstractWebNode webNode3 = new BasicWebNode(2830, 9900, 0);
        AbstractWebNode webNode4 = new BasicWebNode(2821, 9900, 0);
        AbstractWebNode webNode5 = new BasicWebNode(2820, 9907, 0);
        webNode0.addDualConnections(webNode1);
        webNode1.addDualConnections(webNode0);
        webNode1.addDualConnections(webNode2);
        webNode2.addDualConnections(webNode1);
        webNode2.addDualConnections(webNode3);
        webNode3.addDualConnections(webNode2);
        webNode3.addDualConnections(webNode4);
        webNode4.addDualConnections(webNode3);
        webNode4.addDualConnections(webNode5);
        webNode5.addDualConnections(webNode4);

        AbstractWebNode[] webNodes = {webNode0, webNode1, webNode2, webNode3, webNode4, webNode5,};
        WebFinder.getWebFinder().addWebNodes(webNodes);


        EntranceWebNode exitLadder = new EntranceWebNode(2824, 9907, 0, "Ladder", "Climb-up");
        exitLadder.addDualConnections(webNode5);

        EntranceWebNode exitLadderTop = new EntranceWebNode(2824, 3507, 0, "Ladder", "Climb-down");
        exitLadderTop.addDualConnections(exitLadder);

        AbstractWebNode top = new BasicWebNode(2824, 3510, 0);
        exitLadderTop.addDualConnections(top);
        wf.addWebNode(exitLadder);
        wf.addWebNode(top);


        EntranceWebNode reEntryLadder = new EntranceWebNode(2827, 3510, 0, "Ladder", "Climb-down");
        reEntryLadder.addDualConnections(top);

        EntranceWebNode reEntryLadderUnderground = new EntranceWebNode(2827, 9912, 0, "Ladder", "Climb-up");
        reEntryLadderUnderground.addDualConnections(reEntryLadder);

        // back in lair after navigating 2nd ladders
        webNode0 = new BasicWebNode(2827, 9912, 0);
        webNode0.addDualConnections(reEntryLadderUnderground);
        webNode1 = new BasicWebNode(2827, 9917, 0);
        webNode2 = new BasicWebNode(2827, 9925, 0);
        webNode3 = new BasicWebNode(2827, 9932, 0);
        webNode4 = new BasicWebNode(2826, 9935, 0);
        webNode5 = new BasicWebNode(2826, 9943, 0);
        AbstractWebNode webNode6 = new BasicWebNode(2826, 9953, 0);
        AbstractWebNode webNode7 = new BasicWebNode(2828, 9957, 0);
        AbstractWebNode webNode8 = new BasicWebNode(2827, 9964, 0);
        AbstractWebNode webNode9 = new BasicWebNode(2834, 9971, 0);
        AbstractWebNode webNode10 = new BasicWebNode(2844, 9969, 0);
        AbstractWebNode webNode11 = new BasicWebNode(2854, 9969, 0);
        AbstractWebNode webNode12 = new BasicWebNode(2858, 9972, 0);
        AbstractWebNode webNode13 = new BasicWebNode(2866, 9972, 0);
        AbstractWebNode webNode14 = new BasicWebNode(2873, 9972, 0);
        AbstractWebNode webNode15 = new BasicWebNode(2878, 9968, 0);
        AbstractWebNode webNode16 = new BasicWebNode(2884, 9964, 0);
        AbstractWebNode webNode17 = new BasicWebNode(2886, 9957, 0);
        AbstractWebNode webNode18 = new BasicWebNode(2889, 9953, 0);
        AbstractWebNode webNode19 = new BasicWebNode(2892, 9948, 0);
        AbstractWebNode webNode20 = new BasicWebNode(2890, 9941, 0);
        AbstractWebNode webNode21 = new BasicWebNode(2888, 9937, 0);
        AbstractWebNode webNode22 = new BasicWebNode(2883, 9931, 0);
        AbstractWebNode webNode23 = new BasicWebNode(2879, 9927, 0);
        AbstractWebNode webNode24 = new BasicWebNode(2875, 9920, 0);
        AbstractWebNode webNode25 = new BasicWebNode(2871, 9914, 0);
        AbstractWebNode webNode26 = new BasicWebNode(2867, 9911, 0);
        AbstractWebNode webNode27 = new BasicWebNode(2861, 9910, 0);
        AbstractWebNode webNode28 = new BasicWebNode(2856, 9913, 0);
        AbstractWebNode webNode29 = new BasicWebNode(2857, 9915, 0);
        webNode0.addDualConnections(webNode1);
        webNode1.addDualConnections(webNode0);
        webNode1.addDualConnections(webNode2);
        webNode2.addDualConnections(webNode1);
        webNode2.addDualConnections(webNode3);
        webNode3.addDualConnections(webNode2);
        webNode3.addDualConnections(webNode4);
        webNode4.addDualConnections(webNode3);
        webNode4.addDualConnections(webNode5);
        webNode5.addDualConnections(webNode4);
        webNode5.addDualConnections(webNode6);
        webNode6.addDualConnections(webNode5);
        webNode6.addDualConnections(webNode7);
        webNode7.addDualConnections(webNode6);
        webNode7.addDualConnections(webNode8);
        webNode8.addDualConnections(webNode7);
        webNode8.addDualConnections(webNode9);
        webNode9.addDualConnections(webNode8);
        webNode9.addDualConnections(webNode10);
        webNode10.addDualConnections(webNode9);
        webNode10.addDualConnections(webNode11);
        webNode11.addDualConnections(webNode10);
        webNode11.addDualConnections(webNode12);
        webNode12.addDualConnections(webNode11);
        webNode12.addDualConnections(webNode13);
        webNode13.addDualConnections(webNode12);
        webNode13.addDualConnections(webNode14);
        webNode14.addDualConnections(webNode13);
        webNode14.addDualConnections(webNode15);
        webNode15.addDualConnections(webNode14);
        webNode15.addDualConnections(webNode16);
        webNode16.addDualConnections(webNode15);
        webNode16.addDualConnections(webNode17);
        webNode17.addDualConnections(webNode16);
        webNode17.addDualConnections(webNode18);
        webNode18.addDualConnections(webNode17);
        webNode18.addDualConnections(webNode19);
        webNode19.addDualConnections(webNode18);
        webNode19.addDualConnections(webNode20);
        webNode20.addDualConnections(webNode19);
        webNode20.addDualConnections(webNode21);
        webNode21.addDualConnections(webNode20);
        webNode21.addDualConnections(webNode22);
        webNode22.addDualConnections(webNode21);
        webNode22.addDualConnections(webNode23);
        webNode23.addDualConnections(webNode22);
        webNode23.addDualConnections(webNode24);
        webNode24.addDualConnections(webNode23);
        webNode24.addDualConnections(webNode25);
        webNode25.addDualConnections(webNode24);
        webNode25.addDualConnections(webNode26);
        webNode26.addDualConnections(webNode25);
        webNode26.addDualConnections(webNode27);
        webNode27.addDualConnections(webNode26);
        webNode27.addDualConnections(webNode28);
        webNode28.addDualConnections(webNode27);
        webNode28.addDualConnections(webNode29);
        webNode29.addDualConnections(webNode28);

        webNodes = new AbstractWebNode[]{webNode0, webNode1, webNode2, webNode3, webNode4, webNode5, webNode6, webNode7, webNode8, webNode9, webNode10, webNode11, webNode12, webNode13, webNode14, webNode15, webNode16, webNode17, webNode18, webNode19, webNode20, webNode21, webNode22, webNode23, webNode24, webNode25, webNode26, webNode27, webNode28, webNode29,};
        WebFinder.getWebFinder().addWebNodes(webNodes);

        // do last ladders before ice queen

        EntranceWebNode beforeIceQueenExit = new EntranceWebNode(2857, 9917, 0, "Ladder", "Climb-up");
        beforeIceQueenExit.addDualConnections(webNode28);

        EntranceWebNode beforeIceQueenExitAbove = new EntranceWebNode(2857, 3517, 0, "Ladder", "Climb-down");
        beforeIceQueenExitAbove.addDualConnections(beforeIceQueenExit);

        AbstractWebNode beforeIceTop = new BasicWebNode(2858, 3518, 0);
        beforeIceTop.addDualConnections(beforeIceQueenExitAbove);


        EntranceWebNode reEnterIceQueen = new EntranceWebNode(2859, 3519, 0, "Ladder", "Climb-down");
        reEnterIceQueen.addDualConnections(beforeIceTop);

        EntranceWebNode reEnteredIceQueen = new EntranceWebNode(2859, 9919, 0, "Ladder", "Climb-up");
        reEnteredIceQueen.addDualConnections(reEnterIceQueen);
        wf.addWebNode(beforeIceTop);
        wf.addWebNode(reEnterIceQueen);
        wf.addWebNode(beforeIceQueenExitAbove);


        // webnodes leading up to the queen
        webNode0 = new BasicWebNode(2863, 9919, 0);
        webNode0.addDualConnections(reEnteredIceQueen);
        webNode1 = new BasicWebNode(2866, 9922, 0);
        webNode2 = new BasicWebNode(2865, 9927, 0);
        webNode3 = new BasicWebNode(2867, 9935, 0);
        webNode4 = new BasicWebNode(2871, 9940, 0);
        webNode5 = new BasicWebNode(2873, 9945, 0);
        webNode6 = new BasicWebNode(2868, 9951, 0);
        webNode0.addDualConnections(webNode1);
        webNode1.addDualConnections(webNode0);
        webNode1.addDualConnections(webNode2);
        webNode2.addDualConnections(webNode1);
        webNode2.addDualConnections(webNode3);
        webNode3.addDualConnections(webNode2);
        webNode3.addDualConnections(webNode4);
        webNode4.addDualConnections(webNode3);
        webNode4.addDualConnections(webNode5);
        webNode5.addDualConnections(webNode4);
        webNode5.addDualConnections(webNode6);
        webNode6.addDualConnections(webNode5);
        webNodes = new AbstractWebNode[]{webNode0, webNode1, webNode2, webNode3, webNode4, webNode5, webNode6,};
        WebFinder.getWebFinder().addWebNodes(webNodes);
    }

}
