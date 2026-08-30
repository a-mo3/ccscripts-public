package org.dreambot.behaviour.training.quests.animalmagnetism;


import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.behaviour.training.quests.animalmagnetism.behaviour.device.MakeDeviceFinish;
import org.dreambot.behaviour.training.quests.animalmagnetism.behaviour.device.TranslateNotes;
import org.dreambot.behaviour.training.quests.animalmagnetism.behaviour.holyaxe.CutDeadTree;
import org.dreambot.behaviour.training.quests.animalmagnetism.behaviour.holyaxe.TalkToTurael;
import org.dreambot.behaviour.training.quests.animalmagnetism.behaviour.magnet.MakeMagnet;
import org.dreambot.behaviour.training.quests.animalmagnetism.behaviour.magnet.TalkToWitch;
import org.dreambot.behaviour.training.quests.animalmagnetism.behaviour.undeadchickens.*;
import org.dreambot.fractals.BankUtil;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.util.OwnedItems;

public class AnimalMagnetismBranch extends Fractal {
    private final Area URHNEY_HOUSE = new Area(3144, 3173, 3151, 3177, 0);
    public AnimalMagnetismBranch() {
        addChildren(
                new TalkToFractal(() -> !PaidQuest.ANIMAL_MAGNETISM.isStarted()
                        && !OwnedItems.contains(ItemID.GHOSTSPEAK_AMULET) && !Equipment.contains(ItemID.GHOSTSPEAK_AMULET),
                        URHNEY_HOUSE, () -> NPCs.closest("Father Urhney"))
                        .setDialogueOptions("Amulet")
                        .setAppendLogic(() -> {
                            if (!Bank.isCached()) {
                                if (Walking.shouldWalk()) BankUtil.openClosest();
                                return true;
                            }
                            return false;
                        })
                        .setSimpleName("Get ghostspeak"),
                // undead chickens
                new StartAnimalMag().setSimpleName("Start"),
                new GetEctoTokens().setSimpleName("Ecto tokens"),
                new TalkToHusband().setSimpleName("Talk to husband"),
                new TalkToWife().setSimpleName("Talk to wife"),
                new TalkToOldCrone().setSimpleName("Old crone"),
                new GiveAvaChickens().setSimpleName("Give ava chicks"),
                // magnet
                new TalkToWitch().setSimpleName("Talk to witch"),
                new MakeMagnet().setSimpleName("Make magnet"),
                // holy axe
                new CutDeadTree().setSimpleName("Cut dead tree"),
                new TalkToTurael().setSimpleName("Turael"),
                // device
                new TranslateNotes().setSimpleName("Translate notes"),
                new MakeDeviceFinish().setSimpleName("Finish")
        );
    }

    @Override
    public boolean isValid() {
        return !PaidQuest.ANIMAL_MAGNETISM.isFinished();
    }
}
