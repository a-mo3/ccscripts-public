package org.dreambot.behaviour.friends;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.analytics.AnalyticsReporter;
import org.dreambot.api.methods.friend.Friends;
import org.dreambot.api.methods.friend.NameProvider;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.List;
import java.util.function.Supplier;

@Setter
@Accessors(chain = true)
public class ConfigureFriendsList extends Fractal {
    // check friends list is equal to this, unfriend or friend as required
    final Supplier<List<String>> desiredFriendsSupplier;

    public ConfigureFriendsList(Supplier<List<String>> desiredFriends) {
        this.desiredFriendsSupplier = desiredFriends;
    }

    @Override
    public boolean isValid() {
        List<String> desiredFriends = desiredFriendsSupplier.get();
        if (desiredFriends == null) return false;
        boolean hasAllFriends = desiredFriends
                .stream()
                .allMatch(Friends::haveFriend);
//        boolean needsToUnfriend = Friends.all().stream().anyMatch(x -> !desiredFriends.contains(x.getName()));
        return !hasAllFriends;
    }

    @Override
    public int onLoop() {
        if (Widgets.isOpen()) {
            log("Close all widgets");
            Widgets.closeAll();
        }

        List<String> desiredFriends = desiredFriendsSupplier.get();
        if (desiredFriends == null) {
            log("NUll friend list");
            return ReactionGenerator.getNormal();
        }
//        String needsToRemove = Friends.all()
//                .stream()
//                .map(NameProvider::getName)
//                .filter(name -> !desiredFriends.contains(name))
//                .findFirst()
//                .orElse(null);
//        if (needsToRemove != null) {
//            log("Removing friend " + AnalyticsReporter.hashStringSHA256(needsToRemove));
//            if (Widgets.isOpen()) Widgets.closeAll();
//            Friends.deleteFriend(needsToRemove);
//            return ReactionGenerator.getNormal();
//        }

        String needsToAdd = desiredFriends.stream()
                .filter(x -> !Friends.haveFriend(x))
                .findFirst().orElse(null);
        if (needsToAdd != null) {
            log("Adding friend " + AnalyticsReporter.hashStringSHA256(needsToAdd));
            Friends.addFriend(needsToAdd);
        }
        return ReactionGenerator.getNormal();
    }
}
