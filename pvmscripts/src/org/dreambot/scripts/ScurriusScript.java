package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.magic.Spell;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.GetOff330;
import org.dreambot.behaviour.method.scurrius.*;
import org.dreambot.behaviour.misc.GetMoreAvas;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.misc.RechargeBoneStaff;
import org.dreambot.behaviour.misc.SandCrabs;
import org.dreambot.behaviour.quests.RestlessGhost;
import org.dreambot.behaviour.quests.VampyreSlayer;
import org.dreambot.behaviour.quests.XMarksTheSpot;
import org.dreambot.behaviour.quests.animalmagnetism.AnimalMagnetismBranch;
import org.dreambot.behaviour.quests.earnestthechicken.ErnestTheChicken;
import org.dreambot.behaviour.quests.fightarena.FightArena;
import org.dreambot.behaviour.quests.pip.PriestInPeril;
import org.dreambot.behaviour.quests.witchshouse.WitchsHouse;
import org.dreambot.behaviour.training.crafting.CraftingBranch;
import org.dreambot.behaviour.training.firemaking.BurnLogs;
import org.dreambot.behaviour.training.magic.MagicBranch;
import org.dreambot.behaviour.training.magic.MagicCombat;
import org.dreambot.behaviour.training.nmz.ConfigurableMeleeTraining;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.behaviour.training.slayer.SlayerBranch;
import org.dreambot.behaviour.training.slayer.SlayerTaskMap;
import org.dreambot.behaviour.training.woodcutting.MixedChopping;
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
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.fractals.util.PVMUtil;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.fractals.util.RatConfigureQuickPrayers;
import org.dreambot.scriptdata.ScurriusSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.text.DecimalFormat;
import java.util.*;

public class ScurriusScript extends PseudoScript implements ItemContainerListener {
    FractalRoot<ScurriusSettings> tree = new FractalRoot<>(new ScurriusSettings(), getScriptName());

    @Override
    public void onArgs(String... args) {
        String a = args[0];
        if (a != null && !a.isEmpty()) {
            Logger.info("Set settings name to " + a);
            tree = new FractalRoot<>(new ScurriusSettings(), a);
        }
    }

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);

        Logger.info("Init");
        SlayerTaskMap.minLootValue = 1000;
        tree.setSimpleName("cCScurrius");
        AbstractEvent.globalInterruptCondition = () -> Inventory.contains("Coin pouch");

        MuleOff.LOOT = new int[]{
                ItemID.RUNE_FULL_HELM,
                ItemID.ADAMANT_PLATEBODY,
                ItemID.RUNE_MED_HELM,
                ItemID.RUNE_SQ_SHIELD,
                ItemID.RUNE_CHAINBODY,
                ItemID.RUNE_BATTLEAXE,
                ItemID.CHAOS_RUNE,
                ItemID.DEATH_RUNE,
                ItemID.LAW_RUNE,
                ItemID.RUNE_ARROW,
                ItemID.ADAMANT_ARROW
        };
        WithdrawLoadoutEvent.sellList = MuleOff.LOOT;

        HashMap<Skill, CombatStyle> skillStyleMap = new HashMap<>();
        skillStyleMap.put(Skill.STRENGTH, CombatStyle.STRENGTH);
        skillStyleMap.put(Skill.ATTACK, CombatStyle.ATTACK);
        skillStyleMap.put(Skill.DEFENCE, CombatStyle.DEFENCE);


        ScurriusSettings settings = tree.getSettings();
        tree.addChildren(
                new GetMembershipBranch()
                        .setSimpleName("Get Membership"),
                new EmptyDeathsCoffer().setSimpleName("Death coffer"),

                new Fractal(() -> tree.getSettings().doWitchesHouse && !PaidQuest.WITCHS_HOUSE.isFinished())
                        .addChildren(
                                new WitchsHouse().setSimpleName("Witches house for some hp")
                        ).setSimpleName("Quest"),
                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < Math.max(43, tree.getSettings().prayerTarget))
                        .setSimpleName("Prayer training"),
                new GetOff330(GetOff330.MEMBERS_WORLD_FILTER).setSimpleName("Hop off 330"),
                new MuleOff().setSimpleName("Mule off"),
                // Magic
                new Fractal(() -> Skill.MAGIC.getLevel() < settings.magicTarget)
                        .addChildren(
                                new MagicBranch(() -> Skills.getRealLevel(Skill.MAGIC) < 50).setSimpleName("Get 50s"),
                                new MagicCombat(30, 10).setSimpleName("Get 30 HP"),
                                new RatConfigureQuickPrayers(() -> new Prayer[]{Prayer.PROTECT_FROM_MELEE, ScurriusMode.getBestMagePray()})
                                        .setSimpleName("Magic q p"),

                                new MakeBoneWeapon(() -> OwnedItems.contains(ItemID.SCURRIUS_SPINE) && !OwnedItems.contains(ItemID.BONE_STAFF),
                                        MakeBoneWeapon.BONE_STAFF_LOADOUT)
                                        .setSimpleName("Make bone staff"),
                                new GetSpineLamp(() -> OwnedItems.contains(ItemID.SCURRIUS_SPINE) || Inventory.contains(ItemID.SCURRIUS_LAMP),
                                        tree.getSettings().spineLampSkill)
                                        .setSimpleName("Get spine lamp"),
                                new RechargeBoneStaff().setSimpleName("Charge bones staff"),

                                new GoToScurrius(() -> !Client.isDynamicRegion(), ScurriusMode.MAGIC)
                                        .setSimpleName("Magic"),
                                new ScurriusBranch(() -> true, ScurriusMode.MAGIC, false)
                                        .setFlick(tree.getSettings().flicking)
                                        .setPrependLogic(() -> {
                                            boolean shouldDefCase = Skill.DEFENCE.getLevel() < tree.getSettings().magicDefenceTarget;
                                            // bone staff unique handle
                                            if (Equipment.contains(ItemID.BONE_STAFF)) {
                                                if (shouldDefCase) {
                                                    if (Combat.getCombatStyle() != CombatStyle.MAGIC_DEFENCE) {
//                                                        Logger.info("Handle bone staff auto casting " + Combat.getCombatStyle());
//                                                        Logger.info("Set Magic def style");
                                                        Combat.setCombatStyle(CombatStyle.MAGIC_DEFENCE);
                                                    }
                                                } else {
//                                                    Logger.info("Handle bone staff auto casting " + Combat.getCombatStyle());
//                                                    Logger.info("Set Magic style");
                                                    if (Combat.getCombatStyle() != CombatStyle.MAGIC)
                                                        Combat.setCombatStyle(CombatStyle.MAGIC);
                                                }
                                            } else {
                                                if ((shouldDefCase && !Magic.isAutocastDefensive())
                                                        || (Magic.getAutocastSpell() == null || !Magic.canCast(Magic.getAutocastSpell()))) {
                                                    Logger.info("Scurrius needs to set magic autocast");
                                                    if (getSpell() == null) {
                                                        Logger.info("Gotta leave scurrius no runes left.");
                                                        Bank.open();
                                                        return true;
                                                    }
                                                    Logger.info("Change def cast state");

                                                    if (shouldDefCase) {
                                                        Magic.setDefensiveAutocastSpell(getSpell());
                                                    } else {
                                                        Magic.setAutocastSpell(getSpell());
                                                    }
                                                    return true;
                                                }
                                            }
                                            return false;
                                        })
                                        .setSimpleName("Scurrius")
                        )
                        .setSimpleName("Magic"),

                // Melee
                new Fractal(
                        () -> Skill.ATTACK.getLevel() < settings.attackTarget
                                || Skill.DEFENCE.getLevel() < settings.defenceTarget
                                || Skill.STRENGTH.getLevel() < settings.strengthTarget)
                        .addChildren(
                                new Fractal(() -> !FreeQuest.VAMPIRE_SLAYER.isFinished() && tree.getSettings().vampyreSlayer).setSimpleName("Quest")
                                        .addChildren(
                                                new VampyreSlayer().setSimpleName("Vampyre slayer")
                                        ),

                                new Fractal(() -> !PaidQuest.FIGHT_ARENA.isFinished() && tree.getSettings().fightArena).setSimpleName("Quest")
                                        .addChildren(
                                                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < 43)
                                                        .setSimpleName("Prayer 43"),
                                                new GetOff330(GetOff330.MEMBERS_WORLD_FILTER).setSimpleName("Hop off 330"),
                                                new FightArena().setSimpleName("Fight arena")
                                        ),

                                SandCrabs.getMelee(() -> !reachedBase(50, Skill.DEFENCE, Skill.ATTACK, Skill.STRENGTH))
                                        .setSimpleName("Base 50s @ Crabs"),
                                new RatConfigureQuickPrayers(() -> new Prayer[]{Prayer.PROTECT_FROM_MELEE, PVMUtil.getBestMeleePray()})
                                        .setSimpleName("Melee q p"),

                                new MakeBoneWeapon(() -> OwnedItems.contains(ItemID.SCURRIUS_SPINE) && !OwnedItems.contains(ItemID.BONE_MACE),
                                        MakeBoneWeapon.BONE_MACE_LOADOUT)
                                        .setSimpleName("Make bone mace"),
                                new GetSpineLamp(() -> OwnedItems.contains(ItemID.SCURRIUS_SPINE) || Inventory.contains(ItemID.SCURRIUS_LAMP),
                                        tree.getSettings().spineLampSkill)
                                        .setSimpleName("Get spine lamp"),

                                new GoToScurrius(() -> !Client.isDynamicRegion(), ScurriusMode.MELEE),
                                new ScurriusBranch(() -> true, ScurriusMode.MELEE, tree.getSettings().dropFoodForLoot)
                                        .setStyleSupplier(() -> {
                                            int atk = Skills.getRealLevel(Skill.ATTACK);
                                            int str = Skills.getRealLevel(Skill.STRENGTH);
                                            int def = Skills.getRealLevel(Skill.DEFENCE);
                                            List<Skill> skills = new ArrayList<>();
                                            if (atk < tree.getSettings().attackTarget) skills.add(Skill.ATTACK);
                                            if (str < tree.getSettings().strengthTarget) skills.add(Skill.STRENGTH);
                                            if (def < tree.getSettings().defenceTarget) skills.add(Skill.DEFENCE);
                                            return skillStyleMap.get(skills.stream().min(Comparator.comparingInt(Skill::getLevel)).orElse(Skill.STRENGTH));
                                        })
                                        .setSimpleName("Scurrius")
                        )
                        .setSimpleName("Melee"),

                // Range
                new Fractal(() -> tree.getSettings().rangeTarget > 30
                        && Skills.getRealLevel(Skill.RANGED) >= 30
                        && !PaidQuest.ANIMAL_MAGNETISM.isFinished())
                        .addChildren(
                                new ConfigurableMeleeTraining(() -> Skills.getRealLevel(Skill.STRENGTH) < 30
                                        || Skills.getRealLevel(Skill.HITPOINTS) < 10)
                                        .setSimpleName("Melee training for ava quests"),

                                new XMarksTheSpot().setSimpleName("x marks"), // not certain what this is for but not going to risk changing rn
                                new BurnLogs(() -> Skills.getRealLevel(Skill.FIREMAKING) < 4, 7, ItemID.LOGS).setSimpleName("Burn logs need it for slayer"),
                                new SlayerBranch(() -> Skills.getRealLevel(Skill.SLAYER) < 18).setSimpleName("Slayer"),
                                new RestlessGhost().setSimpleName("Restless ghost"),
                                new ErnestTheChicken().setSimpleName("Ernest the chicken"),
                                new PriestInPeril().setSimpleName("Priest in peril"),
                                new CraftingBranch(() -> Skills.getRealLevel(Skill.CRAFTING) < 19).setSimpleName("Craft"),
                                new MixedChopping(() -> Skills.getRealLevel(Skill.WOODCUTTING) < 35).setSimpleName("Chop"),
                                new AnimalMagnetismBranch().setSimpleName("Animal Magnetism")
                        )
                        .setSimpleName("Get avas"),
                new GetMoreAvas().setSimpleName("Get more avas"),
                new Fractal(() -> Skill.RANGED.getLevel() < settings.rangeTarget)
                        .addChildren(
                                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < Math.max(44, tree.getSettings().prayerTarget))
                                        .setSimpleName("Prayer training"),
                                SandCrabs.getRange(() -> Skills.getRealLevel(Skill.RANGED) < 40 || Skills.getRealLevel(Skill.HITPOINTS) < 30)
                                        .setSimpleName("at least 30 base hp & 40 range"),
                                new RatConfigureQuickPrayers(() -> new Prayer[]{Prayer.PROTECT_FROM_MELEE, ScurriusMode.getBestRangePray()})
                                        .setSimpleName("Range q p"),

                                new MakeBoneWeapon(() -> OwnedItems.contains(ItemID.SCURRIUS_SPINE) && !OwnedItems.contains(ItemID.BONE_SHORTBOW),
                                        MakeBoneWeapon.BONE_BOW_LOADOUT)
                                        .setSimpleName("Make bone bow"),
                                new GetSpineLamp(() -> OwnedItems.contains(ItemID.SCURRIUS_SPINE) || Inventory.contains(ItemID.SCURRIUS_LAMP),
                                        tree.getSettings().spineLampSkill)
                                        .setSimpleName("Get spine lamp"),

                                new GoToScurrius(() -> !Client.isDynamicRegion() || Equipment.isSlotEmpty(EquipmentSlot.ARROWS), ScurriusMode.RANGE),
                                new ScurriusBranch(() -> true, ScurriusMode.RANGE, tree.getSettings().dropFoodForLoot)
                                        .setStyleSupplier(() -> {
                                            if (Skill.DEFENCE.getLevel() < tree.getSettings().rangeDefenceTarget)
                                                return CombatStyle.RANGED_DEFENCE;
                                            return CombatStyle.RANGED_RAPID;
                                        })
                                        .setSimpleName("Scurrius")
                        )
                        .setSimpleName("Range")

        );
    }

    private Spell getSpell() {
        Spell[] allowed = new Spell[]{
                Normal.WIND_STRIKE,
                Normal.WIND_BOLT,
                Normal.WIND_BLAST,
                Normal.WIND_WAVE,
                Normal.WIND_SURGE
        };

        return Arrays.stream(allowed).filter(Magic::canCast).reduce((f, s) -> s).orElse(null);
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

//        if (!Bank.isCached()) {
//            Logger.info("Needs bank cache to make sure we're using the correct equipment");
//            if (Widgets.isOpen()) Widgets.closeAll();
//            if (Walking.shouldWalk()) Bank.open();
//            return ReactionGenerator.getNormal();
//        }

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
        return "cCScurrius";
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
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
    }

    public static boolean reachedBase(int base, Skill... skills) {
        return Arrays.stream(skills).allMatch(x -> x.getLevel() >= base);
    }
}
