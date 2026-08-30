package org.dreambot.ui.mappings.prayer;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;
import org.dreambot.behaviour.prayer.CheapPrayerFractal;
import org.dreambot.fractals.Fractal;
import org.dreambot.ui.Base64Util;
import org.dreambot.ui.components.DreamPanel;
import org.dreambot.ui.components.DreamToggleButton;
import org.dreambot.ui.mappings.AbstractSkillMapping;

import javax.swing.*;

public class PrayerMapping extends AbstractSkillMapping<PrayerData> {
    private static PrayerMapping instance = new PrayerMapping();

    public static PrayerMapping getInstance() {
        if (instance == null) instance = new PrayerMapping();
        return instance;
    }

    private PrayerMapping() {
    }

    DreamPanel prayerPanel = null;
    DreamToggleButton cowHeadButton;

    // todo card layout for leeching or buying bones or camadozal maybe.
    @Override
    public DreamPanel makePanel() {
        if (prayerPanel != null) {
            return prayerPanel;
        }
        prayerPanel = new DreamPanel();

        SpringLayout layout = new SpringLayout();

        prayerPanel.setLayout(layout);

        cowHeadButton = new DreamToggleButton(new ImageIcon(Base64Util.decodeToImage(PrayerIcons.COW_HEAD)), true, Logger::info);
        prayerPanel.add(cowHeadButton);
        layout.putConstraint(SpringLayout.WEST, cowHeadButton, 15, SpringLayout.WEST, prayerPanel);
        layout.putConstraint(SpringLayout.NORTH, cowHeadButton, 15, SpringLayout.NORTH, prayerPanel);

        return prayerPanel;
    }

    @Override
    public PrayerData generateDataclass() {
        return new PrayerData()
                .setTargetLvl(getTargetLevel())
                .setBuryCowBones(cowHeadButton.isSelected());
    }

    @Override
    public Fractal makeFractal(PrayerData data) {
        Area cowArea = new Area(
                new Tile(3241, 3298, 0),
                new Tile(3263, 3298, 0),
                new Tile(3265, 3297, 0),
                new Tile(3266, 3255, 0),
                new Tile(3253, 3255, 0),
                new Tile(3253, 3272, 0),
                new Tile(3252, 3279, 0),
                new Tile(3245, 3280, 0));

        final int BONES = 526;

        return new Fractal(() -> data.getTargetLvl() > Skills.getRealLevel(Skill.PRAYER))
                .setSimpleName("Prayer")
                .addChildren(
                        new CheapPrayerFractal(() -> true, cowArea, BONES)
                                .setSimpleName("Bury cow bones")
                );
    }
}
