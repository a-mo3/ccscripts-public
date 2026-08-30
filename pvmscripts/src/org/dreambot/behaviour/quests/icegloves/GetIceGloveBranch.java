package org.dreambot.behaviour.quests.icegloves;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.behaviour.GetOff330;
import org.dreambot.behaviour.training.magic.MagicBranch;
import org.dreambot.behaviour.training.mining.MixedMining;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.fractals.Fractal;

import java.util.function.Supplier;

public class GetIceGloveBranch extends Fractal {
    public GetIceGloveBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Get ice gloves");
        IceGloveWebnodes.init();
        addChildren(
                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < 43).setSimpleName("Unlock prot melee"),
                new GetOff330(x -> x.isNormal() && x.getWorld() != 401 && x.isMembers() && x.getMinimumLevel() < Skills.getTotalLevel()),
                new MixedMining(() -> Skills.getRealLevel(Skill.MINING) < 50).setSimpleName("50 mining to get past rock"),
                new MagicBranch(() -> Skills.getRealLevel(Skill.MAGIC) < 13).setSimpleName("Unlock fire strike"),
                new KillIceQueen(() -> true).setSimpleName("Kill ice queen")
        );
    }
}
