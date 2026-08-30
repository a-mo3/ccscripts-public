package org.dreambot.behaviour.method.moonsofperil;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.behaviour.method.moonsofperil.bloodmoon.BloodMoonAttackPhase;
import org.dreambot.behaviour.method.moonsofperil.bloodmoon.BloodMoonJaguarPhase;
import org.dreambot.behaviour.method.moonsofperil.bloodmoon.BloodMoonRainPhase;
import org.dreambot.behaviour.method.moonsofperil.bloodmoon.GoToBloodMoon;
import org.dreambot.behaviour.method.moonsofperil.bluemoon.BlueMoonAttackPhase;
import org.dreambot.behaviour.method.moonsofperil.bluemoon.BlueMoonBraziersPhase;
import org.dreambot.behaviour.method.moonsofperil.bluemoon.BlueMoonIcePhase;
import org.dreambot.behaviour.method.moonsofperil.bluemoon.GoToBlueMoon;
import org.dreambot.behaviour.method.moonsofperil.eclipsemoon.EclipseMoonAttackPhase;
import org.dreambot.behaviour.method.moonsofperil.eclipsemoon.EclipseMoonClonesPhase;
import org.dreambot.behaviour.method.moonsofperil.eclipsemoon.EclipseMoonShieldPhase;
import org.dreambot.behaviour.method.moonsofperil.eclipsemoon.GoToEclipseMoon;
import org.dreambot.behaviour.quests.perilousmoon.PerilousMoonNodes;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.TickFractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.scriptdata.MoonsOfPerilsSettings;

import java.util.function.Supplier;

@Accessors(chain = true)
public class MoonsOfPerilBranch extends Fractal {
    // this area includes all the caverns/prison & the ancient shrine, does NOT include sepulcher, that is a different Z:1
    public static final Area PERILOUS_ROOMS = new Area(
            new Tile(1400, 9605, 0),
            new Tile(1400, 9523, 0),
            new Tile(1326, 9531, 0),
            new Tile(1330, 9611, 0),
            new Tile(1341, 9757, 0),
            new Tile(1550, 9737, 0),
            new Tile(1533, 9515, 0),
            new Tile(1486, 9524, 0),
            new Tile(1493, 9609, 0),
            new Tile(1526, 9639, 0),
            new Tile(1487, 9660, 0),
            new Tile(1466, 9664, 0),
            new Tile(1464, 9705, 0),
            new Tile(1417, 9706, 0),
            new Tile(1408, 9659, 0),
            new Tile(1368, 9653, 0),
            new Tile(1363, 9613, 0)
    );
    public static final int PMOON_BOSS_BLOOD_DEAD = 9858;
    public static final int PMOON_BOSS_BLUE_DEAD = 9859;
    public static final int PMOON_BOSS_ECLIPSE_DEAD = 9860;

    public static boolean isBloodMoonDead() {
        return PlayerSettings.getBitValue(PMOON_BOSS_BLOOD_DEAD) != 0;
    }

    public static boolean isBlueMoonDead() {
        return PlayerSettings.getBitValue(PMOON_BOSS_BLUE_DEAD) != 0;
    }

    public static boolean isEclipseDead() {
        return PlayerSettings.getBitValue(PMOON_BOSS_ECLIPSE_DEAD) != 0;
    }

    public static boolean areAnyDead() {
        return isEclipseDead() || isBlueMoonDead() || isBloodMoonDead();
    }

    public static boolean areAllDead() {
        return isEclipseDead() && isBlueMoonDead() && isBloodMoonDead();
    }

    // this is important because its used in the script and the quest, so we dont want static fields.
    @Setter
    boolean killBlue = true;
    @Setter
    boolean killBlood = true;
    @Setter
    boolean killEclipse = true;

    public MoonsOfPerilBranch(Supplier<Boolean> acceptCondition, MoonsOfPerilsSettings settings) {
        super(acceptCondition);

        setSimpleName("Moons of Peril");
        PerilousMoonNodes.init();

        EquipmentLoadout outfit = new EquipmentLoadout()
                .addItem(EquipmentSlot.HAT, ItemVariants.DHAROK_HELM)
                .addItem(EquipmentSlot.CHEST, ItemVariants.DHAROK_CHEST)
                .addItem(EquipmentSlot.LEGS, ItemVariants.DHAROK_LEGS)
                .addItem(EquipmentSlot.FEET, ItemID.DRAGON_BOOTS)

                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                .addItem(EquipmentSlot.RING, ItemID.WARRIOR_RING)
                .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)

                .addItem(EquipmentSlot.CAPE, ItemID.OBSIDIAN_CAPE)
                .addItem(EquipmentSlot.CAPE, ItemID.FIRE_CAPE).enabledIfOwned()
                .addItem(EquipmentSlot.SHIELD, ItemID.TOKTZKETXIL_OBSIDIAN_SHIELD)
                .addItem(EquipmentSlot.WEAPON, ItemID.ABYSSAL_WHIP);

        InventoryLoadout inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.CALCIFIED_MOTH)
                .setEnabledCondition(() -> Players.getLocal().getX() > 2800) // if not in moons
                .addItem(ItemVariants.DHAROK_GREATAXE)
                .addItem(ItemID.GLACIAL_TEMOTLI)
                .setEnabledCondition(() -> settings == null || !settings.useDualMacs)
                .addItem(ItemID.DUAL_MACUAHUITL)
                .setEnabledCondition(() -> settings != null && settings.useDualMacs)
                .setStrict(true);
//
        addChildren(
                new MoonsConfigurePrayerFlicks(),
//                // get gear loadout
                new MoonsOfPerilKitUp(outfit, inventoryLoadout),
                // restock supplies

                new MoonsOfPerilRewards(() -> (!killEclipse || isEclipseDead())
                        && (!killBlue || isBlueMoonDead())
                        && (!killBlood || isBloodMoonDead())),

                new MoonsOfPerilGetFightSupplies(
                        // these conditions cover currently restocking
                        () -> (Inventory.contains(ItemID.RAW_BREAM) || Inventory.contains(ItemID.VIAL_OF_WATER))
                                // these are should restock, z check is for in sepulcher
                                || ((Players.getLocal().getZ() == 1 || PERILOUS_ROOMS.contains(Players.getLocal())
                                && (ItemVariants.MOONLIGHT_POTION.getItem() == null || Inventory.count(ItemID.COOKED_BREAM) < 12)))
                ),

                // todo add settings to kill only certain ones
                new TickFractal(() -> killEclipse && !isEclipseDead())
                        .addChildren(
                                new MoonsOfPerilEat(),
                                new GoToEclipseMoon(),
                                new EclipseMoonShieldPhase(),
                                new MoonsPrayFlick(),
                                new EclipseMoonClonesPhase(),
                                new EclipseMoonAttackPhase()
                        )
                        .setSimpleName("Eclipse moon"),

                new TickFractal(() -> killBlue && !isBlueMoonDead())
                        .addChildren(
                                new MoonsOfPerilEat(),
                                new GoToBlueMoon(),
                                new BlueMoonBraziersPhase(),
                                new MoonsPrayFlick(),
                                new BlueMoonIcePhase(),
                                new BlueMoonAttackPhase()
                        )
                        .setSimpleName("Blue moon"),

                new TickFractal(() -> killBlood && !isBloodMoonDead())
                        .addChildren(
                                new MoonsOfPerilEat(),
                                new GoToBloodMoon(),
                                new BloodMoonRainPhase(),
                                new MoonsPrayFlick(),
                                new BloodMoonJaguarPhase(),
                                new BloodMoonAttackPhase()
                        )
                        .setSimpleName("Blood moon")
                );
    }
}
