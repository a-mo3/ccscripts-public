package com.ccscripts.actions.impl.hard;

import com.ccscripts.actions.AbstractAction;
import com.ccscripts.actions.ActionType;
import com.ccscripts.model.MenuRowWrapper;
import lombok.Getter;
import org.dreambot.api.wrappers.widgets.MenuRow;

/**
 * we can use the same for npc and objects
 * opcodes
 * 9 - talk to
 * 10 - attack
 * rest is like + 1 for each menu row
 * take is 20 for ground item
 */
public class EntityInteraction extends AbstractAction {
    // todo serialize this better, db obf will fuck this up
    @Getter
    final MenuRowWrapper row;

    public EntityInteraction(MenuRow row) {
        super(ActionType.ENTITY_INTERACTION);
        this.row = new MenuRowWrapper(row);
    }
}
