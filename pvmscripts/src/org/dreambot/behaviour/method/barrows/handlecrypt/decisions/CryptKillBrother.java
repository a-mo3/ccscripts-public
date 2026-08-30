package org.dreambot.behaviour.method.barrows.handlecrypt.decisions;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.hint.HintArrow;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Spell;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.method.barrows.BarrowsBrother;
import org.dreambot.behaviour.method.barrows.BarrowsLoadout;
import org.dreambot.behaviour.method.barrows.handlecrypt.HandleCryptBranch;
import org.dreambot.behaviour.method.barrows.killbrothers.decisions.BrotherAttack;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;

import java.util.Arrays;

public class CryptKillBrother extends TickDecision {

    final BarrowsLoadout loadout;
    public CryptKillBrother(BarrowsLoadout loadout) {
        this.loadout = loadout;
    }

    @Override
    public boolean evaluate() {
        Character cryptBrother = HintArrow.getPointed();
        if (cryptBrother == null) {
            log("Crypt brother is not present");
            return false;
        }

        // eat and drink prayer handled by prior decisions
        BarrowsBrother brother = Arrays.stream(BarrowsBrother.values()).filter(x -> !x.hasKilled()).findFirst().orElse(null);
        if (brother == null) {
            log("Brother null but crypt brother is alive? what is happening?!");
            return false;
        }

        if (brother.prayerStyle == Prayer.PROTECT_FROM_MAGIC) {
            log("Range switch ahrim");
            loadout.doRangeSwitch();
        } else {
            log("mage switch");
            loadout.doMageSwitch();
        }

        if (Skills.getBoostedLevel(Skill.PRAYER) < 5) {
            log("Needs prayer");
            Item prayerPot = ItemVariants.PRAYER_POTION.getItem();
            if (prayerPot != null) {
                prayerPot.interact();
            } else {
                log("No prayer pot");
            }
        }

        if (!Prayers.isActive(brother.prayerStyle)) {
            Prayers.toggle(true, brother.prayerStyle);
        }

        if (!Combat.isAutoRetaliateOn()) {
            Combat.toggleAutoRetaliate(true);
        }

        Spell autocasted = Magic.getAutocastSpell();
        Spell correctSpell = BrotherAttack.getSpell();
        if (!Equipment.contains(ItemID.MAGIC_SHORTBOW) && (autocasted == null || autocasted != correctSpell)) {
            log("Needs to switch autocast from " + autocasted + " to " + correctSpell);
            Magic.setAutocastSpell(BrotherAttack.getSpell());
            return true;
        }

        if (Players.getLocal().getInteractingCharacter() == null) {
            log("Needs to attack crypt brother");
            cryptBrother.interact();
        }
        return true;
    }
}
