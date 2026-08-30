package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.alerts.Alerts;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.behaviour.GetOff330;
import org.dreambot.behaviour.method.moonsofperil.GrabFrozenItem;
import org.dreambot.behaviour.method.moonsofperil.MoonsOfPerilBranch;
import org.dreambot.behaviour.method.moonsofperil.MoonsPrayFlick;
import org.dreambot.behaviour.misc.FixBarrows;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.misc.MuleOffItem;
import org.dreambot.behaviour.quests.childrenofthesun.ChildrenOfTheSun;
import org.dreambot.behaviour.quests.fishingcontest.FishingBranch;
import org.dreambot.behaviour.quests.fishingcontest.FishingFractal;
import org.dreambot.behaviour.quests.impcatcher.ImpCatcher;
import org.dreambot.behaviour.quests.perilousmoon.PerilousMoon;
import org.dreambot.behaviour.quests.twilightpromise.TwilightPromise;
import org.dreambot.behaviour.training.construction.ConstructionBranch;
import org.dreambot.behaviour.training.cooking.CookingBranch;
import org.dreambot.behaviour.training.firemaking.BurnLogs;
import org.dreambot.behaviour.training.herblore.HerbloreBranch;
import org.dreambot.behaviour.training.hunter.HunterBranch;
import org.dreambot.behaviour.training.magic.EnchantRecoils;
import org.dreambot.behaviour.training.nmz.ConfigurableMeleeTraining;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.behaviour.training.runecraft.RuneCraftingBranch;
import org.dreambot.behaviour.training.slayer.SlayerBranch;
import org.dreambot.behaviour.training.slayer.SlayerTaskMap;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.AbstractEvent;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.MoonsOfPerilsSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

public class MoonsOfPerilScript extends PseudoScript implements ItemContainerListener, ChatListener {
    FractalRoot<MoonsOfPerilsSettings> tree = new FractalRoot<>(new MoonsOfPerilsSettings(), getScriptName());
    AtomicBoolean isBreaking = new AtomicBoolean(false);
    BooleanSupplier breakingSupplier = () -> isBreaking.get();

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);


        Logger.info("Init");
        SlayerTaskMap.minLootValue = 1000;
        final Area SHRIMP_AREA = new Area(3240, 3159, 3246, 3141);
        final Area NAGUA_AREA = new Area(1370, 9570, 1382, 9553, 0);
        tree.setSimpleName("cCMoonsOfPeril");
        AbstractEvent.globalInterruptCondition = () -> Inventory.contains("Coin pouch");

        MuleOff.LOOT = new int[]{
                ItemID.FIRE_RUNE,
                ItemID.COPPER_ORE,
                ItemID.MITHRIL_ORE,
                ItemID.SILVER_ORE,
                ItemID.COAL,
                ItemID.IRON_ORE,
                ItemID.COSMIC_RUNE,
                ItemID.SULPHUR_BLADES,
                ItemID.DEATH_RUNE,
                ItemID.NATURE_RUNE,
                ItemID.TIN_ORE,
                ItemID.LOOP_HALF_OF_KEY,
                ItemID.TOOTH_HALF_OF_KEY,
                ItemID.SHIELD_LEFT_HALF,
                ItemID.BLACK_MASK,
                ItemID.CHAOS_RUNE,

                ItemID.ECLIPSE_ATLATL,
                ItemID.ECLIPSE_MOON_HELM,
                ItemID.ECLIPSE_MOON_CHESTPLATE,
                ItemID.ECLIPSE_MOON_TASSETS,
                ItemID.BLOOD_MOON_HELM,
                ItemID.BLOOD_MOON_CHESTPLATE,
                ItemID.BLOOD_MOON_TASSETS,

                ItemID.BLUE_MOON_SPEAR,
                ItemID.BLUE_MOON_HELM,
                ItemID.BLUE_MOON_CHESTPLATE,
                ItemID.BLUE_MOON_TASSETS,

                ItemID.ATLATL_DART,
                ItemID.WYRMLING_BONES,
                ItemID.SWAMP_TAR,
                ItemID.WATER_ORB,
                ItemID.SUPERCOMPOST,
                ItemID.SOFT_CLAY,
                ItemID.GRIMY_HARRALANDER,
                ItemID.GRIMY_IRIT_LEAF,
                ItemID.MAPLE_SEED,
                ItemID.YEW_SEED,

        };
        WithdrawLoadoutEvent.sellList = MuleOff.LOOT;

        MuleOff.muleOffItems = new MuleOffItem[]{
                new MuleOffItem(ItemID.DUAL_MACUAHUITL, () -> tree.getSettings().useDualMacs, 1),
                new MuleOffItem(ItemID.DUAL_MACUAHUITL, () -> !tree.getSettings().useDualMacs, 0),
                new MuleOffItem(ItemID.WARRIOR_RING, () -> true, 1), // just incase multiple set brought
                new MuleOffItem(ItemID.ABYSSAL_WHIP, () -> true, 1), // just incase multiple set brought
                new MuleOffItem(ItemID.TOKTZKETXIL_OBSIDIAN_SHIELD, () -> true, 1), // just incase multiple set brought
                new MuleOffItem(ItemID.GLACIAL_TEMOTLI, () -> true, 1), // just incase multiple set brought
                new MuleOffItem(ItemID.DRAGON_BOOTS, () -> true, 1), // just incase multiple set brought

                // training supplies, mule off but not sell for money
                new MuleOffItem(ItemID.EMERALD_RING),
                new MuleOffItem(ItemID.SAPPHIRE_RING),
                new MuleOffItem(ItemID.TELEPORT_TO_HOUSE)
        };

        MoonsPrayFlick.enabled = tree.getSettings().prayerFlicking;

        tree.addChildren(
                new GetMembershipBranch()
                        .setSimpleName("Get Membership"),
                new EmptyDeathsCoffer().setSimpleName("Death coffer"),

                new ConfigurableMeleeTraining(() -> Skills.getRealLevel(Skill.STRENGTH) < tree.getSettings().preSlayerCombatTarget)
                        .setCleanAfterAccomplished(true)
                        .setSimpleName("Pre Slayer Sandcrabs"),
//                new MixedCombat(() -> Skills.getRealLevel(Skill.STRENGTH) < tree.getSettings().preSlayerCombatTarget)
//                        .setSimpleName("Pre Slayer Combat Training"),

                new Fractal(() -> !PaidQuest.TWILIGHTS_PROMISE.isFinished() && Skills.getRealLevel(Skill.MAGIC) < 27).addChildren(
                        new ImpCatcher().setSimpleName("Impcatcher"),
                        new EnchantRecoils().setSimpleName("Enchant Recoils until 27")
                ).setSimpleName("Training some mage for twilight promise"),

                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < Math.min(43, tree.getSettings().prayerTarget))
                        .setCleanAfterAccomplished(true)
                        .setSimpleName("Prayer Training"),
                new GetOff330(GetOff330.MEMBERS_WORLD_FILTER).setSimpleName("Get off 330"),

                new HerbloreBranch(() -> Skills.getRealLevel(Skill.HERBLORE) < Math.max(38, tree.getSettings().herbloreTarget), false)
                        .setCleanAfterAccomplished(true)
                        .setPrependLogic(() -> {
                            if (Client.isDynamicRegion()) {
                                Magic.castSpell(Normal.HOME_TELEPORT);
                                Antiban.sleepUntil(() -> !Client.isDynamicRegion(), 34_000);
                                return true;
                            }

                            if (Worlds.getCurrentWorld() == 330) {
                                WorldHopper.hopWorld(Worlds.getRandomWorld(x -> x.isNormal() && x.getWorld() != 401
                                        && x.isMembers() && x.getMinimumLevel() < Skills.getTotalLevel()
                                ));
                                return true;
                            }
                            return false;
                        })
                        // todo hop off 330
                        .setSimpleName("Herblore training (min 38)"),

                new BurnLogs(() -> Skills.getRealLevel(Skill.FIREMAKING) < 4, 7, ItemID.LOGS)
                        .setCleanAfterAccomplished(true)
                        .setSimpleName("Burn logs need it for slayer"),
                new SlayerBranch(() -> Skills.getRealLevel(Skill.SLAYER) < 48)
                        .setCleanAfterAccomplished(true)
                        .setPrependLogic(() -> {
                            if (!Combat.isAutoRetaliateOn()) {
                                if (Widgets.isOpen()) Widgets.closeAll();
                                Combat.toggleAutoRetaliate(true);
                            }

                            return false;
                        })
                        .setSimpleName("Slayer until naguas are unlocked"),

                new HunterBranch(() -> Skills.getRealLevel(Skill.HUNTER) < 20)
                        .setCleanAfterAccomplished(true)
                        .setSimpleName("Hunter training to 20"),

                new RuneCraftingBranch(() -> Skills.getRealLevel(Skill.RUNECRAFTING) < 20)
                        .setCleanAfterAccomplished(true)
                        .setSimpleName("Rune crafting"),

                new FishingFractal(() -> Skills.getRealLevel(Skill.FISHING) < 20,
                        SHRIMP_AREA, () -> NPCs.closest(n -> n.hasAction("Net") && SHRIMP_AREA.contains(n)))
                        .setShouldBank(false)
                        .setInteraction("Net")
                        .setSimpleName("Shrimp until lvl 20")
                        .setCleanAfterAccomplished(true)
                        .setInventoryLoadout(
                                new InventoryLoadout()
                                        .strictIgnore(ItemID.RAW_SHRIMPS, ItemID.RAW_ANCHOVIES)
                                        .addItem(FishingFractal.SMALL_FISHING_NET, 1)
                                        .setStrict(true)
                        ),

                new ConstructionBranch(() -> Skills.getRealLevel(Skill.CONSTRUCTION) < 10)
                        .setCleanAfterAccomplished(true)
                        .setSimpleName("Construction"),

                new CookingBranch(() -> Skill.COOKING.getLevel() < tree.getSettings().cookingTarget)
                        .setCleanAfterAccomplished(true)
                        .setSimpleName("Cooking training to " + tree.getSettings().cookingTarget),

                new FishingBranch(() -> Skill.FISHING.getLevel() < tree.getSettings().fishingTarget)
                        .setCleanAfterAccomplished(true)
                        .setSimpleName("Fishing training to " + tree.getSettings().fishingTarget),

                new FixBarrows(),

                new ChildrenOfTheSun().setSimpleName("Children of the sun"),
                new TwilightPromise().setSimpleName("Twilight promise"),
                new ConfigurableMeleeTraining(() -> Skills.getRealLevel(Skill.STRENGTH) < tree.getSettings().postSlayerCombatTarget
                        || (Math.min(Skill.DEFENCE.getLevel(), Math.min(Skill.ATTACK.getLevel(), Skill.STRENGTH.getLevel())) < 70))
                        .setCleanAfterAccomplished(true)
                        .setSimpleName("Post Slayer Sandcrabs"),
                new Fractal(() -> !PaidQuest.PERILOUS_MOONS.isFinished())
                        .addChildren(
                                new PerilousMoon().setSimpleName("Perilous moon")
                        )
                        .setSimpleName("Unlock bosses"),

                // !areAnyDead, because if all alive its just collected the loot.

                new Fractal(() -> !MoonsOfPerilBranch.areAnyDead() && (MuleOff.timer == null || MuleOff.timer.finished()))
                        .addChildren(
                                new MuleOff().setSimpleName("Mule Off")
                        )
                        .setSimpleName("Safe mule off"),
                new GrabFrozenItem(),
                new Fractal(() -> Worlds.getCurrent().getPing() > tree.getSettings().maxPing)
                        .setPrependLogic(() -> {
                            if (Widgets.isOpen()) Widgets.closeAll();
                            Logger.info("Hopping world because your current world is above maxPing setting.");
                            Logger.info("This can take a while, we're checking all worlds ping");
                            Alerts.addAlert(4000, Color.YELLOW, "Hopping world because your current world is above maxPing setting.");
                            Alerts.addAlert(4000, Color.YELLOW, "Checking all world pings, this can take a while");
                            WorldHopper.hopWorld(Worlds.all().stream()
                                    .filter(x -> x.isNormal() && x.isMembers() && x.getMinimumLevel() == 0)
                                    .filter(x -> x.getPing() < tree.getSettings().maxPing)
                                    .findFirst().orElse(Worlds.getWorld(302)));
                            return true;
                        })
                        .setSimpleName("Hop to low ping world"),
                new MoonsOfPerilBranch(() -> true, tree.getSettings())
                        .setKillBlood(tree.getSettings().killBloodMoon)
                        .setKillBlue(tree.getSettings().killBlueMoon)
                        .setKillEclipse(tree.getSettings().killEclipse)

        );
//        new AIAntiban();
    }

    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) return tree.run();

        if (ClientSettings.isLevelUpInterfaceEnabled()) {
            if (Widgets.isOpen()) Widgets.closeAll();
            Logger.info("Disable level up message");
            ClientSettings.toggleLevelUpInterface(false);
            return ReactionGenerator.getNormal();
        }

        if (!Client.isLoggedIn()) return ReactionGenerator.getNormal();
        if (Client.getGameStateID() == 45) return ReactionGenerator.getNormal(); // 45 is loading
        return tree.run();
    }

    Timer runtime = new Timer();
    public static int grossGp = 0;
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
        return "cCMoonsOfPeril";
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


    //    public void onInventoryItemAdded(Item item) {
//        if (!MoonlightPotionReup.WHOLE_MOONLIGHT_DUNGEON.contains(.getLocal())) return;
//        grossGp += item.getLivePrice() * item.getAmount();
//    }
//
//    @Override
//    public void onInventoryItemChanged(Item incoming, Item existing) {
//        if (!MoonlightPotionReup.WHOLE_MOONLIGHT_DUNGEON.contains(Players.getLocal())) return;
//        int quantity = incoming.getAmount() - existing.getAmount();
//        if (quantity <= 0) return;
//
//        grossGp += incoming.getLivePrice() * quantity;
//    }
//
    @Override
    public void onMessage(Message message) {
        if (message.getMessage().toLowerCase().contains("you have died")) {
            Logger.info("--- DEATH HERE ---");
        }
    }
}
