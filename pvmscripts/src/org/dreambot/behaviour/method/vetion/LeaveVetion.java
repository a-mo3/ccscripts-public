package org.dreambot.behaviour.method.vetion;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.HitSplatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.method.spindel.LootSpindel;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.LootingBag;
import org.dreambot.fractals.util.UtilProvider;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;

public class LeaveVetion extends Fractal implements HitSplatListener {
    static List<Integer> food = Arrays.asList(
            ItemID.BLIGHTED_KARAMBWAN, ItemID.BLIGHTED_ANGLERFISH, ItemID.BLIGHTED_MANTA_RAY
    );

    public LeaveVetion(int maxLoot) {
        super(() -> (inventoryValue() >= maxLoot)
                || (Combat.isInWild() && Inventory.count(x -> food.contains(x.getId())) < 4
                && GroundItems.closest(LootSpindel.lootFilter) == null));
        setSimpleName("Leave Vet'ion");
    }

    Area ESCAPE_CAVE = new Area(3325, 10301, 3392, 10242);

    @Override
    public int onLoop() {
        log("Leave Vetion");
        if (!Combat.isInWild()) {
            Logger.info("Bank all");
            new BankAllInventoryEvent().execute();
            return ReactionGenerator.getNormal();
        }

        UtilProvider.staminaUp();

        Item antiV = ItemVariants.ANTI_DOTE_PP.getItem();
        if (antiV != null && (Combat.isEnvenomed() || Combat.isPoisoned())) {
            if (Inventory.isItemSelected()) Inventory.deselect();
            antiV.interact("Drink");
        }

        Item pp = ItemVariants.STAMINA_POTION.getItem();
        if (pp != null && Walking.getRunEnergy() < 20) {
            if (Inventory.isItemSelected()) Inventory.deselect();
            pp.interact("Drink");
        }

        // pray mage
        if (ESCAPE_CAVE.contains(Players.getLocal())) Prayers.toggle(true, Prayer.PROTECT_FROM_MAGIC);

        if (Walking.shouldWalk()) Walking.walk(BankLocation.EDGEVILLE);

        return ReactionGenerator.getQuick();
    }


    public static List<Integer> ignoredIds = Arrays.asList(
            ItemID.VIGGORAS_CHAINMACE,
            ItemID.URSINE_CHAINMACE,
            ItemID.WEBWEAVER_BOW,
            ItemID.CRAWS_BOW,
            ItemID.BLIGHTED_MANTA_RAY,
            ItemID.BLIGHTED_KARAMBWAN,
            ItemID.SARACHNIS_CUDGEL,
            ItemID.SARADOMIN_SWORD
    );

    public static int inventoryValue() {
        return Inventory.all()
                .stream()
                .mapToInt(x -> {
                    if (x == null) return 0;
                    if (ignoredIds.contains(x.getId())) return 0;
                    return (x.getLivePrice() + 1) * x.getAmount();
                })
                .sum() + LootingBag.value()
                ;
    }
}
