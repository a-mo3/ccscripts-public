package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.PvmMain;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.quests.witchshouse.WitchsHouse;
import org.dreambot.behaviour.training.agility.AgilityBranch;
import org.dreambot.behaviour.training.thieving.DoRoguesDen;
import org.dreambot.behaviour.training.thieving.GenericPickpocket;
import org.dreambot.behaviour.training.thieving.ThievingBranch;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.discordwebhook.AutoProggy;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.AbstractResponseEvent;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.ArdyKnightSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.text.DecimalFormat;

public class ArdyKnightsScript extends PseudoScript implements ItemContainerListener {
    FractalRoot<ArdyKnightSettings> tree = new FractalRoot<>(new ArdyKnightSettings(), getScriptName());

    Area DRAYNOR = new Area(3073, 3256, 3097, 3245);
    Area HOUSE_KNIGHT = new Area(
            new Tile(2648, 3291, 0),
            new Tile(2657, 3291, 0),
            new Tile(2658, 3296, 0),
            new Tile(2652, 3300, 0),
            new Tile(2648, 3296, 0));


    Area PALADIN = new Area(
            new Tile(2575, 3301, 0),
            new Tile(2585, 3302, 0),
            new Tile(2586, 3300, 0),
            new Tile(2588, 3300, 0),
            new Tile(2588, 3294, 0),
            new Tile(2585, 3293, 0),
            new Tile(2582, 3288, 0),
            new Tile(2579, 3287, 0),
            new Tile(2575, 3287, 0),
            new Tile(2575, 3291, 0),
            new Tile(2581, 3292, 0),
            new Tile(2581, 3296, 0),
            new Tile(2576, 3296, 0)
    );

    Area MARKET_KNIGHTS = new Area(2651, 3319, 2672, 3294);

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);
        tree.setSimpleName("cCArdyKnightsFarm");
        ArdyKnightSettings settings = tree.getSettings();

        MuleOff.LOOT = new int[]{
                ItemID.CHAOS_RUNE
        };

        PvmMain.coinAllowedAreas.add(HOUSE_KNIGHT);
        // todo add paladin area

        // for inventory events
        AbstractResponseEvent.addGlobalExitCondition(() -> {
            if (Inventory.contains("Coin pouch")) {
                if (Widgets.isOpen()) Widgets.closeAll();
                Inventory.interact("Coin pouch"); // stack is open-all single is open
            }
            return false;
        }, "Coin pouch exit");

        tree.addChildren(
                new GetMembershipBranch().setSimpleName("Get Membership"),

                new EmptyDeathsCoffer().setSimpleName("Empty grave"),

                new Fractal(() -> !PaidQuest.WITCHS_HOUSE.isFinished() && (tree.getSettings().alwaysWitchesHouse
                        || Skills.getRealLevel(Skill.HITPOINTS) < 20))
                        .addChildren(new WitchsHouse().setSimpleName("Witchs house"))
                        .setSimpleName("Witches house"),

                new ThievingBranch(() -> Skills.getRealLevel(Skill.THIEVING) < Math.max(50, settings.thievingTrainingTarget)).setSimpleName("Theiving"),
                new AgilityBranch(() -> Skills.getRealLevel(Skill.AGILITY) < 50).setSimpleName("Agility fo rogues fit"),
                new DoRoguesDen(() -> Bank.isCached() && !OwnedItems.containsAll(
                        ItemID.ROGUE_BOOTS,
                        ItemID.ROGUE_MASK,
                        ItemID.ROGUE_TOP,
                        ItemID.ROGUE_TROUSERS,
                        ItemID.ROGUE_GLOVES
                )).setSimpleName("Get rogues outfit"),
                new MuleOff().setSimpleName("Mule Off"),

                new GenericPickpocket(() -> Skills.getRealLevel(Skill.THIEVING) > settings.paladinsAfter,
                        () -> NPCs.closest(x -> x.getName().equals("Paladin")),
                        PALADIN)
                        .setPrependLogic(() -> {
                            if (Skills.getBoostedLevel(Skill.HITPOINTS) < 6) {
                                Inventory.interact(ItemID.JUG_OF_WINE);
                                return true;
                            }
                            if (Inventory.isFull()) new BankAllInventoryEvent().execute();
                            return false;
                        })
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.HAT, ItemID.ROGUE_MASK)
                                .addItem(EquipmentSlot.LEGS, ItemID.ROGUE_TROUSERS)
                                .addItem(EquipmentSlot.HANDS, ItemID.ROGUE_GLOVES)
                                .addItem(EquipmentSlot.FEET, ItemID.ROGUE_BOOTS)
                                .addItem(EquipmentSlot.CHEST, ItemID.ROGUE_TOP)
                                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                        )
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemVariants.COMBAT_BRACLET)
                                .addItem(ItemID.JUG_OF_WINE, 1, 22)
                                .setRefill(500))
                        .setSimpleName("Paladin"),

                new GenericPickpocket(() -> true,
                        () -> NPCs.closest(x -> x.getName().equals("Knight of Ardougne") && HOUSE_KNIGHT.contains(x)),
                        HOUSE_KNIGHT)
                        .setHopWhenNoTarget(true)
                        .setPrependLogic(() -> {
                            if (Skills.getBoostedLevel(Skill.HITPOINTS) < 6) {
                                Inventory.interact(ItemID.JUG_OF_WINE);
                                return true;
                            }
                            if (Inventory.isFull()) new BankAllInventoryEvent().execute();
                            return false;
                        })
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.HAT, ItemID.ROGUE_MASK)
                                .addItem(EquipmentSlot.LEGS, ItemID.ROGUE_TROUSERS)
                                .addItem(EquipmentSlot.HANDS, ItemID.ROGUE_GLOVES)
                                .addItem(EquipmentSlot.FEET, ItemID.ROGUE_BOOTS)
                                .addItem(EquipmentSlot.CHEST, ItemID.ROGUE_TOP)
                                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                        )
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemVariants.COMBAT_BRACLET)
                                .addItem(ItemID.JUG_OF_WINE, 1, 24)
                                .setRefill(500))
                        .setSimpleName("Pickpocket Knights")
        );
    }


    @Override
    public int onLoop() {

        if (Inventory.count("Coin pouch") > 27) {
            if (Widgets.isOpen()) Widgets.closeAll();
            Inventory.interact("Coin pouch"); // stack is open-all single is open
        }

        if (MyVarps.getTutVarp() < 1000) return tree.run();
        return tree.run();
    }

    Timer runtime = new Timer();
    int grossGp = 0;

    @Override
    public String[] getPaintInfo() {
        return new String[]{
                FractalAPI.hierarchy + " " + FractalAPI.status,
        };
    }

    @Override
    public String getScriptName() {
        return "cCArdyKnightsFarm";
    }

    @Override
    public int getMoneyMade() {
        return grossGp;
    }

    @Override
    public Timer getRuntime() {
        return runtime;
    }

    @Override
    public long getMuleOffTime() {
        return MuleOff.timer == null ? 0 : MuleOff.timer.remaining();
    }

    @Override
    public Fractal getFractal() {
        return tree;
    }

    private String formatTime(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
        DecimalFormat format = new DecimalFormat("00");
        return String.format("%s:%s:%s",
                format.format(hours),
                format.format(minutes),
                format.format(seconds));
    }


    public void onInventoryItemAdded(Item item) {
        if (Widgets.isOpen()) return;
        if (!DRAYNOR.contains(Players.getLocal())) return;
        if (!item.getName().contains("seed")) return;
        grossGp += item.getLivePrice();

    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        if (!DRAYNOR.contains(Players.getLocal())) return;
        if (!existing.getName().contains("seed")) return;
        int quantity = incoming.getAmount() - existing.getAmount();
        if (quantity <= 0) {
            Logger.info("Quantity under zero");
            return;
        }
        int gp = quantity * existing.getLivePrice();
        grossGp += gp;
    }

    @Override
    public void onInventoryItemSwapped(Item incoming, Item outgoing) {
        if (!DRAYNOR.contains(Players.getLocal())) return;
    }
}
