package org.dreambot.behaviour;


import org.dreambot.api.Client;
import org.dreambot.api.input.Keyboard;
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
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.WebNodeType;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.loadout.LoadoutItem;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.stream.Collectors;

public class KillBrothers extends Fractal implements ChatListener {
    public KillBrothers() {
//        this.minRun = 20;
        Client.getInstance().addEventListener(this);
        this.paintArraySupplier = () -> new String[]{
                "Killed " + BarrowsBrothers.killedBrothersCount() + "/6",
                "Tunnel brother " + tunnelBrother,
                "Dharok " + BarrowsBrothers.DHAROK.hasKilled(),
                "Verac " + BarrowsBrothers.VERAC.hasKilled(),
                "Guthan " + BarrowsBrothers.GUTHAN.hasKilled(),
                "Ahrim " + BarrowsBrothers.AHRIM.hasKilled(),
                "Torag " + BarrowsBrothers.TORAG.hasKilled(),
                "Karil " + BarrowsBrothers.KARIL.hasKilled()
        };

    }

    // the brother that is in the crypt not their tomb
    public static BarrowsBrothers tunnelBrother = null;
    public static boolean hasDug = false;


    //    public static BarrowsBrothers tunnelBrother = BarrowsBrothers.DHAROK;
    @Override
    public boolean isValid() {
        return (BarrowsBrothers.killedBrothersCount() < 5 || tunnelBrother == null) || !hasDug;
    }

    // for handling having killed 6/6 brothers but being outside of crypt because of a reset
    int brotherCycle = 0;
    boolean talkToOldMan;
    final Area EVERY_WHERE_ELSE = new Area(2943, 3529, 3533, 3124);

    @Override
    public int onLoop() {
        if (Dialogues.canEnterInput()) {
            Logger.info("Handle input");
            Keyboard.type(1, true);
            return ReactionGenerator.getNormal();
        }


        if (EVERY_WHERE_ELSE.contains(Players.getLocal())) {
            KillBrothers.resetBarrowsState();
            GetLoadout.finished = true;
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

        if (Players.getLocal().getZ() == 0 && talkToOldMan) {
            if (Dialogues.areOptionsAvailable()) talkToOldMan = false;

            if (Dialogues.inDialogue()) {
                Dialog.solve("I'll be back soon.");
                return ReactionGenerator.getQuick();
            }

            NPC oldMan = NPCs.closest("Strange Old Man");
            if (oldMan != null && oldMan.interact("Talk-to")) {
                Sleep.sleepUntil(Dialogues::inDialogue, 4400);
            }
            return ReactionGenerator.getNormal();
        }

        if (Players.getLocal().getZ() == 0 && Dialogues.inDialogue()) {
            String dialogue = Dialogues.getNPCDialogue();
            Logger.info(dialogue + "");
            if (dialogue != null && dialogue.contains("But we talk first.")) {
                talkToOldMan = true;
            } else {
                Dialog.solve("I'll be back soon.");
            }
            return ReactionGenerator.getQuick();
        }

        // noticed one time a teleport out of a tomb.
        BarrowsBrothers brother = Arrays.stream(BarrowsBrothers.values()).filter(x -> !x.hasKilled() && !x.equals(tunnelBrother)).findFirst().orElse(null);
        if (BarrowsBrothers.killedBrothersCount() == 6)
            brother = BarrowsBrothers.values()[brotherCycle % BarrowsBrothers.values().length];
        if (BarrowsBrothers.killedBrothersCount() == 5) {
            KillBrothers.tunnelBrother = Arrays.stream(BarrowsBrothers.values()).filter(x -> !x.hasKilled()).findFirst().orElse(null);
        }
        if (Players.getLocal().getZ() == 3) hasDug = true;
        if (!hasDug) brother = BarrowsBrothers.DHAROK;
        if (brother == null) {
            Logger.info("brother is null");
            return ReactionGenerator.getNormal();
        }
        Logger.info("Brother " + brother.name);

        if (Players.getLocal().getZ() != 3) {
            if (PrayerUtils.isActive(Prayer.PROTECT_FROM_MELEE, Prayer.PROTECT_FROM_MISSILES, Prayer.PROTECT_FROM_MAGIC)) {
                PrayerUtils.disableAll(Prayer.PROTECT_FROM_MELEE, Prayer.PROTECT_FROM_MISSILES, Prayer.PROTECT_FROM_MAGIC);
                return ReactionGenerator.getNormal();
            }
            if (!Players.getLocal().getTile().equals(brother.digTile)) {
                Walking.walkExact(brother.digTile);
                return ReactionGenerator.getNormal();
            }
            Inventory.interact(ItemID.SPADE, "Dig");
            Sleep.sleepUntil(() -> Players.getLocal().getZ() == 3, 2400);
            return ReactionGenerator.getNormal();
        }

        WidgetChild text = Widgets.get(x -> x.getParentID() == 229 && x.getText().contains("hidden tunnel, do you want to enter?"));
        Logger.info(text + " ");
        // todo ig dreambot doesnt have anything for gettext that isnt npc?
        if (Dialogues.inDialogue() && text != null && text.isVisible()) {
            tunnelBrother = brother;
            BarrowsBrothers finalBrother = brother;
            brother = Arrays.stream(BarrowsBrothers.values()).filter(x -> !x.equals(finalBrother)).findFirst().orElse(BarrowsBrothers.AHRIM);
            // dont return here, let the exiting handle leaving which implicity clears the dialogue
        }

        // leave tomb if you are not in appropriate brothers tomb
        if (!brother.tombArea.contains(Players.getLocal())) {
//            setStatus("Exiting tomb");
            GameObject stairs = GameObjects.closest("Staircase");
            if (stairs != null && stairs.interact("Climb-up")) {
                Sleep.sleepUntil(() -> Players.getLocal().getZ() != 3, 2400);
            }
            return ReactionGenerator.getNormal();
        }

        // right before fighting brother make sure you have resources
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

        // fight brother, do switch
        EquipmentLoadout combatSwitch = BarrowSettings.SWITCH_MAP.get(brother.weakness);
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

        // todo grave handle

        if (Combat.getHealthPercent() < 50 && Inventory.contains(ItemID.SHARK)) {
            Inventory.interact(ItemID.SHARK);
            Sleep.sleepUntil(() -> Combat.getHealthPercent() > 50, 600);
            return ReactionGenerator.getNormal();
        }

        if (ItemVariants.PRAYER_POTION.getItem() != null && Skills.getBoostedLevel(Skill.PRAYER) < BarrowSettings.MIN_PRAYER) {
//            setStatus("Drinking prayer pot...");
            Inventory.interact(x -> ItemVariants.PRAYER_POTION.contains(x.getID()), "Drink");
            Sleep.sleepUntil(() -> Skills.getBoostedLevel(Skill.PRAYER) > BarrowSettings.MIN_PRAYER, 1400);
            return ReactionGenerator.getQuick();
        }

        if (!Prayers.isActive(brother.prayerStyle)) {
//            Prayers.forceActivate(brother.prayerStyle);
            Prayers.toggle(true, brother.prayerStyle);
        }

        NPC brotherNPC = (NPC) HintArrow.getPointed();
        if (brotherNPC == null || !brotherNPC.hasAction("Attack")) {
            GameObject sarc = GameObjects.closest("Sarcophagus");
            if (sarc != null && sarc.interact("Search")) {
                Sleep.sleepUntil(() -> HintArrow.getPointed() != null, 2400, 100);
            }
            return ReactionGenerator.getNormal();
        }

        if (!Players.getLocal().isInCombat()) {
            brotherNPC.interact("Attack");
            return ReactionGenerator.getNormal();
        }


        return ReactionGenerator.getNormal();
    }

    // varbits dont reset until you dig again, so once we loot chest reset the state
    public static void resetBarrowsState() {
        hasDug = false;
        tunnelBrother = null;
    }

    @Override
    public void onMessage(Message message) {
        if (Players.getLocal().getZ() == 3 && message.getMessage().equalsIgnoreCase("you don't find anything.")) {

            // when this fractal isnt valid, is not finding anything in handle crypt
            // set the tunnel brother to null so you start rotating brothers
            // for edge case caused by leaving / logging out after using the door
            if (!this.isValid()) KillBrothers.tunnelBrother = null;

            Logger.info(message.getType());
            Logger.info("not tunnel brother, +1 barrows cycle");
            brotherCycle++;
        }
    }
}