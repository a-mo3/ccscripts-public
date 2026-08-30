package task.impl;

import enums.TargetNPC;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import task.AbstractTask;
import util.EatUtil;

import java.util.ArrayList;
import java.util.List;

public class PickPocketNode extends AbstractTask {
    // elves have different names
    List<String> elvenNames = new ArrayList<String>() {{
        add("Anaire");
        add("Aranwe");
        add("Aredhel");
        add("Caranthir");
        add("Celebrian");
        add("Celegorm");
        add("Cirdan");
        add("Curufin");
        add("Earwen");
        add("Edrahil");
        add("Elenwe");
        add("Elladan");
        add("Enel");
        add("Erestor");
        add("Enerdhil");
        add("Enelye");
        add("Feanor");
        add("Findis");
        add("Finduilas");
        add("Fingolfin");
        add("Fingon");
        add("Galathil");
        add("Gelmir");
        add("Glorfindel");
        add("Guilin");
        add("Hendor");
        add("Idril");
        add("Imin");
        add("Iminye");
        add("Indis");
        add("Ingwe");
        add("Ingwion");
        add("Lenwe");
        add("Lindir");
        add("Maeglin");
        add("Mahtan");
        add("Miriel");
        add("Mithrellas");
        add("Nellas");
        add("Nerdanel");
        add("Nimloth");
        add("Oropher");
        add("Orophin");
        add("Saeros");
        add("Salgant");
        add("Tatie");
        add("Thingol");
        add("Turgon");
        add("Vaire");
    }};

    @Override
    public boolean accept() {
        return config.isPickpocketing() && config.getPickpocketTarget() != null;
    }

    @Override
    public int execute() {
        NPC grave = NPCs.closest("grave");
        if (grave != null && grave.interact("loot")) {
            Sleep.sleepUntil(() -> !grave.exists(), 5000);
        }
        // check reqs
        if (config.getPickpocketTarget().REQLVL > Skills.getRealLevel(Skill.THIEVING)) {
            Logger.log("YOU DO NOT HAVE THE REQUIRED LEVEL FOR THIS TARGET.");
            ScriptManager.getScriptManager().stop();
        }
        // handle pouches
        if (Inventory.count("Coin pouch") == 28) {
            if (Inventory.interact("Coin pouch", "Open-all")) {
                Sleep.sleepUntil(() -> !Inventory.contains("Coin pouch"), 1200);
            }
        }
        // handle eating
        if (Skills.getBoostedLevel(Skill.HITPOINTS) <= config.getPickpocketTarget().MAX_DMG + 2) {
            if (EatUtil.hasFood()) {
                if (EatUtil.eat()) {
                    Sleep.sleepUntil(() -> Skills.getBoostedLevel(Skill.HITPOINTS) > (config.getPickpocketTarget().MAX_DMG + 2), 1300);
                }
            } else {
                if (config.isBankingMode()) {
                    config.setActivityTile(Players.getLocal().getTile());
                    config.setShouldBank(true);
                    return 20;
                }
            }
        }
        // handle necklaces
        if (config.isUseNecklace() && !Equipment.contains("Dodgy necklace")) {
            if (Inventory.contains("Dodgy necklace")) {
                if (Inventory.interact("Dodgy necklace", "Wear")) {
                    Sleep.sleepUntil(() -> Equipment.contains("Dodgy necklace"), 1000);
                }
            } else {
                if (config.isBankingMode()) {
                    config.setActivityTile(Players.getLocal().getTile());
                    config.setShouldBank(true);
                    return 20;
                }
            }
        }
        // handle full inv
        if (Inventory.isFull()) {
            Inventory.dropAll(x -> x.getName().contains("seed") && LivePrices.get(x.getID()) <= 200);
            Inventory.dropAll("Jug");
            Sleep.sleep(Calculations.random(100, 178));
        }

        // the actual pickpocketing
        NPC target = getTarget();
        if (target != null) {
            config.setActivityTile(Players.getLocal().getTile());
            if (!Players.getLocal().isInCombat() && target.interact("Pickpocket")) {
                Sleep.sleepUntil(() -> Players.getLocal().getAnimation() != -1, 1000);
                if (Players.getLocal().getAnimation() == 881) { // successful pickpocket
                    Sleep.sleep(100);
                } else if (Players.getLocal().getAnimation() == 424) {
                    Sleep.sleep(Calculations.random(config.getPickpocketTarget().STUN_TIME, config.getPickpocketTarget().STUN_TIME + 230));
                }
            }
        }
        return Calculations.random(120, 320);
    }

    private NPC getTarget() {
        if (config.getPickpocketTarget() == TargetNPC.ELF) {
            return NPCs.closest(x -> elvenNames.contains(x.getName()) && x.hasAction("Pickpocket"));
        }
        return NPCs.closest(config.getPickpocketTarget().NAME);
    }
}
