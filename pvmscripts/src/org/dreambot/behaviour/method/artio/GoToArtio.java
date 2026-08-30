package org.dreambot.behaviour.method.artio;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.behaviour.method.spindel.GoToSpindel;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.*;
import org.dreambot.scriptdata.ArtioSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;

public class GoToArtio extends Fractal implements ChatListener {
    static Area HUNTER_ENTRANCE = new Area(3112, 3679, 3118, 3670);
    Area FEROX_POOL = new Area(3127, 3636, 3130, 3633);
    public static final Area ARTIO_ARENA = new Area(1746, 11560, 1772, 11520);
    //            = new Area(1617, 11567, 1645, 11528, 2);
    public static boolean shouldHop = false; // used for when the world is taken and you should hop before entering

    // assume true and set false when you get the you havent killed enough bosses message,
    // set true every time you are out of the wild so once you unlock it will be used
    // not peeking costs 50k per erroneous enter and ~3 min wasted time if you TP out
    boolean shouldPeek = true;
    // this is reset when a world is peeked and is empty
    Timer peekTimer = new Timer(2400);
    int peekedWorldId = -1;

    public GoToArtio() {
        super(() -> !ARTIO_ARENA.contains(Players.getLocal()));
        Client.getInstance().addEventListener(this);

        this.inventoryLoadout = SettingsRepository.findInstanceOf(new ArtioSettings()).loadout.loadout.setStrictSupplier(() -> !Combat.isInWild());
        this.equipmentLoadout = SettingsRepository.findInstanceOf(new ArtioSettings()).loadout.equipmentLoadout;
    }

    @Override
    public int onLoop() {
        if (!Combat.isInWild())
            PrayerUtils.disable(Prayer.PROTECT_FROM_MISSILES, Prayer.PROTECT_FROM_MAGIC, Prayer.AUGURY, Prayer.EAGLE_EYE);

        if (shouldHop) {
            Logger.info("World hop");
            WorldHopper.hopWorld(Worlds.getRandomWorld(
                    w -> w.isNormal() && w.getMinimumLevel() < Skills.getTotalLevel() && w.isMembers()
            ));
            shouldHop = false;

            return ReactionGenerator.getQuick();
        }

        // restore stats at ferox pool
        if (!areStatusFull() && !Combat.isInWild()) {
            if (!FEROX_POOL.contains(Players.getLocal())) {
                if (Walking.shouldWalk()) Walking.walk(FEROX_POOL);
                return ReactionGenerator.getNormal();
            }

            GameObject rejPool = GameObjects.closest("Pool of Refreshment");
            if (rejPool != null && !Players.getLocal().isMoving()) {
                rejPool.interact("Drink");
                Sleep.sleepUntil(GoToSpindel::areStatusFull, 2300);
            }
            return ReactionGenerator.getQuick();
        }

        if (!HUNTER_ENTRANCE.contains(Players.getLocal())) {
            UtilProvider.staminaUp();
            if (Walking.shouldWalk()) Walking.walk(HUNTER_ENTRANCE);
            return ReactionGenerator.getQuick();
        }

        if (Dialogues.inDialogue()) {
            Dialog.solve("again");
            return ReactionGenerator.getQuick();
        }

        // walk to the jaunt, PK node will kick in if theres a problem

        if (shouldPeek) {
            GameObject entrance = GameObjects.closest(x -> x.hasAction("Enter") && x.hasAction("Peek"));
            if (entrance == null) {
                Logger.info("Failed to find entrance - Peek mode");
                return ReactionGenerator.getQuick();
            }

            if (!peekTimer.finished()) {
                // the world was recently peeked
                if (Worlds.getCurrentWorld() == peekedWorldId) {
                    // this world is empty, go in
                    entrance.interact("Enter");
                } else {
                    Character attackingMe = Players.getLocal().getCharacterInteractingWithMe();
                    Character imAttacking = Players.getLocal().getInteractingCharacter();
                    if (attackingMe instanceof NPC && attackingMe.hasAction("Attack")) {
                        Logger.info("Kill mob attacking me " + attackingMe.getName());
                        if (imAttacking == null || !imAttacking.equals(attackingMe)) attackingMe.interact("Attack");
                        return ReactionGenerator.getQuick();
                    }
                    // recently peeked but was not empty, hop worlds
                    World world = Worlds.getRandomWorld(w -> w.isNormal() && w.getMinimumLevel() < Skills.getTotalLevel() && w.isMembers());
                    WorldHopper.hopWorld(world);
//                    Sleep.sleepUntil(() -> Worlds.getCurrentWorld() == world.getWorld(), 6400);
                }
                return ReactionGenerator.getQuick();
            }

            if (peekedWorldId != Worlds.getCurrentWorld() || peekTimer.finished()) {
                // peek entrance, listener will set state if its empty
                entrance.interact("Peek");
                return ReactionGenerator.getQuick();
            }


            return ReactionGenerator.getQuick();
        }

        // should not peek, <20 kc
        GameObject entrance = GameObjects.closest(x -> x.hasAction("Enter"));
        if (entrance != null) {
            log("Enter hunters end");
            entrance.interact("Enter");
            Sleep.sleepUntil(() -> CombatUtil.getThreat() != null || ARTIO_ARENA.contains(Players.getLocal()),
                    1200);
        }

        return ReactionGenerator.getQuick();
    }

    public static boolean areStatusFull() {
        if (Combat.isPoisoned() || Combat.isEnvenomed()) return false;
        if (Skills.getBoostedLevel(Skill.HITPOINTS) < Skills.getRealLevel(Skill.HITPOINTS)) return false;
        if (Skills.getBoostedLevel(Skill.PRAYER) < Skills.getRealLevel(Skill.PRAYER)) return false;
        return Walking.getRunEnergy() > 60;
    }

    @Override
    public void onMessage(Message message) {
        if (message.getType() == MessageType.PLAYER) return;
        // reset shouldPeek to true so it will be used after unlocking
        if (!Combat.isInWild()) shouldPeek = true;
        // invalidate an old world
        if (peekTimer.finished()) peekedWorldId = -1;

        String msg = message.getMessage();
        if (msg.contains("glean any useful information")) {
            // < 20 kc
            shouldPeek = false;
            return;
        }

        if (msg.contains("The cave is empty.")) {
            Logger.info("Found an empty world " + Worlds.getCurrentWorld());
            peekTimer.reset();
            peekedWorldId = Worlds.getCurrentWorld();
            return;
        }

        if (msg.contains("There is activity inside.")) {
            peekTimer.reset();
        }
    }
}
