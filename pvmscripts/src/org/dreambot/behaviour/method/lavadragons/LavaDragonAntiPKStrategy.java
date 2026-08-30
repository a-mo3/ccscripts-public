package org.dreambot.behaviour.method.lavadragons;

import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.fractals.util.CombatUtil;
import org.dreambot.pktrie.PKTrie;

import java.util.function.Predicate;

public enum LavaDragonAntiPKStrategy {
    SKULLED_IN_COMBAT_RANGE(x -> x.isSkulled() && CombatUtil.canAttackMe(x)),
    SHARED_PKER_LIST(x -> (x.isSkulled() || PKTrie.checkString(x.getName())) && CombatUtil.canAttackMe(x)),
    ;

    final Predicate<Player> pkClassifier;

    LavaDragonAntiPKStrategy(Predicate<Player> pkClassifier) {
        this.pkClassifier = pkClassifier;
    }
}
