package org.dreambot.behaviour.quests.trollstronghold;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.behaviour.training.magic.MagicBranch;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.quest.Operation;
import org.dreambot.fractals.quest.VarbitRequirement;
import org.dreambot.fractals.util.VarplayerRequirement;
import org.dreambot.webnodes.TrollStrongholdNodes;

public class TrollStronghold extends Fractal {
    static VarplayerRequirement beatenDad = new VarplayerRequirement(317, 20, Operation.GREATER_EQUAL);
    VarplayerRequirement prisonDoorUnlocked = new VarplayerRequirement(317, 30, Operation.GREATER_EQUAL);

    VarbitRequirement freedEadgar = new VarbitRequirement(0, 1);
    VarplayerRequirement freedGodric = new VarplayerRequirement(317, 40);

    public TrollStronghold() {
        // only care to beat dad to get access to gwd
        super(() -> !PaidQuest.TROLL_STRONGHOLD.isFinished());
        TrollStrongholdNodes.init();
        this.paintArraySupplier = () -> new String[]{
                "State " + PaidQuest.TROLL_STRONGHOLD.getConfigValue()
        };
        setSimpleName("Troll Stronghold");
        addChildren(
                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < 43).setSimpleName("43 prayer min"),
                new MagicBranch(() -> Skills.getRealLevel(Skill.MAGIC) < 35).setSimpleName("35 Magic"),

                new TalkToFractal(() -> questState() == 0,
                        new Tile(2896, 3529),
                        () -> NPCs.closest("Denulth"))
                        .setDialogueOptions("How goes your fight with the trolls?",
                                "Is there anything I can do to help?",
                                "I'll get Godric back!",
                                "Yes.")
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.AMULET, ItemVariants.GAMES_NECKLACE))
                        .setSimpleName("Denulth start"),

                // go kill dad
                new DadFight(() -> !beatenDad.check()).setSimpleName("Fight dad"),

                // todo rest of the quest for edgars russe!!!
                // kill troll general for key
                new PrisonKeyFight(() -> !prisonDoorUnlocked.check() && !Inventory.contains(ItemID.PRISON_KEY)),

                new CellKeys(() -> !freedGodric.check() || freedEadgar.isNotComplete()).setSimpleName("Get Cell Keys"),

                new TalkToFractal(() -> true,
                        new Tile(2919, 3574, 0),
                        () -> NPCs.closest("Dunstan"))
                        .setSimpleName("Finish Troll Stronghold")

        );
    }

    private int questState() {
        return PaidQuest.TROLL_STRONGHOLD.getConfigValue();
    }
}
