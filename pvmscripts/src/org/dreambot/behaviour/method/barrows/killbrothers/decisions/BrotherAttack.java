package org.dreambot.behaviour.method.barrows.killbrothers.decisions;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.hint.HintArrow;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.magic.Spell;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.behaviour.method.barrows.BarrowsBrother;
import org.dreambot.behaviour.method.barrows.BarrowsKillBrothersBranch;
import org.dreambot.behaviour.method.barrows.BarrowsLoadout;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;

import java.util.Arrays;

public class BrotherAttack extends TickDecision {
    final BarrowsLoadout loadout;

    public BrotherAttack(BarrowsLoadout loadout) {
        setSimpleName("Brother attack");
        this.loadout = loadout;
    }

    @Override
    public boolean evaluate() {
        BarrowsBrother currentlyIn = Arrays.stream(BarrowsBrother.values())
                .filter(x -> x.tombArea.contains(Players.getLocal()))
                .findAny()
                .orElse(null);

        Character tgt = Players.getLocal().getInteractingCharacter();
        if (currentlyIn == null) {
            log("Failed to find what brother this tomb belongs to");
            return true;
        }

        if (currentlyIn.hasKilled() || currentlyIn == BarrowsKillBrothersBranch.tunnelBrother) {
            log("Exit tomb");
            GameObject stairs = GameObjects.closest("Staircase");
            if (stairs != null && stairs.interact("Climb-up")) {
                Sleep.sleepUntil(() -> Players.getLocal().getZ() != 3, 2400);
            }
            return true;
        }

        Character ourBrother = HintArrow.getPointed();
        if (ourBrother == null) {
            log("No brother found, open sarc");

            WidgetChild text = Widgets.get(x -> x.getParentID() == 229 && x.getText().contains("hidden tunnel, do you want to enter?"));
            if (Dialogues.inDialogue() && text != null) {
                log("This is the tunnel brother " + currentlyIn);
                BarrowsKillBrothersBranch.tunnelBrother = currentlyIn;
                return true;
            }

            GameObject sarc = GameObjects.closest("Sarcophagus");
            if (sarc != null && sarc.interact("Search")) {
                Sleep.sleepUntil(() -> HintArrow.getPointed() != null, 1200, 100);
            }
            return true;
        }

        Spell autocasted = Magic.getAutocastSpell();
        Spell correctSpell = getSpell();
        if (!Equipment.contains(ItemID.MAGIC_SHORTBOW) && (autocasted == null || autocasted != correctSpell)) {
            log("Needs to switch autocast from " + autocasted + " to " + correctSpell);
            Magic.setAutocastSpell(getSpell());
            return true;
        }

        if (currentlyIn.prayerStyle == Prayer.PROTECT_FROM_MAGIC) {
            log("Range switch ahrim");
            loadout.doRangeSwitch();
        } else {
            log("mage switch");
            loadout.doMageSwitch();
        }

        if (tgt == null) {
            log("Not attacking the brother");
            ourBrother.interact("Attack");
        }
        return true;
    }

    public static Spell getSpell() {
        Spell[] allowed = new Spell[]{
                Normal.WIND_STRIKE,
                Normal.WIND_BOLT,
                Normal.WIND_BLAST,
                Normal.WIND_WAVE,
                Normal.WIND_SURGE
        };

        return Arrays.stream(allowed).filter(Magic::canCast).reduce((f, s) -> s).orElse(null);
    }
}
