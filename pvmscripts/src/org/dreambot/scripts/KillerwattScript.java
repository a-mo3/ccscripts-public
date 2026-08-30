package org.dreambot.scripts;

import org.dreambot.ChangeAlchWarning;
import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.impl.EntranceWebNode;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.behaviour.misc.GetMoreAvas;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.misc.SandCrabs;
import org.dreambot.behaviour.quests.RestlessGhost;
import org.dreambot.behaviour.quests.animalmagnetism.AnimalMagnetismBranch;
import org.dreambot.behaviour.quests.animalmagnetism.util.LeaveAvaRoom;
import org.dreambot.behaviour.quests.earnestthechicken.ErnestTheChicken;
import org.dreambot.behaviour.quests.impcatcher.ImpCatcher;
import org.dreambot.behaviour.quests.pip.PriestInPeril;
import org.dreambot.behaviour.training.crafting.CraftingBranch;
import org.dreambot.behaviour.training.farming.FarmingBranch;
import org.dreambot.behaviour.training.firemaking.BurnLogs;
import org.dreambot.behaviour.training.magic.EnchantDueling;
import org.dreambot.behaviour.training.magic.EnchantRecoils;
import org.dreambot.behaviour.training.nmz.ConfigurableMeleeTraining;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.behaviour.training.slayer.SlayerBranch;
import org.dreambot.behaviour.training.slayer.SlayerTaskMap;
import org.dreambot.behaviour.training.slayer.behaviour.StandardCombat;
import org.dreambot.behaviour.training.woodcutting.MixedChopping;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.KillerwattSettings;
import org.dreambot.scriptdata.loadouts.KillerwattLoadout;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

public class KillerwattScript extends PseudoScript implements ItemContainerListener, ChatListener {
    FractalRoot<KillerwattSettings> tree = new FractalRoot<>(new KillerwattSettings(), getScriptName());
    AtomicBoolean isBreaking = new AtomicBoolean(false);
    BooleanSupplier breakingSupplier = () -> isBreaking.get();
    Area KILLERWATTS = new Area(2646, 5229, 2683, 5196, 2);

    Timer sackFullRecently = new Timer(2400);
    boolean hasHerbInSack = true;

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);


        WithdrawLoadoutEvent.sellList = new int[]{
                ItemID.AMULET_OF_GLORY
        };

        // ernest the chicken webnode
        // WebFinder.getWebFinder().createAndAddNode(new Tile(3109, 3366, 2));
        // fix the entrance node on the interdimensional rift
        WebFinder.getWebFinder().getNodesWithin(2, new Tile(3110, 3363, 2))
                .forEach(x -> {
                    Logger.info("node " + x.getType());
                    if (x instanceof EntranceWebNode) {
                        EntranceWebNode e = (EntranceWebNode) x;
                        Logger.info("Entrance " + e.getAction() + e.getEntityName());
                        e.setAction("Enter");
                        e.setEntityName("Interdimensional rift");
                    }
                });

        SlayerTaskMap.minLootValue = tree.getSettings().minLootValue;
        tree.setSimpleName("cCKillerwatts");
        tree.addChildren(
                new GetMembershipBranch()
                        .setSimpleName("Get Membership"),

                new Fractal(() -> Skills.getRealLevel(Skill.SLAYER) < 37)
                        .setSimpleName("Training")
                        .addChildren(
                                new ConfigurableMeleeTraining(() -> Skills.getRealLevel(Skill.STRENGTH) < tree.getSettings().preSlayerCombatTarget)
                                        .setSimpleName("Pre Slayer Combat Training"),
                                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < tree.getSettings().prayerTarget)
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

                                new SlayerBranch(() -> Skills.getRealLevel(Skill.SLAYER) < 37)
                                        .setPrependLogic(() -> {
                                            if (!Combat.isAutoRetaliateOn()) {
                                                if (Widgets.isOpen()) Widgets.closeAll();
                                                Combat.toggleAutoRetaliate(true);
                                            }
                                            return false;
                                        })
                                        .setSimpleName("Slayer Until Killerwatt unlocked @ 55")
                        ),
                new Fractal(() -> tree.getSettings().rangeTarget > 1 && !PaidQuest.ANIMAL_MAGNETISM.isFinished())
                        .addChildren(
                                new BurnLogs(() -> Skills.getRealLevel(Skill.FIREMAKING) < 4, 7, ItemID.LOGS).setSimpleName("Burn logs need it for slayer"),
                                new SlayerBranch(() -> Skills.getRealLevel(Skill.SLAYER) < 18).setSimpleName("Slayer"),
                                new RestlessGhost().setSimpleName("Restless ghost"),
                                new ErnestTheChicken().setSimpleName("Ernest the chicken"),
                                new PriestInPeril().setSimpleName("Priest in peril"),
                                new CraftingBranch(() -> Skills.getRealLevel(Skill.CRAFTING) < 19).setSimpleName("Craft"),
                                new MixedChopping(() -> Skills.getRealLevel(Skill.WOODCUTTING) < 35).setSimpleName("Chop"),
                                new AnimalMagnetismBranch().setSimpleName("Animal Magnetism")
                        ),

                new Fractal(() -> !Combat.isInWild() && Bank.isCached()
                        && !OwnedItems.containsAny(ItemID.AVAS_ASSEMBLER, ItemID.AVAS_ACCUMULATOR, ItemID.AVAS_ATTRACTOR)
                        && tree.getSettings().rangeTarget > 1)
                        .setSimpleName("Get Avas").addChildren(
                                new GetMoreAvas().setSimpleName("More avas")
                        ),
                new LeaveAvaRoom().setSimpleName("Leave avas"),
                SandCrabs.getRange(() -> Skills.getRealLevel(Skill.RANGED) < tree.getSettings().rangeTarget)
                        .setSimpleName("Range training"),

                new ErnestTheChicken().setSimpleName("Ernest the chicken quest"),
                new FarmingBranch(() -> Bank.isCached() && !OwnedItems.contains(ItemID.HERB_SACK)).setSimpleName("Farming for herb pouch"),

                new MuleOff()
                        .setSimpleName("Mule Off"),

                new StandardCombat(KILLERWATTS, "Killerwatt", ItemID.SHARK, ItemID.SWORDFISH)
                        .setStyleSupplier(() -> CombatStyle.RANGED_RAPID)
                        .setEquipmentLoadout(KillerwattLoadout.DHIDE_DARTS)
                        .setInventoryLoadout(KillerwattLoadout.FOOD)
                        .setSimpleName("Kill da watts")

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

        if (hasHerbInSack && !Inventory.isFull() && BankLocation.GRAND_EXCHANGE.distance(Players.getLocal().getTile()) < 50) {
            Inventory.interact(ItemID.HERB_SACK, "Empty");
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
        return "cCKillerWattFarm";
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
        if (!KILLERWATTS.contains(Players.getLocal())) return;
        Logger.info("item added");
        grossGp += (item.getLivePrice() + 1) * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        if (!KILLERWATTS.contains(Players.getLocal())) return;
        Logger.info("item changed");
        int quantity = incoming.getAmount() - existing.getAmount();
        if (quantity <= 0) {
            Logger.info("Quantity under zero");
            return;
        }

        grossGp += (incoming.getLivePrice() + 1) * quantity;
    }

    @Override
    public void onInventoryItemSwapped(Item incoming, Item outgoing) {
        if (!KILLERWATTS.contains(Players.getLocal())) return;
        Logger.info("item swapped");
        int quantity = incoming.getAmount() - outgoing.getAmount();
        if (quantity <= 0) return;

        grossGp += (incoming.getLivePrice() + 1) * quantity;
    }

    @Override
    public void onMessage(Message message) {
        if (message.getType() == MessageType.PLAYER) return;
        // todo sack full message reset timer
        String msg = message.getMessage();
        if (msg.contains("have no grimy herbs in your inventory") || msg.contains("for the herbs")) {
            sackFullRecently.finished();
        }

        if (msg.contains("herb sack is empt")) {
            hasHerbInSack = false;
        }
    }
}
