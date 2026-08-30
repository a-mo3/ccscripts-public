package org.dreambot.behaviour.wilddiary;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.behaviour.quests.animalmagnetism.util.SpecialWalker;
import org.dreambot.behaviour.quests.entertheabyss.EnterTheAbyss;
import org.dreambot.behaviour.quests.runemysteries.RuneMysteries;
import org.dreambot.behaviour.training.agility.AgilityBranch;
import org.dreambot.behaviour.training.magic.MagicBranch;
import org.dreambot.behaviour.training.mining.GenericMineLeaf;
import org.dreambot.behaviour.training.mining.MixedMining;
import org.dreambot.behaviour.training.nmz.ConfigurableMeleeTraining;
import org.dreambot.behaviour.training.slayer.behaviour.StandardCombat;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.generic.GoDoFractal;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariant;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.quest.VarbitRequirement;
import org.dreambot.fractals.util.VarplayerRequirement;
import org.dreambot.settings.timing.ReactionGenerator;

public class EasyWildernessDiary extends Fractal {
    static VarplayerRequirement hasntDoneEasyDiary = new VarplayerRequirement(1190, false, 8);

    public EasyWildernessDiary() {
        super(hasntDoneEasyDiary::check);
        setSimpleName("Easy wild diary");

        VarplayerRequirement notLowAlch = new VarplayerRequirement(1192, false, 1);
        VarplayerRequirement notWildyLever = new VarplayerRequirement(1192, false, 2);
        VarplayerRequirement notChaosAltar = new VarplayerRequirement(1192, false, 3);
        VarplayerRequirement notChaosTemple = new VarplayerRequirement(1192, false, 4);
        VarplayerRequirement notKillMammoth = new VarplayerRequirement(1192, false, 5);
        VarplayerRequirement notEarthWarrior = new VarplayerRequirement(1192, false, 6);
        VarplayerRequirement notDemonicPrayer = new VarplayerRequirement(1192, false, 7);
        VarplayerRequirement notEnterKBDLair = new VarplayerRequirement(1192, false, 8);
        VarplayerRequirement notSpiderEggs = new VarplayerRequirement(1192, false, 9);
        VarplayerRequirement notIronOre = new VarplayerRequirement(1192, false, 10);
        VarplayerRequirement notEnterAbyss = new VarplayerRequirement(1192, false, 11);
        VarplayerRequirement notEquipTeamCape = new VarplayerRequirement(1192, false, 12);
        VarbitRequirement firstTimeAbyss = new VarbitRequirement(626, 1);
        Area edgeLever = new Area(3089, 3478, 3093, 3474);
        Area fountainOfRune = new Area(3375, 3895, 3378, 3893);
        Area chaosAltar = new Area(2946, 3823, 2953, 3818);
        Area chaosRunecraftingAltar = new Area(3047, 3605, 3071, 3582);
        Area mammoths = new Area(3157, 3601, 3174, 3586);
        Area earthWarriors = new Area(3117, 9995, 3125, 9987);
        Area redSpiders = new Area(3116, 9959, 3128, 9947);
        Area ironOre = new Area(3071, 3773, 3078, 3768);
        Area demonicRuins = new Area(3287, 3888, 3290, 3883);
        Area zammyMage = new Area(3098, 3566, 3113, 3553);
        Area kbdPoisonSpiders = new Tile(3065, 10254).getArea(6);
        // medium diary req but dont train if its complete
        VarplayerRequirement notWildyAgi = new VarplayerRequirement(1192, false, 16);


        addChildren(
                new AgilityBranch(() -> notWildyAgi.check() && Skills.getRealLevel(Skill.AGILITY) < 40).setSimpleName("Agility"),
                new MixedMining(() -> Skills.getRealLevel(Skill.MINING) < 15).setSimpleName("Mining training"),
                new MagicBranch(() -> Skills.getRealLevel(Skill.MAGIC) < 21).setSimpleName("Magic training"),

                new RuneMysteries().setSimpleName("Rune mysteries"),
                new EnterTheAbyss().setSimpleName("Enter the abyss"),

                new TalkToFractal(notWildyLever::check, edgeLever, () -> GameObjects.closest("Lever"), "Pull")
                        .setDialogueOptions("Yes")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.KNIFE, 2) // bring knife to get out and to low alch at fountain
                                .setStrictSupplier(() -> Inventory.contains(ItemID.COINS_995))
                        ).setSimpleName("Pull wildy lever"),

                new GoDoFractal(notLowAlch::check, fountainOfRune.getCenter(), () -> {
                    Logger.info("Low alch knife now.");
                    Magic.castSpellOn(Normal.LOW_LEVEL_ALCHEMY, Inventory.get(ItemID.KNIFE));
                    return ReactionGenerator.getNormal();
                }).setInventoryLoadout(new InventoryLoadout()
                        .addItem(ItemID.KNIFE, 2)
                        .addItem(ItemID.VARROCK_TELEPORT, 2, 5)
                        .setStrictSupplier(() -> Inventory.contains(ItemID.COINS_995))
                ).setSimpleName("Low alch a knife"),

                new GoDoFractal(notEnterKBDLair::check, kbdPoisonSpiders.getCenter(), () -> {
                    GameObject lever = GameObjects.closest("Lever");
                    if (lever != null) {
                        lever.interact("Pull");
                        Sleep.sleepUntil(() -> !notEnterKBDLair.check(), 2400);
                    }

                    return ReactionGenerator.getNormal();
                })
                        .setInventoryLoadout(new InventoryLoadout().setStrict(true))
                        .setPrependLogic(() -> {
                            if (!SpecialWalker.leaveAvasRoom()) {
                                return true;
                            }
                            if (kbdPoisonSpiders.contains(Players.getLocal())) {
                                if (!Prayers.isActive(Prayer.PROTECT_FROM_MELEE) && Skills.getBoostedLevel(Skill.PRAYER) > 1)
                                    Prayers.toggle(true, Prayer.PROTECT_FROM_MELEE);
                            }

                            return false;
                        })
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.AMULET, ItemVariants.BURNING_AMULET)
                        )
                        .setSimpleName("Go to KBD"),

                new ConfigurableMeleeTraining(() -> Skills.getRealLevel(Skill.ATTACK) < 20)
                        .setSimpleName("20 Atk min for mammoths"),
                new StandardCombat(notKillMammoth::check, mammoths, () -> NPCs.closest("Mammoth"), ItemID.SHARK)
                        .setOverhead(Prayer.PROTECT_FROM_MELEE)
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.SHARK, 1, 20)
                                .setStrictSupplier(() -> Inventory.contains(ItemID.COINS_995))
                        )
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.WEAPON, ItemID.MITHRIL_SCIMITAR)
                        )
                        .setSimpleName("Kill Mammoth"),

                new TalkToFractal(notSpiderEggs::check, redSpiders, () -> GroundItems.closest(ItemID.RED_SPIDERS_EGGS), "Take")
                        .setPrependLogic(() -> {
                            if (Inventory.isFull()) {
                                log("Spider eggs must empty inventory");
                                new BankAllInventoryEvent().execute();
                            }
                            return false;
                        })
                        .setSimpleName("Get 5 spider eggs"),
                new StandardCombat(notEarthWarrior::check, earthWarriors, () -> NPCs.closest("Earth warrior"), ItemID.SHARK)
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.SHARK, 1, 8)
                                .setStrictSupplier(() -> Inventory.contains(ItemID.COINS_995))
                        )
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.WEAPON, ItemID.MITHRIL_SCIMITAR)
                        )
                        .setSimpleName("Kill Earth Warrior"),

                new GenericMineLeaf(notIronOre::check, "iron rocks", ironOre)
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.BRONZE_PICKAXE)
                                .setStrict(true))
                        .setSimpleName("Mine an iron rock"),

                new TalkToFractal(() -> notChaosTemple.check() || notEquipTeamCape.check(), chaosRunecraftingAltar, () -> GameObjects.closest("Mysterious ruins"), "Enter")
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.HAT, ItemID.CHAOS_TIARA)
                        )
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemVariants.AMULET_OF_GLORY)
                                .addItem(ItemID.TEAM1_CAPE)
                                .setEnabledCondition((notEquipTeamCape::check))
                                .setStrictSupplier(() -> !Combat.isInWild())
                        )
                        .setSimpleName("Enter chaos temple")
                        .setPrependLogic(() -> {
                            // do the team cape thang
                            if (notEquipTeamCape.check() && Equipment.isSlotFull(EquipmentSlot.CAPE))
                                Equipment.unequip(EquipmentSlot.CAPE);
                            if (Combat.isInWild() && notEquipTeamCape.check() && Inventory.contains(ItemID.TEAM1_CAPE))
                                Inventory.interact(ItemID.TEAM1_CAPE);
                            return false;
                        }),

                // pray at and enter temple
                new TalkToFractal(notChaosAltar::check, chaosAltar, () -> GameObjects.closest("Chaos Altar"), "Pray-at")
                        .setPrependLogic(() -> {
                            if (Skills.getBoostedLevel(Skill.PRAYER) > 1) {
                                Prayers.toggle(true, Prayer.PROTECT_FROM_MELEE);
                            }

                            return false;
                        })
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.HAT, ItemID.CHAOS_TIARA)
                        )
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemVariants.AMULET_OF_GLORY)
                                .addItem(ItemID.TEAM1_CAPE)
                                .setEnabledCondition((notEquipTeamCape::check))
                                .setStrictSupplier(() -> !Combat.isInWild())
                        )
                        .setSimpleName("Pray at chaos altar"),

                new StandardCombat(notDemonicPrayer::check, demonicRuins, () -> null)
                        .setOverhead(Prayer.PROTECT_FROM_MELEE)
                        .setSimpleName("Get prayer at ruins"),

                new TalkToFractal(notEnterAbyss::check, zammyMage, () -> NPCs.closest("Mage of Zamorak"), "Teleport")
                        .setInventoryLoadout(new InventoryLoadout().setStrict(true))
                        .setEquipmentLoadout(new EquipmentLoadout().setStrict(true))
                        .setSimpleName("Go to abyss"),

                new TalkToFractal(() -> true, new Tile(3117, 3514).getArea(5), () -> NPCs.closest("Lesser Fanatic"))
                        .setSimpleName("Claim reward")
        );
    }
}
