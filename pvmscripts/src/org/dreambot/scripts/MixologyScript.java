package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.method.mixology.MixologyBranch;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.quests.childrenofthesun.ChildrenOfTheSun;
import org.dreambot.behaviour.training.herblore.HerbloreBranch;
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
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.MixologySettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.text.DecimalFormat;
import java.util.HashMap;

public class MixologyScript extends PseudoScript implements ItemContainerListener {
    FractalRoot<MixologySettings> tree = new FractalRoot<>(new MixologySettings(), getScriptName());

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);

        Logger.info("Init");
        SlayerTaskMap.minLootValue = 1000;
        tree.setSimpleName("cCMixologyFarm");
        AbstractEvent.globalInterruptCondition = () -> Inventory.contains("Coin pouch");

        MuleOff.LOOT = new int[]{
                ItemID.ALDARIUM,
                ItemID.CHUGGING_BARREL_DISASSEMBLED,
                ItemID.STRENGTH_POTION3,
        };
        WithdrawLoadoutEvent.sellList = new int[]{
                ItemID.ALDARIUM,
                ItemID.CHUGGING_BARREL_DISASSEMBLED,
        };

        HashMap<Skill, CombatStyle> skillStyleMap = new HashMap<>();
        skillStyleMap.put(Skill.STRENGTH, CombatStyle.STRENGTH);
        skillStyleMap.put(Skill.ATTACK, CombatStyle.ATTACK);
        skillStyleMap.put(Skill.DEFENCE, CombatStyle.DEFENCE);


        tree.addChildren(
                new GetMembershipBranch()
                        .setSimpleName("Get Membership"),
                new EmptyDeathsCoffer().setSimpleName("Death coffer"),
                new Fractal(() -> MuleOff.timer == null
                        || (MuleOff.timer.finished() && (!MIXOLOGY_AREA.contains(Players.getLocal()) || Inventory.isEmpty())))
                        .addChildren(
                                new MuleOff().setSimpleName("Mule Off")
                        )
                        .setSimpleName("Safe mule off"),
                new HerbloreBranch(() -> Skill.HERBLORE.getLevel() < Math.max(60, tree.getSettings().herbloreTarget), false)
                        .setSimpleName("Herblore training"),
                new ChildrenOfTheSun().setSimpleName("Children of sun"),
                new TalkToFractal(() -> PlayerSettings.getBitValue(9652) < 3, new Tile(3280, 3412), () -> NPCs.closest("Regulus Cento"))
                        .setDialogueOptions("Let's do it!")
                        .setSimpleName("First time valamore")
                        .setInventoryLoadout(new InventoryLoadout().setStrict(true)),
                new MixologyBranch(() -> true, tree.getSettings().rewardTarget).setSimpleName("Mixology")

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
        return "cCMixologyFarm";
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

    public static final Area MIXOLOGY_AREA = new Area(1375, 9337, 1406, 9305);

    public void onInventoryItemAdded(Item item) {
        if (!MIXOLOGY_AREA.contains(Players.getLocal())) return;
        if (ItemID.ALDARIUM == item.getUnnotedItemId() || ItemID.CHUGGING_BARREL_DISASSEMBLED == item.getUnnotedItemId()) {
            grossGp += (item.getLivePrice() * item.getAmount());
        }
    }
}
