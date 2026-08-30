package org.dreambot.behaviour.quests.twilightpromise;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;
import org.dreambot.api.methods.walking.web.node.impl.EntranceWebNode;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.behaviour.training.slayer.SlayerLoadouts;
import org.dreambot.behaviour.training.thieving.GenericPickpocket;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.generic.UseOnFractal;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.quest.Operation;
import org.dreambot.fractals.quest.VarbitRequirement;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.OwnedItems;

import java.util.Arrays;

public class TwilightPromise extends Fractal {
    public TwilightPromise() {
        super(() -> !PaidQuest.TWILIGHTS_PROMISE.isFinished());


        WebFinder web = WebFinder.getWebFinder();
        // add crypt entrance

        EntranceWebNode cryptEntrance = new EntranceWebNode(new Tile(1692, 3088), "Staircase", "Climb-down");
        EntranceWebNode cryptExit = new EntranceWebNode(new Tile(1691, 9492), "Staircase", "Climb-up");

        web.getNearest(cryptEntrance, 30).addDualConnections(cryptEntrance);
        cryptEntrance.addDualConnections(cryptExit);
        web.addWebNode(cryptEntrance);
        // add crypt webnodes
        BasicWebNode b = new BasicWebNode(1694, 9492);
        b.addDualConnections(cryptExit);
        web.addWebNode(b);

        web.createAndAddNode(new Tile(1699, 9494));
        web.createAndAddNode(new Tile(1699, 9505));
        web.createAndAddNode(new Tile(1695, 9509));
        web.createAndAddNode(new Tile(1690, 9509));
        web.createAndAddNode(new Tile(1685, 9514));

        // colosseum webnodes
        EntranceWebNode colosseumExit = new EntranceWebNode(new Tile(1796, 9506), "Stairs", "Exit");
        EntranceWebNode colossumEntance = new EntranceWebNode(new Tile(1796, 3106), "Colosseum entrance", "Enter");
        BasicWebNode basicColoNode = new BasicWebNode(1800, 9506);
        basicColoNode.addDualConnections(colosseumExit);
        colossumEntance.addDualConnections(colosseumExit);
        web.addWebNode(basicColoNode);
        web.getNearest(colossumEntance, 30).addDualConnections(colossumEntance);

        // the HQ
        web.createAndAddNode(new Tile(1645, 3148));

        EntranceWebNode bottomHQEntrance = new EntranceWebNode(new Tile(1638, 3155), "Staircase", "Climb-up");
        EntranceWebNode bottomHQExit = new EntranceWebNode(new Tile(1638, 3155, 1), "Staircase", "Climb-down");
        BasicWebNode bHQWebNode = new BasicWebNode(1644, 3155, 1);
        web.addWebNode(bHQWebNode);

        bottomHQEntrance.addDualConnections(bottomHQExit);
        web.getNearest(bottomHQEntrance, 20).addDualConnections(bottomHQEntrance);

        bottomHQExit.addDualConnections(bHQWebNode);

        EntranceWebNode topHQEntance = new EntranceWebNode(1650, 3155, 1, "Staircase", "Climb-up");
        EntranceWebNode topHQExit = new EntranceWebNode(new Tile(1651, 3155, 2), "Staircase", "Climb-down");

        bHQWebNode.addDualConnections(topHQEntance);
        topHQEntance.addDualConnections(topHQExit);

        BasicWebNode topbasic = new BasicWebNode(1652, 3152, 2);
        topbasic.addDualConnections(topHQExit);
        web.addWebNode(topbasic);

        web.createAndAddNode(new Tile(1652, 3147, 2));
        web.createAndAddNode(new Tile(1646, 3147, 2));
        web.createAndAddNode(new Tile(1645, 3143, 2));
//        web.createAndAddNode(new Tile(1645, 3143, 2));


        VarbitRequirement beenToVarlamore = new VarbitRequirement(9650, 1);

        VarbitRequirement talkedToBazaarKnight = new VarbitRequirement(9829, 1, Operation.GREATER_EQUAL);
        // 2 is first time pickpocketing amulet
        VarbitRequirement finishedBazaarKnight = new VarbitRequirement(9829, 3, Operation.GREATER_EQUAL);

        VarbitRequirement talkedToCothonKnight = new VarbitRequirement(9830, 1, Operation.GREATER_EQUAL);
        VarbitRequirement foundCrate = new VarbitRequirement(9830, 2, Operation.GREATER_EQUAL);
        VarbitRequirement finishedCothonKnight = new VarbitRequirement(9830, 3, Operation.GREATER_EQUAL);

        VarbitRequirement talkedToPubKnights = new VarbitRequirement(9831, 1, Operation.GREATER_EQUAL);
//        VarbitRequirement pubKnightFollowing = new VarplayerRequirement(447, List.of(13393, NpcID.KNIGHT_OF_VARLAMORE_12912), 16);
        VarbitRequirement pubKnightSobered = new VarbitRequirement(9831, 2, Operation.GREATER_EQUAL);
        VarbitRequirement finishedPubKnights = new VarbitRequirement(9831, 3, Operation.GREATER_EQUAL);

        VarbitRequirement talkedToColosseumKnight = new VarbitRequirement(9832, 1, Operation.GREATER_EQUAL);
        VarbitRequirement defeatedColosseumKnight = new VarbitRequirement(9832, 2, Operation.GREATER_EQUAL);
        VarbitRequirement finishedColosseumKnight = new VarbitRequirement(9832, 3, Operation.GREATER_EQUAL);

        this.paintArraySupplier = () -> new String[]{
                "State: " + PaidQuest.TWILIGHTS_PROMISE.getConfigValue(),
                "Bazaar knight: " + finishedBazaarKnight.value(),
                "Cothon knight: " + finishedCothonKnight.value(),
                "Pub knight: " + finishedPubKnights.value(),
                "Colloseum knight: " + finishedColosseumKnight.value(),
        };
        Area nullTile = null;

        Area teomatArea = new Tile(1437, 3171).getArea(40);
        addChildren(
//                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < 43).setSimpleName("43 Prayer req"),

                new TalkToFractal(() -> PlayerSettings.getBitValue(9652) < 3, new Tile(3280, 3412), () -> NPCs.closest("Regulus Cento"))
                        .setDialogueOptions("Let's do it!")
                        .setSimpleName("First time valamore")
                        .setInventoryLoadout(new InventoryLoadout().setStrict(true)),

                new TalkToFractal(() -> PaidQuest.TWILIGHTS_PROMISE.getConfigValue() < 4, new Tile(1687, 3141), () -> NPCs.closest("Ennius Tullus"))
                        .setDialogueOptions("Yes.")
                        .setSimpleName("Start Twilight promise @ Ennius"),

                new TalkToFractal(() -> PaidQuest.TWILIGHTS_PROMISE.getConfigValue() < 8, new Tile(1699, 3087), () -> NPCs.closest("Metzli, Teokan of Ranul"))
                        .setDialogueOptions("to be meeting")
                        .setSimpleName("Talk to Metzli"),

                new TalkToFractal(() -> PaidQuest.TWILIGHTS_PROMISE.getConfigValue() < 12, new Tile(1684, 9505), () -> NPCs.closest("Prince Itzla Arkan"))
                        .setDialogueOptions("head dow")
                        .setSimpleName("Talk to prince"),

                new TalkToFractal(() -> PaidQuest.TWILIGHTS_PROMISE.getConfigValue() < 14, new Tile(1684, 3156), () -> NPCs.closest("Ennius Tullus"))
                        .setDialogueOptions("Yes.")
                        .setSimpleName("Ennius"),

                new TalkToFractal(() -> PaidQuest.TWILIGHTS_PROMISE.getConfigValue() >= 14 && OwnedItems.contains(ItemID.VARLAMORE_CREST) && !Inventory.contains(ItemID.VARLAMORE_CREST),
                        new Tile(1684, 3156),
                        () -> NPCs.closest("Ennius Tullus"))
                        .setDialogueOptions("Yes.")
                        .setDialogueOptions()
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.VARLAMORE_CREST)
                                .setEnabledCondition(() -> OwnedItems.contains(ItemID.VARLAMORE_CREST))
                        )
                        .setSimpleName("Ennius"),

                new GenericPickpocket(() -> finishedBazaarKnight.value() == 1, () -> NPCs.closest(x -> x.getName().contains("Citizen") && x.hasAction("Pickpocket")), new Tile(1682, 3104).getArea(4))
                        .setSleepTime(2400)
                        .setSimpleName("Pickpocket"),

                new TalkToFractal(finishedBazaarKnight::isNotComplete, new Tile(1682, 3104), () -> NPCs.closest("Knight of Varlamore"))
                        .setDialogueOptions("get going")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.VARLAMORE_CREST)
                                .enabledIfOwned()
                        )
                        .setSimpleName("Bazaar Knight"),

                new TalkToFractal(() -> finishedCothonKnight.value() == 1, new Tile(1778, 3149), () -> GameObjects.closest(x -> x.hasAction("Search") && x.getTile().equals(new Tile(1778, 3149))))
                        .setInteraction("Search")
                        .setSimpleName("Search crate"),

                new TalkToFractal(finishedCothonKnight::isNotComplete, new Tile(1746, 3120), () -> NPCs.closest("Knight of Varlamore"))
                        .setSimpleName("Cothon knight"),

                new Fractal(() -> finishedPubKnights.value() == 1).addChildren(
                        new TalkToFractal(() -> {
                            NPC knight = NPCs.closest(x -> x.getId() == 13393 || x.getId() == 13392);
                            return knight != null && !Players.getLocal().equals(knight.getInteractingCharacter());
                        },
                                nullTile,
                                () -> NPCs.closest(x -> x.getId() == 13393 || x.getId() == 13392))
                                .setSimpleName("Get Knight to follow"),

                        new TalkToFractal(() -> true, new Tile(1757, 3069).getArea(2), () -> null)
                                .setPrependLogic(() -> {
                                    NPC knight = NPCs.closest(x -> x.getId() == 13393 || x.getId() == 13392);
                                    if (knight == null) {
                                        if (Walking.shouldWalk()) Walking.walk(1723, 3074); // pub
                                        return true;
                                    }


                                    if (Walking.isRunEnabled()) {
                                        Walking.toggleRun();
                                        return true;
                                    }

                                    Sleep.sleep(1000); // go slow for this drunkard

                                    return false;
                                })
                                .setSimpleName("Walk")

                ).setSimpleName("Fountain"),

                new TalkToFractal(finishedPubKnights::isNotComplete, new Tile(1721, 3075), () -> NPCs.closest("Knight of Varlamore"))
                        .setSimpleName("Pub knight"),

                new TalkToFractal(finishedColosseumKnight::isNotComplete, new Tile(1805, 9522), () -> NPCs.closest("Knight of Varlamore"))
                        .setDialogueOptions("do this") // starts the fight
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_AIR)
                                .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                                .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
                                .addItem(EquipmentSlot.AMULET, ItemID.HOLY_SYMBOL)
                        )
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.SHARK, 1, 6)
                                .addItem(ItemVariants.PRAYER_POTION, 1, 6)
                                .addItem(ItemID.MIND_RUNE, 600)
                                .setEnabledCondition(() -> !Inventory.contains(ItemID.MIND_RUNE))
                                .addItem(ItemID.VARLAMORE_CREST).enabledIfOwned()
                                .setStrictSupplier(() -> Inventory.contains(ItemID.COINS_995))
                        )
                        .setPrependLogic(() -> {
                            if (!Client.isDynamicRegion()) return false;

                            Item prayerPot = ItemVariants.PRAYER_POTION.getItem();
                            if (Skills.getBoostedLevel(Skill.PRAYER) < 5 && prayerPot != null) {
                                prayerPot.interact("Drink");
                                return true;
                            }

                            Item shark = Inventory.get(ItemID.SHARK);
                            if (Skills.getBoostedLevel(Skill.HITPOINTS) < 10 && shark != null) {
                                shark.interact("Eat");
                                return true;
                            }

                            if (!Combat.isAutoRetaliateOn()) Combat.toggleAutoRetaliate(true);

                            if (!Prayers.isActive(Prayer.PROTECT_FROM_MELEE)) {
                                Prayers.toggle(true, Prayer.PROTECT_FROM_MELEE);
                            }


                            NPC tgt = (NPC) Players.getLocal().getInteractingCharacter();
                            if (tgt != null) {
                                String overhead = Arrays.toString(tgt.getCurrentOverheadSpriteIndices());
                                if (overhead.toLowerCase().contains("0")) { // melee overhead
                                    if (!Magic.isAutocasting() || Magic.getAutocastSpell() != Normal.WIND_STRIKE) {
                                        log("Set autocast wind strike");
                                        Magic.setAutocastSpell(Normal.WIND_STRIKE);
                                    }
                                    return true;
                                }

                                if (Magic.isAutocasting() || Magic.isAutocastDefensive()) {
                                    Logger.info("Going melee");
                                    Combat.setCombatStyle(CombatStyle.ATTACK);
                                }
                                return true;
                            }
                            if (Client.isDynamicRegion()) return true;
                            return false;
                        })
                        .setSimpleName("Colo knight"),

                new TalkToFractal(() -> PaidQuest.TWILIGHTS_PROMISE.getConfigValue() == 22, new Tile(1684, 3156), () -> NPCs.closest("Ennius Tullus"))
                        .setSimpleName("Pub knight"),

                new TalkToFractal(() -> PaidQuest.TWILIGHTS_PROMISE.getConfigValue() < 28, new Tile(1648, 3144, 2).getArea(2),
                        () -> GameObjects.closest(x -> x.getTile().equals(new Tile(1648, 3142, 2)) && x.hasAction("Search", "Open")))
                        .setInteraction("Search", "Open")
                        .setPrependLogic(() -> {
                            Item note = Inventory.get(x -> x.hasAction("Read"));
                            if (note != null) {
                                note.interact("Read");
                                Sleep.sleep(2400);
                                return true;
                            }
                            return false;
                        })
                        .setSimpleName("Get letter"),

                new TalkToFractal(() -> PaidQuest.TWILIGHTS_PROMISE.getConfigValue() < 34, new Tile(1684, 3156), () -> NPCs.closest("Ennius Tullus"))
                        .setSimpleName("Bring letter to Ennius"),

                new TalkToFractal(() -> PaidQuest.TWILIGHTS_PROMISE.getConfigValue() == 34, new Tile(1699, 3141), () -> NPCs.closest("Regulus Cento"))
                        .setDialogueOptions("Teomat")
                        .setSimpleName("First time valamore"),

                new UseOnFractal(() -> PaidQuest.TWILIGHTS_PROMISE.getConfigValue() == 36, () -> Inventory.get("Quetzal feed"), () -> NPCs.closest("Renu"), true)
                        .setDialogueOptions("")
                        .setSleepCondition(() -> PaidQuest.TWILIGHTS_PROMISE.getConfigValue() > 36)
                        .setSleepTimeout(6000)
                        .setSimpleName("Feed renu"),

                // travel to teomat, need gear here
                new TalkToFractal(() -> PaidQuest.TWILIGHTS_PROMISE.getConfigValue() == 38 && !teomatArea.contains(Players.getLocal()), new Tile(1697, 3140), () -> NPCs.closest("Renu"))
                        .setDoReachCheck(false)
                        .setInteraction("Travel")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.SHARK, 1, 12)
                        )
                        .setEquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
                        .setPrependLogic(() -> {
                            WidgetChild teomatTravel = Widgets.get(x -> x.hasAction("The Teomat"));
                            if (teomatTravel != null) {
                                teomatTravel.interact();
                                Sleep.sleep(6000);
                                return true;
                            }
                            return false;
                        })
                        .setSimpleName("Travel to teomat, needs gear to fight after"),

                new TalkToFractal(() -> PaidQuest.TWILIGHTS_PROMISE.getConfigValue() < 42, new Tile(1454, 3173), () -> NPCs.closest("Prince Itzla Arkan"))
                        .setSimpleName("Prince in teomat"),
                new TalkToFractal(() -> PaidQuest.TWILIGHTS_PROMISE.getConfigValue() < 44, new Tile(1448, 3196), () -> NPCs.closest("Metzli, Teokan of Ranul"))
                        .setSimpleName("Start fight"),

                new Fractal(() -> PaidQuest.TWILIGHTS_PROMISE.getConfigValue() < 48)
                        .setPrependLogic(() -> {
                            if (Dialogues.inDialogue()) {
                                Dialog.solve("");
                            }

                            if (Skills.getBoostedLevel(Skill.HITPOINTS) < 15 && Inventory.contains(ItemID.SHARK)) {
                                Inventory.interact(ItemID.SHARK, "Eat");
                                return true;
                            }

                            if (Players.getLocal().isInCombat()) {
                                return true;
                            }

                            NPC cultist = NPCs.closest("Cultist");
                            if (cultist != null && cultist.interact("Attack")) {
                                Sleep.sleepUntil(() -> Players.getLocal().isInCombat(), 4400);
                            }
                            return true;
                        })
                        .setSimpleName("Fight cultists"),

                new TalkToFractal(() -> PaidQuest.TWILIGHTS_PROMISE.getConfigValue() < 50, new Tile(1454, 3173), () -> NPCs.closest("Prince Itzla Arkan"))
                        .setSimpleName("Prince in teomat")
        );
    }
}
