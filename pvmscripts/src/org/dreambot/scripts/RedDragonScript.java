package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.quest.Quests;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.obstacle.impl.PassableObstacle;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.behaviour.GetOff330;
import org.dreambot.behaviour.method.rdk.RDKRefreshPrayer;
import org.dreambot.behaviour.method.rdk.RDKRestock;
import org.dreambot.behaviour.method.rdk.RedDragonLoadout;
import org.dreambot.behaviour.method.rdk.SafespotReds;
import org.dreambot.behaviour.misc.GetMoreAvas;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.misc.RechargeTrident;
import org.dreambot.behaviour.quests.*;
import org.dreambot.behaviour.quests.animalmagnetism.AnimalMagnetismBranch;
import org.dreambot.behaviour.quests.animalmagnetism.util.LeaveAvaRoom;
import org.dreambot.behaviour.quests.doricsquest.DoricsQuest;
import org.dreambot.behaviour.quests.druidicritual.DruidicRitual;
import org.dreambot.behaviour.quests.dwarfcannon.DwarfCannon;
import org.dreambot.behaviour.quests.earnestthechicken.ErnestTheChicken;
import org.dreambot.behaviour.quests.entertheabyss.EnterTheAbyss;
import org.dreambot.behaviour.quests.impcatcher.ImpCatcher;
import org.dreambot.behaviour.quests.pip.PriestInPeril;
import org.dreambot.behaviour.quests.runemysteries.RuneMysteries;
import org.dreambot.behaviour.quests.theknightssword.TheKnightsSword;
import org.dreambot.behaviour.quests.witchshouse.WitchsHouse;
import org.dreambot.behaviour.training.crafting.CraftingBranch;
import org.dreambot.behaviour.training.firemaking.BurnLogs;
import org.dreambot.behaviour.training.magic.ConfigurableMagicBranch;
import org.dreambot.behaviour.training.magic.F2PMagicBranch;
import org.dreambot.behaviour.training.nmz.ConfigurableMeleeTraining;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.behaviour.training.range.ConfigurableRangeTraining;
import org.dreambot.behaviour.training.slayer.SlayerBranch;
import org.dreambot.behaviour.training.woodcutting.MixedChopping;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.RedDragonSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.text.DecimalFormat;

public class RedDragonScript extends PseudoScript implements ItemContainerListener, ChatListener {
    FractalRoot<RedDragonSettings> tree = new FractalRoot<>(new RedDragonSettings(), getScriptName());

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);
        tree.setSimpleName("cCRedDragonFarm");

        // todo configure loot table

        LocalPathFinder.getLocalPathFinder().addObstacle(new PassableObstacle("Web", "Slash"));
        RedDragonSettings settings = tree.getSettings();
        tree.addChildren(

                new F2PMagicBranch(() -> Skills.getRealLevel(Skill.MAGIC) < settings.ftpMagicTarget, settings.ftpDefTarget)
                        .setSimpleName("FTP magic training"),

                new GetMembershipBranch().setSimpleName("Get Membership"),
                new AutoProggy().setSimpleName("Auto proggy"),
                new AntibanFractal().setSimpleName("Antiban"),
                new EmptyDeathsCoffer().setSimpleName("Empty grave"),

                new Fractal(() -> !PaidQuest.WITCHS_HOUSE.isFinished() && (Skills.getRealLevel(Skill.HITPOINTS) < 20))
                        .addChildren(new WitchsHouse().setSimpleName("house"))
                        .setSimpleName("Witches"),
                new ConfigurableMagicBranch(() -> settings.magicTarget > Skills.getRealLevel(Skill.MAGIC))
                        .setSimpleName("Magic level"),

                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < settings.prayerTarget).setSimpleName("Prayer"),
                // get anti dragon shield
                new Fractal(() -> Quests.getQuestPoints() < 32).setSimpleName("Questin")
                        .addChildren(
                                new ConfigurableMeleeTraining(() -> Combat.getCombatLevel() < 20
                                        || Skills.getRealLevel(Skill.ATTACK) < 10
                                        || Skills.getRealLevel(Skill.HITPOINTS) < 35)
                                        .setSimpleName("Melee training for quests"),
                                new CooksAssistant().setSimpleName("Cooks assistant"), // 1
                                new RomeoAndJulietBranch().setSimpleName("Romeo and juliet"), // 5
                                new ImpCatcher().setSimpleName("Imp catcher"), // 1
                                new DoricsQuest().setSimpleName("Dorics quest"), // 1
                                new TheKnightsSword().setSimpleName("Knights sword"), // 1
                                new RuneMysteries().setSimpleName("Rune mysteries"), // 1
                                new DwarfCannon().setSimpleName("Dwarf cannon"), // 1
                                new EnterTheAbyss().setSimpleName("Enter the abyss"),// 0
                                new GoblinDiplomacy().setSimpleName("Goblin diplomacy"), // 5
                                new DruidicRitual().setSimpleName("Druidic Ritual"), // 4
                                new ErnestTheChicken().setSimpleName("Ernest the chicken"),// 4
                                new VampyreSlayer().setSimpleName("Vampyre Slayer"), // 3
                                new SheepShearer().setSimpleName("Sheep shearer"), // 1
                                new MonksFriend().setSimpleName("Monks Friend"), // 1
                                new RestlessGhost().setSimpleName("Restless Ghost"), // 1
                                new PriestInPeril().setSimpleName("PIP") // 1
                        ),
                new DragonSlayerOne().setSimpleName("DS1 until shield unlocked"),
                new Fractal(() -> (settings.ftpRangeTarget > 0 || settings.rangeTarget > 0) && !PaidQuest.ANIMAL_MAGNETISM.isFinished())
                        .setSimpleName("Get Avas")
                        .addChildren(
                                new ConfigurableMeleeTraining(() -> Combat.getCombatLevel() < 20
                                        || Skills.getRealLevel(Skill.HITPOINTS) < 10)
                                        .setSimpleName("Melee training"),

                                new XMarksTheSpot().setSimpleName("X marks the spot"),
                                new ClientOfKourend().setSimpleName("Client of Kourend"),
                                new ConfigurableRangeTraining(() -> Skills.getRealLevel(Skill.HITPOINTS) < 30 || Skills.getRealLevel(Skill.RANGED) < 30)
//                                        .setDefenceTarget(settings.defenceTarget)
                                        .setSimpleName("Range Sandcrabs"),
                                new BurnLogs(() -> Skills.getRealLevel(Skill.FIREMAKING) < 4, 7, ItemID.LOGS).setSimpleName("Fire making for slayer"),
                                new SlayerBranch(() -> Skills.getRealLevel(Skill.SLAYER) < 18).setSimpleName("Slayer"),
                                new RestlessGhost().setSimpleName("Restless ghost"),
                                new ErnestTheChicken().setSimpleName("Ernest the chicken"),
                                new PriestInPeril().setSimpleName("Priest in peril"),
                                new CraftingBranch(() -> Skills.getRealLevel(Skill.CRAFTING) < 19).setSimpleName("Craft"),
                                new MixedChopping(() -> Skills.getRealLevel(Skill.WOODCUTTING) < 35).setSimpleName("Chop"),
                                new AnimalMagnetismBranch().setSimpleName("Animal Magnetism")
                        ),

                new RechargeTrident().setSimpleName("Recharge trident"),

                new GetMoreAvas().setSimpleName("Get more avas devices"),
                new LeaveAvaRoom().setSimpleName("Leave avas"),
                new MuleOff().setSimpleName("Mule off"),

                new RDKRestock(() -> (Equipment.isSlotEmpty(EquipmentSlot.ARROWS) && settings.loadout == RedDragonLoadout.RANGE)
                        || !Inventory.contains(ItemID.JUG_OF_WINE),
                        settings)
                        .setSimpleName("Restocking")
                        .setPrependLogic(() -> {
                            if (Client.isDynamicRegion()) {
                                Magic.castSpell(Normal.HOME_TELEPORT);
                                Antiban.sleepUntil(() -> !Client.isDynamicRegion(), 24000);
                            }

                            if (SafespotReds.FORTHOS_DUNGEON.contains(Players.getLocal())) {
                                SafespotReds.handlePray();
                                WebFinder.getWebFinder().disableEquipmentTeleports();
                                WebFinder.getWebFinder().disableInventoryTeleports();
                                if (!SafespotReds.FORTHOS_EXIT.contains(Players.getLocal())) {
                                    if (Walking.shouldWalk(8)) Walking.walk(SafespotReds.FORTHOS_EXIT);
                                    return true;
                                }

                                GameObject ladder = GameObjects.closest("Ladder");
                                if (ladder != null && ladder.interact("Climb-up")) {
                                    Antiban.sleepUntil(() -> !SafespotReds.FORTHOS_DUNGEON.contains(Players.getLocal()),
                                            2400);
                                }
                                return true;
                            }


                            WebFinder.getWebFinder().enableEquipmentTeleports();
                            WebFinder.getWebFinder().enableInventoryTeleports();
                            return false;
                        }),
                new RDKRefreshPrayer().setSimpleName("Get prayer"),
                new SafespotReds().setSimpleName("Kill reds")
        );
    }

    // set true when logout listener fires
    public static boolean changeWorld = false;

    public static void logout() {
        Client.setIdleTime(30_000);
        changeWorld = true;
    }

    @Override
    public int onLoop() {
        if (!Client.isLoggedIn() && changeWorld) {
            WorldHopper.changeWorldDirect(Worlds.getRandomWorld(GetOff330.MEMBERS_WORLD_FILTER));
            changeWorld = false;
            return ReactionGenerator.getNormal();
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
                "In combat " + Players.getLocal().isInCombat(),
                "Tick " + Client.getGameTick(),
                "Deaths " + deathCount
        };
    }

    @Override
    public String getScriptName() {
        return "cCRedDragonFarm";
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
        if (!SafespotReds.FORTHOS_DUNGEON.contains(Players.getLocal())) return;
        grossGp += item.getLivePrice() * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        if (!SafespotReds.FORTHOS_DUNGEON.contains(Players.getLocal())) return;
        int quantity = incoming.getAmount() - existing.getAmount();
        if (quantity >= 0) return;

        grossGp += incoming.getLivePrice() * quantity;
    }

    int deathCount = 0;

    @Override
    public void onMessage(Message message) {
        if (message.getMessage().toLowerCase().contains("you are dead")) {
            deathCount++;
        }
    }
}
