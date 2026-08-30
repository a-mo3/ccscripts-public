package org.dreambot.scripts;

import org.dreambot.ChangeAlchWarning;
import org.dreambot.PseudoScript;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.prayer.Prayer;
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
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.quests.impcatcher.ImpCatcher;
import org.dreambot.behaviour.quests.pip.PriestInPeril;
import org.dreambot.behaviour.training.firemaking.BurnLogs;
import org.dreambot.behaviour.training.magic.EnchantDueling;
import org.dreambot.behaviour.training.magic.EnchantRecoils;
import org.dreambot.behaviour.training.nmz.ConfigurableMeleeTraining;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
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
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.GargoyleSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

public class Gargoyle extends PseudoScript implements ItemContainerListener {
    FractalRoot tree = new FractalRoot(new GargoyleSettings(), getScriptName());
    AtomicBoolean isBreaking = new AtomicBoolean(false);
    BooleanSupplier breakingSupplier = () -> isBreaking.get();

    private GargoyleSettings getSettings() {
        return SettingsRepository.getSetting(getScriptName(), new GargoyleSettings());
    }

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);


        WithdrawLoadoutEvent.sellList = new int[]{
                ItemID.AMULET_OF_GLORY
        };
        Logger.info("Init");
        Area gargs = new Area(3430, 3554, 3452, 3531, 2);
        SlayerTaskMap.minLootValue = getSettings().minLootValue;
        tree.setSimpleName("cCGargoyles");
        tree.addChildren(
                new AutoProggy().setSimpleName("Auto proggy"),
                new AntibanFractal().setSimpleName("Antiban"),
                new ReactionSettingsFractal(),
                new PutPetAway(),
                new TutorialTree().setSimpleName("Tutorial island"),
                new EmptyDeathsCoffer().setSimpleName("Empty death"),
                new GetMembershipBranch().setSimpleName("Get Membership"),
                new LampHandler().setSimpleName("Lamp handler"),


//                        new StandardCombat(new Area(2862, 3589, 2880, 3584), "Mountain troll", ItemID.SHARK)
//                                .setLootFilter(x -> LivePrices.get(x.getId()) > 100)
//                        .setCannonTile(2867, 3587)
//                                .setEquipmentLoadout(SlayerLoadouts.MELEE_LOADOUT)
//                                .setInventoryLoadout(SlayerLoadouts.CANNON_LOADOUT)
//                                .setSimpleName("Trolls"),

                new ConfigurableMeleeTraining(() -> Skills.getRealLevel(Skill.STRENGTH) < getSettings().preSlayerCombatTarget)
                        .setSimpleName("Pre Slayer Combat Training"),
                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < getSettings().prayerTarget)
                        .setSimpleName("Prayer Training"),

                new ImpCatcher().setSimpleName("Impcatcher")
                        .setPrependLogic(() -> {
                            if (Client.isDynamicRegion()) {
                                Magic.castSpell(Normal.HOME_TELEPORT);
                                Antiban.sleepUntil(() -> !Client.isDynamicRegion(), 32_000);
                            }
                            return false;
                        }),
                new EnchantRecoils().setSimpleName("Enchant Recoils "),
                new EnchantDueling().setSimpleName("Enchant Duelings "),
                new BurnLogs(() -> Skills.getRealLevel(Skill.FIREMAKING) < 4, 7, ItemID.LOGS)
                        .setSimpleName("Burn logs need it for slayer"),
//                new Crafting(() -> Skills.getRealLevel(Skill.CRAFTING) < 55)
//                        .setSimpleName("Crafting for slayer helm"),
//                new DwarfCannon().setSimpleName("Dwarf cannon"),

                new SlayerBranch(() -> Skills.getRealLevel(Skill.SLAYER) < 75)
                        .setPrependLogic(() -> {
                            if (!Combat.isAutoRetaliateOn()) {
                                if (Widgets.isOpen()) Widgets.closeAll();
                                Combat.toggleAutoRetaliate(true);
                            }

                            return false;
                        })
                        .setSimpleName("Slayer Until Gargoyles unlocked"),

                new ConfigurableMeleeTraining(() -> Skills.getRealLevel(Skill.STRENGTH) < getSettings().postSlayerCombatTarget)
                        .setSimpleName("Post Slayer Combat Training"),
                new MuleOff()
                        .setSimpleName("Mule Off"),

                new PriestInPeril().setSimpleName("Priest In Peril"),

                new StandardCombat(gargs, "Gargoyle", ItemID.SHARK)
                        .setStyleSupplier(() -> CombatStyle.SHARED)
                        .setLootFilter(x -> LivePrices.get(x.getId()) > getSettings().minLootValue
                                || x.getItem().isNoted() || x.getItem().isStackable()
                                || SlayerLoadouts.gargLoot.contains(x.getId())
                        )
                        .setOverhead(getSettings().useGuthans ? null : Prayer.PROTECT_FROM_MELEE)
                        .setEatPercentThreshold(30)
                        .setEquipmentLoadout(getSettings().useGuthans ? SlayerLoadouts.GUTHANS :
                                new EquipmentLoadout(SlayerLoadouts.PRAYER_LOADOUT)
                                        .addItem(EquipmentSlot.HAT, ItemID.NOSE_PEG))
                        .setInventoryLoadout(
                                getSettings().useGuthans ? SlayerLoadouts.FOOD_GUTHANS :
                                        new InventoryLoadout()
                                                .addItem(ItemID.SHARK, 1, 6)
                                                .setRefill(200)
                                                .addItem(ItemVariants.PRAYER_POTION, 8, 8)
                                                .setEnabledCondition(() -> ItemVariants.PRAYER_POTION.getItem() == null) // todo or when ur not at gargoyles
                                                .setRefill(50)
                                                // teleports for Xieve & turael
                                                .addItem(ItemVariants.GAMES_NECKLACE)
                                                .setEnabledCondition(() -> Combat.getCombatLevel() < 85)
                                                .setRefill(5)
                                                .addItem(ItemID.ROCK_HAMMER)
                                                .addItem(ItemID.NOSE_PEG)
                                                .setEnabledCondition(() -> !Equipment.contains(ItemID.NOSE_PEG))
                                                .addItem(ItemID.NATURE_RUNE, 200)
                                                .setEnabledCondition(() -> !Inventory.contains(ItemID.NATURE_RUNE))
                                                .addItem(ItemID.FIRE_RUNE, 600)
                                                .setEnabledCondition(() -> !Inventory.contains(ItemID.FIRE_RUNE))
                                                .addItem(ItemID.FENKENSTRAINS_CASTLE_TELEPORT, 1, 6)
                        )
                        .setAcceptCondition(() -> true)
                        .setPrependLogic(() -> {
                            if (Inventory.isFull()) {
                                new BankAllInventoryEvent().execute();
                                return true;
                            }

                            if (Inventory.contains(x -> SlayerLoadouts.gargAlachables.contains(x.getId()))) {
                                if (Magic.canCast(Normal.HIGH_LEVEL_ALCHEMY)) {
                                    Magic.castSpellOn(Normal.HIGH_LEVEL_ALCHEMY,
                                            Inventory.get(x -> SlayerLoadouts.gargAlachables.contains(x.getId())));
                                    return true;
                                }
                            }

                            if (Magic.isSpellSelected()) Magic.deselect();

                            Character garg = Players.getLocal().getInteractingCharacter();
                            if (!getSettings().unlockedAutoHammer
                                    && garg != null
                                    && garg.getName().toLowerCase().contains("gargoyle")
                                    && garg.getHealthPercent() < 5) {
                                Item rockHammer = Inventory.get(ItemID.ROCK_HAMMER);
                                if (rockHammer != null) {
                                    Logger.info("Hammer gargoyle");
                                    rockHammer.useOn(garg);
                                }
                                return true;
                            }

                            return false;
                        })
                        .setSimpleName("Gargoyles")
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
        Character tgt = local.getInteractingCharacter();
        if (tgt != null) target = tgt.getName();

        return new String[]{
                FractalAPI.hierarchy + " " + FractalAPI.status,
                "target: " + target,
                "tgt me " + Players.getLocal().getCharactersInteractingWithMe().size(),
                "Style " + Combat.getCombatStyle()
        };
    }

    @Override
    public String getScriptName() {
        return "cCGargoyle";
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
        if (Players.getLocal().getZ() != 2) return;
        grossGp += (item.getLivePrice() + 1) * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        Logger.info("item changed");
        if (Players.getLocal().getZ() != 2) return;
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
        if (Players.getLocal().getZ() != 2) return;
        int quantity = incoming.getAmount() - outgoing.getAmount();
        if (quantity <= 0) return;

        grossGp += (incoming.getLivePrice() + 1) * quantity;
    }
}
