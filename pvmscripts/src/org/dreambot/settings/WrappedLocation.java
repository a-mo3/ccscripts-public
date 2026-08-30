package org.dreambot.settings;

import lombok.Getter;
import org.dreambot.api.methods.world.Location;

/**
 * Wrap world location enum from dreambot because US_WEST and US_EAST worlds dont actually exist
 * so when used in settings it usually causes npes
 */
public enum WrappedLocation {
    USA(Location.USA),
    USA_EAST(Location.USA),
    USA_WEST(Location.USA),
    UK(Location.UK),
    AUSTRALIA(Location.AUSTRALIA),
    GERMANY(Location.GERMANY),
    ANY(null)
    ;

    private final Location realLocation;

    public boolean isRegion(Location l) {
        if (this == ANY) return true;
        return l == realLocation;
    }

    WrappedLocation(Location realLocation) {
        this.realLocation = realLocation;
    }
}
