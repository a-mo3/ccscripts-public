package org.dreambot.behaviour.method.artio;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.interactive.Projectiles;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.AnimationListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.graphics.SpotAnimation;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Locatable;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.*;
import java.util.function.Supplier;

import static org.dreambot.behaviour.method.bluedragons.KillBlueDragon.AUGURY_UNLOCKED;

public class MagicFightArtio extends Fractal implements AnimationListener {
    // Orb projective ID

    Map<Integer, Integer> spotAnimationTimings = new HashMap<>();

    public MagicFightArtio(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        Client.getInstance().addEventListener(this);
        // todo barrage
        spotAnimationTimings.put(179, 24); // entangle, 14.4 seconds
        spotAnimationTimings.put(180, 16); // snare 9.6 seconds
        spotAnimationTimings.put(181, 8); // bind 4.8 seconds

        this.paintArraySupplier = () -> {
            NPC artio = NPCs.closest("Artio");
            return new String[]{
                    String.format("Game/NextFreeze %d/%d", Client.getGameTick(), nextFreezeTick),
                    String.format("Artio distance %f", artio == null ? 0 : artio.distance()),
                    String.format("Artio Sdistance %f", artio == null ? 0 : artio.getServerTile().distance()),
                    "Artio Entangled: " + isArtioEntangled(),
                    "Entangled but moving? " + (artio == null ? "-" : String.valueOf((isArtioEntangled() && artio.isMoving())))
            };
        };
    }


    // Projectile: 133

    // bear trap ID
    // Graphics Object: 2343
    public static final int BEAR_TRAP_ID = 47146;

    // range animation
    // 10013

    // magic animation
    // 10014

    // melee animation
    // A 10012

    // Ani 10015 is placing bear traps

    // clear entangle animation

    // the tick at which you should freeze artio again
    int nextFreezeTick = -1;
    // the tick on which the freeze was cast
    int lastFreezeTick = -1;
    // the duration of the freeze
    int freezeDuration = -1;

    Timer runAway = new Timer(2400);
    Timer eatTimer = new Timer(800);

    @Override
    public int onLoop() {
        NPC artio = NPCs.closest("Artio");

        if (artio == null) {
            log("Could not find artio");
            return ReactionGenerator.getNormal();
        }

        // todo consider this but only if you think hes been frozen for some ticks now, as he can be moving while frozen
//        if (isArtioEntangled() && artio.isMoving()) {
//            log("Artio moved suspiciously ");
//            lastFreezeTick = -1;
//            nextFreezeTick = -1;
//            freezeDuration = -1;
//        }

        Prayers.toggle(true, getBestMagePray());
        if (artio.distance() < (isArtioEntangled() ? 3 : 5)) {
            Prayers.toggle(true, Prayer.PROTECT_FROM_MELEE);
        } else {
            if (Projectiles.closest(133) != null) {
                Prayers.toggle(true, Prayer.PROTECT_FROM_MAGIC);
            } else {
                Prayers.toggle(true, Prayer.PROTECT_FROM_MISSILES);
            }
        }

        // eat
        if (Combat.getHealthPercent() < 75 && eatTimer.finished()) {
            if (Inventory.contains(ItemID.BLIGHTED_MANTA_RAY)) {
                log("Eat manta ray");
                Inventory.interact(ItemID.BLIGHTED_MANTA_RAY, "Eat");
                eatTimer.reset();
            }
        }

        Character artioTarget = artio.getInteractingCharacter();
        if (artioTarget != null && !artioTarget.equals(Players.getLocal())) {
            log("Artio is targetting someone else, we leave.");
            LeaveArtio.leaveArtio();
            GoToArtio.shouldHop = true;
            return ReactionGenerator.getNormal();
        }

        // if artio is not entangled, re-intangle that mutha fucka
        if (Client.getGameTick() > nextFreezeTick) {
            log("Time to entangle");
            Magic.castSpellOn(Normal.ENTANGLE, artio);
            nextFreezeTick += 1;
            return ReactionGenerator.getQuick();
        } else {
            // only run away when artio is entangled, entangled immunity is 3 seconds, artio attack spd is 3 seconds, tank 1 hit then gap
            if (artio.distance() < 3 && runAway.finished() && isArtioEntangled()) {
                // run away from artio
                GameObjects.setIncludeNullNames(true);
                Tile edgeTile = Arrays.stream(artio.getSurroundingArea(5).getTiles())
                        .filter(t -> t.getY() < artio.getY())
                        .filter(Locatable::canReach)
                        .filter(x -> x.distance(artio) < 5 && x.distance(artio) >= 3)
                        // todo optimize this retarded filter
                        .filter(tile -> GameObjects.closest(x -> x.getId() == BEAR_TRAP_ID && tile.equals(x.getTile())) == null)
                        .min(Comparator.comparingDouble(Tile::distance))
                        .orElse(null);
                if (edgeTile == null) {
                    // the Y filter can cause problems if you get to the very bottom
                    edgeTile = Arrays.stream(artio.getSurroundingArea(5).getTiles())
                            .filter(Locatable::canReach)
                            .filter(x -> x.distance(artio) < 5 && x.distance(artio) >= 3)
                            // todo optimize this retarded filter
                            .filter(tile -> GameObjects.closest(x -> x.getId() == BEAR_TRAP_ID && tile.equals(x.getTile())) == null)
                            .min(Comparator.comparingDouble(Tile::distance))
                            .orElse(null);
                }

                GameObjects.setIncludeNullNames(false);

                if (edgeTile == null) {
                    Logger.warn("Failed to find an acceptable safetile to run to");
                } else {
                    log("Running to safe" + edgeTile.distance() + " toArtio:  " + edgeTile.distance(artio));
                    Walking.walkExact(edgeTile);
                    runAway.reset();
                }
            }
        }

        // boost pot
        int magicBoost = Skills.getBoostedLevel(Skill.MAGIC) - Skills.getRealLevel(Skill.MAGIC);
        if (magicBoost <= 2) {
            log("Drink magic potion");
            Inventory.interact(ItemVariants.MAGIC_POTION.getItem(), "Drink");
        }


        // hit artio
        Player local = Players.getLocal();
        Character tgt = local.getInteractingCharacter();
        if (Combat.getSpecialPercentage() >= 50 && !Combat.isSpecialActive()) Combat.toggleSpecialAttack(true);
        if (tgt == null) artio.interact("Attack");
        return ReactionGenerator.getNormal();
    }

    private boolean isArtioEntangled() {
        // we ignore the + 5 tick immunity here so we can tell if its time to tank or run
        return Client.getGameTick() < lastFreezeTick + freezeDuration;
    }

    public static final int ARTIO_CLEAR_ENTANGLE_ANI = 10015; //

    @Override
    public void onNpcAnimation(NPC npc, int animation, int animationDelay) {
        if (npc.getName().equals("Artio")) {
            log(npc + " Animated " + animation + " Delay " + animationDelay);
            if (animation == ARTIO_CLEAR_ENTANGLE_ANI) {
                log("Artio roared reset entangle");
                lastFreezeTick = Client.getGameTick();
                nextFreezeTick = lastFreezeTick + 4;
                freezeDuration = 0;
                return;
            }
        }
    }

    @Override
    public void onNPCSpotAnimation(NPC npc, SpotAnimation animation) {
        log(npc + " Spot Animated " + animation.getAnimationId() + " Delay " + animation.getDelay() + " Tick " + animation.getTick());
        if (npc == null || npc.getName() == null || !npc.getName().equals("Artio")) return;

        if (!spotAnimationTimings.containsKey(animation.getAnimationId())) return;
        // extra 5 ticks because hes immune for a bit
        // todo considerations for what swampbark pieces you have on
        lastFreezeTick = Client.getGameTick();
        freezeDuration = spotAnimationTimings.get(animation.getAnimationId());
        nextFreezeTick = lastFreezeTick + freezeDuration + 5;
    }

    private Stack<Tile> getPath() {
        Stack<Tile> t = new Stack<>();
        return t;
    }

    @Override
    public void onPlayerAnimation(Player player, int animation, int animationDelay) {
        if (player.equals(Players.getLocal())) log(String.format("LP animated %d %d", animation, animationDelay));
    }

    @Override
    public void onPlayerSpotAnimation(Player player, SpotAnimation animation) {
        if (player.equals(Players.getLocal())) log(String.format("LP animated %d", animation.getAnimationId()));
    }

    public static Prayer getBestMagePray() {
        int lvl = Skills.getRealLevel(Skill.PRAYER);
        if (lvl >= 77 && PlayerSettings.getBitValue(AUGURY_UNLOCKED) == 1) return Prayer.AUGURY;
        if (lvl >= 45) return Prayer.MYSTIC_MIGHT;
        return Prayer.MYSTIC_LORE;
    }
}
