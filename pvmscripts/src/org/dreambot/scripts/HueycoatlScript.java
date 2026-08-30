package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.Location;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.behaviour.GetOff330;
import org.dreambot.behaviour.method.huey.*;
import org.dreambot.behaviour.method.huey.comms.HueyCommsClient;
import org.dreambot.behaviour.method.huey.mainfight.HueyMainAttack;
import org.dreambot.behaviour.method.huey.mainfight.HueyWaveDodge;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.misc.MuleOffItem;
import org.dreambot.behaviour.quests.childrenofthesun.ChildrenOfTheSun;
import org.dreambot.behaviour.training.magic.ConfigurableMagicBranch;
import org.dreambot.behaviour.training.nmz.ConfigurableMeleeTraining;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.behaviour.training.slayer.SlayerTaskMap;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.antiban.AntibanFractal;
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
import org.dreambot.scriptdata.HueycoatlSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.awt.*;
import java.text.DecimalFormat;

public class HueycoatlScript extends PseudoScript implements ItemContainerListener, ChatListener {
    FractalRoot<HueycoatlSettings> tree = new FractalRoot<>(new HueycoatlSettings(), getScriptName());
    Integer partyWorld = null;

    @Override
    public void onArgs(String... args) {
        for (String arg : args) {
            try {

                for (HueyLoadout value : HueyLoadout.values()) {
                    if (value.name().equals(arg)) {
                        Logger.info("Set loadout to " + value.name());
                        tree.getSettings().loadout = value;
                    }
                }

                partyWorld = Integer.parseInt(arg);
            } catch (NumberFormatException e) {
                continue;
            }
        }
    }

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);

        Logger.info("Init");
        SlayerTaskMap.minLootValue = 1000;
        tree.setSimpleName("cCHueycoatl");
        AbstractEvent.globalInterruptCondition = () -> Inventory.contains("Coin pouch");

        MuleOff.LOOT = new int[]{
                ItemID.HUEYCOATL_HIDE,
                ItemID.SOILED_PAGE,
                ItemID.TOME_OF_EARTH,

                ItemID.RUNE_MACE,
                ItemID.RUNE_SCIMITAR,
                ItemID.RUNE_PLATESKIRT,
                ItemID.ADAMANT_PLATEBODY,
                ItemID.ADAMANT_PLATEBODY,
                ItemID.RUNE_PLATEBODY,

                // todo consider if we're using these runes, would only matter for death
                ItemID.DEATH_RUNE,
                ItemID.COSMIC_RUNE,
                ItemID.NATURE_RUNE,

                ItemID.HUASCA_SEED,
                ItemID.AVANTOE_SEED,
                ItemID.KWUARM_SEED,
                ItemID.LANTADYME_SEED,
                ItemID.TOADFLAX_SEED,
                ItemID.TORSTOL_SEED,
                ItemID.RANARR_SEED,

                ItemID.ADAMANT_BOLTSUNF,
                ItemID.AIR_ORB,
                ItemID.RAW_SHARK,
                ItemID.SUNFIRE_SPLINTERS,
                ItemID.DRAGON_BONES,
                ItemID.CANNONBALL,
                ItemID.ADAMANTITE_ORE,
                ItemID.RUNE_DART_TIP,
                ItemID.LIMPWURT_ROOT,
                ItemID.BLOOD_MOON_CHESTPLATE_BROKEN,
                ItemID.BLOOD_MOON_HELM_BROKEN,
                ItemID.BLOOD_MOON_TASSETS_BROKEN,
                ItemID.PRAYER_POTION3,
                ItemID.PRAYER_POTION2,
                ItemID.PRAYER_POTION1
        };
        WithdrawLoadoutEvent.sellList = MuleOff.LOOT;

        MuleOff.muleOffItems = new MuleOffItem[]{
                new MuleOffItem(
                        ItemID.DRAGON_HUNTER_WAND, () -> true, tree.getSettings().loadout == HueyLoadout.MAGE_AHRIMS_DHW ? 1 : 0
                )
        };

        tree.addChildren(
                new GetMembershipBranch()
                        .setSimpleName("Get Membership"),
                new EmptyDeathsCoffer().setSimpleName("Death coffer"),

                new TimedShuffleFractal(40, 190)
                        .addChildren(
                                // train prayer
                                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < Math.max(43, tree.getSettings().prayerTarget))
                                        .setSimpleName("Prayer training"),

                                // train magic
                                new ConfigurableMagicBranch(() -> Skills.getRealLevel(Skill.MAGIC) < tree.getSettings().magicTarget)
                                        .setSimpleName("Magic training"),

                                // train melee
                                new ConfigurableMeleeTraining(() -> Skills.getRealLevel(Skill.DEFENCE) < tree.getSettings().meleeTarget)
                                        .setSimpleName("Melee training")
                        ),

                // quest has no reqs
                // quest
                new ChildrenOfTheSun().setSimpleName("Children of the sun"),

                new Fractal(() -> partyWorld != null && Worlds.getCurrent().getWorld() != partyWorld)
                        .setPrependLogic(() -> {
                            Logger.info("Hop to party world " + partyWorld);
                            WorldHopper.hopWorld(partyWorld);
                            return true;
                        })
                        .setSimpleName("Enforce party world"),

                new MuleOff().setSimpleName("Mule off"),
                new GetOff330().setSimpleName("Get off 330"),
                new HueyRegroup().setSimpleName("Regrouping"),
                new HueyMainBranch(() -> HueyData.isInHueyFight() && NPCs.closest("Hueycoatl body") == null, tree.getSettings()).setSimpleName("Main"),
                new HueyTailBranch(HueyData::isInHueyFight, tree.getSettings()).setSimpleName("Tail"),
                new HueyGearUp(() -> true,
                        tree.getSettings().loadout,
                        tree.getSettings()
                ).setSimpleName("Gear up")
        );
        HueyData.useBurningClaws = tree.getSettings().useBurningClawsSpec;
        if (tree.getSettings().teamSize > 1 && tree.getSettings().instanceTeam) {
            Logger.info("Init huey comms " + tree.getSettings().teamSize);
            HueyCommsClient.getInstance(tree.getSettings().teamSize, tree.getSettings().regionPreference);
        }
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
    public void onScriptPaint(Graphics g) {
        Tile dest = Walking.getDestination();
//        g.setColor(Color.white);
//        for (GraphicsObject obj : GraphicsObjects.all(HueyData.isShockwave)) {
//            g.drawPolygon(obj.getTile().getPolygon());
//        }
        g.setColor(Color.GRAY);
        if (dest != null) g.drawPolygon(dest.getPolygon());
        g.setColor(Color.BLUE);

        Tile srvTile = Players.getLocal().getServerTile();
        if (srvTile != null) g.drawPolygon(srvTile.getPolygon());
    }

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

        String teamInfo = "-";
        if (tree != null && tree.getSettings().instanceTeam && HueyCommsClient.currentTeam != null) {
            teamInfo = String.format("Team: %d - Size: %d - amLeader: %b", HueyCommsClient.currentTeam.getTeamId(),
                    HueyCommsClient.currentTeam.getMembers().size(),
                    HueyCommsClient.currentTeam.getTeamLeader().equals(Players.getLocal().getName())

            );

        }

        return new String[]{
                FractalAPI.hierarchy + " " + FractalAPI.status,
                "target: " + target,
                "Wave dir: " + HueyWaveDodge.nextWave + " - " + HueyMainAttack.lastTailSide,
                "Magic " + Client.getGameTick() + " " + HueyWaveDodge.magicMoveByTick,
                "Move " + HueyWaveDodge.moveTick,
                "Ori " + (local == null ? " - " : local.getOrientation()),
                "Ping " + Worlds.getCurrent().getPing(),
                teamInfo,
                "Regroup " + HueyCommsClient.needsToRegroup
        };
    }

    @Override
    public String getScriptName() {
        return "cCHueycoatl";
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
    public void onInventoryItemAdded(Item item) {
        if (!Client.isDynamicRegion() && !HueyData.HUEY_MAIN_AREA.contains(Players.getLocal())) return;
        if (Bank.isOpen()) return;
        grossGp += item.getLivePrice() * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        if (!Client.isDynamicRegion() && !HueyData.HUEY_MAIN_AREA.contains(Players.getLocal())) return;
        int quantity = incoming.getAmount() - existing.getAmount();
        if (quantity <= 0) return;
        if (Bank.isOpen()) return;

        grossGp += incoming.getLivePrice() * quantity;
    }

    @Override
    public void onMessage(Message message) {
        if (message.getMessage().toLowerCase().contains("you have died")) {
            Logger.info("--- DEATH HERE ---");
        }
    }

    @Override
    public void onExit() {
        Logger.info("On exit");
        HueyCommsClient.getInstance(tree.getSettings().teamSize, Location.GERMANY).close();
    }
}
