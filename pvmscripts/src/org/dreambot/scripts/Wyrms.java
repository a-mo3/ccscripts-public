package org.dreambot.scripts;

import org.dreambot.ChangeAlchWarning;
import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.misc.MixedCombat;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.misc.SandCrabs;
import org.dreambot.behaviour.quests.impcatcher.ImpCatcher;
import org.dreambot.behaviour.training.firemaking.BurnLogs;
import org.dreambot.behaviour.training.magic.EnchantDueling;
import org.dreambot.behaviour.training.magic.EnchantRecoils;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.behaviour.training.slayer.Helper;
import org.dreambot.behaviour.training.slayer.SlayerBranch;
import org.dreambot.behaviour.training.slayer.SlayerLoadouts;
import org.dreambot.behaviour.training.slayer.SlayerTaskMap;
import org.dreambot.behaviour.training.slayer.behaviour.StandardCombat;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.TimedShuffleFractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.WyrmSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

public class Wyrms extends PseudoScript implements ItemContainerListener {
    Area turoths = new Area(2714, 10014, 2730, 9994);
    FractalRoot<WyrmSettings> tree = new FractalRoot<>(new WyrmSettings(), getScriptName());
    AtomicBoolean isBreaking = new AtomicBoolean(false);
    BooleanSupplier breakingSupplier = () -> isBreaking.get();
    final Area WYRMS = new Area(1285, 10204, 1255, 10176);

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);


        WithdrawLoadoutEvent.sellList = new int[]{
                ItemID.AMULET_OF_GLORY
        };
        Logger.info("Init");
        SlayerTaskMap.minLootValue = tree.getSettings().minLootValue;
        tree.setSimpleName("cCWyrms");
        tree.addChildren(
                new GetMembershipBranch()
                        .setSimpleName("Get Membership"),

                new TimedShuffleFractal(Calculations.random(40, 125)).addChildren(
                        new MixedCombat(() -> Skills.getRealLevel(Skill.STRENGTH) < tree.getSettings().preSlayerCombatTarget)
                                .setSimpleName("Pre Slayer Combat Training"),
                        new ImpCatcher().setSimpleName("Impcatcher")
                                .setPrependLogic(() -> {
                                    if (Client.isDynamicRegion()) {
                                        Magic.castSpell(Normal.HOME_TELEPORT);
                                        Antiban.sleepUntil(() -> !Client.isDynamicRegion(), 32_000);
                                    }
                                    return false;
                                }),
                        new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < tree.getSettings().prayerTarget && !tree.getSettings().noPrayMode)
                                .setSimpleName("Prayer Training")
                ),

                new EnchantRecoils().setSimpleName("Enchant Recoils "),
                new EnchantDueling().setSimpleName("Enchant Duelings "),
                new BurnLogs(() -> Skills.getRealLevel(Skill.FIREMAKING) < 4, 7, ItemID.LOGS)
                        .setSimpleName("Burn logs need it for slayer"),
//                new Crafting(() -> Skills.getRealLevel(Skill.CRAFTING) < 55)
//                        .setSimpleName("Crafting for slayer helm"),
//                new DwarfCannon().setSimpleName("Dwarf cannon"),

                new SlayerBranch(() -> Skills.getRealLevel(Skill.SLAYER) < 62)
                        .setPrependLogic(() -> {
                            if (!Combat.isAutoRetaliateOn()) {
                                if (Widgets.isOpen()) Widgets.closeAll();
                                Combat.toggleAutoRetaliate(true);
                            }

                            return false;
                        })
                        .setSimpleName("Slayer until Wyrms are unlocked"),

                new TimedShuffleFractal(Calculations.random(40, 160))
                        .addChildren(
                                new MixedCombat(() -> Skills.getRealLevel(Skill.STRENGTH) < tree.getSettings().postSlayerCombatTarget)
                                        .setSimpleName("Post Slayer Combat Training"),
                                SandCrabs.getRange(() -> Skills.getRealLevel(Skill.RANGED) < 70 && tree.getSettings().noPrayMode)
                                        .setSimpleName("Get 70 range for black dhide - no pray mode")
                        ),
                new MuleOff()
                        .setSimpleName("Mule Off"),

                new StandardCombat(WYRMS, "Wyrm", ItemID.SHARK)
                        .setLootFilter(x -> LivePrices.get(x.getId()) > 350 || x.getId() == ItemID.COINS_995 || x.getItem().isStackable())
                        .setOverhead(tree.getSettings().noPrayMode ? null : Prayer.PROTECT_FROM_MAGIC)
                        .setEquipmentLoadout(tree.getSettings().noPrayMode ? new EquipmentLoadout()
                                                                             .addItem(EquipmentSlot.HAT, ItemID.ANCIENT_COIF)
                                                                             .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY)
                                                                             .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS)

                                                                             .addItem(EquipmentSlot.CAPE, ItemID.GUTHIX_CLOAK)
                                                                             .setEnabledCondition(() -> Skills.getRealLevel(Skill.PRAYER) >= 40)

                                // dragon sword or rune sword or mithril
                                                                             .addItem(EquipmentSlot.WEAPON, ItemID.IRON_SCIMITAR)
                                                                             .setEnabledCondition(() -> Skills.getRealLevel(Skill.ATTACK) < 20)

                                                                             .addItem(EquipmentSlot.WEAPON, ItemID.MITHRIL_SCIMITAR)
                                                                             .setEnabledCondition(() -> Helper.skillBetween(Skill.ATTACK, 30, 20))

                                                                             .addItem(EquipmentSlot.WEAPON, ItemID.ADAMANT_SCIMITAR)
                                                                             .setEnabledCondition(() -> Helper.skillBetween(Skill.ATTACK, 40, 30))

                                                                             .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_SCIMITAR)
                                                                             .setEnabledCondition(() -> Helper.skillBetween(Skill.ATTACK, 70, 40))

                                                                             .addItem(EquipmentSlot.WEAPON, ItemID.ABYSSAL_WHIP)
                                                                             .setEnabledCondition(() -> Helper.skillBetween(Skill.ATTACK, 100, 70))
                                // glory
                                                                             .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)

                                                                             .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                                                                             .setRefill(5)

                                                                             .addItem(EquipmentSlot.FEET, ItemID.BOOTS_OF_STONE)

                                                                             .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                                                                             .setEnabledCondition(() -> Combat.getCombatLevel() >= 85)
                                                                             .setRefill(5) // no pray mode end
                                : new EquipmentLoadout(SlayerLoadouts.PRAYER_LOADOUT)
                                  .remove(x -> x.getSlot() == EquipmentSlot.FEET)
                                  .addItem(EquipmentSlot.HAT, ItemID.SARADOMIN_MITRE)
                                  .addItem(EquipmentSlot.FEET, ItemID.BOOTS_OF_STONE)
                        )
                        .setInventoryLoadout(tree.getSettings().noPrayMode ?
                                new InventoryLoadout()
                                .addItem(ItemID.SHARK, 1, 15)
                                .setRefill(200)
                                .setRefill(50)
                                        // teleports for Xieve & turael
                                .addItem(ItemVariants.GAMES_NECKLACE)
                                .setEnabledCondition(() -> Combat.getCombatLevel() < 85)
                                .setRefill(5)
                                .addItem(ItemVariants.SKILLS_NECKLACE)
                                .setRefill(10)
                                .setStrictSupplier(Inventory::isFull)
                                : new InventoryLoadout()
                                  .addItem(ItemID.SHARK, 1, 10)
                                  .setRefill(200)
                                  .addItem(ItemVariants.PRAYER_POTION, 4, 4)
                                  .setEnabledCondition(() -> ItemVariants.PRAYER_POTION.getItem() == null
                                                             || BankLocation.GRAND_EXCHANGE.distance(Players.getLocal().getTile()) < 20)
                                  .setRefill(50)
                                // teleports for Xieve & turael
                                  .addItem(ItemVariants.GAMES_NECKLACE)
                                  .setEnabledCondition(() -> Combat.getCombatLevel() < 85)
                                  .setRefill(5)
                                  .addItem(ItemVariants.SKILLS_NECKLACE)
                                  .setRefill(10)
                                  .setStrictSupplier(Inventory::isFull)
                        )
                        .setPrependLogic(() -> {
                            if (tree.getSettings().buryBones && Inventory.contains(ItemID.WYRM_BONES)) {
                                Inventory.interact(ItemID.WYRM_BONES, "Bury");
                                return true;
                            }

                            if (!WYRMS.contains(Players.getLocal()) && Prayers.isActive(Prayer.PROTECT_FROM_MAGIC)) {
                                Prayers.toggle(false, Prayer.PROTECT_FROM_MAGIC);
                                return true;
                            }

                            return false;
                        })
                        .setSimpleName("Wyrm")
        );
//        new AIAntiban();
    }

    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) return tree.run();
        if (!ChangeAlchWarning.setHighAlchWarning(500_000)) {
            Logger.info("Setting alch warning price");
            return ReactionGenerator.getNormal();
        }

        return tree.run();
    }

    Timer runtime = new Timer();
    int grossGp = 0;
    DecimalFormat df = new DecimalFormat("###,###,###");

    @Override
    public String[] getPaintInfo() {

        String muleOff = "-";
        if (MuleOff.timer != null) muleOff = formatTime(MuleOff.timer.remaining());
        Player local = Players.getLocal();
        String target = "";
        if (local != null) {
            Character tgt = local.getInteractingCharacter();
            if (tgt != null) target = tgt.getName();
        }

        return new String[]{
                FractalAPI.hierarchy + " " + FractalAPI.status,
                "target: " + target,
        };
    }

    @Override
    public String getScriptName() {
        return "cCWyrmsFarm";
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
        Logger.info("item added");
        if (!WYRMS.contains(Players.getLocal())) return;
        grossGp += (item.getLivePrice() + 1) * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        Logger.info("item changed");
        if (!WYRMS.contains(Players.getLocal())) return;
        int quantity = incoming.getAmount() - existing.getAmount();
        if (quantity <= 0) {
            Logger.info("Quantity under zero");
            return;
        }

        grossGp += (incoming.getLivePrice() + 1) * quantity;
    }

    @Override
    public void onInventoryItemSwapped(Item incoming, Item outgoing) {
        Logger.info("item swapped");
        if (!WYRMS.contains(Players.getLocal())) return;
        int quantity = incoming.getAmount() - outgoing.getAmount();
        if (quantity <= 0) return;

        grossGp += (incoming.getLivePrice() + 1) * quantity;
    }
}
