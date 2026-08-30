package org.dreambot.behaviour.training.agility.wild;

import lombok.Getter;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.behaviour.training.hunter.BlackChinAntiPkNode;

@Getter
public enum BHAgressionMode {
    ON_SMOKE(
            x -> x.canReach() && !x.isAnimating()
                    && x.distance() < 7 && BlackChinAntiPkNode.canAttackMe(x)
                    && !TickClanRag.clanMembers.contains(x.getName())
    ), // tag anyone not in the clan
    SKULLED_ONLY(
            x -> x.isSkulled() && x.canReach() && !x.isAnimating()
                    && x.distance() < 7 && BlackChinAntiPkNode.canAttackMe(x)
                    && !TickClanRag.clanMembers.contains(x.getName())

    ), // tag anyone skulled not in the clan
    PEACFUL(x -> false), // tag no one <3
    ;

    final Filter<Player> filter;

    BHAgressionMode(Filter<Player> filter) {
        this.filter = filter;
    }
}
