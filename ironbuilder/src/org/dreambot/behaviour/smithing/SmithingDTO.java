package org.dreambot.behaviour.smithing;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.IronFractal;
import org.dreambot.generics.GenericEntityInteraction;
import org.dreambot.gui.FractalDTO;
import org.dreambot.gui.option.RequiredCategory;
import org.dreambot.gui.option.UIOptionCategory;
import org.dreambot.loadouts.InventoryLoadout;
import org.dreambot.loadouts.InventoryLoadoutItem;
import org.dreambot.loadouts.data.ItemID;
import org.dreambot.loadouts.data.ItemSpawn;

@Setter
@Getter
@Accessors(chain = true)
public class SmithingDTO extends FractalDTO {
    @SerializedName("mode")
    @UIOptionCategory
    public SmithingMode mode = SmithingMode.BARS;

    @SerializedName("bar")
    @RequiredCategory("BARS")
    SmithingBar bar = SmithingBar.BRONZE;

    @SerializedName("item")
    @RequiredCategory({"ITEM", "ORE_TO_ITEM"})
    SmithingItem item = SmithingItem.BRONZE_AXE;

    @SerializedName("furnaceLocations")
    @RequiredCategory("BARS") // for ore to item we always do at lum furnace
    FurnaceLocation[] allowedFuranceLocs = FurnaceLocation.values();

    @SerializedName("targetLevel")
    public int target;

    @Override
    public FractalDTO getInstance() {
        return null;
    }

    @Override
    public IronFractal toFractal() {
        FurnaceLocation loc = allowedFuranceLocs[Calculations.random(allowedFuranceLocs.length)];
        switch (mode) {
            case BARS:
                new GenericEntityInteraction(() -> true, () -> GameObjects.closest(loc.furnaceFilter))
                        .setEntityLocation(loc.area)
                        .setInventoryLoadout(bar.loadout)
                        .setSimpleName(name());
            case ITEM:
                new GenericEntityInteraction(() -> true, () -> GameObjects.closest("Anvil"))
                        .setProcessingItem(item.itemId)
                        // todo sleep cond probably
                        .addInventoryItem(new InventoryLoadoutItem(ItemID.HAMMER)
                                .setRestockMethod(new GenericEntityInteraction(ItemSpawn.FALADOR_HAMMER)))
                        .addInventoryItem(new InventoryLoadoutItem(item.bar.barId)
                                .setInventoryMin(item.barCount).setInventoryMax(27))
                        .setSimpleName(name());
            case ORE_TO_ITEM:
            default:
                Logger.info("Invalid smithing mode " + mode);
                return null;
        }
    }

    @Override
    public String name() {
        return "Smith " + mode + " till " + target;
    }
}
