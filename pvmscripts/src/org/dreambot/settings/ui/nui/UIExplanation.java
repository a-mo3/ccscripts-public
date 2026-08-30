package org.dreambot.settings.ui.nui;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface UIExplanation {
    String value();
}
