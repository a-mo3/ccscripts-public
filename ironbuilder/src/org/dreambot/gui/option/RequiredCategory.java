package org.dreambot.gui.option;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * this annotation is used in data classes that represent a field that should only be shown when
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredCategory {
    String[] value();
}
