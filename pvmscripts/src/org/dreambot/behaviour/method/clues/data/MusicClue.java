/*
 * Copyright (c) 2019, Hydrox6 <ikada@protonmail.ch>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dreambot.behaviour.method.clues.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.behaviour.method.clues.ClueScroll;
import org.dreambot.behaviour.method.clues.ClueScrollType;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MusicClue extends ClueScroll {
    private static final Tile LOCATION = new Tile(2990, 3384, 0);
    private static final String CECILIA = "Cecilia";
    private static final Pattern SONG_PATTERN = Pattern.compile("<col=ffffff>([A-Za-z !&',.]+)</col>");

    private final String song;

    // this whole class is likely not needed because you just go and talk to cecillia

    public String[] getNpcs() {
        return new String[]{CECILIA};
    }

    public static MusicClue forText(String text) {
        final Matcher m = SONG_PATTERN.matcher(text);
        if (m.find()) {
            final String song = m.group(1);
            return new MusicClue(song);
        }
        return null;
    }

    public Tile getLocation() {
        return LOCATION;
    }

    @Override
    public int solve() {

        // walk to cecila
        if (getLocation().distance() > 5) {
            if (Walking.shouldWalk()) {
                Logger.info("Go to cecila");
                Walking.walk(getLocation());
            }
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue()) {
            Logger.info("Handle dialogue");
            Dialog.solve("");
            return ReactionGenerator.getNormal();
        }

        if (!playSong()) return ReactionGenerator.getNormal();

        // talk to cecila
        NPC cecila = NPCs.closest(CECILIA);
        if (cecila == null) {
            Logger.info("Failed to find cecila");
            return ReactionGenerator.getNormal();
        }

        cecila.interact();
        Sleep.sleepUntil(Dialogues::inDialogue, 2400);

        return ReactionGenerator.getNormal();
    }

    @Override
    public ClueScrollType getType() {
        return ClueScrollType.MUSIC;
    }

    private boolean playSong() {
        if (!Tabs.isOpen(Tab.MUSIC)) {
            Logger.info("Play song - open tab");
            Tabs.open(Tab.MUSIC);
            return false;
        }

        WidgetChild songButton = Widgets.get(x -> x.hasAction("Play") && x.getName().contains(song));
        if (songButton == null) {
            Logger.info("Failed to find song: " + song);
            return false;
        }

        // todo no menu manip scroll handle
        songButton.interact("Play");
        return true;
    }
}
