package org.dreambot.behaviour.tutorial.charcreator.design;

import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Logger;
import org.dreambot.behaviour.tutorial.charcreator.ApperanceWidgets;
import org.dreambot.behaviour.tutorial.charcreator.CharacterFeature;

import java.util.Random;

// these are only the male head designs
public enum HeadDesign implements CharacterFeature {
    BALD(256),
    DREADLOCKS(257),
    LONG(258),
    MEDIUM(259),
    TONSURE(260),
    SHORT(261),
    CROPPED(262),
    WILD_SPIKES(263),
    SPIKES(264),
    MOHAWK(265),
    WIND_BRAIDS(385),
    QUIFF(386),
    SAMURAI(387),
    PRINCELY(388),
    CURTAINS(389),
    LONG_CURTAINS(390),
    FRONT_SPLIT(407),
    TOUSLED(400),
    SIDE_WEDGE(401),
    FRONT_WEDGE(402),
    FRONT_SPIKES(403),
    FROHAWK(404),
    REAR_SKIRT(405),
    QUEUE(406),
    BUN(477),
    PIG_TAILS(478),
    EARMUFFS(479),
    SIDE_PONY(480),
    CURLS(481),
    PONYTAIL(482),
    BRAIDS(483),
    BUNCHES(484),
    BOB(485),
    LAYERED(486),
    STRAIGHT(487),
    STRAIGHT_BRAIDS(488),
    TWO_BACK(489),
    MULLET(457),
    UNDERCUT(458),
    LOW_BUN(472),
    MESSY_BUN(473),
    POMPADOUR(459),
    AFRO(460),
    SHORT_LOCS(461),
    SPIKY_MOHAWK(462),
    SLICKED_MOHAWK(463),
    LONG_QUIFF(464),
    SHORT_SHOPPY(465),
    SIDE_AFRO(466),
    PUNK(467),
    HALF_SHAVED(468),
    FREMENNIK(469),
    ELVEN(470),
    MEDIUM_COILS(471),
    HIGH_PONYTAIL(474),
    PLAITS(475),
    HIGH_BUNCHES(476),
    ;
    public int value;

    HeadDesign(int value) {
        this.value = value;
    }

    @Override
    public int currentlySelected() {
        final int index = 8;
        return Players.getLocal().getAppearance()[index];
    }

    @Override
    public boolean isComplete() {
        final int index = 8;
        int id = Players.getLocal().getAppearance()[index];
        Logger.info(String.format("Head design %d %d", id, value));
        return id == value;
    }

    @Override
    public boolean selectLeft() {
        return ApperanceWidgets.HEAD_DESIGN.selectLeft();
    }

    @Override
    public boolean selectRight() {
        return ApperanceWidgets.HEAD_DESIGN.selectRight();
    }

    public static HeadDesign getRandom() {
        HeadDesign[] a = values();
        return a[new Random().nextInt(a.length)];
    }

    @Override
    public int getTarget() {
        return value;
    }

    @Override
    public int getOrdinal() {
        return ordinal();
    }
}
