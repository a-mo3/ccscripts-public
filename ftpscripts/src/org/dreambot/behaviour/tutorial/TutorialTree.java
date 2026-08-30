package org.dreambot.behaviour.tutorial;


import org.dreambot.api.Client;
import org.dreambot.api.utilities.Logger;
import org.dreambot.behaviour.tutorial.banktutorial.BankLeaf;
import org.dreambot.behaviour.tutorial.combattutorial.HandleCombatTabsLeaf;
import org.dreambot.behaviour.tutorial.combattutorial.MeleeRatLeaf;
import org.dreambot.behaviour.tutorial.combattutorial.RangeRatLeaf;
import org.dreambot.behaviour.tutorial.cooktutorial.TalkToChef;
import org.dreambot.behaviour.tutorial.gielinorguide.GielinorGuideLeaf;
import org.dreambot.behaviour.tutorial.gielinorguide.SetNameLeaf;
import org.dreambot.behaviour.tutorial.prayertutorial.PrayerRoomLeaf;
import org.dreambot.behaviour.tutorial.questtutorial.QuestGuyLeaf;
import org.dreambot.behaviour.tutorial.smithingtutorial.MineLeaf;
import org.dreambot.behaviour.tutorial.smithingtutorial.SmeltLeaf;
import org.dreambot.behaviour.tutorial.survivaltutorial.FishAndCookShirmpLeaf;
import org.dreambot.behaviour.tutorial.survivaltutorial.SurvivalExpertLeaf;
import org.dreambot.behaviour.tutorial.wizardtutorial.WizardLeaf;
import org.dreambot.fractals.Fractal;

public class TutorialTree extends Fractal {
    boolean hasTut = Client.getInstance().getScriptManager().hasSDNScript(1738)
            || Client.getInstance().getScriptManager().hasSDNScript(1737);

    public TutorialTree() {
//        setCleanAfterAccomplished(true);
        addChildren(
                new GielinorGuideBranch().addChildren(
                                new SetNameLeaf(TutorialNameStrategy.RANDOM).setSimpleName("Setting name"),
                                new GielinorGuideLeaf().setSimpleName("Talk to guide"))
                        .setSimpleName("Guide tutorial"),
                new SurvivalTrainingBranch()
                        .addChildren(
                                new SurvivalExpertLeaf().setSimpleName("Talk to trainer"),
                                new FishAndCookShirmpLeaf().setSimpleName("Cook shrimp")
                        )
                        .setSimpleName("Survival Tutorial"),
                new CookTutorialBranch().addChildren(
                                new TalkToChef().setSimpleName("Talk to chef")
                        )
                        .setSimpleName("Cooking tutorial"),
                new QuestTutorialBranch()
                        .addChildren(
                                new QuestGuyLeaf().setSimpleName("Quest guy")
                        )
                        .setSimpleName("Quest tutorial"),
                new SmithingTutorialBranch()
                        .addChildren(
                                new MineLeaf().setSimpleName("Mine ores"),
                                new SmeltLeaf().setSimpleName("Smelt")
                        )
                        .setSimpleName("Smithing tutorial"),
                new CombatTutorialBranch()
                        .addChildren(
                                new HandleCombatTabsLeaf().setSimpleName("Tabs"),
                                new MeleeRatLeaf().setSimpleName("Melee rat"),
                                new RangeRatLeaf().setSimpleName("Range rat")
                        )
                        .setSimpleName("Combat"),
                new BankTutorialBranch()
                        .addChildren(
                                new BankLeaf().setSimpleName("Bank")
                        )
                        .setSimpleName("Banking tutorial"),
                new PrayerTutorialBranch()
                        .addChildren(
                                new PrayerRoomLeaf().setSimpleName("Prayer room")
                        )
                        .setSimpleName("Prayer tutorial"),
                new WizardTutorialBranch()
                        .addChildren(
                                new WizardLeaf().setSimpleName("Wizard")
                        )
                        .setSimpleName("Wizard")
        );
    }

    @Override
    public boolean isValid() {
        if (MyVarps.getTutVarp() < 1000 && !hasTut && Client.isLoggedIn())
            Logger.info("You need cctutorial for the script to do tutorial island");
        return MyVarps.getTutVarp() < 1000 && hasTut;
    }

//    @Override
//    public TutorialBranchSettings getSettings() {
//        return SettingsRepository.getSetting(getSimpleName(), new TutorialBranchSettings());
//    }
//
//    @Override
//    public String settingName() {
//        return "tutorialIsland";
//    }
}
