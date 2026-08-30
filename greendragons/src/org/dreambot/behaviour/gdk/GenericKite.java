package org.dreambot.behaviour.gdk;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.SmartLootEvent;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.List;
import java.util.function.Supplier;

public class GenericKite extends Fractal {
    final Tile safe;
    final Supplier<NPC> dragonSupplier;

    public GenericKite(Tile safe, Supplier<NPC> dragonSupplier) {
        this.safe = safe;
        this.dragonSupplier = dragonSupplier;
        this.paintArraySupplier = () -> {
            NPC dragon = dragonSupplier.get();
            return new String[]{
                    "Dragon " + (dragon == null ? "null" : dragon.getServerTile().distance()),
            };
        };
    }

    final Area GREEN_DRAGONS_17 = new Area(2961, 3628, 2994, 3602);

    @Override
    public int onLoop() {
        Item staminaPot = ItemVariants.STAMINA_POTION.getItem();
        if (Walking.getRunEnergy() < 5 && staminaPot != null) {
            staminaPot.interact("Drink");
            Sleep.sleepUntil(() -> Walking.getRunEnergy() > 20, 1400);
        }

        if (!Walking.isRunEnabled() && (Walking.getRunEnergy() > 20 || Players.getLocal().isHealthBarVisible())) {
            Walking.toggleRun();
        }

        if (safe.distance() > 8) {
            Logger.info("Walking to safe spot");
            if (Walking.shouldWalk(8)) Walking.walk(safe);
            return ReactionGenerator.getQuick();
        }

        if (safe.equals(Players.getLocal().getTile()) && Skills.getBoostedLevel(Skill.HITPOINTS) <= ScriptSettings.getSettingsData().eatAbove && Combat.getHealthPercent() != 100) {
            Inventory.interact(ItemID.JUG_OF_WINE, "Drink");
            return ReactionGenerator.getQuick();
        }

        if (Dialogues.inDialogue()) {
            Dialog.solve();
        }

        int boosted = Skills.getBoostedLevel(Skill.RANGED) - Skills.getRealLevel(Skill.RANGED);
        if (boosted < ScriptSettings.getSettingsData().minBoost) {
            Item rangePot = ItemVariants.RANGE_POTION.getItem();
            if (rangePot != null) {
                rangePot.interact("Drink");
                Sleep.sleepUntil(() -> Skills.getBoostedLevel(Skill.RANGED) - Skills.getRealLevel(Skill.RANGED) > ScriptSettings.getSettingsData().minBoost, 1400);
            }
        }

        NPC dragon = dragonSupplier.get();
        if (dragon != null) {
            Character attackingDragon = dragon.getInteractingCharacter();
            if (attackingDragon != null && !attackingDragon.equals(Players.getLocal())) {
                Logger.info("Dragon is already under attack");
                if (!Players.getLocal().isInCombat() && ScriptSettings.getSettingsData().avoidCompetition) {
                    Logger.info("Hopping world");
                    WorldHopper.hopWorld(worldSupplier.get());
                }
                return ReactionGenerator.getQuick();
            }

            Tile dragonsTile = dragon.getServerTile();
            double dist = dragonsTile.distance();
//            Logger.info(String.format("Dragon on %s %f.2 away", dragonsTile, dist));
            if (dist <= (Walking.isRunEnabled() ? 6 : 8) && !Players.getLocal().getServerTile().equals(safe)) {
                Logger.info("");
                if (Walking.shouldWalk(6)) {
                    Walking.walkExact(safe);
                    Sleep.sleepUntil(() -> Players.getLocal().getServerTile().equals(safe), 1600);
                }
                Sleep.sleep(300);
            }

            Character c = Players.getLocal().getInteractingCharacter();
            if (c == null || !c.equals(dragon)) {
                if ((dist >= 4 || isOnSafe()) && dragon.interact("Attack")) {
                    Sleep.sleepUntil(() -> {
                        Character tgt = Players.getLocal().getInteractingCharacter();
                        return tgt != null && tgt.equals(dragon);
                    }, 1000);
                }
            }
            return ReactionGenerator.getQuick();
        }

        Supplier<List<GroundItem>> lootSupplier = () -> GroundItems.all(
                x -> (ItemVariants.LOOTING_BAG.contains(x.getID()) && ScriptSettings.getSettingsData().useLootingBag)
                        ||
                        (x.getAmount() * LivePrices.get(x.getID()) > ScriptSettings.getSettingsData().minLootValue
                                && isSafeToLoot(x)
//                        &&FORTRESS_DRAGONS.contains(x)
                                && (x.getID() != ScriptSettings.getFoodId() || Inventory.getEmptySlots() > 1)) || (x.getID() == ItemID.RUNE_ARROW && safe.distance(x) < 15 && isSafeToLoot(x))
        );
        if (!lootSupplier.get().isEmpty()) {
            Logger.info("Loot event: " + new SmartLootEvent(lootSupplier, ItemID.JUG_OF_WINE, ItemID.JUG).executed());
            return ReactionGenerator.getQuick();
        }


        return ReactionGenerator.getQuick();
    }

    private boolean isOnSafe() {
        return safe.equals(Players.getLocal().getTile());
    }

    private boolean isSafeToLoot(GroundItem i) {
        NPC dragon = NPCs.closest(d -> d.getName().equals("Green dragon"), i.getTile());
        return dragon == null || dragon.distance(i) > 5 || i.distance(safe) < 10;
    }
}
