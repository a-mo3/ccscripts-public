package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.quest.Quests;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.behaviour.method.greendragon.GDKAntiPK;
import org.dreambot.behaviour.method.greendragon.GDKLoadout;
import org.dreambot.behaviour.method.greendragon.KillGreenDragons;
import org.dreambot.behaviour.method.spindel.RechargeWildyWeapon;
import org.dreambot.behaviour.misc.*;
import org.dreambot.behaviour.quests.*;
import org.dreambot.behaviour.quests.animalmagnetism.AnimalMagnetismBranch;
import org.dreambot.behaviour.quests.childrenofthesun.ChildrenOfTheSun;
import org.dreambot.behaviour.quests.doricsquest.DoricsQuest;
import org.dreambot.behaviour.quests.druidicritual.DruidicRitual;
import org.dreambot.behaviour.quests.dwarfcannon.DwarfCannon;
import org.dreambot.behaviour.quests.earnestthechicken.ErnestTheChicken;
import org.dreambot.behaviour.quests.entertheabyss.EnterTheAbyss;
import org.dreambot.behaviour.quests.impcatcher.ImpCatcher;
import org.dreambot.behaviour.quests.pip.PriestInPeril;
import org.dreambot.behaviour.quests.runemysteries.RuneMysteries;
import org.dreambot.behaviour.quests.theknightssword.TheKnightsSword;
import org.dreambot.behaviour.training.combat.F2PMeleeCombats;
import org.dreambot.behaviour.training.crafting.CraftingBranch;
import org.dreambot.behaviour.training.firemaking.BurnLogs;
import org.dreambot.behaviour.training.magic.F2PMagicBranch;
import org.dreambot.behaviour.training.nmz.ConfigurableMeleeTraining;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.behaviour.training.range.DistributedRangeTraining;
import org.dreambot.behaviour.training.slayer.SlayerBranch;
import org.dreambot.behaviour.training.woodcutting.MixedChopping;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.discordwebhook.AutoProggy;
import org.dreambot.discordwebhook.scouter.ScoutFractal;
import org.dreambot.fractals.*;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.EmptyLootingBagEvent;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.paint.PaintInfo;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.pktrie.PKTrie;
import org.dreambot.scriptdata.GDKSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GreenDragonScript extends PseudoScript implements PaintInfo, ChatListener, ItemContainerListener, SpawnListener {
    Timer runtime = new Timer();
    FractalRoot<GDKSettings> tree = new FractalRoot<>(new GDKSettings(), getScriptName());
    public static int deathCount = 0;
    boolean shouldHop = false;
    int grossGp = 0;
    boolean needsToRecharge = false;

    public void init() {
        Client.getInstance().addEventListener(this);

        WithdrawLoadoutEvent.sellList = MuleOff.LOOT;
        List<Integer> newMuleOff = Arrays.stream(MuleOff.LOOT).boxed().collect(Collectors.toList());
        for (int i1 : new int[]{ItemID.DRAGON_SWORD, ItemID.MITHRIL_SCIMITAR, ItemID.MITHRIL_PLATESKIRT, ItemID.ADAMANT_SCIMITAR,
                ItemID.RANGING_POTION3,
                ItemID.RANGING_POTION2,
                ItemID.RANGING_POTION1,

                ItemID.ATTACK_POTION1,
                ItemID.ATTACK_POTION2,
                ItemID.ATTACK_POTION3,

                ItemID.STRENGTH_POTION3,
                ItemID.STRENGTH_POTION2,
                ItemID.STRENGTH_POTION1,

                ItemID.ANTIFIRE_POTION3,
                ItemID.ANTIFIRE_POTION2,
                ItemID.ANTIFIRE_POTION1,
                ItemID.TELEPORT_TO_HOUSE,

                ItemID.ADAMANT_SWORD, ItemID.IRON_SCIMITAR, ItemID.IRON_PLATEBODY, ItemID.MITHRIL_PLATEBODY, ItemID.IRON_PLATESKIRT}) {
            newMuleOff.add(i1);
        }
        MuleOff.LOOT = newMuleOff.stream().mapToInt(i -> i).toArray();

        MuleOff.muleOffItems = new MuleOffItem[]{
                new MuleOffItem(ItemID.ABYSSAL_WHIP, () -> true, 1)
        };

        // mule off items should be good with default
        tree.setSimpleName("cCGreenDragons")
                .addChildren(
                        new AutoProggy().setSimpleName("Auto proggy"),
                        new AntibanFractal().setSimpleName("Antiban"),
                        new ScoutFractal(),
                        new ReactionSettingsFractal(),
                        new PutPetAway(),
                        new TutorialTree().setSimpleName("Tutorial island"),
                        new EmptyDeathsCoffer().setSimpleName("Empty death"),

                        new LampHandler().setSimpleName("lamp handler"),

                        new TimedShuffleFractal(45, 200)
                                .addChildren(
                                        new F2PMeleeCombats(() -> shouldTrainMelee() && tree.getSettings().ftpMeleeTraining,
                                                tree.getSettings().attackTarget,
                                                tree.getSettings().strengthTarget,
                                                tree.getSettings().defenceTarget,
                                                () -> {
                                                    int atk = Skills.getRealLevel(Skill.ATTACK);
                                                    int str = Skills.getRealLevel(Skill.STRENGTH);
                                                    int def = Skills.getRealLevel(Skill.DEFENCE);
                                                    if (Skills.getRealLevel(Skill.ATTACK) >= tree.getSettings().attackTarget)
                                                        atk = 100;
                                                    if (Skills.getRealLevel(Skill.STRENGTH) >= tree.getSettings().strengthTarget)
                                                        str = 100;
                                                    if (Skills.getRealLevel(Skill.DEFENCE) >= tree.getSettings().defenceTarget)
                                                        def = 100;
                                                    if (str <= Math.min(atk, def)) return CombatStyle.STRENGTH;
                                                    if (atk <= def) return CombatStyle.ATTACK;
                                                    return CombatStyle.DEFENCE;
                                                }
                                        ).setSimpleName("F2P Melee Combats"),

                                        new F2PMagicBranch(() -> tree.getSettings().ftpMagicTraining
                                                && Skills.getRealLevel(Skill.MAGIC) < tree.getSettings().magicTarget,
                                                tree.getSettings().defenceTarget
                                        ).setSimpleName("F2P Magic Training"),


                                        new DistributedRangeTraining(() -> tree.getSettings().ftpRangeTraining
                                                && Skills.getRealLevel(Skill.RANGED) < tree.getSettings().rangeTarget)
                                                .setSimpleName("F2P range")
                                ),

                        new GetMembershipBranch().setSimpleName("Get Membership"),
                        new MuleOff(() -> tree.getSettings().enforceMaxGP && OwnedItems.count(ItemID.COINS_995) > tree.getSettings().maxGP)
                                .setSimpleName("Mule off (Exceeds max gp)"),

                        new TimedShuffleFractal(45, 350)
                                .addChildren(

                                        new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < tree.getSettings().prayerTarget)
                                                .setSimpleName("Prayer training"),

                                        new ConfigurableMeleeTraining(this::shouldTrainMelee)
                                                .setStyleSupplier(() -> {
                                                    int atk = Skills.getRealLevel(Skill.ATTACK);
                                                    int str = Skills.getRealLevel(Skill.STRENGTH);
                                                    int def = Skills.getRealLevel(Skill.DEFENCE);
                                                    if (Skills.getRealLevel(Skill.ATTACK) >= tree.getSettings().attackTarget)
                                                        atk = 100;
                                                    if (Skills.getRealLevel(Skill.STRENGTH) >= tree.getSettings().strengthTarget)
                                                        str = 100;
                                                    if (Skills.getRealLevel(Skill.DEFENCE) >= tree.getSettings().defenceTarget)
                                                        def = 100;
                                                    if (str <= Math.min(atk, def)) return CombatStyle.STRENGTH;
                                                    if (atk <= def) return CombatStyle.ATTACK;
                                                    return CombatStyle.DEFENCE;
                                                })
                                                .setPrependLogic(() -> {
                                                    if (Client.isDynamicRegion()) {
                                                        Magic.castSpell(Normal.HOME_TELEPORT);
                                                        Antiban.sleepUntil(() -> !Client.isDynamicRegion(), 32_000);
                                                    }
                                                    return false;
                                                })
                                                .setSimpleName("Melee training"),

                                        SandCrabs.getRange(() -> Skills.getRealLevel(Skill.RANGED) < tree.getSettings().rangeTarget)
                                                .setDefenceTarget(tree.getSettings().defenceTarget)
                                                .setSimpleName("Range training")
                                                .setPrependLogic(() -> {
                                                    if (Client.isDynamicRegion()) {
                                                        Magic.castSpell(Normal.HOME_TELEPORT);
                                                        Antiban.sleepUntil(() -> !Client.isDynamicRegion(), 32_000);
                                                    }
                                                    return false;
                                                })
                                ),


                        new ShuffleFractal(() -> Quests.getQuestPoints() < 32).setSimpleName("Questin")
                                .addChildren(
                                        new XMarksTheSpot().setSimpleName("X marks the spot"),
                                        new ClientOfKourend().setSimpleName("Client of kourend"),
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
                                        new PriestInPeril().setSimpleName("PIP"), // 1
                                        new ChildrenOfTheSun().setSimpleName("COS")
                                ),
                        new DragonSlayerOne().setSimpleName("DS1 until shield unlocked"),

                        new TurnInLootKeys().setSimpleName("Turn in loot keys"),
                        new MuleOff()
                                .setSimpleName("Mule Off"),

                        new RechargeWildyWeapon(ItemID.URSINE_CHAINMACE_U, ItemID.URSINE_CHAINMACE, () -> false,
                                tree.getSettings().etherRechargeQuantity).setSimpleName("Recharge Ursine")
                                .setAcceptCondition(() -> tree.getSettings().gdkLoadout.name().contains("URSINE")
                                        && (OwnedItems.contains(ItemID.URSINE_CHAINMACE_U) || needsToRecharge)),

                        new RechargeWildyWeapon(ItemID.VIGGORAS_CHAINMACE_U, ItemID.VIGGORAS_CHAINMACE, () -> false,
                                tree.getSettings().etherRechargeQuantity).setSimpleName("Recharge Viggora")
                                .setAcceptCondition(() -> tree.getSettings().gdkLoadout.name().contains("VIGGORA")
                                        && (OwnedItems.contains(ItemID.VIGGORAS_CHAINMACE_U) || needsToRecharge)),


                        new Fractal(() -> (tree.getSettings().gdkLoadout == GDKLoadout.RCB_LOBSTERS || tree.getSettings().gdkLoadout == GDKLoadout.DARTS_LOBSTERS)
                                && !OwnedItems.contains(ItemVariants.AVAS)
                                && Bank.isCached())
                                .addChildren(
                                        new Fractal(() -> !PaidQuest.ANIMAL_MAGNETISM.isFinished())
                                                .setSimpleName("Get Avas")
                                                .addChildren(
                                                        new MixedCombat(() -> Skills.getRealLevel(Skill.STRENGTH) + Skills.getRealLevel(Skill.ATTACK) < 30
                                                                || Skills.getRealLevel(Skill.HITPOINTS) < 10)
                                                                .setSimpleName("Melee training"),

                                                        new XMarksTheSpot().setSimpleName("X marks the spot"),
                                                        new ClientOfKourend().setSimpleName("Client of Kourend"),
                                                        SandCrabs.getRange(() -> Skills.getRealLevel(Skill.HITPOINTS) < 30
                                                                        || Skills.getRealLevel(Skill.RANGED) < 30)
                                                                .setSimpleName("Range Sandcrabs"),
                                                        new BurnLogs(() -> Skills.getRealLevel(Skill.FIREMAKING) < 4, 7, ItemID.LOGS),
                                                        new SlayerBranch(() -> Skills.getRealLevel(Skill.SLAYER) < 18).setSimpleName("Slayer"),
                                                        new RestlessGhost().setSimpleName("Restless ghost"),
                                                        new ErnestTheChicken().setSimpleName("Ernest the chicken"),
                                                        new PriestInPeril().setSimpleName("Priest in peril"),
                                                        new CraftingBranch(() -> Skills.getRealLevel(Skill.CRAFTING) < 19).setSimpleName("Craft"),
                                                        new MixedChopping(() -> Skills.getRealLevel(Skill.WOODCUTTING) < 35).setSimpleName("Chop"),
                                                        new AnimalMagnetismBranch().setSimpleName("Animal Magnetism")
                                                ),
                                        new GetMoreAvas().setSimpleName("Get more avas")
                                ).setSimpleName("Ava"),

                        new GDKAntiPK().setSimpleName("Anti PK"),
                        new Fractal(() -> !Inventory.contains(x -> KillGreenDragons.getFoodIds().contains(x.getId()))
                                || Inventory.count(ItemID.COINS_995) > 5_000
                                || !tree.getSettings().gdkLoadout.getEquipmentLoadout().isFulfilled()
                                || !tree.getSettings().gdkLoadout.getMethod().hasResourcesToCastSpell()
                                || !Inventory.contains(x -> x.getName().toLowerCase().contains("anti")))
                                .setInventoryLoadout(tree.getSettings().gdkLoadout.getInventoryLoadout().setStrict(true))
                                .setEquipmentLoadout(tree.getSettings().gdkLoadout.getEquipmentLoadout())
                                .setSimpleName("Restocking"),
                        tree.getSettings().gdkLoadout.getMethod().setSimpleName("Kill drags")
                );
        // ernest the chicken webnode
        // WebFinder.getWebFinder().createAndAddNode(new Tile(3109, 3366, 2));
    }

    public static boolean hasLootInBag = true;
    private long loopSpd;
    private long lastTimestamp;
    boolean hasLoadedTrie = false;
    Timer trieRefresh = new Timer(60 * 1000 * 45);
    Timer playerLogTimer = new Timer(60 * 1000);

    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) return tree.run();
        // todo anti pk
//        if (!ScriptSettings.getSettingsData().disablePkList && trieRefresh.finished() || !hasLoadedTrie) {
//            PKTrie.refreshPkerList();
//            trieRefresh.reset();
//            hasLoadedTrie = true;
//        }

        if (ClientSettings.isFeroxExitWarningEnabled()) {
            Logger.info("Disable ferox exit warnings");
            if (Bank.isOpen() || GrandExchange.isOpen()) Widgets.closeAll();
            ClientSettings.toggleFeroxExitWarning(false);
            return ReactionGenerator.getNormal();
        }

        loopSpd = System.currentTimeMillis() - lastTimestamp;
        lastTimestamp = System.currentTimeMillis();
        if (ClientSettings.isAcceptAidEnabled()) {
            if (Widgets.isOpen()) Widgets.closeAll();
            ClientSettings.toggleAcceptAid(false);
            return ReactionGenerator.getNormal();
        }


        if (!Combat.isAutoRetaliateOn() && Combat.isInWild()) {
            if (Widgets.isOpen()) Widgets.closeAll();
            Combat.toggleAutoRetaliate(true);
            return ReactionGenerator.getNormal();
        }

//        Player attackingMe = Players.closest(x -> x.isSkulled() && x.isInteracting(Players.getLocal()));
//        if (Combat.isInWild() && Players.getLocal().isInCombat() && attackingMe != null) {
//            Logger.info("Being attack by " + attackingMe.getName());
//            Logger.info("Level: " + attackingMe.getLevel());
//            attackingMe.getEquipment().forEach(x -> Logger.info("Equipment " + x.getId() + " " + x.getName()));
//        }


        Player attckingMe = Players.closest(x -> x.isSkulled() && x.isInteracting(Players.getLocal()));
        if (Combat.isInWild() && Players.getLocal().isInCombat() && attckingMe != null && playerLogTimer.finished()) {
            Logger.info("Being attack by " + attckingMe.getName());
            Logger.info("Level: " + attckingMe.getLevel());
            Logger.info("My Level: " + Combat.getCombatLevel());
            Logger.info("Wilderness level: " + Combat.getWildernessLevel());
//            Logger.info("Predicted: " + AntiPkNode.canAttackMe(attckingMe));
            attckingMe.getEquipment().forEach(x -> Logger.info("Equipment " + x.getId() + " " + x.getName()));
            playerLogTimer.reset();
            PKTrie.reportPker(attckingMe.getName());
        }

        if (ClientSettings.isWorldHopConfirmationEnabled()) {
            Logger.info("Disabling hop confirmations");
            ClientSettings.toggleWorldHopConfirmation(false);
            return ReactionGenerator.getQuick();
        }

        // dynamic region check for nmz training
        if (!Combat.isInWild() && !Client.isDynamicRegion() && Prayers.isActive(Prayer.PROTECT_FROM_MELEE)) {
            Prayers.toggle(false, Prayer.PROTECT_FROM_MELEE);
        }


        if (ClientSettings.isWorldHopConfirmationEnabled()) {
            ClientSettings.toggleWorldHopConfirmation(false);
            return ReactionGenerator.getNormal();
        }

        if (shouldHop && Client.hasMembersAccess() && !Players.getLocal().isInCombat()) {
            if (WorldHopper.hopWorld(
                    Worlds.getRandomWorld(x -> !x.isF2P() && x.isNormal() && x.getWorld() != 401 && x.getMinimumLevel() < Combat.getCombatLevel())
            )) shouldHop = false;
            return ReactionGenerator.getNormal();
        }

        if (Inventory.contains(ItemID.LOOTING_BAG_CLOSED)) {
            if (Widgets.isOpen()) Widgets.closeAll();
            Logger.info("Opening looting bag");
            Inventory.interact(ItemID.LOOTING_BAG_CLOSED, "Open");
        }

        if (Bank.isOpen() && ItemVariants.LOOTING_BAG.getItem() != null && hasLootInBag) {
            Logger.info("Emptying looting bag");
            if (!Bank.isOpen()) {
                if (Walking.shouldWalk()) BankUtil.openClosest();
                return ReactionGenerator.getQuick();
            }

            EmptyLootingBagEvent.Response r = new EmptyLootingBagEvent().executed();
            Logger.info("Empty bag: " + r);
            if (r == EmptyLootingBagEvent.Response.BAG_EMPTY) hasLootInBag = false;
            return ReactionGenerator.getQuick();
        }

        if (!Walking.isRunEnabled() && Walking.getRunEnergy() > 30) {
            Walking.toggleRun();
        }

        if (ClientSettings.isSellPriceWarningEnabled()) {
            if (Bank.isOpen() || GrandExchange.isOpen()) Widgets.closeAll();
            ClientSettings.toggleSellPriceWarning(false);
            return ReactionGenerator.getNormal();
        }

        if (ClientSettings.isBuyPriceWarningEnabled()) {
            if (Bank.isOpen() || GrandExchange.isOpen()) Widgets.closeAll();
            ClientSettings.toggleBuyPriceWarning(false);
            return ReactionGenerator.getNormal();
        }
        return tree.run();
    }

    @Override
    public String[] getPaintInfo() {
        String muleOff = "-";
        Player local = Players.getLocal();
        String target = "";
        if (local != null) {
            Character tgt = local.getInteractingCharacter();
            if (tgt != null) target = tgt.getName();
        }

        return new String[]{
                FractalAPI.hierarchy + " " + FractalAPI.status,
                "Deaths: " + deathCount,
                "LoopSpd: " + loopSpd,
                "target: " + target,
        };
    }

    @Override
    public String getScriptName() {
        return "cCGreenDragonFarm";
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

    @Override
    public void onMessage(Message message) {
        if (message.getType() == MessageType.PLAYER) return;
        if (message.getMessage().toLowerCase().contains("you are dead")) {
            shouldHop = true;
            deathCount++;
        }

        if (message.getMessage().toLowerCase().contains("not enough revenant ether")) {
            needsToRecharge = true;
        }

        if (message.getMessage().toLowerCase().contains("has run out of revenant")) {
            needsToRecharge = true;
        }

        if (message.getMessage().toLowerCase().contains("chainmace is out of charges")) {
            needsToRecharge = true;
        }
        if (message.getMessage().toLowerCase().contains("giving it a total of")) {
            needsToRecharge = false;
        }
    }

    @Override
    public void onLootBagItemAdded(Item item) {
        Logger.info("Loot bag added");
        grossGp += item.getLivePrice() * item.getAmount();
    }


    @Override
    public void onInventoryItemAdded(Item item) {
        if (!Combat.isInWild()) return;
        grossGp += item.getLivePrice() * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        if (!Combat.isInWild()) return;
        int quantity = incoming.getAmount() - existing.getAmount();
        if (quantity >= 0) return;

        grossGp += incoming.getLivePrice() * quantity;
    }

    private boolean shouldTrainMelee() {
        GDKSettings settings = tree.getSettings();
        if (Skills.getRealLevel(Skill.ATTACK) < settings.attackTarget) return true;
//        if (Skills.getRealLevel(Skill.DEFENCE) < settings.defenceTarget) return true;
        return Skills.getRealLevel(Skill.STRENGTH) < settings.strengthTarget;
    }
}
