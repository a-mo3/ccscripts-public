package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.clan.chat.ClanChat;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;
import org.dreambot.api.methods.walking.web.node.impl.EntranceWebNode;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.behaviour.friends.CorpConfigureClanChat;
import org.dreambot.behaviour.friends.ConfigureFriendsList;
import org.dreambot.behaviour.method.corp.CorpClient;
import org.dreambot.behaviour.method.corp.CorpLoadout;
import org.dreambot.behaviour.method.corp.InitCorpConnection;
import org.dreambot.behaviour.method.corp.behaviour.FightCorpBranch;
import org.dreambot.behaviour.method.corp.behaviour.GetIntoCorpFight;
import org.dreambot.behaviour.method.corp.behaviour.ResetSpecSploit;
import org.dreambot.behaviour.method.corp.messages.CorpRole;
import org.dreambot.behaviour.misc.FixBarrows;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.quests.ClientOfKourend;
import org.dreambot.behaviour.quests.RestlessGhost;
import org.dreambot.behaviour.quests.XMarksTheSpot;
import org.dreambot.behaviour.quests.animalmagnetism.AnimalMagnetismBranch;
import org.dreambot.behaviour.quests.earnestthechicken.ErnestTheChicken;
import org.dreambot.behaviour.quests.pip.PriestInPeril;
import org.dreambot.behaviour.training.agility.AreaUtils;
import org.dreambot.behaviour.training.crafting.CraftingBranch;
import org.dreambot.behaviour.training.firemaking.BurnLogs;
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
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.PVMUtil;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.fractals.util.RatConfigureQuickPrayers;
import org.dreambot.scriptdata.CorpSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CorpScript extends PseudoScript implements ItemContainerListener, ChatListener {
    FractalRoot<CorpSettings> tree = new FractalRoot<>(new CorpSettings(), getScriptName());

    // we use a forceHost quickstart arg so that
    boolean forceHost;

    @Override
    public void onArgs(String... args) {
        if (args == null) return;
        Logger.info("Check args " + Arrays.toString(args));
        forceHost = Arrays.stream(args).anyMatch(x -> x.toLowerCase().contains("forcehost"));

        CorpLoadout forceLoadout = Arrays.stream(args)
                .map(CorpLoadout::forName)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
        if (forceLoadout != null) {
            Logger.info("Force corp loadout to " + forceLoadout);
            tree.getSettings().loadout = forceLoadout;
        }
    }

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);
        tree.setSimpleName("cCCorpFarm");

        MuleOff.LOOT = new int[]{
                // todo spirit shields
                ItemID.ELYSIAN_SPIRIT_SHIELD,
                ItemID.ARCANE_SPIRIT_SHIELD,
                ItemID.SPECTRAL_SPIRIT_SHIELD,

                ItemID.MYSTIC_ROBE_TOP,
                ItemID.MYSTIC_ROBE_BOTTOM,
                ItemID.MYSTIC_AIR_STAFF,
                ItemID.MYSTIC_WATER_STAFF,
                ItemID.MYSTIC_FIRE_STAFF,
                ItemID.MYSTIC_EARTH_STAFF,
                ItemID.SPIRIT_SHIELD,

                ItemID.SOUL_RUNE,
                ItemID.RUNITE_BOLTS,
                ItemID.DEATH_RUNE,
                ItemID.ONYX_BOLTS_E,
                ItemID.CANNONBALL,
                ItemID.ADAMANT_ARROW,
                ItemID.LAW_RUNE,
                ItemID.COSMIC_RUNE,

                ItemID.RAW_SHARK,
                ItemID.PURE_ESSENCE,
                ItemID.ADAMANTITE_BAR,
                ItemID.GREEN_DRAGONHIDE,
                ItemID.ADAMANTITE_ORE,
                ItemID.RUNITE_ORE,
                ItemID.TEAK_PLANK,
                ItemID.MAHOGANY_LOGS,
                ItemID.MAGIC_LOGS,
                ItemID.WHITEBERRY_SEED,
                ItemID.WHITE_BERRIES,
                ItemID.ANTIDOTE4_5952,
                ItemID.RANARR_SEED,
                ItemID.HOLY_ELIXIR
        };
        WithdrawLoadoutEvent.sellList = MuleOff.LOOT;

        // todo add webnodes for the lms casual floor sploit
        EntranceWebNode bottomLMSStairs = new EntranceWebNode(3138, 3636, 0, "Stairs", "Climb");
        EntranceWebNode topLMSStairs = new EntranceWebNode(3138, 3637, 1, "Stairs", "Climb");
        BasicWebNode casLMSBasic = new BasicWebNode(3141, 3637, 1);

        WebFinder wf = WebFinder.getWebFinder();
        wf.getNearest(bottomLMSStairs, 10).addDualConnections(bottomLMSStairs);
        bottomLMSStairs.addDualConnections(topLMSStairs);
        topLMSStairs.addDualConnections(casLMSBasic);
        wf.addWebNodes(bottomLMSStairs, topLMSStairs, casLMSBasic);
        Area feroxArea = new Area(3124, 3645, 3155, 3615);
        tree.addChildren(

                new GetMembershipBranch().setSimpleName("Get Membership"),
                new EmptyDeathsCoffer().setSimpleName("Empty grave"),

                // prayer training
                new PrayerBranch(() -> !forceHost && Skills.getRealLevel(Skill.PRAYER) < tree.getSettings().prayerTarget)
                        .setSimpleName("Prayer training: " + tree.getSettings().prayerTarget),

                // combat / range training

                new Fractal(() -> !forceHost && !PaidQuest.ANIMAL_MAGNETISM.isFinished() && Skills.getRealLevel(Skill.RANGED) >= 30 && tree.getSettings().rangeTarget >= 30)
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

                new ConfigurableRangeTraining(() -> !forceHost && tree.getSettings().loadout.mode == Skill.RANGED && Skills.getRealLevel(Skill.RANGED) < tree.getSettings().rangeTarget)
                        .setSimpleName("Range training " + tree.getSettings().rangeTarget),

                new ConfigurableMeleeTraining(() -> !forceHost && tree.getSettings().loadout.mode == Skill.ATTACK && Skills.getRealLevel(Skill.ATTACK) < tree.getSettings().attackTarget)
                        .setSimpleName("Melee training"),

                new InitCorpConnection(tree.getSettings(), forceHost).setSimpleName("Init connection"),
                new RatConfigureQuickPrayers(() -> CorpClient.getRole() != null && CorpClient.getRole() != CorpRole.HOST,
                        () -> new Prayer[]{Prayer.PROTECT_FROM_MAGIC,
                                tree.getSettings().loadout.mode == Skill.RANGED ? PVMUtil.getBestRangePray() : PVMUtil.getBestMeleePray()})
                        .setSimpleName("Configure q p"),

                new FixBarrows().setSimpleName("Fix barrows"),
                new ConfigureFriendsList(CorpClient::getMembers).setSimpleName("Setup friends list"),
                new CorpConfigureClanChat(() -> !ClanChat.inChat()
                        || (CorpClient.getRole() != CorpRole.HOST && !CorpClient.getLeader().equalsIgnoreCase(ClanChat.getOwner()))
                ).setSimpleName("Configure clan"),

                new ResetSpecSploit(ResetSpecSploit::isInLMSCasual)
                        .setSimpleName("Reset Spec"),
                // get loadout
                new Fractal(() -> CorpClient.getRole() != CorpRole.HOST
                        && (!Inventory.contains(ItemID.SHARK) || (CorpClient.getRole() == CorpRole.SPECIAL_FORCES && ItemVariants.RING_OF_DUELING.getItem() == null)
                        || (!Client.isDynamicRegion()
                        && (!AreaUtils.containsIgnorePlane(feroxArea, Players.getLocal()) || ItemVariants.GAMES_NECKLACE.getItem() == null)
                        && GameObjects.closest("Passage") == null
                        && !tree.getSettings().loadout.isFulfilled())
                ))
                        .setSimpleName("Get loadout")
                        .setPrependLogic(() -> {
                            Item rod = ItemVariants.RING_OF_DUELING.getItem();
                            if (Client.isDynamicRegion() && rod != null) {
                                Logger.info("Loadout force tp");
                                rod.interact("Ferox Enclave");
                                return true;
                            }
                            return false;
                        })
                        .setEquipmentLoadout(tree.getSettings().loadout.equipmentLoadout)
                        .setInventoryLoadout(tree.getSettings().loadout.inventoryLoadout),

                new Fractal(() -> CorpClient.getRole() == CorpRole.HOST
                        && !Client.isDynamicRegion() // you dont need anything once ur hosting
                        && (Players.getLocal().getY() < 3500 && ItemVariants.GAMES_NECKLACE.getItem() == null))
                        .setSimpleName("Get loadout(HOST)")
                        .setInventoryLoadout(new InventoryLoadout().addItem(ItemVariants.GAMES_NECKLACE)),

                // recharging spec if you are a spec forces
                new ResetSpecSploit(() -> Combat.getSpecialPercentage() < 50
                        && tree.getSettings().loadout.mode != Skill.RANGED
                        && CorpClient.getRole() == CorpRole.SPECIAL_FORCES
                        && (CorpClient.getDHWSpecsLanded() < 3 || CorpClient.getBGSDamageDelt() < 200))
                        .setSimpleName("Reset Spec"),

                new MuleOff().setSimpleName("Mule off"),
                new GetIntoCorpFight(() -> !Client.isDynamicRegion() || CorpClient.getRole() == CorpRole.HOST)
                        .setSimpleName("Go To Corp"),
                new FightCorpBranch(() -> true, tree.getSettings())
                        .setSimpleName("Corp branch")

        );
    }


    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) tree.run();

        if (ClientSettings.isLevelUpInterfaceEnabled()) {
            if (Widgets.isOpen()) Widgets.closeAll();
            Logger.info("Disable level up message");
            ClientSettings.toggleLevelUpInterface(false);
            return ReactionGenerator.getNormal();
        }

        if (!Client.isLoggedIn()) return ReactionGenerator.getNormal();
        if (Client.getGameStateID() == 45) return ReactionGenerator.getNormal();
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
                "TeamID: " + (CorpClient.getTeam() == null ? "" : CorpClient.getTeam().getTeamId()),
                "Team Leader: " + (CorpClient.getTeam() == null ? "" : CorpClient.getLeader()),
                "DHW Specs landed: " + CorpClient.getDHWSpecsLanded(),
                "BGS Damage landed: " + CorpClient.getBGSDamageDelt(),
                "Corp role: " + CorpClient.getRole(),
                "Corp world: " + CorpClient.getCorpWorld(),
        };
    }

    @Override
    public String getScriptName() {
        return "cCCorpFarm";
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

    List<Integer> ignoredLoot = Arrays.asList(
            ItemID.DRAGON_WARHAMMER,
            ItemID.DARK_BOW,
            ItemID.BANDOS_GODSWORD,
            ItemID.ZAMORAKIAN_SPEAR,
            ItemID.DRAGON_SPEAR,
            ItemID.RUNE_CROSSBOW
    );

    @Override
    public void onInventoryItemAdded(Item item) {
        if (!Client.isDynamicRegion()) return;
        if (ignoredLoot.contains(item.getId())) return;
        grossGp += item.getLivePrice() * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        if (!Client.isDynamicRegion()) return;
        if (ignoredLoot.contains(incoming.getId())) return;
        int quantity = incoming.getAmount() - existing.getAmount();
        if (quantity <= 0) return;

        grossGp += incoming.getLivePrice() * quantity;
    }

    @Override
    public void onLootBagItemAdded(Item item) {
        Logger.info("Loot bag added");
        grossGp += item.getLivePrice() * item.getAmount();
    }

    int deathCount = 0;

    @Override
    public void onMessage(Message message) {
        if (message.getMessage().toLowerCase().contains("you are dead")) {
            deathCount++;
        }
    }

    @Override
    public void onExit() {
        CorpClient.getInstance().close();
    }

}
