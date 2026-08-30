package org.dreambot.gui.option;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * should always be on an enum, this represents a combobox that other fields depend on being set to their value
 * to be shown
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface UIOptionCategory {
}
