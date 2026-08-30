package org.dreambot.behaviour.method.mta.enchant;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.magic.Spell;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.AnimationListener;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.method.mta.MTANodes;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.function.Supplier;

public class EnchantRoomMTA extends Fractal implements AnimationListener {
    public EnchantRoomMTA(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        MTANodes.init();
        setSimpleName("Enchant room");
        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.WEAPON, ItemID.MUD_BATTLESTAFF)
                .setEnabledCondition(() -> Skill.MAGIC.getLevel() < 87)
                .addItem(EquipmentSlot.WEAPON, ItemID.LAVA_BATTLESTAFF)
                .setEnabledCondition(() -> Skill.MAGIC.getLevel() >= 87)
                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
        ;
        Client.getInstance().addEventListener(this);

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.COSMIC_RUNE, 1, 1600);

        this.paintArraySupplier = () -> new String[]{
                "Bonus on " + currentBonus,
                ""
        };
    }

    EnchantShape currentBonus = null;
    public static final Area ENCHANT_ARENA = new Area(3340, 9662, 3386, 9616);
    // where you pick up the shapes
    Area SHAPES_CORNER = new Area(3343, 9660, 3355, 9647);
    // where you deposit, "Hole"
    Area DEPOSIT_SPOT = new Area(3360, 9643, 3366, 9637);
    final int ENCHANT_ANI = 931;
    // last gametick you animated enchant on
    int lastAnimatedOn;

    @Override
    public int onLoop() {
        if (!ENCHANT_ARENA.contains(Players.getLocal())) {
            log("Go to enchant room");
            if (Dialogues.inDialogue()) {
                log("Handle dialogue");
                Dialog.solve("");
                return ReactionGenerator.getNormal();
            }
            if (Walking.shouldWalk()) Walking.walk(ENCHANT_ARENA);
            return ReactionGenerator.getNormal();
        }

        if (Inventory.isFull() && Inventory.contains("Orb")) {
            if (!DEPOSIT_SPOT.contains(Players.getLocal())) {
                log("Go to deposit hole");
                if (Walking.shouldWalk()) Walking.walk(DEPOSIT_SPOT);
                return ReactionGenerator.getNormal();
            }

            GameObject depositHole = GameObjects.closest("Hole");
            if (depositHole != null) {
                log("Deposit orbs");
                depositHole.interact("Deposit");
                Sleep.sleepUntil(() -> !Inventory.contains("Orb"), 3400);
            } else {
                log("Failed to find deposit hole");
            }
            return ReactionGenerator.getNormal();
        }

        currentBonus = Arrays.stream(EnchantShape.values())
                .filter(EnchantShape::isBonus)
                .findFirst()
                .orElse(null);

        if (!SHAPES_CORNER.contains(Players.getLocal())) {
            log("Go to corner");
            if (Walking.shouldWalk()) Walking.walk(SHAPES_CORNER);
            return ReactionGenerator.getNormal();
        }

        if (currentBonus == null) {
            log("Cant find current bonus");
            return ReactionGenerator.getNormal();
        }

        Item alchable = Inventory.get(x -> x.getId() == ItemID.MTA_DRAGONSTONE || x.getId() == currentBonus.itemID);
        if (Client.getGameTick() - lastAnimatedOn > 2 && alchable != null) {
            Magic.castSpellOn(enchantSpell(), alchable);
            return ReactionGenerator.getNormal();
        }

        GameObject pile = currentBonus.getNearest();
        if (pile == null) {
            log("Can't find a pile");
            return ReactionGenerator.getNormal();
        }

        pile.interact();
        return ReactionGenerator.getNormal();
    }

    private Spell enchantSpell() {
        int lvl = Skill.MAGIC.getLevel();
        if (lvl >= 87) return Normal.LEVEL_6_ENCHANT;
        if (lvl >= 68) return Normal.LEVEL_5_ENCHANT;
        return Normal.LEVEL_4_ENCHANT;
    }

    @Override
    public void onPlayerAnimation(Player player, int animation, int animationDelay) {
        if (player == null || !player.equals(Players.getLocal())) return;
        if (animation != ENCHANT_ANI) return;

    }
}
