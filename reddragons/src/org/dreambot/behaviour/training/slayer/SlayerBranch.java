package org.dreambot.behaviour.training.slayer;

import org.dreambot.api.Client;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.obstacle.impl.PassableObstacle;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.behaviour.training.slayer.behaviour.GetTaskLeaf;
import org.dreambot.fractals.Fractal;
import org.dreambot.muling.Log;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SlayerBranch extends Fractal implements ChatListener {
    //Chat messages
    private static final Pattern CHAT_GEM_PROGRESS_MESSAGE = Pattern.compile("^(?:You're assigned to kill|You have received a new Slayer assignment from .*:) (?:[Tt]he )?(?<name>.+?)(?: (?:in|on|south of) (?:the )?(?<location>[^;]+))?(?:; only | \\()(?<amount>\\d+)(?: more to go\\.|\\))$");
    private static final String CHAT_GEM_COMPLETE_MESSAGE = "You need something new to hunt.";
    private static final Pattern CHAT_COMPLETE_MESSAGE = Pattern.compile("You've completed (?:at least )?(?<tasks>[\\d,]+) (?:Wilderness )?tasks?(?: and received \\d+ points, giving you a total of (?<points>[\\d,]+)| and reached the maximum amount of Slayer points \\((?<points2>[\\d,]+)\\))?");
    private static final String CHAT_CANCEL_MESSAGE = "Your task has been cancelled.";
    private static final String CHAT_CANCEL_MESSAGE_JAD = "You no longer have a slayer task as you left the fight cave.";
    private static final String CHAT_CANCEL_MESSAGE_ZUK = "You no longer have a slayer task as you left the Inferno.";
    private static final String CHAT_SUPERIOR_MESSAGE = "A superior foe has appeared...";
    private static final String CHAT_BRACELET_SLAUGHTER = "Your bracelet of slaughter prevents your slayer";
    private static final String CHAT_BRACELET_EXPEDITIOUS = "Your expeditious bracelet helps you progress your";
    private static final Pattern COMBAT_BRACELET_TASK_UPDATE_MESSAGE = Pattern.compile("^You still need to kill (\\d+) monsters to complete your current Slayer assignment");


    public static String task;

    public SlayerBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        LocalPathFinder.getLocalPathFinder().addObstacle(new PassableObstacle("Gate of War", "Open"));
        Logger.info("registering this!");
        Client.getInstance().addEventListener(this);
        init();
    }

    public SlayerBranch() {
        Log.info("registering this!");
        Client.getInstance().addEventListener(this);
        init();
    }

    private void init() {
        this.paintArraySupplier = () -> new String[]{
                "Current task: " + task + " " + PlayerSettings.getConfig(394) + " left",
                "Level: " + Skills.getRealLevel(Skill.SLAYER)
        };
        addChildren(
          new GetTaskLeaf().setSimpleName("Get new task")
        );
    }

    @Override
    public int onLoop() {
        SlayerTaskMap.execSlayerTask(task.toLowerCase());
        return ReactionGenerator.getNormal();
    }

    @Override
    public void onMessage(Message message) {
//        MessageType type = message.getType();
//        if (type != MessageType.GAME && type != MessageType.) {
//            return;
//        }
        String chatMsg = Text.removeTags(message.getMessage());
        if (chatMsg.equals(CHAT_GEM_COMPLETE_MESSAGE)
                || chatMsg.equals(CHAT_CANCEL_MESSAGE)
                || chatMsg.equals(CHAT_CANCEL_MESSAGE_JAD)
                || chatMsg.equals(CHAT_CANCEL_MESSAGE_ZUK)) {
            task = "new";
            return;
        }

        if (chatMsg.contains("You have completed your task!")) {
            Log.info("Task complete");
            task = "new";
            return;
        }

        Matcher mProgress = CHAT_GEM_PROGRESS_MESSAGE.matcher(chatMsg);
        Log.info(message.getMessage());
        if (mProgress.find()) {
            String name = mProgress.group("name");
            int gemAmount = Integer.parseInt(mProgress.group("amount"));
            String location = mProgress.group("location");
            Log.info("MSG LISTENER", name + " " + gemAmount + " " + location);
            task = name;
            return;
        }
    }
}
