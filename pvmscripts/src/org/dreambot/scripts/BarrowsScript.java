package org.dreambot.scripts;

import org.dreambot.ChangeAlchWarning;
import org.dreambot.PseudoScript;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.behaviour.GetOff330;
import org.dreambot.behaviour.method.barrows.BarrowsBrother;
import org.dreambot.behaviour.method.barrows.BarrowsKillBrothersBranch;
import org.dreambot.behaviour.method.barrows.BarrowsRestock;
import org.dreambot.behaviour.method.barrows.BarrowsVarbits;
import org.dreambot.behaviour.method.barrows.handlecrypt.HandleCryptBranch;
import org.dreambot.behaviour.misc.GetMoreAvas;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.misc.MuleOffItem;
import org.dreambot.behaviour.misc.RechargeTrident;
import org.dreambot.behaviour.quests.RestlessGhost;
import org.dreambot.behaviour.quests.XMarksTheSpot;
import org.dreambot.behaviour.quests.animalmagnetism.AnimalMagnetismBranch;
import org.dreambot.behaviour.quests.earnestthechicken.ErnestTheChicken;
import org.dreambot.behaviour.quests.pip.PriestInPeril;
import org.dreambot.behaviour.training.crafting.CraftingBranch;
import org.dreambot.behaviour.training.firemaking.BurnLogs;
import org.dreambot.behaviour.training.magic.ConfigurableMagicBranch;
import org.dreambot.behaviour.training.nmz.ConfigurableMeleeTraining;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.behaviour.training.range.ConfigurableRangeTraining;
import org.dreambot.behaviour.training.slayer.SlayerBranch;
import org.dreambot.behaviour.training.slayer.SlayerTaskMap;
import org.dreambot.behaviour.training.woodcutting.MixedChopping;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.discordwebhook.AutoProggy;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.TimedShuffleFractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.AbstractEvent;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.BarrowsSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.text.DecimalFormat;
import java.util.Arrays;

public class BarrowsScript extends PseudoScript implements ItemContainerListener, ChatListener {
    FractalRoot<BarrowsSettings> tree = new FractalRoot<>(new BarrowsSettings(), getScriptName());
    Area BARROWS_CRYPT = new Area(3521, 9725, 3581, 9665);

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);


        Logger.info("Init");
        SlayerTaskMap.minLootValue = 1000;
        tree.setSimpleName("cCBarrows");
        AbstractEvent.globalInterruptCondition = () -> Inventory.contains("Coin pouch");

        MuleOff.LOOT = new int[]{
                ItemID.AHRIMS_HOOD,
                ItemID.AHRIMS_STAFF,
                ItemID.AHRIMS_ROBETOP,
                ItemID.AHRIMS_ROBESKIRT,

                ItemID.DHAROKS_HELM,
                ItemID.DHAROKS_GREATAXE,
                ItemID.DHAROKS_PLATEBODY,
                ItemID.DHAROKS_PLATELEGS,

                ItemID.GUTHANS_HELM,
                ItemID.GUTHANS_WARSPEAR,
                ItemID.GUTHANS_PLATEBODY,
                ItemID.GUTHANS_CHAINSKIRT,

                ItemID.KARILS_COIF,
                ItemID.KARILS_CROSSBOW,
                ItemID.KARILS_LEATHERTOP,
                ItemID.KARILS_LEATHERSKIRT,

                ItemID.BOLT_RACK,

                ItemID.TORAGS_HELM,
                ItemID.TORAGS_HAMMERS,
                ItemID.TORAGS_PLATEBODY,
                ItemID.TORAGS_PLATELEGS,

                ItemID.VERACS_HELM,
                ItemID.VERACS_FLAIL,
                ItemID.VERACS_BRASSARD,
                ItemID.VERACS_PLATESKIRT,

                ItemID.DEATH_RUNE,
                ItemID.CHAOS_RUNE,
                ItemID.AMULET_OF_GLORY_UNCHARGED,
                ItemID.RING_OF_WEALTH,
                ItemID.MIND_RUNE,
                ItemID.SAPPHIRE_RING,
                ItemID.RING_OF_RECOIL,
                ItemID.EMERALD_RING,
                ItemID.TRIDENT_OF_THE_SEAS_FULL,
                ItemID.PRAYER_POTION1,
                ItemID.PRAYER_POTION2,
                ItemID.PRAYER_POTION3,

        };
        WithdrawLoadoutEvent.sellList = new int[]{
                ItemID.AHRIMS_HOOD,
                ItemID.AHRIMS_STAFF,
                ItemID.AHRIMS_ROBETOP,
                ItemID.AHRIMS_ROBESKIRT,

                ItemID.DHAROKS_HELM,
                ItemID.DHAROKS_GREATAXE,
                ItemID.DHAROKS_PLATEBODY,
                ItemID.DHAROKS_PLATELEGS,

                ItemID.GUTHANS_HELM,
                ItemID.GUTHANS_WARSPEAR,
                ItemID.GUTHANS_PLATEBODY,
                ItemID.GUTHANS_CHAINSKIRT,

                ItemID.KARILS_COIF,
                ItemID.KARILS_CROSSBOW,
                ItemID.KARILS_LEATHERTOP,
                ItemID.KARILS_LEATHERSKIRT,

                ItemID.BOLT_RACK,

                ItemID.TORAGS_HELM,
                ItemID.TORAGS_HAMMERS,
                ItemID.TORAGS_PLATEBODY,
                ItemID.TORAGS_PLATELEGS,

                ItemID.VERACS_HELM,
                ItemID.VERACS_FLAIL,
                ItemID.VERACS_BRASSARD,
                ItemID.VERACS_PLATESKIRT,

                ItemID.AMULET_OF_GLORY_UNCHARGED,
                ItemID.RING_OF_WEALTH,
                ItemID.TRIDENT_OF_THE_SEAS_FULL
        };

        MuleOff.muleOffItems = new MuleOffItem[]{
        };

        tree.addChildren(
                new GetMembershipBranch()
                        .setSimpleName("Get Membership"),
                new EmptyDeathsCoffer().setSimpleName("Death coffer"),

                new TimedShuffleFractal(20, 75)
                        .addChildren(
                                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < tree.getSettings().prayerTarget).setSimpleName("Prayer"),
                                new ConfigurableMeleeTraining(() -> Skills.getRealLevel(Skill.STRENGTH) < 30
                                        || Skills.getRealLevel(Skill.HITPOINTS) < 10)
                                        .setSimpleName("Melee training for quests"),
                                new ConfigurableMagicBranch(() -> Skills.getRealLevel(Skill.MAGIC) < tree.getSettings().magicTarget)
                                        .setSimpleName("Magic training"),
                                new CraftingBranch(() -> Skills.getRealLevel(Skill.CRAFTING) < 19).setSimpleName("Craft"),
                                new MixedChopping(() -> Skills.getRealLevel(Skill.WOODCUTTING) < 35).setSimpleName("Chop"),

                                new ConfigurableRangeTraining(() -> Skills.getRealLevel(Skill.RANGED) < tree.getSettings().rangeTarget
                                        || Skills.getRealLevel(Skill.DEFENCE) < 40, 40)
                                        .setSimpleName("Range until 40 def & " + tree.getSettings().rangeTarget + " range")
                        ),

                new Fractal(() -> Skills.getRealLevel(Skill.RANGED) < tree.getSettings().rangeTarget
                        || (tree.getSettings().rangeTarget > 1 && !PaidQuest.ANIMAL_MAGNETISM.isFinished()))
                        .setSimpleName("Range Training")
                        .addChildren(
                                new XMarksTheSpot().setSimpleName("x marks"), // not certain what this is for but not going to risk changing rn

                                new BurnLogs(() -> Skills.getRealLevel(Skill.FIREMAKING) < 4, 7, ItemID.LOGS).setSimpleName("Burn logs need it for slayer"),
                                new SlayerBranch(() -> Skills.getRealLevel(Skill.SLAYER) < 18).setSimpleName("Slayer"),
                                new RestlessGhost().setSimpleName("Restless ghost"),
                                new ErnestTheChicken().setSimpleName("Ernest the chicken"),
                                new PriestInPeril().setSimpleName("Priest in peril"),
                                new AnimalMagnetismBranch().setSimpleName("Animal Magnetism")
                        ),

                new PriestInPeril().setSimpleName("Priest in peril"),

                new EmptyDeathsCoffer().setSimpleName("Emptying deaths coffer"),
                new RechargeTrident().setSimpleName("Charge trident"),

                new Fractal(() -> BankLocation.GRAND_EXCHANGE.distance(Players.getLocal().getTile()) < 50
                        && (MuleOff.timer == null || MuleOff.timer.finished()))
                        .addChildren(
                                new MuleOff().setSimpleName("Mule Off")
                        )
                        .setSimpleName("Safe"),
//                new MuleOff().setSimpleName("Mule Off"),
                new GetMoreAvas(() -> tree.getSettings().rangeTarget > 0).setSimpleName("Get another avas"),

                new HandleCryptBranch(() -> !BarrowsRestock.forceRestock && BarrowsBrother.killedBrothersCount() >= 5, tree.getSettings()),
                new BarrowsKillBrothersBranch(() -> !BarrowsRestock.forceRestock && BarrowsRestock.BARROWS.contains(Players.getLocal()) || Arrays.stream(BarrowsBrother.values())
                        .anyMatch(x -> x.tombArea.contains(Players.getLocal())), tree.getSettings().loadout)
                        .setSimpleName("Kill brothers"),
                new BarrowsRestock(() -> true,
//                        () -> BankLocation.GRAND_EXCHANGE.getArea(100).contains(Players.getLocal())
//                        || BankLocation.FEROX_ENCLAVE.getArea(50).contains(Players.getLocal()),
                        tree.getSettings().loadout
                ).setSimpleName("Get loadout")
        );
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

        if (!ChangeAlchWarning.setHighAlchWarning(500_000)) {
            Logger.info("Setting alch warning price");
            return ReactionGenerator.getNormal();
        }

        if (Inventory.contains(ItemID.CRYPT_MAP)) {
            Logger.info("Study crypt map");
            if (Widgets.isOpen()) Widgets.closeAll();
            Inventory.interact(ItemID.CRYPT_MAP, "Study");
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

        BasicWebNode n = new BasicWebNode(1, 1, 1);
        BasicWebNode ni = new BasicWebNode(2, 1, 1);
        n.addDualConnections(ni);
        int rewardPot = PlayerSettings.getBitValue(BarrowsVarbits.BARROWS_REWARD_POTENTIAL);
        Character attackingMe = NPCs.closest(x -> x.isInteracting(Players.getLocal()));

        return new String[]{
                FractalAPI.hierarchy + " " + FractalAPI.status,
                "target: " + target,
                "target me " + (attackingMe == null ? "- " : attackingMe.getName()),
                "Tunnel brother " + BarrowsKillBrothersBranch.tunnelBrother,
                "Door North " + PlayerSettings.getBitValue(BarrowsVarbits.BARROWS_DOOR_NORTH),
                "Door West " + PlayerSettings.getBitValue(BarrowsVarbits.BARROWS_DOOR_WEST),
                "Door East " + PlayerSettings.getBitValue(BarrowsVarbits.BARROWS_DOOR_EAST),
                "Door South " + PlayerSettings.getBitValue(BarrowsVarbits.BARROWS_DOOR_SOUTH),
                "Reward score " + rewardPot
        };
    }

    @Override
    public String getScriptName() {
        return "cCBarrowsFarm";
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
//        if (quantity <= 0) return
//        grossGp += incoming.getLivePrice() * quantity;
//    }
//
    @Override
    public void onMessage(Message message) {
        if (message.getMessage().toLowerCase().contains("you have died")) {
            Logger.info("--- DEATH HERE ---");
            BarrowsRestock.forceRestock = true;
        }
        if (message.getMessage().toLowerCase().contains("you are dead")) {
            Logger.info("--- DEATH HERE ---");
            BarrowsRestock.forceRestock = true;
        }
    }

    private boolean needsToTrain() {
        return !PaidQuest.ANIMAL_MAGNETISM.isFinished()
                || Skills.getRealLevel(Skill.RANGED) < 50
                || Skills.getRealLevel(Skill.MAGIC) < 75
                || Skills.getRealLevel(Skill.PRAYER) < 43
                || Skills.getRealLevel(Skill.DEFENCE) < 40;
    }
}
