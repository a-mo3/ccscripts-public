package org.dreambot.fractals.util;

import lombok.Getter;
import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.WebNodeType;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.script.listener.AnimationListener;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.HitSplatListener;
import org.dreambot.api.wrappers.graphics.SpotAnimation;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.pktrie.PKTrie;

import java.util.HashMap;

/**
 * singleton that keeps track of
 * teleblock state
 * last in combat time
 */
public class CombatUtil implements ChatListener, HitSplatListener, AnimationListener {
    static CombatUtil singleton;

    @Getter
    int teleblockedWorld = -1;
    // manage entangle / ice barraged state
    HashMap<Integer, Long> spotAnimationTimings = new HashMap<>();
    long lastEntangledTimestamp = -1;
    long lastEntangleLength = -1;
    @Getter
    long lastTeleblockTimestamp = -1;
    long teleblockLength = 5 * 60_000;

    private CombatUtil() {
        Client.getInstance().addEventListener(this);
        spotAnimationTimings.put(179, 14400L); // entangle, 14.4 seconds
        spotAnimationTimings.put(180, 9600L); // snare 9.6 seconds
        spotAnimationTimings.put(181, 4800L); // bind 4.8 seconds
        // todo do ice barrage
    }

    public void setTeleblockedWorld(int teleblockedWorld) {
        WebFinder wf = WebFinder.getWebFinder();
        if (teleblockedWorld < 0) {
            wf.enableEquipmentTeleports();
            wf.enableInventoryTeleports();
            wf.enableWebNodeType(WebNodeType.TELEPORT_NODE);
        } else {
            wf.disableInventoryTeleports();
            wf.disableEquipmentTeleports();
            wf.disableWebNodeType(WebNodeType.TELEPORT_NODE);
        }

        lastTeleblockTimestamp = teleblockedWorld <= 0 ? -1 : System.currentTimeMillis();
        this.teleblockedWorld = teleblockedWorld;
    }

    // manage last attacked a wilderness boss
    long lastInCombatTimestamp = -1;


    public boolean isOnLogoutTimer() {
        return System.currentTimeMillis() - lastInCombatTimestamp < 10_000;
    }

    /**
     * @return true if you are past 3 second attack timer for revs and wildy bosses
     */
    public boolean isOnSpecialTPTimer() {
        return System.currentTimeMillis() - lastInCombatTimestamp < 3000;
    }

    public static CombatUtil get() {
        if (singleton == null) singleton = new CombatUtil();
        return singleton;
    }

    public boolean isTeleblocked() {
        if (teleblockedWorld > 0 && teleblockedWorld != Worlds.getCurrentWorld()) {
            setTeleblockedWorld(-1);
            lastTeleblockTimestamp = -1;
            return false;
        }
        return teleblockedWorld == Worlds.getCurrentWorld() && (System.currentTimeMillis() - lastTeleblockTimestamp < teleblockLength);
    }

    public boolean isInCombat() {
        return Players.getLocal().isInCombat() || (System.currentTimeMillis() - lastInCombatTimestamp) < 10_000;
    }

    public boolean isEntangled() {
        return System.currentTimeMillis() - lastEntangledTimestamp < lastEntangleLength;
    }

    public long msLeftOnEntangle() {
        return lastEntangleLength - (System.currentTimeMillis() - lastEntangledTimestamp);
    }

    public long msLeftOnLogout() {
        return 10_000 - (System.currentTimeMillis() - lastInCombatTimestamp);
    }

    public long msLeftOnTB() {
        return teleblockLength - (System.currentTimeMillis() - lastTeleblockTimestamp);
    }


    @Override
    public void onPlayerSpotAnimation(Player player, SpotAnimation animation) {
        if (player == null || player.getName() == null) return;
        if (player.getName().equals(Players.getLocal().getName())) {
            // your spot animation
            if (spotAnimationTimings.containsKey(animation.getAnimationId())) {
                lastEntangledTimestamp = System.currentTimeMillis();
                lastEntangleLength = spotAnimationTimings.get(animation.getAnimationId());
            }
        }
    }

    @Override
    public void onHitSplatAdded(Entity entity, int type, int damage, int id, int special, int gameCycle) {
        // todo track when you hit a wildy boss
        if (entity == null || entity.getName() == null) return;
        String entityName = entity.getName().toLowerCase();
        Character interactingWith = Players.getLocal().getInteractingCharacter();

        if (Players.getLocal().getName().toLowerCase().equals(entityName)) {
            lastInCombatTimestamp = System.currentTimeMillis();
        }

        if (interactingWith != null && interactingWith.equals(entity)) {
            lastInCombatTimestamp = System.currentTimeMillis();
        }

        if (interactingWith != null && interactingWith.getName().toLowerCase().contains("revenant")) {
            lastInCombatTimestamp = System.currentTimeMillis();
        }
    }

    @Override
    public void onMessage(Message message) {
        if (message.getType() == MessageType.PLAYER) return;
        String msg = message.getMessage();
        if (msg.contains("has been cast on you")) {
            if (msg.contains("2 minutes")) {
                teleblockLength = (long) (2.5 * 60_000); // when you are praying mage
                setTeleblockedWorld(Worlds.getCurrentWorld());
                return;
            }

            teleblockLength = 5 * 60_000;
            setTeleblockedWorld(Worlds.getCurrentWorld());
            lastTeleblockTimestamp = System.currentTimeMillis();
        }

        if (msg.contains("Tele Block has expired.")) {
            setTeleblockedWorld(-1); // should get timeout as well
        }

        if (msg.contains("you are dead!")) {
            WebFinder wf = WebFinder.getWebFinder();
            wf.enableEquipmentTeleports();
            wf.enableInventoryTeleports();
            wf.enableWebNodeType(WebNodeType.TELEPORT_NODE);
        }
    }

    public static Player getThreat() {
        return Players.closest(x -> x.distance() < 20 && canAttackMe(x) && (x.isSkulled() || PKTrie.checkString(x.getName())));
    }

    private static final Area FEROX = new Area(
            new Tile(3156, 3646, 0),
            new Tile(3155, 3633, 0),
            new Tile(3151, 3621, 0),
            new Tile(3143, 3617, 0),
            new Tile(3122, 3617, 0),
            new Tile(3123, 3630, 0),
            new Tile(3125, 3631, 0),
            new Tile(3125, 3639, 0),
            new Tile(3137, 3640, 0),
            new Tile(3137, 3646, 0)
    );

    public static boolean canAttackMe(Player threat) {
        if (threat == null) return false;
        if (FEROX.contains(threat)) {
            return false;
        }
        if (threat.getName() != null && threat.getName().equals(Players.getLocal().getName())) return false;
        int threatLvl = threat.getLevel();
        int mylvl = Combat.getCombatLevel();
        int wildernessLvl = Combat.getWildernessLevel();
        return threatLvl >= (mylvl - wildernessLvl) && threatLvl <= (wildernessLvl + mylvl);
    }
}