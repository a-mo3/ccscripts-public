package org.dreambot.behaviour.method.nightmare.phosani;

import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.GraphicsObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.graphics.GraphicsObject;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.method.nightmare.PhosaniBranch;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.PVMUtil;
import org.dreambot.fractals.util.PrayerUtils;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PhosaniBlackHoles extends Fractal {
    public PhosaniBlackHoles(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
    }

    @Override
    public int onLoop() {
        // disable all defence Prayers, none are needed in this phase
        PrayerUtils.disable(Prayer.PROTECT_FROM_MELEE, Prayer.PROTECT_FROM_MAGIC, Prayer.PROTECT_FROM_MISSILES);

        List<GraphicsObject> blackHoles = GraphicsObjects.all(x -> x.getId() == PhosaniBranch.NIGHTMARE_SHADOW_GRAPHIC_OBJ);
        NPC nightmare = NPCs.closest("Phosani's Nightmare");
        if (nightmare == null) {
            Logger.info("No phosanis nightmare found");
            return ReactionGenerator.getQuick();
        }
        // dont want to end up next to a mushroom or you will get fucked up, im not certain these are game objects
        List<Area> mushroomAreas = GameObjects.all(x -> PhosaniBranch.isMushroom(x.getId()))
                .stream()
                .map(x -> x.getSurroundingArea(2))
                .collect(Collectors.toList());

        // find out if this is in quarters phase
        // todo on lost we cached this area it might be mad expensive
        Area safeCorner = PhosaniQuartersPhase.getGoodFlowerArea();
        if (safeCorner == null) safeCorner = Players.getLocal().getSurroundingArea(8);
        List<GameObject> goodFlowers = GameObjects.all(x -> PhosaniBranch.isGoodFlower(x.getId()));
        // the corners of the safe area will be area(greatestY flower, greatestX flower)
        // might have to set z on safe corner
        Area underPhosani = new Area(nightmare.getTrueTile(), nightmare.getTrueTile().translate(4, 4));
        // find the best tile and walk to it
        Area finalSafeCorner = safeCorner;
        Tile safeTile = Arrays.stream(PVMUtil.attackableTiles(nightmare, 4))
                .filter(x -> blackHoles.stream().noneMatch(hole -> hole.getTile().equals(x.getTile())))
                .filter(x -> !underPhosani.contains(x))
                .filter(x -> mushroomAreas.stream().noneMatch(mushroomRadius -> mushroomRadius.contains(x)))
                .filter(finalSafeCorner::contains)
                .min(Comparator.comparingDouble(Tile::distance))
                .orElse(null);

        if (safeTile == null) {
            Logger.info("Safetile null!!! panic!!!");
            safeTile = Arrays.stream(Players.getLocal().getSurroundingArea(8).getTiles())
                    .filter(x -> blackHoles.stream().noneMatch(hole -> hole.getTile().equals(x.getTile())))
                    .filter(x -> !underPhosani.contains(x))
                    .filter(x -> mushroomAreas.stream().noneMatch(mushroomRadius -> mushroomRadius.contains(x)))
                    .filter(finalSafeCorner::contains)
                    .min(Comparator.comparingDouble(Tile::distance))
                    .orElse(null);
        }

        if (!Players.getLocal().getTile().equals(safeTile)) {
            Walking.walkExact(safeTile);
            Tile finalSafeTile = safeTile;
            Sleep.sleepUntil(() -> Players.getLocal().getTile().equals(finalSafeTile), 1200);
        }

        if (safeTile.equals(Players.getLocal().getTile())) {
            Character target = Players.getLocal().getInteractingCharacter();
            if (target == null || !target.getName().toLowerCase().contains("nightmare")) {
                if (!PhosaniBranch.isMagicPhase()) {
                    nightmare.interact("Attack");
                } else {
                    // attack a pillar
//                    new EquipEvent(SettingsRepository.findInstanceOf(new PhosaniSettings()).loadout.getMageLoadout())
//                            .executed();

                    // attack the active pillar!
                    NPC pillar = NPCs.closest(x -> PhosaniMageAttack.INACTIVE_TOTEMS.contains(x.getId()));
                    if (pillar == null) {
                        Logger.error("pillar was null, magic attack phase");
                        return ReactionGenerator.getQuick();
                    }

                    // todo might need a distance check to ensure you dont walk onto an unsafe tile
                    Character tgt = Players.getLocal().getInteractingCharacter();
                    if (tgt == null || tgt.getId() != pillar.getId()) {
                        pillar.interact("Charge");
                        Sleep.sleepUntil(() -> {
                            Character t = Players.getLocal().getInteractingCharacter();
                            return t != null && t.getId() == pillar.getId();
                        }, 800);
                    }

                }
            }
        }

        // todo watch phosani or the totem, flicking augury / piety
        return ReactionGenerator.getQuick();
    }
}
