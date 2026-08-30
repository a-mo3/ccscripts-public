package com.ccscripts.model;

import lombok.Getter;
import lombok.ToString;
import org.dreambot.api.wrappers.widgets.MenuRow;

/**
 * wraps event row to get around dreambot obf
 */
@Getter
@ToString
public class MenuRowWrapper {
    final int index;
    final String action;
    final String object;
    final int opcode;
    final int id;
    final int x;
    final int y;
    final int itemCode;
    final int targetRegionIndex;
    final boolean shift;

    public MenuRowWrapper(MenuRow menuRow) {
        this.index = menuRow.getIndex();
        this.action = menuRow.getAction();
        this.object = menuRow.getObject();
        this.opcode = menuRow.getOpCode();
        this.id = menuRow.getId();
        this.x = menuRow.getXCode();
        this.y = menuRow.getYCode();
        this.itemCode = menuRow.getItemIdCode();
        this.targetRegionIndex = menuRow.getTargetRegionIndex();
        this.shift = menuRow.isShift();
    }
}
