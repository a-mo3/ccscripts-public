package org.dreambot.behaviour.tutorial;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.hint.HintArrow;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.settings.Varcs;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.AccountManager;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.fractals.IronFractal;
import org.dreambot.fractals.IronmanType;
import org.dreambot.generics.*;
import org.dreambot.gui.settings.SettingFractal;

import java.util.function.BooleanSupplier;

/**
 * does tutorial island
 */
public class TutorialTree extends SettingFractal<TutSettings> {
    public TutorialTree() {
        super(() -> tutState() < 1000);
        setSimpleName("Tutorial island");

        IronmanType desiredType = getSettings().ironmanType;
        addChildren(
                new IronFractal(() -> tutState() == 1).addChildren(
                        new GenericWidgetInteraction(() -> Widgets.get(929, 7)).setSimpleName("Experienced"),
                        new GenericWidgetInteraction(w -> w.hasAction("Confirm")).setSimpleName("Confirm")
                                .setSleepAround(35_000, 10_000),
                        new GenericWidgetInteraction(w -> w.isVisible() && w.hasAction("Set name"))
                                .setSleepAround(35_000, 5000).setSimpleName("Set name"),
                        new GenericWidgetInteraction(w -> w.hasAction("Look up name")).setAction("Look up name").setSimpleName("Look up")
                                .setSleepAround(35_000, 5000)
                                .setPrependLogic(() -> {
                                    log("Look up " + Varcs.getString(436));
                                    if (Varcs.getString(436).isEmpty()) Varcs.setString(436, "penis");
                                    Sleep.sleepTicks(2);
                                    return false;
                                }),
                        new GenericWidgetInteraction(w -> w.hasAction("Enter name")).setSimpleName("Enter name")
                ).setSimpleName("Set name"),

                new IronFractal(() -> tutState() < 20).addChildren(
                        new GenericEntityInteraction(() -> tutState() == 10, () -> GameObjects.closest("Door"))
                                .setAction("Open").setSimpleName("Door"),
                        new GenericWidgetInteraction(() -> tutState() == 3 && !Tabs.isDisabled(Tab.OPTIONS), Tab.OPTIONS::getWidgetChild).setSimpleName("Settings"),
                        new GenericEntityInteraction("Gielinor Guide").setSimpleName("Guide")
                ).setSimpleName("Guide"),

                new IronFractal(() -> tutState() < 120).addChildren(
                        // fishing shrimps >= 40
                        new GenericItemUse(() -> tutState() == 90, () -> GameObjects.closest("Fire"), "Raw shrimps")
                                .setSleepAround(5000, 600).setSimpleName("Cook shrimp"),
                        new GenericInventoryCombination(() -> tutState() == 80, "Logs", "Tinderbox")
                                .setSleepAround(3000, 400).setSimpleName("Set fire"),
                        new GenericEntityInteraction(() -> tutState() == 70, () -> GameObjects.closest("Tree")).setSimpleName("Tree"),
                        new GenericWidgetInteraction(() -> tutState() == 50 && !Tab.SKILLS.isDisabled(), Tab.SKILLS::getWidgetChild).setSimpleName("Settings"),
                        new GenericEntityInteraction(() -> tutState() == 40, () -> NPCs.closest("Fishing spot")).setAction("Net").setSimpleName("Fish"),
                        new GenericWidgetInteraction(() -> tutState() == 30 && !Tab.INVENTORY.isDisabled(), Tab.INVENTORY::getWidgetChild).setSimpleName("Settings"),
                        new GenericEntityInteraction("Survival Expert").setSimpleName("Expert")
                ).setSimpleName("Survival"),

                new IronFractal(() -> tutState() < 170).addChildren(
                        new GenericItemUse(() -> Inventory.contains("Bread dough"), () -> GameObjects.closest("Range"), "Bread dough").setSimpleName("Bake"),
                        new GenericInventoryCombination(() -> Inventory.contains("Pot of flour"), "Pot of flour", "Bucket of water")
                                .setSleepAround(1700, 200).setSimpleName("Dough"),
                        new GenericEntityInteraction(() -> true, "Master Chef", new Area(3078, 3086, 3073, 3083))
                                .setSleepAround(1400, 400).setSimpleName("Talk to cook")
                ).setSimpleName("Cook"),

                new GenericWidgetInteraction(() -> tutState() == 230 && !Tab.QUEST.isDisabled(), Tab.QUEST::getWidgetChild).setSimpleName("Quest tab"),
                new GenericEntityInteraction(() -> tutState() < 250, "Quest guide", new Area(3082, 3125, 3089, 3119))
                        .setSimpleName("Quests"),

                new IronFractal(() -> tutState() < 360).addChildren(
                        new GenericWidgetInteraction(w -> w.hasAction("Smith") && w.getName().contains("Bronze dagger") && w.isVisible())
                                .setSleepAround(4000, 300).setSimpleName("Dagger"),
                        new GenericEntityInteraction(() -> tutState() >= 340, () -> GameObjects.closest("Anvil"))
                                .setSleepAround(8000, 500).setSimpleName("Make dagger"),
                        new GenericEntityInteraction(() -> tutState() == 320, () -> GameObjects.closest("Furnace"))
                                .setSleepAround(8000, 500).setSimpleName("Smelt bronze"),
                        new GenericEntityInteraction(() -> tutState() == 310, () -> GameObjects.closest("Copper rocks"))
                                .setSleepAround(4000, 500).setSimpleName("Mine Copper"),
                        new GenericEntityInteraction(() -> tutState() == 300, () -> GameObjects.closest("Tin rocks"))
                                .setSleepAround(4000, 500).setSimpleName("Mine tin"),
                        new GenericEntityInteraction(() -> true, "Mining Instructor", new Area(3077, 9510, 3086, 9496))
                                .setSimpleName("Guide")
                ).setSimpleName("Smithing"),

                new IronFractal(() -> tutState() < 500).addChildren(
                        new IronFractal(() -> Players.getLocal().isInCombat()).setSleepAround(4400, 100).setSimpleName("In combat"),

                        new GenericItemUse("Shortbow"),
                        new GenericItemUse("Bronze arrow"),
                        new GenericEntityInteraction(() -> tutState() >= 470 && HintArrow.getPointed() != null && "Giant rat".equals(HintArrow.getPointed().getName()),
                                () -> NPCs.closest("Giant rat"))
                                .setDoReachCheck(false)
                                .setSleepAround(2000, 400).setSimpleName("Range Rat"),

                        new GenericEntityInteraction(() -> tutState() >= 440 && tutState() < 470,
                                "Giant rat", new Tile(3103, 9517, 0), 3)
                                .setSimpleName("Melee Rat"),

                        new GenericWidgetInteraction(() -> tutState() == 430 && !Tab.COMBAT.isDisabled(), Tab.COMBAT::getWidgetChild).setSimpleName("Combat tab"),
                        new GenericItemUse("Bronze sword"),
                        new GenericItemUse("Wooden shield"),
                        new GenericItemUse(() -> tutState() == 405, "Bronze dagger")
                                .setPrependLogic(() -> {
                                    Widgets.closeAll();
                                    return false;
                                })
                                .setSimpleName("Equip bronze dagger"),
                        new GenericWidgetInteraction(() -> tutState() == 400, w -> w.hasAction("View equipment stats")).setSimpleName("Equipment Stats"),
                        new GenericWidgetInteraction(() -> tutState() == 390 && !Tab.EQUIPMENT.isDisabled(), Tab.EQUIPMENT::getWidgetChild).setSimpleName("Equipment tab"),
                        new GenericEntityInteraction(() -> true, "Combat Instructor", new Tile(3108, 9510, 0), 5)
                                .setSimpleName("Combat Guide")
                ).setSimpleName("Combat"),

                new GenericEntityInteraction(() -> tutState() <= 510, "Bank booth", new Area(3118, 3125, 3124, 3119)),
                new GenericEntityInteraction(() -> tutState() <= 520, "Poll booth", new Area(3118, 3125, 3124, 3119)),
                new GenericWidgetInteraction(() -> tutState() == 531 && !Tabs.isDisabled(Tab.ACCOUNT_MANAGEMENT), Tab.ACCOUNT_MANAGEMENT::getWidgetChild)
                        .setPrependLogic(() -> {
                            Widgets.closeAll();
                            return false;
                        })
                        .setSleepAround(3000, 2000).setSimpleName("Settings"),
                new GenericEntityInteraction(() -> tutState() < 540, "Account guide", new Tile(3127, 3123, 0), 2),
                new GenericWidgetInteraction(() -> tutState() == 560 && !Tab.PRAYER.isDisabled(), Tab.PRAYER::getWidgetChild).setSimpleName("Prayer tab"),
                new GenericEntityInteraction(() -> tutState() < 610, "Brother Brace", new Area(3128, 3103, 3120, 3110)),

                new GenericWidgetInteraction(() -> tutState() == 630 && !Tab.MAGIC.isDisabled(), Tab.MAGIC::getWidgetChild).setSimpleName("Magic tab"),
                new GenericCastSpell(() -> tutState() == 650, Normal.WIND_STRIKE, () -> NPCs.closest("Chicken"))
                        .setSimpleName("Cast on chicken"),

                new IronFractal(() -> IronmanType.getCurrent() != desiredType).addChildren(
                        new GenericWidgetInteraction(x -> x.hasAction("Proceed")).setSimpleName("Proceed")
                                .setPrependLogic(() -> {
                                    if (AccountManager.getAccountBankPin().isEmpty()) AccountManager.setPin("6969");
                                    return false;
                                })
                                .setSleepAround(4000, 500),

                        new GenericWidgetScroll(() -> Widgets.get(x -> x.hasAction(desiredType.type)), () -> Widgets.get(890, 3))
                                .setSimpleName("Scroll"),
                        new GenericWidgetInteraction(x -> x.hasAction(desiredType.type)).setSimpleName(desiredType.type)
                                .setSleepAround(4000, 500),

                        new GenericEntityInteraction(() -> true, "Ironman tutor", new Tile(3131, 3084, 0), 5)
                                .setDialogueChoices("Ironman", "Ironmen")
                ).setSimpleName("Set ironman"),

                new GenericEntityInteraction(() -> tutState() <= 671, "Magic Instructor", new Area(3140, 3091, 3143, 3084))
                        .setDialogueChoices("not", "Yes"),

                new GenericCastSpell(() -> true, Normal.HOME_TELEPORT).setSleepAround(65_000, 2000)

        );

        setSleeps(this);
    }

    void setSleeps(IronFractal f) {
        if (!f.getChildren().isEmpty()) {
            f.getChildren().forEach(this::setSleeps);
        }
        f.setSleepAround(Calculations.random(3400, 60_000), 500);
    }

    public static int tutState() {
        return PlayerSettings.getConfig(281);
    }

    @Override
    public String settingName() {
        return "tutIsland";
    }

    @Override
    public TutSettings defaultSettings() {
        return new TutSettings();
    }
}
