package org.dreambot.behaviour.quests;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.generic.TalkToFractal;

public class DragonSlayerOne extends Fractal {
    public DragonSlayerOne() {
        this.acceptCondition = () -> FreeQuest.DRAGON_SLAYER.getConfigValue() < 2;

        addChildren(
                new TalkToFractal(() -> FreeQuest.DRAGON_SLAYER.getConfigValue() == 0,
                        new Tile(3190, 3360, 0).getArea(5),
                        () -> NPCs.closest("Guildmaster"))
                        .setDialogueOptions("quest?", "Yes.", "better get", "")
                        .setSimpleName("Start DS1"),

                new TalkToFractal(() -> FreeQuest.DRAGON_SLAYER.getConfigValue() == 1,
                        new Tile(3068, 3517, 0).getArea(2),
                        () -> NPCs.closest("Oziach"))
                        .setDialogueOptions("sell me", "Guild", "give me a quest", "dragon", "better get", "")
                        .setSimpleName("Start DS1..")
        );
    }
}
