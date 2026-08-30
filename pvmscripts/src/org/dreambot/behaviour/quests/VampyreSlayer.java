package org.dreambot.behaviour.quests;

import org.dreambot.CondHelper;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.behaviour.misc.AdvStandardCombat;
import org.dreambot.behaviour.misc.CombatLoadouts;
import org.dreambot.behaviour.misc.SandCrabs;
import org.dreambot.behaviour.training.nmz.ConfigurableMeleeTraining;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;

public class VampyreSlayer extends Fractal {
    public VampyreSlayer() {
        this.acceptCondition = () -> !FreeQuest.VAMPIRE_SLAYER.isFinished();
        this.paintArraySupplier = () -> new String[]{
                "Vampire Slayer: " + FreeQuest.VAMPIRE_SLAYER.getConfigValue()
        };

        Area countArea = new Tile(3077, 9769, 0).getArea(3);

        addChildren(
                SandCrabs.getMelee(() -> Worlds.getCurrent().isMembers() && !ConfigurableMeleeTraining.reachedBase(15, Skill.DEFENCE, Skill.ATTACK, Skill.STRENGTH)).setSimpleName("Base 15s for vampyre slayer"),
                new TalkToFractal(() -> !FreeQuest.VAMPIRE_SLAYER.isStarted(),
                        new Tile(3098, 3268, 0).getArea(5),
                        () -> NPCs.closest("Morgan"))
                        .setDialogueOptions("Yes.", "Ok,")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.BEER, 1, 3)
                                .addItem(ItemID.GARLIC)
                                .addItem(ItemID.HAMMER)
                                .addItem(ItemID.SALMON, 5)
                                .addItem(ItemID.STAKE)
                                .setEnabledCondition(() -> OwnedItems.contains(ItemID.STAKE))
                        )
                        .setEquipmentLoadout(
                                new EquipmentLoadout()
                                        .addItem(EquipmentSlot.WEAPON, ItemID.IRON_SWORD)
                                        .setEnabledCondition(() -> Skills.getRealLevel(Skill.ATTACK) < 20)
                                        .setRefill(5)
                                        .addItem(EquipmentSlot.WEAPON, ItemID.MITHRIL_SWORD)
                                        .setEnabledCondition(() -> CondHelper.skillBetween(Skill.ATTACK, 30, 20))
                                        .setRefill(5)
                                        .addItem(EquipmentSlot.WEAPON, ItemID.ADAMANT_SWORD)
                                        .setEnabledCondition(() -> CondHelper.skillBetween(Skill.ATTACK, 40, 30))
                                        .setRefill(5)
                                        .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_SWORD)
                                        .setEnabledCondition(() -> CondHelper.skillBetween(Skill.ATTACK, 60, 40))
                                        .setRefill(5)
                        )
                        .setPrependLogic(() -> {
                            if (!Combat.isAutoRetaliateOn()) {
                                Combat.toggleAutoRetaliate(true);
                            }
                            return false;
                        })
                        .setPrependLogic(() -> {
                            if (!Combat.isAutoRetaliateOn()) {
                                Combat.toggleAutoRetaliate(true);
                            }
                            return false;
                        })
                        .setSimpleName("Start @ Morgan"),


                new TalkToFractal(() -> !Inventory.contains(ItemID.STAKE),
                        new Tile(3222, 3399, 0).getArea(5),
                        () -> NPCs.closest("Dr Harlow"))
                        .setDialogueOptions("Morgan", "Okay mate.", "Yes.")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.BEER, 1, 3)
                                .addItem(ItemID.GARLIC)
                                .addItem(ItemID.HAMMER)
                                .addItem(ItemID.SALMON, 5)
                                .addItem(ItemID.STAKE)
                                .setEnabledCondition(() -> OwnedItems.contains(ItemID.STAKE))
                        )
                        .setPrependLogic(() -> {
                            String npcChat = Dialogues.getNPCDialogue();
                            if (Bank.isCached() && npcChat != null && npcChat.contains("give you a stake but")) {
                                log("You probably have stake in coffer, forcing an empty");
                                EmptyDeathsCoffer.forceEmpty = true;
                            }
                            return false;
                        })
                        .setSimpleName("Dr Harlow"),

                new AdvStandardCombat(() -> true, countArea, () -> NPCs.closest("Count Draynor"), ItemID.SALMON)
                        .setEquipmentLoadout(CombatLoadouts.SCIMITAR_LOADOUT_F2P)
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.GARLIC)
                                .addItem(ItemID.HAMMER)
                                .addItem(ItemID.STAKE)
                                .addItem(ItemVariants.SUPER_COMBAT_POTION, 2, 2)
                                .setEnabledCondition(() -> !Inventory.contains(x -> ItemVariants.SUPER_COMBAT_POTION.contains(x.getId()))
                                        && Worlds.getCurrent().isMembers())
                                .addItem(ItemID.SALMON, 20)
                                .setRefill(50)
                                .setEnabledCondition(() -> !Inventory.contains(ItemID.SALMON))
                        )

                        .setPrependLogic(() -> {
                            Logger.info("Killing count");
                            if (countArea.contains(Players.getLocal()) && NPCs.closest("Count Draynor") == null) {
                                GameObject coffer = GameObjects.closest("Coffin");
                                if (coffer != null && coffer.interact("Open")) {
                                    Antiban.sleepUntil(() -> NPCs.closest("Count Draynor") != null, 2000);
                                }
                                return true;
                            }

                            return false;
                        }).setSimpleName("Kill count")

        );
    }
}
