package org.dreambot.behaviour;

import org.dreambot.Barrows;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.hint.HintArrow;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.WebNodeType;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.data.NpcID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.loadout.LoadoutItem;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.muling.Log;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HandleCrypt extends Fractal {
    public static int BARROWS_REWARDS = 463;
    public static final Area BARROWS_CRYPT = new Area(3520, 9726, 3585, 9664);
    public static final Area CHEST_AREA = new Area(3546, 9699, 3556, 9689);
    //    private final ImmutableList<WidgetInfo> POSSIBLE_SOLUTIONS = ImmutableList.of(
//            WidgetInfo.BARROWS_PUZZLE_ANSWER1,
//            WidgetInfo.BARROWS_PUZZLE_ANSWER2,
//            WidgetInfo.BARROWS_PUZZLE_ANSWER3
//    );
    private final List<Integer> CRYPT_MOBS = Arrays.asList(
            NpcID.BLOODWORM,
            NpcID.SKELETON_1685,
            NpcID.GIANT_CRYPT_SPIDER
    );
    // i think its possible for a brother to be not killed, but ran past in another room so he wont spawn at chest
    // if im right about this we use this to make sure we dont get stuck only opening the chest
    private int openCounter = 0;

    private final int CHEST_ID = 20973;

    public HandleCrypt() {
        this.paintArraySupplier = () -> new String[]{
                "rewards: " + getRewardPotential() + " + " + Arrays.stream(BarrowsBrothers.values())
                        .filter(x -> !x.hasKilled())
                        .mapToInt(x -> x.combatLevel).sum(),
        };
    }

    @Override
    public int onLoop() {
        WebFinder.getWebFinder().disableWebNodeType(WebNodeType.TELEPORT_NODE);
        WidgetChild solution = getSolution();
        if (solution != null) {
            Logger.info("Found solve");
            solution.interact();
            return ReactionGenerator.getNormal();
        }

        if (Equipment.contains(ItemVariants.TRIDENT.getIds())) {
            if (Skills.getRealLevel(Skill.DEFENCE) < ScriptSettings.getSettingsData().barrowsDefTarget) {
                // mage def cast
                if (Combat.getCombatStyle() != CombatStyle.MAGIC_DEFENCE)
                    Combat.setCombatStyle(CombatStyle.MAGIC_DEFENCE);
            } else {
                if (Combat.getCombatStyle() != CombatStyle.MAGIC) Combat.setCombatStyle(CombatStyle.MAGIC);
            }
        }

        Walking.setObstacleSleeping(false);

        if (!Inventory.contains(ItemID.SHARK) && Combat.getHealthPercent() < 50) {
            Logger.info("Resetting no sharks");
            GetLoadout.finished = true;
            KillBrothers.resetBarrowsState();
            return ReactionGenerator.getQuick();
        }

        if (!Inventory.contains(ItemVariants.PRAYER_POTION.getIds()) && Skills.getBoostedLevel(Skill.PRAYER) == 0) {
            Logger.info("Resetting no prayer potions");
            GetLoadout.finished = true;
            KillBrothers.resetBarrowsState();
            return ReactionGenerator.getQuick();
        }

        if (Equipment.isSlotEmpty(EquipmentSlot.ARROWS)) {
            Logger.info("Resetting no arrows");
            GetLoadout.finished = true;
            KillBrothers.resetBarrowsState();
            return ReactionGenerator.getQuick();
        }

        if (KillBrothers.tunnelBrother == null) {
            KillBrothers.tunnelBrother = BarrowsBrothers.DHAROK;
            Logger.error("Tunnel brother is null?! HOW?!");
            return ReactionGenerator.getNormal();
        }

        if (Combat.getHealthPercent() < 50 && Inventory.contains(ItemID.SHARK)) {
            Inventory.interact(ItemID.SHARK);
            Sleep.sleepUntil(() -> Combat.getHealthPercent() > 50, 600);
            return ReactionGenerator.getQuick();
        }

        if (!BARROWS_CRYPT.contains(Players.getLocal()) || Players.getLocal().getZ() == 3) {
            setStatus("Enter crypt");

            // theres some edge case where 5 brothers dead but ones still spawning on top, i think from teleporting up
            NPC brotherNPC = (NPC) HintArrow.getPointed();
            if (brotherNPC != null) {
//                if (Arrays.stream(BarrowsBrothers.values()).allMatch(BarrowsBrothers::hasKilled))
//                    KillBrothers.tunnelBrother = null;
                // fight brother, do switch
                if (ItemVariants.PRAYER_POTION.getItem() != null && Skills.getBoostedLevel(Skill.PRAYER) < BarrowSettings.MIN_PRAYER) {
                    setStatus("Drinking prayer pot...");
                    Inventory.interact(x -> ItemVariants.PRAYER_POTION.contains(x.getID()), "Drink");
                    Sleep.sleepUntil(() -> Skills.getBoostedLevel(Skill.PRAYER) > BarrowSettings.MIN_PRAYER, 1400);
                    return ReactionGenerator.getQuick();
                }


                EquipmentLoadout combatSwitch = BarrowSettings.SWITCH_MAP.get(KillBrothers.tunnelBrother.weakness);
                if (combatSwitch == null) {
                    Logger.error("Combat switch is null???");
                    return ReactionGenerator.getNormal();
                }
                if (!combatSwitch.isFulfilled() && BarrowsBrothers.killedBrothersCount() != 6) {
                    Logger.info("swwitch " + combatSwitch.getMissingItem().getItemId());
                    EquipEvent.Response res = new EquipEvent(
                            combatSwitch.getMissingItems().stream().map(LoadoutItem::getItemId).filter(Inventory::contains).collect(Collectors.toList())
                    ).executed();
                    Logger.info(res);
                    if (res == EquipEvent.Response.INVALID_INPUT) {
                        Logger.info("Invalid input going to GE");
                        WebFinder.getWebFinder().enableWebNodeType(WebNodeType.TELEPORT_NODE);
                        Walking.walk(BankLocation.GRAND_EXCHANGE);
                        return ReactionGenerator.getNormal();
                    }
                    return ReactionGenerator.getQuick();
                }

                if (!Prayers.isActive(KillBrothers.tunnelBrother.prayerStyle)) {
//            Prayers.forceActivate(brother.prayerStyle);
                    Prayers.toggle(true, KillBrothers.tunnelBrother.prayerStyle);
                }

                if (!Players.getLocal().isInCombat()) {
                    brotherNPC.interact("Attack");
                }
                return ReactionGenerator.getNormal();
            }

            Log.info("Entering crypt");
            if (Players.getLocal().getZ() != 3) {
                if (PrayerUtils.isActive(Prayer.PROTECT_FROM_MELEE, Prayer.PROTECT_FROM_MISSILES, Prayer.PROTECT_FROM_MAGIC)) {
                    setStatus("Disabling prayers");
                    PrayerUtils.disableAll(Prayer.PROTECT_FROM_MELEE, Prayer.PROTECT_FROM_MISSILES, Prayer.PROTECT_FROM_MAGIC);
                    return ReactionGenerator.getNormal();
                }

                if (!Players.getLocal().getTile().equals(KillBrothers.tunnelBrother.digTile)) {
                    setStatus("Walking to brothers pile");
                    if (Walking.shouldWalk(6)) Walking.walk(KillBrothers.tunnelBrother.digTile);
                    return ReactionGenerator.getNormal();
                }

                setStatus("Dig to enter barrows jaunt");
                Inventory.interact(ItemID.SPADE, "Dig");
                Sleep.sleepUntil(() -> Players.getLocal().getZ() == 3, 3400);
            }

            if (!KillBrothers.tunnelBrother.tombArea.contains(Players.getLocal())) {
                GameObject stairs = GameObjects.closest("Staircase");
                if (stairs != null && stairs.interact("Climb-up")) {
                    Sleep.sleepUntil(() -> Players.getLocal().getZ() != 3, 4400);
                }
                return ReactionGenerator.getNormal();
            }

            if (Dialogues.inDialogue()) {
                Dialog.solve("Yeah I'm fearless!");
                return ReactionGenerator.getNormal();
            }

            GameObject sarc = GameObjects.closest("Sarcophagus");
            if (sarc != null && sarc.interact("Search")) {
                Sleep.sleepUntil(() -> HintArrow.getPointed() != null, 2400, 100);
            }
            return ReactionGenerator.getNormal();
        }


        NPC brotherNPC = (NPC) HintArrow.getPointed();
        if (brotherNPC != null && !brotherNPC.getName().toLowerCase().contains("grave")) {
            Logger.info("Attacking brother");
            EquipmentLoadout combatSwitch = BarrowSettings.SWITCH_MAP.get(KillBrothers.tunnelBrother.weakness);
            if (combatSwitch == null) {
                Logger.error("Combat switch is null???");
                return ReactionGenerator.getNormal();
            }
//
            if (ItemVariants.PRAYER_POTION.getItem() != null && Skills.getBoostedLevel(Skill.PRAYER) < BarrowSettings.MIN_PRAYER) {
                setStatus("Drinking prayer pot...");
                Inventory.interact(x -> ItemVariants.PRAYER_POTION.contains(x.getID()), "Drink");
                Sleep.sleepUntil(() -> Skills.getBoostedLevel(Skill.PRAYER) > BarrowSettings.MIN_PRAYER, 1400);
                return ReactionGenerator.getQuick();
            }


            if (!Prayers.isActive(KillBrothers.tunnelBrother.prayerStyle)) {
                Prayers.toggle(true, KillBrothers.tunnelBrother.prayerStyle);
            }

            if (!combatSwitch.isFulfilled()) {
                Logger.info("switch " + combatSwitch.getMissingItem().getItemId());
                EquipEvent.Response res = new EquipEvent(
                        combatSwitch.getMissingItems().stream().map(LoadoutItem::getItemId)
                                .filter(Inventory::contains).collect(Collectors.toList())
                ).executed();
                Logger.info(res);

                if (res == EquipEvent.Response.INVALID_INPUT) {
                    Logger.info("Invalid input going to GE");
                    WebFinder.getWebFinder().enableWebNodeType(WebNodeType.TELEPORT_NODE);
                    Walking.walk(BankLocation.GRAND_EXCHANGE);
                    return ReactionGenerator.getNormal();
                }

                return ReactionGenerator.getQuick();
            }

            if (Combat.getHealthPercent() < 50 && Inventory.contains(ItemID.SHARK)) {
                Inventory.interact(ItemID.SHARK);
                Sleep.sleepUntil(() -> Combat.getHealthPercent() > 50, 600);
                return ReactionGenerator.getQuick();
            }

            setStatus("Fighting brother: " + brotherNPC.getName());


            if (!isInteractingWith(Players.getLocal(), brotherNPC)) {
                brotherNPC.interact("Attack");
            }

            if (Players.getLocal().isInCombat()) {
                return ReactionGenerator.getNormal();
            }

            if (!Players.getLocal().isInCombat()) {
                brotherNPC.interact("Attack");
                return ReactionGenerator.getNormal();
            }

            return ReactionGenerator.getNormal();
        }

        if (!BarrowSettings.RANGE_SWITCH.isFulfilled()) { // range is way cheaper
//            new EquipmentLoadoutEvent(BarrowSettings.RANGE_SWITCH).execute();

            EquipEvent.Response res = new EquipEvent(
                    BarrowSettings.RANGE_SWITCH.getMissingItems().stream()
                            .map(LoadoutItem::getItemId)
                            .filter(Inventory::contains)
                            .collect(Collectors.toList())
            ).executed();
            Logger.info("Range switch: " + res);
            if (res == EquipEvent.Response.INVALID_INPUT) {
                Logger.info("Invalid input going to GE");
                WebFinder.getWebFinder().enableWebNodeType(WebNodeType.TELEPORT_NODE);
                Walking.walk(BankLocation.GRAND_EXCHANGE);
                return ReactionGenerator.getNormal();
            }

            return ReactionGenerator.getNormal();
        }

        NPC mobToKill = NPCs.closest(x -> CRYPT_MOBS.contains(x.getID()) && x.canReach() && x.canReach() && x.hasAction("Attack"));
        if (shouldKillMobs() && mobToKill != null) {
            setStatus("Killing mob.");

            if (Combat.getHealthPercent() < 50 && Inventory.contains(ItemID.SHARK)) {
                Logger.info("Eating shark");
                Inventory.interact(ItemID.SHARK);
                Sleep.sleepUntil(() -> Combat.getHealthPercent() > 50, 600);
                return ReactionGenerator.getQuick();
            }

            NPC mobImFighting = NPCs.closest(x -> isInteractingWith(Players.getLocal(), x) && x.canReach());
            if (mobImFighting != null && Players.getLocal().isInCombat()) {
                if (Players.getLocal().getInteractingCharacter() == null) mobImFighting.interact("Attack");
                Logger.info("mob im fighting");
                return ReactionGenerator.getNormal();
            }

            NPC mobAttackingMe = NPCs.closest(x -> isInteractingWith(Players.getLocal(), x) && x.canReach());
            if (mobAttackingMe != null && mobAttackingMe.interact("Attack")) {
                Logger.info("Attack mob attacking me");
                return ReactionGenerator.getNormal();
            }

            if (!Players.getLocal().isInCombat()) {
                Logger.info("not in combat attack");
                mobToKill.interact("Attack");
                return ReactionGenerator.getQuick();
            }

            return ReactionGenerator.getNormal();
        }

        if (!CHEST_AREA.contains(Players.getLocal())) {
            setStatus("Entering chest area");
            if (Walking.shouldWalk()) {
//                Walking.walk(CHEST_AREA.getTile());

                Barrows.lastPath = LocalPathFinder.getLocalPathFinder().calculate(Players.getLocal().getTile(), CHEST_AREA.getRandomTile());
                BarrowsWalkEvent.Response res = new BarrowsWalkEvent(Barrows.lastPath)
                        .setBreakCondition(() -> Combat.getHealthPercent() < 50
                                || ((HintArrow.getPointed() != null && HintArrow.getPointed().canReach() && HintArrow.getPointed().distance() < 10)
                                || (shouldKillMobs() && NPCs.closest(x -> CRYPT_MOBS.contains(x.getID()) && x.canReach() && x.canReach()) != null)))
                        .executed();
                Logger.info("Walked: " + res);

            }
            return ReactionGenerator.getNormal();
        }

        if (Inventory.emptySlotCount() < 4) {
            Inventory.dropAll(ItemID.STEEL_ARROW, ItemID.VIAL);
            Inventory.interact(ItemID.SHARK, "Eat");
            return ReactionGenerator.getQuick();
        }

        if (Inventory.emptySlotCount() < 5 && Inventory.contains(ItemID.SHARK)) {
            Inventory.drop(ItemID.SHARK);
            return ReactionGenerator.getQuick();
        }

        GameObject chest = GameObjects.closest(CHEST_ID);

        if (chest != null) {
            openCounter++;
            if (Arrays.stream(BarrowsBrothers.values()).anyMatch(x -> !x.hasKilled()) && openCounter < 6) {
                Logger.info("A Brother has not been killed, only do an open interact");
                chest.interact("Open");
                Sleep.sleepUntil(() -> HintArrow.getPointed() != null, 3200);
                return ReactionGenerator.getNormal();
            }
            openCounter = 0;
            chest.interact(x -> x.contains("Search") || x.contains("Open"));
            return ReactionGenerator.getNormal();
        }
        return ReactionGenerator.getNormal();
    }

    public static int getRewardPotential() {
        return PlayerSettings.getBitValue(BARROWS_REWARDS);
    }

    public static boolean shouldKillMobs() {
        int remainingBrothers = Arrays.stream(BarrowsBrothers.values())
                .filter(x -> !x.hasKilled())
                .mapToInt(x -> x.combatLevel).sum();
        return getRewardPotential() + remainingBrothers < ScriptSettings.getRewardTarget();
    }

    private boolean isInteractingWith(Player p, NPC target) {
        Character c = p.getCharacterInteractingWithMe();
        Character i = target.getInteractingCharacter();

        return (c != null && c.equals(target)) || (i != null && i.equals(p));
    }

    private WidgetChild getSolution() {
        WidgetChild first = Widgets.get(25, 3);
        if (first == null) {
            return null;
        }

        int target = first.getDisabledMediaType() - 3;
        return Widgets.get(x -> x.getDisabledMediaType() == target);
    }
}
