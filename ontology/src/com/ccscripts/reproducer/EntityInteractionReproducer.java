package com.ccscripts.reproducer;

import com.ccscripts.actions.AbstractAction;
import com.ccscripts.actions.impl.hard.EntityInteraction;
import com.ccscripts.model.MenuRowWrapper;
import lombok.Setter;
import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;

/**
 * the semantics of this are not really accurate because this is a menu action not an interaction with an *entity*
 * <p>
 * 57 widget interaction
 * 1006 minimap
 * 23 walk
 */
public class EntityInteractionReproducer extends AbstractActionReproducer {
    private final EntityInteraction entityInteraction;
    final MenuRowWrapper menuRowWrapper;

    // if this is true, the entity must be on the same tile it was on the action
    @Setter
    boolean mustMatchTile = false;

    @Setter
    boolean dropsMatchSlot = true;

    public EntityInteractionReproducer(EntityInteraction entityInteraction, int cont) {
        super("Entity " + entityInteraction.getRow());
        this.entityInteraction = entityInteraction;
        this.menuRowWrapper = entityInteraction.getRow();
    }

    @Override
    public EntityInteraction getAction() {
        return entityInteraction;
    }

    @Override
    public void execute() {
        String menuObj = menuRowWrapper.getObject();
        String action = menuRowWrapper.getAction();

        log("Object interaction " + menuRowWrapper.getObject() + " " + menuRowWrapper.getAction() + " Op " + menuRowWrapper.getOpcode());
        if (menuRowWrapper.getOpcode() == 57) {

            if ("Drop".equals(action)) {
                Inventory.drop(x ->
                        x.getId() == menuRowWrapper.getItemCode()
                                && (!dropsMatchSlot || (x.getSlot() == menuRowWrapper.getX() && x.getId() == menuRowWrapper.getItemCode())));
                return;
            }

            if ("Withdraw-All".equals(menuRowWrapper.getAction())) {
                log("Bank withdraw all " + menuRowWrapper.getObject());
                Bank.withdrawAll(menuRowWrapper.getObject());
                return;
            }

            log("Widget interaction " + menuRowWrapper);
            if (menuObj == null || menuObj.isEmpty()) {
                WidgetChild w = Widgets.get(x -> x.hasAction(menuRowWrapper.getAction()));
                log("Action only " + w);
                if (w != null) w.interact();
                return;
            }

            WidgetChild w = Widgets.get(
                    x -> x.hasAction(menuRowWrapper.getAction())
                            && x.getName().contains(menuRowWrapper.getObject())
            );

            if (w != null) {
                log("Successfully found widget " + w);
                w.interact();
            } else {
                log("Widget interaction failed to find appropriate");
            }
            return;
        }

        if (menuRowWrapper.getOpcode() < 9) {
            // move to where the cursor was
            double dist = entityInteraction.getMousePoint().distance(Mouse.getPosition());
            log("dist " + dist);
            if (dist > 10) Mouse.move(entityInteraction.getMousePoint().getPoint());

            // object interaction, i think this is always true but im unsure
            log(menuRowWrapper + "");
            GameObject obj = GameObjects.closest(x -> x.getName().equals(menuObj)
                    && (!mustMatchTile || (x.getLocalX() == menuRowWrapper.getX() && x.getLocalY() == menuRowWrapper.getY()))
                    && x.hasAction(action));
            if (obj == null) {
                log("Failed to find appropriate obj " + menuRowWrapper);
                return;
            }
            log("Interaction " + obj.getLocalX() + " " + obj.getLocalY() + " " + mustMatchTile);
            obj.interact();
        }
    }
}
