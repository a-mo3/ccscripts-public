package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
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
import org.dreambot.behaviour.training.farming.FarmingBranch;
import org.dreambot.behaviour.training.thieving.DoRoguesDen;
import org.dreambot.behaviour.training.thieving.GenericPickpocket;
import org.dreambot.behaviour.training.thieving.ThievingBranch;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.MasterFarmerSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.text.DecimalFormat;

public class MasterFarmersScript extends PseudoScript implements ItemContainerListener {
    FractalRoot<MasterFarmerSettings> tree = new FractalRoot<>(new MasterFarmerSettings(), getScriptName());

    Area DRAYNOR = new Area(3073, 3256, 3097, 3245);

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);
        tree.setSimpleName("cCMasterFarmers");
        MasterFarmerSettings settings = tree.getSettings();

        MuleOff.LOOT = new int[]{
                // Allotments
//                ItemID.POTATO_SEED,
//                ItemID.ONION_SEED,
//                ItemID.CABBAGE_SEED,
                ItemID.TOMATO_SEED,
                ItemID.SWEETCORN_SEED,
                ItemID.STRAWBERRY_SEED,
                ItemID.WATERMELON_SEED,
                ItemID.SNAPE_GRASS_SEED,

                // hops
                ItemID.BARLEY_SEED,
                ItemID.HAMMERSTONE_SEED,
                ItemID.ASGARNIAN_SEED,
                ItemID.JUTE_SEED,
                ItemID.YANILLIAN_SEED,
                ItemID.KRANDORIAN_SEED,
//                ItemID.WILDBLOOD_SEED,

                // flowers
//                ItemID.MARIGOLD_SEED,
                ItemID.NASTURTIUM_SEED,
                ItemID.ROSEMARY_SEED,
                ItemID.WOAD_SEED,
                ItemID.LIMPWURT_SEED,

                // bushes
//                ItemID.REDBERRY_SEED,
                ItemID.CADAVABERRY_SEED,
                ItemID.DWELLBERRY_SEED,
                ItemID.JANGERBERRY_SEED,
                ItemID.WHITEBERRY_SEED,
                ItemID.POISON_IVY_SEED,

                // special
//                ItemID.MUSHROOM_SPORE,
                ItemID.BELLADONNA_SEED,
                ItemID.CACTUS_SEED,

                // Herbs
                ItemID.GUAM_SEED,
//                ItemID.MARRENTILL_SEED,
                ItemID.TARROMIN_SEED,
                ItemID.HARRALANDER_SEED,
                ItemID.RANARR_SEED,
                ItemID.TOADFLAX_SEED,
                ItemID.IRIT_SEED,
                ItemID.AVANTOE_SEED,
                ItemID.KWUARM_SEED,
                ItemID.SNAPDRAGON_SEED,
                ItemID.CADANTINE_SEED,
                ItemID.LANTADYME_SEED,
                ItemID.DWARF_WEED_SEED,
                ItemID.TORSTOL_SEED,
        };

        tree.addChildren(
                new GetMembershipBranch().setSimpleName("Get Membership"),
                new AutoProggy().setSimpleName("Auto proggy"),
                new AntibanFractal().setSimpleName("Antiban"),

                new EmptyDeathsCoffer().setSimpleName("Empty grave"),

                new Fractal(() -> !PaidQuest.WITCHS_HOUSE.isFinished() && (tree.getSettings().alwaysWitchesHouse
                        || Skills.getRealLevel(Skill.HITPOINTS) < 20))
                        .addChildren(new WitchsHouse().setSimpleName("Witchs house"))
                        .setSimpleName("Witches house"),

                new ThievingBranch(() -> Skills.getRealLevel(Skill.THIEVING) < Math.max(50, settings.thievingTrainingTarget)).setSimpleName("Theiving"),
                new AgilityBranch(() -> settings.getRoguesOutfit && Skills.getRealLevel(Skill.AGILITY) < 50).setSimpleName("Agility fo rogues fit"),
                new FarmingBranch(() -> Skill.FARMING.getLevel() < settings.farmingTarget).setSimpleName("Farming"),
                new DoRoguesDen(() -> Bank.isCached() && !OwnedItems.containsAll(
                        ItemID.ROGUE_BOOTS,
                        ItemID.ROGUE_MASK,
                        ItemID.ROGUE_TOP,
                        ItemID.ROGUE_TROUSERS,
                        ItemID.ROGUE_GLOVES
                )).setSimpleName("Get rogues outfit"),
                new MuleOff().setSimpleName("Mule Off"),
                // todo pickpocket master farmers
                new GenericPickpocket(() -> true,
                        () -> NPCs.closest("Master farmer"),
                        DRAYNOR)
                        .setPrependLogic(() -> {
                            if (Skills.getBoostedLevel(Skill.HITPOINTS) < 6) {
                                Inventory.interact(ItemID.JUG_OF_WINE);
                                return true;
                            }
                            if (Inventory.isFull()) {
                                if (Inventory.contains(x -> x.getId() != ItemID.JUG_OF_WINE && x.getLivePrice() < settings.minLootValue)) {
                                    Logger.info("Dropping all cheap seeds");
                                    Inventory.dropAll(x -> x.getId() != ItemID.JUG_OF_WINE && x.getLivePrice() < settings.minLootValue);
                                    return true;
                                }

                                new BankAllInventoryEvent().execute();
                            }
                            return false;
                        })
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.HAT, ItemID.ROGUE_MASK)
                                .addItem(EquipmentSlot.LEGS, ItemID.ROGUE_TROUSERS)
                                .addItem(EquipmentSlot.HANDS, ItemID.ROGUE_GLOVES)
                                .addItem(EquipmentSlot.FEET, ItemID.ROGUE_BOOTS)
                                .addItem(EquipmentSlot.CHEST, ItemID.ROGUE_TOP)
                        )
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.JUG_OF_WINE, 1, 8)
                                .setRefill(500))
                        .setSimpleName("Pickpocket farmers")
        );
    }


    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) return tree.run();
        return tree.run();
    }

    Timer runtime = new Timer();
    int grossGp = 0;

    @Override
    public String[] getPaintInfo() {
        return new String[]{
                FractalAPI.hierarchy + " " + FractalAPI.status,
                "Combat: " + Players.getLocal().isInCombat()
        };
    }

    @Override
    public String getScriptName() {
        return "cCMasterFarmers";
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
