package org.dreambot.scripts;

import org.dreambot.ChangeAlchWarning;
import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.method.teletabs.poh.PohTabsBranch;
import org.dreambot.behaviour.method.teletabs.poh.PohTeleTabOption;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.training.magic.ConfigurableMagicBranch;
import org.dreambot.behaviour.training.nmz.ConfigurableMeleeTraining;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.PohTeleTabSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

public class PohTeleTabsScript extends PseudoScript implements ItemContainerListener {
    FractalRoot tree = new FractalRoot(new PohTeleTabSettings(), getScriptName());
    AtomicBoolean isBreaking = new AtomicBoolean(false);
    BooleanSupplier breakingSupplier = () -> isBreaking.get();
    Area MONEY_ZONE = new Area(1652, 3784, 1697, 3751);

    @Override
    public void onArgs(String... args) {
        for (String arg : args) {
            Arrays.stream(PohTeleTabOption.values())
                    .filter(x -> x.name().toLowerCase().contains(arg))
                    .findFirst()
                    .ifPresent(x -> {
                        Logger.info("Set tab option to " + x);
                        SettingsRepository.findInstanceOf(new PohTeleTabSettings()).option = x;
                    });
        }
    }

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);


        MuleOff.LOOT = new int[]{
                ItemID.VARROCK_TELEPORT,
                ItemID.LUMBRIDGE_TELEPORT,
                ItemID.FALADOR_TELEPORT,
                ItemID.CAMELOT_TELEPORT,
                ItemID.TELEPORT_TO_HOUSE,
                ItemID.KOUREND_CASTLE_TELEPORT,
                ItemID.ARDOUGNE_TELEPORT,
                ItemID.WATCHTOWER_TELEPORT,
                ItemID.AMULET_OF_GLORY,
                ItemID.RING_OF_WEALTH
        };

        tree.setSimpleName("cCTeleTabs");
        tree.addChildren(
                new GetMembershipBranch().setSimpleName("Get Membership"),
                new AutoProggy().setSimpleName("Auto proggy"),
                new AntibanFractal().setSimpleName("Antiban"),

                // todo construction and building the lectern

                new ConfigurableMeleeTraining(() -> Skills.getRealLevel(Skill.ATTACK) < 30)
                        .setStyleSupplier(() -> Skills.getRealLevel(Skill.ATTACK) > Skills.getRealLevel(Skill.STRENGTH) ? CombatStyle.STRENGTH : CombatStyle.ATTACK)
                        .setSimpleName("Train atk/str so get staff reqs"),

                new ConfigurableMagicBranch(() -> Skills.getRealLevel(Skill.MAGIC) < SettingsRepository.findInstanceOf(new PohTeleTabSettings()).option.magicReq)
                        .setSimpleName("Magic till " + SettingsRepository.findInstanceOf(new PohTeleTabSettings()).option.magicReq),

                new MuleOff()
                        .setSimpleName("Mule Off"),

                new PohTabsBranch(() -> true, SettingsRepository.findInstanceOf(new PohTeleTabSettings()).option)
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
        return "cCPOHTeleTabs";
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
        if (!Client.isDynamicRegion()) return;
        if (!item.getName().toLowerCase().contains("teleport")) return;
        grossGp += item.getLivePrice() * item.getAmount() - LivePrices.get(ItemID.SOFT_CLAY) - LivePrices.get(ItemID.LAW_RUNE);
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        if (!Client.isDynamicRegion()) return;
        if (!incoming.getName().toLowerCase().contains("teleport")) return;
        int quantity = incoming.getAmount() - existing.getAmount();
        if (quantity <= 0) return;
        grossGp += incoming.getLivePrice() * quantity - LivePrices.get(ItemID.SOFT_CLAY) - LivePrices.get(ItemID.LAW_RUNE);
    }
}
