package org.dreambot.behaviour.quests;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.misc.SandCrabs;
import org.dreambot.behaviour.training.crafting.CraftingBranch;
import org.dreambot.behaviour.training.magic.ConfigurableMagicBranch;
import org.dreambot.behaviour.training.slayer.behaviour.StandardCombat;
import org.dreambot.behaviour.training.woodcutting.GenericChopLeaf;
import org.dreambot.behaviour.training.woodcutting.MixedChopping;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.GoDoFractal;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.OwnedItems;

public class LostCity extends Fractal {
    Area ZOMBIE_AREA = new Area(2839, 9771, 2849, 9760);
    Area DRAMEN_TREES = new Area(2852, 9741, 2868, 9730);
    Area ENTRANA_DUNGEON_ENTRANCE = new Area(2817, 3375, 2822, 3372);
    Tile SPIRIT_SAFESPOT = new Tile(2859, 9731);
    Timer spellTimer = new Timer(2000);
    Area ZANARIS_SHED = new Area(3202, 3170, 3205, 3167);

    public LostCity() {
        super(() -> !PaidQuest.LOST_CITY.isFinished());

        AbstractWebNode webNode0 = new BasicWebNode(2841, 9765, 0);
        AbstractWebNode webNode1 = new BasicWebNode(2843, 9755, 0);
        AbstractWebNode webNode2 = new BasicWebNode(2845, 9747, 0);
        AbstractWebNode webNode3 = new BasicWebNode(2854, 9750, 0);
        AbstractWebNode webNode4 = new BasicWebNode(2859, 9748, 0);
        AbstractWebNode webNode5 = new BasicWebNode(2860, 9741, 0);
        AbstractWebNode webNode6 = new BasicWebNode(2859, 9735, 0);
        webNode0.addDualConnections(webNode1);
        webNode1.addDualConnections(webNode0);
        webNode1.addDualConnections(webNode2);
        webNode2.addDualConnections(webNode1);
        webNode2.addDualConnections(webNode3);
        webNode3.addDualConnections(webNode2);
        webNode3.addDualConnections(webNode4);
        webNode4.addDualConnections(webNode3);
        webNode4.addDualConnections(webNode5);
        webNode5.addDualConnections(webNode4);
        webNode5.addDualConnections(webNode6);
        webNode6.addDualConnections(webNode5);

        AbstractWebNode[] webNodes = {webNode0, webNode1, webNode2, webNode3, webNode4, webNode5, webNode6,};
        WebFinder.getWebFinder().addWebNodes(webNodes);

        setSimpleName("Lost City");
        addChildren(
                new MixedChopping(() -> Skills.getRealLevel(Skill.WOODCUTTING) < 36).setSimpleName("Wc to 36"),

                new ConfigurableMagicBranch(() -> Skills.getRealLevel(Skill.MAGIC) < 39)
                        .setSimpleName("Need 39 magic"),

                SandCrabs.getMelee(() -> Skills.getRealLevel(Skill.HITPOINTS) < 20)
                        .setSimpleName("Minmum 20hp for this quest"),

                new CraftingBranch(() -> Skills.getRealLevel(Skill.CRAFTING) < 31)
                        .setSimpleName("31 crafting for lost city"),

                new TalkToFractal(() -> questState() == 0,
                        new Tile(3151, 3207, 0),
                        () -> NPCs.closest("warrior"))
                        .setDialogueOptions("camped out here", "makes you think", "hidden how are", "like you don't",
                                "Yes.")
                        .setInventoryLoadout(new InventoryLoadout().addItem(ItemID.MITHRIL_AXE))
                        .setSimpleName("Warrior"),

                new Fractal(() -> questState() == 1).setSimpleName("Find Shamus")
                        .addChildren(
                                new GenericChopLeaf(
                                        () -> NPCs.closest("Shamus") == null,
                                        new Tile(3138, 3212, 0).getArea(17),
                                        x -> x.hasAction("Chop"))
                                        .setAction("Chop")
                                        .setInventoryLoadout(new InventoryLoadout().addItem(ItemID.MITHRIL_AXE))
                                        .setSimpleName("Knock shamus from tree"),

                                new TalkToFractal(
                                        () -> true,
                                        new Tile(3138, 3212, 0),
                                        () -> NPCs.closest("Shamus"))
                                        .setDialogueOptions("shed")
                                        .setSimpleName("Talk to shamus")
                        ),

                new GoDoFractal(() -> OwnedItems.contains(ItemID.DRAMEN_STAFF),
                        ZANARIS_SHED.getCenter(),
                        null
                )
                        .setSimpleName("Finish by going to zanaris")
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.WEAPON, ItemID.DRAMEN_STAFF)
                        ),

                // cut staff
                new Fractal(() -> OwnedItems.count(ItemID.DRAMEN_BRANCH) >= 1)
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.DRAMEN_BRANCH)
                                .addItem(ItemID.KNIFE)
                        )
                        .setAfterLoadouts(() -> {
                            if (Widgets.isOpen()) Widgets.closeAll();
                            Inventory.combine(ItemID.KNIFE, ItemID.DRAMEN_BRANCH);
                            Sleep.sleepUntil(() -> Inventory.contains(ItemID.DRAMEN_STAFF), 4400);
                            return true;
                        })
                        .setSimpleName("Cut staff"),

                new Fractal(Client::isInCutscene)
                        .setSimpleName("Cutscene"),
                // ensure in entrana
                new TalkToFractal(() -> !inEntrana(),
                        new Tile(3048, 3234, 0),
                        () -> NPCs.closest("Monk of Entrana"))
                        .setInteraction("Take-boat")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.SHARK, 1, 20)
                                .setRefill(75)
                                .addItem(ItemID.AIR_RUNE, 250)
                                .setRefill(750)
                                .addItem(ItemID.EARTH_RUNE, 250)
                                .setRefill(750)
                                .addItem(ItemID.CHAOS_RUNE, 250)
                                .setRefill(750)
                                .addItem(ItemID.COINS_995, 500)
                                .addItem(ItemID.VARROCK_TELEPORT, 1, 3)
                                .addItem(ItemID.KNIFE, 1)
                        )
                        // stop walking while traveling on boat
                        .setPrependLogic(() -> Client.isInCutscene() || Client.isDynamicRegion())
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                                .setStrict(true))
                        .setSimpleName("Go to Entrana"),

                new TalkToFractal(() -> !ENTRANA_DUNGEON.contains(Players.getLocal()),
                        ENTRANA_DUNGEON_ENTRANCE,
                        () -> GameObjects.closest("Ladder"))
                        .setInteraction("Climb-down")
                        .setDialogueOptions("risk")
                        .setSimpleName("Enter Dungeon"),

                // kill zombies for an axe
                new StandardCombat(() -> !Inventory.contains(ItemID.BRONZE_AXE),
                        ZOMBIE_AREA,
                        () -> NPCs.closest("Zombie"),
                        ItemID.SHARK)
                        .setLootFilter(x -> x.getId() == ItemID.BRONZE_AXE)
                        .setEatPercentThreshold(75)
                        .setPrependLogic(() -> {
                            Character c = Players.getLocal().getCharacterInteractingWithMe();
                            if (c != null && spellTimer.finished()) {
                                Magic.castSpellOn(Normal.CRUMBLE_UNDEAD, c);
                                spellTimer.reset();
                            }

                            return false;
                        })
                        .setSimpleName("Kill zombie for an axe"),

                new Fractal(() -> NPCs.closest("Tree spirit") != null)
                        .setPrependLogic(() -> {
                            // find safespot tile and sit on it
                            if (!SPIRIT_SAFESPOT.equals(Players.getLocal().getTile())) {
                                Walking.walkExact(SPIRIT_SAFESPOT);
                                return true;
                            }
                            // cast earth strike
                            NPC spirit = NPCs.closest("Tree spirit");
                            if (spirit != null && spellTimer.finished()) {
                                Magic.castSpellOn(Normal.CRUMBLE_UNDEAD, spirit);
                                spellTimer.reset();
                            }

                            return false;
                        })
                        .setSimpleName("Safespot tree spirit"),

                // chop tree and fight tree spirit
                new GenericChopLeaf(() -> true,
                        DRAMEN_TREES,
                        x -> x.getName().equalsIgnoreCase("Dramen tree"))
                        .setSimpleName("Chop a dramen branch")

                        .setPrependLogic(() -> {
                            if (Skills.getBoostedLevel(Skill.HITPOINTS) < 11) {
                                Inventory.interact(ItemID.SHARK, "Eat");
                                Sleep.sleep(600);
                            }
                            return false;
                        })

        );
    }

    public static final Area ENTRANA = new Area(2796, 3393, 2883, 3328);
    public static final Area ENTRANA_DUNGEON = new Area(2816, 9782, 2875, 9729);

    private boolean inEntrana() {
        return ENTRANA.contains(Players.getLocal()) || ENTRANA_DUNGEON.contains(Players.getLocal());
    }

    private int questState() {
        return PaidQuest.LOST_CITY.getConfigValue();
    }
}
