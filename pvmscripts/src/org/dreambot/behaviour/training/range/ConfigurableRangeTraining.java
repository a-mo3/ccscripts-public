package org.dreambot.behaviour.training.range;

import lombok.experimental.Accessors;
import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.behaviour.GetOff330;
import org.dreambot.behaviour.method.gemstone.GemstoneCrab;
import org.dreambot.behaviour.method.gemstone.GemstoneCrabMeleeLoadout;
import org.dreambot.behaviour.method.scurrius.GoToScurrius;
import org.dreambot.behaviour.method.scurrius.MakeBoneWeapon;
import org.dreambot.behaviour.method.scurrius.ScurriusBranch;
import org.dreambot.behaviour.method.scurrius.ScurriusMode;
import org.dreambot.behaviour.misc.SandCrabs;
import org.dreambot.behaviour.quests.childrenofthesun.ChildrenOfTheSun;
import org.dreambot.behaviour.quests.rfd.GetRockCake;
import org.dreambot.behaviour.training.nmz.ConfigurableCombatMode;
import org.dreambot.behaviour.training.nmz.RangeNightmareZone;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.fractals.util.RatConfigureQuickPrayers;
import org.dreambot.settings.fractalsettings.ConfigurableFractal;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.webintegration.WebLoadoutLoader;

import java.util.function.Supplier;

@Accessors(chain = true)
public class ConfigurableRangeTraining extends Fractal implements ConfigurableFractal<ConfigurableRangeSettings> {
    public ConfigurableRangeTraining(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Range training");
        ConfigurableRangeSettings settings = getSettings();

        // parse custom for gemstone
        EquipmentLoadout parsedCustomLoadout = null;
        if (getSettings().gemstoneCustomLoadout != null && !getSettings().gemstoneCustomLoadout.isEmpty()) {
            try {
                parsedCustomLoadout = WebLoadoutLoader.parseEquipment(getSettings().gemstoneCustomLoadout);
            } catch (Exception e) {
                log("Failed to parse gemstone custom");
            }
        }

        addChildren(
                new Fractal(() -> settings.trainingMode == ConfigurableRangeMode.GEMSTONE_CRAB && ConfigurableRangeMode.GEMSTONE_CRAB.isOwned())
                        .addChildren(
                                new ChildrenOfTheSun().setSimpleName("COS"),
                                // kill gemstone
                                new GetOff330(GetOff330.MEMBERS_WORLD_FILTER).setSimpleName("Hop off 330"),
                                GemstoneCrab.getRange(100, settings.defTarget)
                                        .setInventoryLoadout(settings.gemstoneLoadout.inventoryLoadout)
                                        .setEquipmentLoadout(parsedCustomLoadout != null ? parsedCustomLoadout : getSettings().gemstoneLoadout.equipmentLoadout)
                        ),

                new Fractal(() -> settings.trainingMode == ConfigurableRangeMode.SCURRIUS && ConfigurableRangeMode.SCURRIUS.isOwned())
                        .addChildren(
                                SandCrabs.getRange(() -> Skills.getRealLevel(Skill.RANGED) < 40 || Skills.getRealLevel(Skill.HITPOINTS) < 30)
                                        .setSimpleName("at least 30 base hp & 40 range"),
                                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < 44)
                                        .setSimpleName("Prayer training"),
                                new GetOff330(GetOff330.MEMBERS_WORLD_FILTER).setSimpleName("Hop off 330"),
                                new RatConfigureQuickPrayers(() -> new Prayer[]{Prayer.PROTECT_FROM_MELEE, ScurriusMode.getBestRangePray()})
                                        .setSimpleName("Range q p"),

                                new MakeBoneWeapon(() -> OwnedItems.contains(ItemID.SCURRIUS_SPINE) && !OwnedItems.contains(ItemID.BONE_SHORTBOW),
                                        MakeBoneWeapon.BONE_BOW_LOADOUT)
                                        .setSimpleName("Make bone bow"),

                                new GoToScurrius(() -> !Client.isDynamicRegion() || Equipment.isSlotEmpty(EquipmentSlot.ARROWS), ScurriusMode.RANGE),
                                new ScurriusBranch(() -> true, ScurriusMode.RANGE, false)
                                        .setFlick(getSettings().flicking)
                                        .setStyleSupplier(() -> {
                                            return CombatStyle.RANGED_RAPID;
                                        })
                                        .setSimpleName("Scurrius")
                        ).setSimpleName("Scurrius"),

                new Fractal(() -> settings.trainingMode == ConfigurableRangeMode.NMZ && ConfigurableRangeMode.NMZ.isOwned())
                        .setSimpleName("Range NMZ")
                        .addChildren(
                                new GetOff330(GetOff330.MEMBERS_WORLD_FILTER).setSimpleName("Hop off 330"),
                                new RangeNightmareZone(() -> true, getSettings().nmzCustom.getLoadout())
                        ),

                new GetOff330(GetOff330.MEMBERS_WORLD_FILTER).setSimpleName("Hop off 330"),
                new DistributedRangeTraining(() -> settings.trainingMode == ConfigurableRangeMode.DISTRIBUTED).setSimpleName("Distributed mobs"),
                // todo pass down def target
                SandCrabs.getRange(acceptCondition)
                        .setSimpleName("Crabs")
        );
    }


    public ConfigurableRangeTraining(Supplier<Boolean> acceptCondition, int defTarget) {
        super(acceptCondition);
        setSimpleName("Range training");
        ConfigurableRangeSettings settings = getSettings();

        // parse custom for gemstone
        EquipmentLoadout parsedCustomLoadout = null;
        if (getSettings().gemstoneCustomLoadout != null && !getSettings().gemstoneCustomLoadout.isEmpty()) {
            try {
                parsedCustomLoadout = WebLoadoutLoader.parseEquipment(getSettings().gemstoneCustomLoadout);
            } catch (Exception e) {
                log("Failed to parse gemstone custom");
            }
        }
        addChildren(
                new Fractal(() -> settings.trainingMode == ConfigurableRangeMode.GEMSTONE_CRAB && ConfigurableRangeMode.GEMSTONE_CRAB.isOwned())
                        .addChildren(
                                GemstoneCrab.getRange(100, defTarget)
                                        .setInventoryLoadout(settings.gemstoneLoadout.inventoryLoadout)
                                        .setEquipmentLoadout(parsedCustomLoadout != null ? parsedCustomLoadout : getSettings().gemstoneLoadout.equipmentLoadout)
                        ),


                new Fractal(() -> settings.trainingMode == ConfigurableRangeMode.SCURRIUS && ConfigurableCombatMode.SCURRIUS.isOwned())
                        .addChildren(
                                SandCrabs.getRange(() -> Skills.getRealLevel(Skill.RANGED) < 40 || Skills.getRealLevel(Skill.HITPOINTS) < 30)
                                        .setSimpleName("at least 30 base hp & 40 range"),
                                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < 43)
                                        .setSimpleName("Prayer training"),
                                new RatConfigureQuickPrayers(() -> new Prayer[]{Prayer.PROTECT_FROM_MELEE, ScurriusMode.getBestRangePray()})
                                        .setSimpleName("Range q p"),

                                new MakeBoneWeapon(() -> OwnedItems.contains(ItemID.SCURRIUS_SPINE) && !OwnedItems.contains(ItemID.BONE_SHORTBOW),
                                        MakeBoneWeapon.BONE_BOW_LOADOUT)
                                        .setSimpleName("Make bone bow"),

                                new GoToScurrius(() -> !Client.isDynamicRegion() || Equipment.isSlotEmpty(EquipmentSlot.ARROWS), ScurriusMode.RANGE),
                                new ScurriusBranch(() -> true, ScurriusMode.RANGE, false)
                                        .setStyleSupplier(() -> {
                                            if (Skill.DEFENCE.getLevel() < defTarget) return CombatStyle.RANGED_DEFENCE;
                                            return CombatStyle.RANGED_RAPID;
                                        })
                                        .setSimpleName("Scurrius")
                        ).setSimpleName("Scurrius"),

                new Fractal(() -> settings.trainingMode == ConfigurableRangeMode.NMZ && ConfigurableRangeMode.NMZ.isOwned())
                        .setSimpleName("Range NMZ")
                        .addChildren(
                                new RangeNightmareZone(() -> true, getSettings().nmzCustom.getLoadout())
                        ),

                new DistributedRangeTraining(() -> settings.trainingMode == ConfigurableRangeMode.DISTRIBUTED).setSimpleName("Distributed mobs"),
                // todo pass down def target
                SandCrabs.getRange(acceptCondition)
                        .setDefenceTarget(defTarget)
                        .setSimpleName("Crabs")
        );
    }

    @Override
    public ConfigurableRangeSettings getSettings() {
        return SettingsRepository.getSetting(settingName(), new ConfigurableRangeSettings());
    }

    @Override
    public String settingName() {
        return "Range";
    }
}
