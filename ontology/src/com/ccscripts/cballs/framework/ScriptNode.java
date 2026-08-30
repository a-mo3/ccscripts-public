package com.ccscripts.cballs.framework;

import com.ccscripts.actions.AbstractAction;
import com.piler.constraints.NodeConstraint;
import com.ccscripts.reproducer.AbstractActionReproducer;
import com.ccscripts.reproducer.EntityInteractionReproducer;
import com.ccscripts.reproducer.MouseMovementReproducer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Accessors(chain = true)
public abstract class ScriptNode extends Node {
    /**
     * every reproducer will have a continuity number, for things like
     * open bank replay pre hovers cballs, when we open bank, we want to use the one that deposits cballs
     *
     * this limits how far you can get in ontologies that have breaks, eg when you get a full inv in mining
     * even though you have n samples for mining youd only have like n/27 samples for full inv, so you are limited
     * to resetting to one of those when that happens
     *
     * the flag below prevents this
     */
    @Getter @Setter
    static int currentContinuityNumber = Integer.MAX_VALUE;
    boolean dontResetContinuity = false;

    private List<Replay> replays = new ArrayList<>();

    // needs to be pure
    public abstract boolean isValid();

    // fall back onloop if theres not enough data to execute replications
    public abstract int fallBack();

    @Override
    public ScriptNode validChild() {
        return this;
    }

    abstract public String getIdentifier();

    abstract public String getExpectedNextState();

    @Getter
    protected NodeConstraint constraint = null;

    /**
     * should execute a sleepUntil after an interaction
     * we pass the action for different sleeps after different things
     * ie no sleep for mouse or widget (other than implicit in replay timings)
     * and some conditional sleep for an interaction with furnace
     * <p>
     * true if slept false otherwise, for implicit timing
     */
//    abstract protected boolean sleepAfterAction(AbstractAction action);
    protected void sleepAfterReplay() {
        Sleep.sleepUntil(() -> !isValid(), 1000);
    }

    DecimalFormat df = new DecimalFormat("###,###,###");

    public final void execute() {
        if (replays.isEmpty()) {
            Logger.info("No inference data fallback ");
            Sleep.sleep(fallBack());
            return;
        }

        Logger.info("Data pool " + replays.size() + " " + getIdentifier());


        Replay replay = replays.stream()
                .filter(x -> x.getContinuityNumber() - 1 == currentContinuityNumber)
                .findFirst()
                .orElse(null);
//                .orElse(replays.get(Calculations.random(replays.size())));
        // todo make this null and pick a random without resetting continuity number for a given flag
        if (replay == null) {
            replay = replays.get(Calculations.random(replays.size()));

        }

        List<AbstractAction> a = replay.getReproducers().stream().map(AbstractActionReproducer::getAction).collect(Collectors.toList());

        Logger.log(Color.PINK, a.size());
        Logger.log(Color.PINK, "last " + a.get(a.size() - 1) + " first " + a.get(0));
        Logger.log(Color.PINK, df.format(a.get(a.size() - 1).getTimestamp() - a.get(0).getTimestamp()) + " ms");
        long lastTimestamp = 0;
        for (AbstractAction abstractAction : a) {
            if (lastTimestamp == 0) {
                lastTimestamp = abstractAction.getTimestamp();
                continue;
            }
            Logger.log(Color.PINK, abstractAction.getType() + " for " + (abstractAction.getTimestamp() - lastTimestamp) + "ms");

            lastTimestamp = abstractAction.getTimestamp();
        }

        if (!dontResetContinuity) currentContinuityNumber = replay.getContinuityNumber();
        for (int i = 0; i < replay.getReproducers().size(); i++) {
            AbstractActionReproducer r = replay.get(i);
            // todo check constraint to see if we should execute
            // we need to track time to execute so we dont sleep twice as long, ie a mouse movement will take atleast 50ms*samples
            long executionTime = System.currentTimeMillis();
            r.execute();
            executionTime = System.currentTimeMillis() - executionTime;
            Logger.log(Color.CYAN, "execution time " + executionTime);
            Logger.log(Color.PINK, r.getAction().getType());

            /*
            sleep after interaction seems to be a bad idea, it fucks up with things
            like interacting with bank, sleeping until its open, and then all the mouse paths fire
            instead we'll just do it once at the end
             */
            if (i + 1 >= replay.getReproducers().size()) {
                Logger.log(Color.CYAN, "EOS");
                break;
            }

            if (!isValid()) {
                Logger.log(Color.CYAN, "EOS - no longer valid");
                return;
            }

            AbstractActionReproducer nr = replay.get(i + 1);
            /*
            calculate the difference in times between actions in the replay and sleep that much
            todo have break conditions in sleeps, maybe
            maybe a more complicated kind of sleep, like one that allows AFKs but not longer than X when a condition is met
             */
            long time = nr.getAction().getTimestamp() - (r.getAction().getTimestamp() + executionTime);
//            time = time - executionTime;
            if (time <= 0) {
                Logger.log(Color.CYAN, "Skipped Implicit Sleeping " + df.format(time) + " " + df.format(executionTime));
                continue;
            }
            Logger.log(Color.CYAN, "Sleeping " + df.format(time));
            Sleep.sleepUntil(() -> !isValid(), time);
        }

        // after replay sleep until after condition
        Logger.log("Sleep after gen");
        sleepAfterReplay();
    }

    /**
     *
     * @return list of rectangles of entities a user should click on while training
     */
    public abstract List<Rectangle> trainingHighlights();

    protected void log(String msg) {
        Logger.log(Color.magenta, getIdentifier() + " - " + msg);
    }

    protected abstract boolean shouldConfigureReproducers();

    /**
     * depending on a nodes context, you may want reproducers to act differently for an action
     * like in combat you want an entity interaction to search closest or a similar distance to the replay
     * but in mining you'd want the exact tile
     * this is called after all nodes have been populated
     */
    public void configureReproducers() {
        if (!shouldConfigureReproducers()) return;
        for (Replay replay : replays) {
            for (AbstractActionReproducer reproducer : replay.getReproducers()) {
                if (reproducer instanceof EntityInteractionReproducer) {
                    configureEntity((EntityInteractionReproducer) reproducer);
                    continue;
                }
            }
        }
    }

    protected ScriptNode configureEntity(EntityInteractionReproducer reproducer) {
        return this;
    }

    /**
     * ie toggling exact replay or adjusted, what is reasonable to hover, whatever
     * should be overriden by a scriptnode that needs it
     */
    protected ScriptNode configureMouse(MouseMovementReproducer reproducer) {
        return this;
    }

}
