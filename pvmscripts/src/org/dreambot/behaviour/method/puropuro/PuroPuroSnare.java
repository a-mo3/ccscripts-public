package org.dreambot.behaviour.method.puropuro;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.scriptdata.PuroPuroSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PuroPuroSnare extends Fractal {
    // we pretty much just want to run around the edges, collecting whatevers on the edges that matches our criteria
    Map<Tile, Tile> corners = new HashMap<>();

    PuroPuroSettings settings;
    Area impArea;

    public PuroPuroSnare(PuroPuroSettings settings) {
        super(() -> settings.puroMode == PuroMode.SNARE);
        this.settings = settings;
        setSimpleName("Puro puro(Snare)");
        // node are init by other puro
        impArea = impAreas[Calculations.random(impAreas.length)];
    }

    // checks impling name and sees if we have the level and have this type enabled in settings
    private boolean isAcceptableImpling(String implingName) {
        int lvl = Skills.getRealLevel(Skill.HUNTER);
        if (lvl >= 17 && implingName.startsWith("Baby")) return settings.babyImpling;
        if (lvl >= 22 && implingName.startsWith("Young")) return settings.youngImpling;
        if (lvl >= 28 && implingName.startsWith("Gourmet")) return settings.gourmetImpling;
        if (lvl >= 36 && implingName.startsWith("Earth")) return settings.earthImpling;
        if (lvl >= 42 && implingName.startsWith("Essence")) return settings.essenceImpling;
        if (lvl >= 50 && implingName.startsWith("Eclectic")) return settings.eclecticImpling;
        if (lvl >= 58 && implingName.startsWith("Mature")) return settings.natureImpling;
        if (lvl >= 65 && implingName.startsWith("Magpie")) return settings.magpieImpling;
        if (lvl >= 74 && implingName.startsWith("Ninja")) return true;
        if (lvl >= 80 && implingName.startsWith("Crystal")) return true;
        if (lvl >= 83 && implingName.startsWith("Dragon")) return true;
        if (lvl >= 89 && implingName.startsWith("Lucky")) return true;
        return false;
    }

    Area[] impAreas = {
            // south corner
            new Area(2597, 4313, 2621, 4290),
            // north corner
            new Area(2562, 4349, 2585, 4325)
    };

    Timer snareCooldown = new Timer(16_000);

    @Override
    public int onLoop() {
        if (Inventory.contains(ItemID.MAGIC_BUTTERFLY_NET)) {
            log("Equip butterfly net");
            Inventory.interact(ItemID.MAGIC_BUTTERFLY_NET);
            return ReactionGenerator.getNormal();
        }

        if (settings.overworldCircles) {
            // make sure we aren't all on the scouted world
            log("Crop world " + CropCircleScouter.getWorld());
            if (Worlds.getCurrentWorld() == CropCircleScouter.getWorld()) {
                log("Get off the scouted world");
                WorldHopper.hopWorld(Worlds.getRandomWorld(x -> x.isNormal() && x.getWorld() != 401 && x.isMembers() && x.getMinimumLevel() == 0));
                return ReactionGenerator.getNormal();
            }
        }

        if (!Inventory.contains(ItemID.IMPLING_JAR)) {
            log("No jars go bank");
            if (Walking.shouldWalk()) Walking.walk(BankLocation.GRAND_EXCHANGE);
            return ReactionGenerator.getNormal();
        }

        // find an out ring eligible impling
        NPC imp = NPCs.closest(x -> x.distance() < 5
                && Arrays.stream(impAreas).anyMatch(area -> area.contains(x))
                && isAcceptableImpling(x.getName()));

        if (imp != null) {
            log("Found imp " + imp);
            // todo snare
            if (Magic.canCast(Normal.SNARE) && snareCooldown.finished() && imp.distance() < 8) {
                log("Snare imp");
                snareCooldown.finished();
                Magic.castSpellOn(Normal.SNARE, imp);
                return ReactionGenerator.getNormal();
            }

            imp.interact();
            Sleep.sleepUntil(() -> !imp.exists() || Arrays.stream(impAreas).anyMatch(a -> a.contains(imp)), 1000);
            return ReactionGenerator.getNormal();
        }

        // go to corner
        if (!impArea.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(impArea);
            return ReactionGenerator.getNormal();
        }
        return ReactionGenerator.getNormal();
    }
}
