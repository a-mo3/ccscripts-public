/*
 * Copyright (c) 2018, Lotto <https://github.com/devLotto>
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

import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.Getter;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.behaviour.method.clues.ClueScroll;
import org.dreambot.behaviour.method.clues.ClueScrollType;

import java.util.List;
import java.util.function.Supplier;


@Getter
public class CipherClue extends ClueScroll {
    static final List<CipherClue> CLUES = ImmutableList.of(
            CipherClue.builder()
                    .text("BMJ UIF LFCBC TFMMFS")
                    .npc(11875) // base npc for Ali or Isma'il the Kebab seller
                    .location(new Tile(3354, 2974, 0))
                    .area("Pollnivneach")
                    .question("How many coins would you need to purchase 133 kebabs from me?")
                    .answer("399")
                    .build(),
            CipherClue.builder()
                    .text("GUHCHO")
                    .npc(9636)
                    .location(new Tile(3440, 9895, 0))
                    .area("Paterdomus")
                    .question("Please solve this for x: 7x - 28=21")
                    .answer("7")
                    .build(),
            CipherClue.builder()
                    .text("HQNM LZM STSNQ")
                    .npc(311)
                    .location(new Tile(3227, 3227, 0))
                    .area("Outside Lumbridge castle")
                    .question("How many snakeskins are needed in order to craft 44 boots, 29 vambraces and 34 bandanas?")
                    .answer("666")
                    .build(),
            CipherClue.builder()
                    .text("ZHLUG ROG PDQ")
                    .npc(954)
                    .location(new Tile(3224, 3112, 0))
                    .area("Kalphite Lair entrance. Fairy ring BIQ")
                    .question("SIX LEGS! All of them have 6! There are 25 of them! How many legs?")
                    .answer("150")
                    .build(),
            CipherClue.builder()
                    .text("ECRVCKP MJCNGF")
                    .npc(6971)
                    .location(new Tile(1845, 3754, 0))
                    .area("Large eastern building in Port Piscarilius")
                    .question("How many fishing cranes can you find around here?")
                    .answer("5")
                    .build(),
//            CipherClue.builder()
//                    .text("OVEXON")
//                    .npc(5304)
//                    .locationProvider((plugin) -> isElunedInPrifddinas(plugin) ? new Tile(3229, 6062, 0) : new Tile(2289, 3144, 0))
//                    .areaProvider((plugin) -> isElunedInPrifddinas(plugin) ? "Prifddinas" : "Outside Lletya")
//                    .question("A question on elven crystal math. I have 5 and 3 crystals, large and small respectively. A large crystal is worth 10,000 coins and a small is worth but 1,000. How much are all my crystals worth?")
//                    .answer("53,000")
//                    .build(),
            CipherClue.builder()
                    .text("VTYR APCNTGLW")
                    .npc(4058)
                    .location(new Tile(2634, 4682, 1))
                    .area("Fisher Realm, first floor. Fairy ring BJR")
                    .question("How many cannons are on this here castle?")
                    .answer("5")
                    .build(),
            CipherClue.builder()
                    .text("UZZU MUJHRKYYKJ")
                    .npc(2914)
                    .location(new Tile(2501, 3487, 0))
                    .area("Otto's Grotto")
                    .question("How many pyre sites are found around this lake?")
                    .answer("3")
                    .build(),
            CipherClue.builder()
                    .text("XJABSE USBJCPSO")
                    .npc(5081)
                    .location(new Tile(3112, 3162, 0))
                    .area("First floor of Wizards Tower. Fairy ring DIS")
                    .question("How many air runes would I need to cast 630 wind waves?")
                    .answer("3150")
                    .build(),
            CipherClue.builder()
                    .text("HCKTA IQFHCVJGT")
                    .npc(1840)
                    .location(new Tile(2446, 4428, 0))
                    .area("Zanaris throne room")
                    .question("There are 3 inputs and 4 letters on each ring How many total individual fairy ring codes are possible?")
                    .answer("64")
                    .build(),
            CipherClue.builder()
                    .text("ZSBKDO ZODO")
                    .npc(601)
                    .location(new Tile(3680, 3537, 0))
                    .area("Dock northeast of the Ectofuntus")
                    .build(),
            CipherClue.builder()
                    .text("GBJSZ RVFFO")
                    .npc(1161)
                    .location(new Tile(2347, 4435, 0))
                    .area("Fairy Resistance Hideout")
                    .build(),
            CipherClue.builder()
                    .text("QSPGFTTPS HSBDLMFCPOF")
                    .npc(7048)
                    .location(new Tile(1625, 3802, 0))
                    .area("Ground floor of Arceuus Library")
                    .question("How many round tables can be found on this floor of the library?")
                    .answer("9")
                    .build(),
            CipherClue.builder()
                    .text("IWPPLQTP")
                    .npc(2153)
                    .location(new Tile(2541, 3548, 0))
                    .area("Barbarian Outpost Agility course")
                    .build(),
            CipherClue.builder()
                    .text("BSOPME MZETQPS")
                    .npc(4293)
                    .location(new Tile(2329, 3689, 0))
                    .area("Piscatoris Fishing Colony general store/bank")
                    .build(),
            CipherClue.builder()
                    .text("ESBZOPS QJH QFO")
                    .location(new Tile(3077, 3260, 0))
                    .area("Inside of Martin the Master Gardener's pig pen in Draynor Village.")
                    .build(),
            CipherClue.builder()
                    .text("BXJA UNJMNA YRCAR")
                    .npc(13135)
                    .location(new Tile(1559, 3045, 0))
                    .area("Top of the Hunter Guild")
                    .build()
    );

    private final String text;
    private final int npc;
    private final Supplier<Tile> locationProvider;
    private final Supplier<String> areaProvider;
    private final String question;
    private final String answer;

    @Builder
    private CipherClue(
            String text,
            Integer npc,
            Tile location,
            Supplier<Tile> locationProvider,
            String area,
            Supplier<String> areaProvider,
            String question,
            String answer
    ) {
        this.text = "The cipher reveals who to speak to next: " + text;
        this.npc = npc != null ? npc : -1;
        this.locationProvider = locationProvider != null ? locationProvider : () -> location;
        this.areaProvider = areaProvider != null ? areaProvider : () -> area;
        this.question = question;
        this.answer = answer;
    }

    @Override
    public int solve() {
        return 0;
    }

    @Override
    public ClueScrollType getType() {
        return ClueScrollType.CIPHER;
    }
}
