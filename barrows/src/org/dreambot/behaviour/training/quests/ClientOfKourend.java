package org.dreambot.behaviour.training.quests;


import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.data.NpcID;
import org.dreambot.fractals.generic.GoDoFractal;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.generic.UseOnFractal;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.quest.VarbitRequirement;
import org.dreambot.settings.timing.ReactionGenerator;

public class ClientOfKourend extends Fractal {
    VarbitRequirement talkedToLeenz = new VarbitRequirement(5620, 1);
    VarbitRequirement talkedToRegath = new VarbitRequirement(5621, 1);
    VarbitRequirement talkedToMunty = new VarbitRequirement(5622, 1);
    VarbitRequirement talkedToJennifer = new VarbitRequirement(5623, 1);
    VarbitRequirement talkedToHorace = new VarbitRequirement(5624, 1);
    final Tile VEOS_KOUREND = new Tile(1824, 3690, 0);
    final Tile LEENZ_TILE = new Tile(1807, 3726, 0);
    final Tile REGATH_TILE = new Tile(1720, 3724, 0);
    final Tile HORACE_TILE = new Tile(1774, 3589, 0);
    final Tile JENNIFER_TILE = new Tile(1518, 3586, 0);
    final Tile MUNTY_TILE = new Tile(1551, 3752, 0);
    final Tile ALTAR = new Tile(1712, 3883, 0);
    final Area ALTAR_AREA = new Area(1712, 3884, 1719, 3881);

    public ClientOfKourend() {
        this.acceptCondition = () -> !PaidQuest.CLIENT_OF_KOUREND.isFinished();
        this.paintArraySupplier = () -> new String[]{
                "State: " + PaidQuest.CLIENT_OF_KOUREND.getState(),
                "Value: " + PaidQuest.CLIENT_OF_KOUREND.getConfigValue(),
        };
        addChildren(
                new TalkToFractal(() -> !PaidQuest.CLIENT_OF_KOUREND.isStarted(), VEOS_KOUREND, () -> NPCs.closest("Veos"))
                        .setDialogueOptions("Yes.",
                                "Goodbye.",
                                "Sounds interesting! How can I help?",
                                "Can you take me to Great Kourend?",
                                "Have you got any quests for me?",
                                "Let's talk about your client...",
                                "I've lost something you've given me.")
                        // todo staminas should probably be considered, this quest is just running around kourend
                        .setInventoryLoadout(new InventoryLoadout().addItem(ItemID.FEATHER).setStrict(true))
                        .setSimpleName("Get a feather and start"),
                new UseOnFractal(() -> Inventory.containsAll(ItemID.FEATHER, ItemID.ENCHANTED_SCROLL),
                        () -> Inventory.get(ItemID.FEATHER),
                        () -> Inventory.get(ItemID.ENCHANTED_SCROLL)
                ),

                new TalkToFractal(talkedToLeenz::isNotComplete, LEENZ_TILE, () -> NPCs.closest(NpcID.LEENZ))
                        .setDialogueOptionsSupplier(() -> new String[]{
                                // lmfao
                                "What is there to do",
                                Calculations.random(10) % 2 == 0 ? "Can I ask you about" : "Why should I gain favour"
                        })
                        .setSimpleName("Talk to Leenz"),

                new TalkToFractal(talkedToHorace::isNotComplete, HORACE_TILE, () -> NPCs.closest(NpcID.HORACE))
                        .setDialogueOptionsSupplier(() -> new String[]{
                                "What is there to do",
                                Calculations.random(10) % 2 == 0 ? "Can I ask you about" : "Why should I gain favour"
                        })
                        .setSimpleName("Talk to Horace"),

                new TalkToFractal(talkedToMunty::isNotComplete, MUNTY_TILE, () -> NPCs.closest(NpcID.MUNTY))
                        .setDialogueOptionsSupplier(() -> new String[]{
                                "What is there to do",
                                Calculations.random(10) % 2 == 0 ? "Can I ask you about" : "Why should I gain favour"
                        })
                        .setSimpleName("Talk to Munty"),

                new TalkToFractal(talkedToRegath::isNotComplete, REGATH_TILE, () -> NPCs.closest(NpcID.REGATH))
                        .setDialogueOptionsSupplier(() -> new String[]{
                                "What is there to do",
                                Calculations.random(10) % 2 == 0 ? "Can I ask you about" : "Why should I gain favour"
                        })
                        .setSimpleName("Talk to Regath"),

                new TalkToFractal(talkedToJennifer::isNotComplete, JENNIFER_TILE, () -> NPCs.closest(NpcID.JENNIFER))
                        .setDialogueOptionsSupplier(() -> new String[]{
                                "What is there to do",
                                Calculations.random(10) % 2 == 0 ? "Can I ask you about" : "Why should I gain favour"
                        })
                        .setSimpleName("Talk to Jennifer"),


                new TalkToFractal(() -> PaidQuest.CLIENT_OF_KOUREND.getConfigValue() != 4,
                        VEOS_KOUREND, () -> NPCs.closest("Veos"))
                        .setDialogueOptions("Let's talk about your client...")
                        .setSimpleName("Talk to veos"),

                new GoDoFractal(() -> PaidQuest.CLIENT_OF_KOUREND.getConfigValue() == 4, ALTAR, () -> {
                    Item orb = Inventory.get(ItemID.MYSTERIOUS_ORB);
                    if (orb != null) {
                        Inventory.interact(orb, "Activate");
                    }
                    return ReactionGenerator.getNormal();
                }).setEquipmentLoadout(new EquipmentLoadout().addItem(EquipmentSlot.AMULET, ItemVariants.GAMES_NECKLACE))
                        .setInventoryLoadout(new InventoryLoadout().addItem(ItemID.MYSTERIOUS_ORB))
                        .setSimpleName("Ponder the orb")
        );
    }
}
