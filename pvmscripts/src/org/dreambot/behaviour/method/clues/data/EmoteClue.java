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
import lombok.Getter;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.emotes.Emote;
import org.dreambot.api.methods.emotes.Emotes;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.method.clues.ClueScroll;
import org.dreambot.behaviour.method.clues.ClueScrollType;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.settings.timing.ReactionGenerator;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

import static org.dreambot.api.methods.emotes.Emote.*;
import static org.dreambot.behaviour.method.clues.data.STASHUnit.*;


@Getter
public class EmoteClue extends ClueScroll {
//    private static final AnyRequirementCollection ANY_SLAYER_HELMET = any("Any slayer helmet",
//            ItemVariationMapping.getVariations(SLAYER_HELMET).stream()
//                    .map(ItemRequirements::item)
//                    .toArray(SingleItemRequirement[]::new));
//    private static final AnyRequirementCollection ANY_RING_OF_WEALTH = any("Any ring of wealth",
//            ItemVariationMapping.getVariations(RING_OF_WEALTH).stream()
//                    .map(ItemRequirements::item)
//                    .toArray(SingleItemRequirement[]::new));
//    private static final AnyRequirementCollection ANY_PHARAOHS_SCEPTRE = any("Pharaoh's sceptre",
//            ItemVariationMapping.getVariations(PHARAOHS_SCEPTRE).stream()
//                    .map(ItemRequirements::item)
//                    .toArray(SingleItemRequirement[]::new));
//    private static final AnyRequirementCollection ANY_TEAM_CAPE = any("Any team cape",
//            Stream.of(
//                            ItemVariationMapping.getVariations(TEAM1_CAPE).stream(),
//                            Stream.of(TEAM_CAPE_I, TEAM_CAPE_X, TEAM_CAPE_ZERO))
//                    .reduce(Stream::concat)
//                    .orElseGet(Stream::empty)
//                    .map(ItemRequirements::item)
//                    .toArray(SingleItemRequirement[]::new));
//    static final AnyRequirementCollection ACTIVE_CRYSTAL_BOW_OR_BOW_OF_FAERDHINEN = any("Crystal Bow or Bow of Faerdhinen",
//            Stream.of(
//                            ItemVariationMapping.getVariations(BOW_OF_FAERDHINEN_INACTIVE).stream(),
//                            Stream.of(CRYSTAL_BOW, CRYSTAL_BOW_24123))
//                    .reduce(Stream::concat)
//                    .orElseGet(Stream::empty)
//                    .filter(itemId -> itemId != BOW_OF_FAERDHINEN_INACTIVE)
//                    .map(ItemRequirements::item)
//                    .toArray(SingleItemRequirement[]::new));

    static final List<EmoteClue> CLUES = ImmutableList.of(
//            new EmoteClue("Beckon on the east coast of the Kharazi Jungle. Beware of double agents! Equip any vestment stole and a heraldic rune shield.", "Kharazi Jungle",
//                    NORTHEAST_CORNER_OF_THE_KHARAZI_JUNGLE,
//                    new Tile(2954, 2933, 0),
//                    DOUBLE_AGENT_108, BECKON,
//                    any("Any stole", new EquipmentLoadoutItem(GUTHIX_STOLE), new EquipmentLoadoutItem(SARADOMIN_STOLE), new EquipmentLoadoutItem(ZAMORAK_STOLE), new EquipmentLoadoutItem(ARMADYL_STOLE), new EquipmentLoadoutItem(BANDOS_STOLE), new EquipmentLoadoutItem(ANCIENT_STOLE)),
//                    any("Any heraldic rune shield", new EquipmentLoadoutItem(RUNE_SHIELD_H1), new EquipmentLoadoutItem(RUNE_SHIELD_H2), new EquipmentLoadoutItem(RUNE_SHIELD_H3), new EquipmentLoadoutItem(RUNE_SHIELD_H4), new EquipmentLoadoutItem(RUNE_SHIELD_H5))),
            new EmoteClue("Cheer in the Barbarian Agility Arena. Headbang before you talk to me. Equip a steel platebody, maple shortbow and a Wilderness cape.",
                    "Barbarian Outpost",
                    BARBARIAN_OUTPOST_OBSTACLE_COURSE,
                    new Tile(2552, 3556, 0),
                    CHEER, HEADBANG,
                    new EquipmentLoadout()
                            .addItem(EquipmentSlot.WEAPON, ItemID.MAPLE_SHORTBOW)
                            .addItem(EquipmentSlot.CAPE, ItemID.TEAM1_CAPE)
                            .addItem(EquipmentSlot.CHEST, ItemID.STEEL_PLATEBODY)),
//            new EmoteClue("Bow upstairs in the Edgeville Monastery. Equip a completed prayer book.", "Edgeville Monastery",
//                    SOUTHEAST_CORNER_OF_THE_MONASTERY,
//                    new Tile(3056, 3484, 1),
//                    BOW,
//                    any("Any god book", new EquipmentLoadoutItem(HOLY_BOOK), new EquipmentLoadoutItem(BOOK_OF_BALANCE), new EquipmentLoadoutItem(UNHOLY_BOOK), new EquipmentLoadoutItem(BOOK_OF_LAW), new EquipmentLoadoutItem(BOOK_OF_WAR), new EquipmentLoadoutItem(BOOK_OF_DARKNESS), new EquipmentLoadoutItem(HOLY_BOOK_OR), new EquipmentLoadoutItem(BOOK_OF_BALANCE_OR), new EquipmentLoadoutItem(UNHOLY_BOOK_OR), new EquipmentLoadoutItem(BOOK_OF_LAW_OR), new EquipmentLoadoutItem(BOOK_OF_WAR_OR), new EquipmentLoadoutItem(BOOK_OF_DARKNESS_OR))),
//            new EmoteClue("Cheer in the Shadow dungeon. Equip a rune crossbow, climbing boots and any mitre.", "Shadow dungeon", ENTRANCE_OF_THE_CAVE_OF_DAMIS, new Tile(2629, 5071, 0), CHEER, any("Any mitre", new EquipmentLoadoutItem(GUTHIX_MITRE), new EquipmentLoadoutItem(SARADOMIN_MITRE), new EquipmentLoadoutItem(ZAMORAK_MITRE), new EquipmentLoadoutItem(ANCIENT_MITRE), new EquipmentLoadoutItem(BANDOS_MITRE), new EquipmentLoadoutItem(ARMADYL_MITRE)), any("Rune crossbow", new EquipmentLoadoutItem(RUNE_CROSSBOW), new EquipmentLoadoutItem(RUNE_CROSSBOW_OR)), any("Climbing boots", new EquipmentLoadoutItem(CLIMBING_BOOTS), new EquipmentLoadoutItem(CLIMBING_BOOTS_G)), any("Ring of visibility or ring of shadows", new EquipmentLoadoutItem(RING_OF_VISIBILITY), new EquipmentLoadoutItem(RING_OF_SHADOWS), new EquipmentLoadoutItem(RING_OF_SHADOWS_UNCHARGED))),
//            new EmoteClue("Cheer at the top of the agility pyramid. Beware of double agents! Equip a blue mystic robe top and any rune heraldic shield.", "Agility Pyramid", AGILITY_PYRAMID, new Tile(3043, 4697, 3), DOUBLE_AGENT_108, CHEER, new EquipmentLoadoutItem(MYSTIC_ROBE_TOP), any("Any rune heraldic shield", new EquipmentLoadoutItem(RUNE_SHIELD_H1), new EquipmentLoadoutItem(RUNE_SHIELD_H2), new EquipmentLoadoutItem(RUNE_SHIELD_H3), new EquipmentLoadoutItem(RUNE_SHIELD_H4), new EquipmentLoadoutItem(RUNE_SHIELD_H5))),
//            new EmoteClue("Dance in Iban's temple. Beware of double agents! Equip Iban's staff, a black mystic top and a black mystic bottom.", "Iban's temple", WELL_OF_VOYAGE, new Tile(2011, 4712, 0), DOUBLE_AGENT_141, DANCE, any("Any iban's staff", new EquipmentLoadoutItem(IBANS_STAFF), new EquipmentLoadoutItem(IBANS_STAFF_U)), new EquipmentLoadoutItem(MYSTIC_ROBE_TOP_DARK), new EquipmentLoadoutItem(MYSTIC_ROBE_BOTTOM_DARK)),
//            new EmoteClue("Dance on the Fishing Platform. Equip barrows gloves, an amulet of glory and a dragon med helm.", "Fishing Platform", SOUTHEAST_CORNER_OF_THE_FISHING_PLATFORM, new Tile(2782, 3273, 0), DANCE, any("Any amulet of glory", new EquipmentLoadoutItem(AMULET_OF_GLORY), new EquipmentLoadoutItem(AMULET_OF_GLORY1), new EquipmentLoadoutItem(AMULET_OF_GLORY2), new EquipmentLoadoutItem(AMULET_OF_GLORY3), new EquipmentLoadoutItem(AMULET_OF_GLORY4), new EquipmentLoadoutItem(AMULET_OF_GLORY5), new EquipmentLoadoutItem(AMULET_OF_GLORY6)), new EquipmentLoadoutItem(BARROWS_GLOVES), new EquipmentLoadoutItem(DRAGON_MED_HELM)),
//            new EmoteClue("Flap at the death altar. Beware of double agents! Equip a death tiara, a legend's cape and any ring of wealth.", "Death altar", DEATH_ALTAR, new Tile(2205, 4838, 0), DOUBLE_AGENT_141, FLAP, ANY_RING_OF_WEALTH, new EquipmentLoadoutItem(DEATH_TIARA), new EquipmentLoadoutItem(CAPE_OF_LEGENDS)),
//            new EmoteClue("Headbang in the Fight Arena pub. Equip a pirate bandana, a dragonstone necklace and and a magic longbow.", "Fight Arena pub", OUTSIDE_THE_BAR_BY_THE_FIGHT_ARENA, new Tile(2568, 3149, 0), HEADBANG, any("Any pirate bandana", new EquipmentLoadoutItem(PIRATE_BANDANA), new EquipmentLoadoutItem(PIRATE_BANDANA_7124), new EquipmentLoadoutItem(PIRATE_BANDANA_7130), new EquipmentLoadoutItem(PIRATE_BANDANA_7136)), new EquipmentLoadoutItem(DRAGON_NECKLACE), new EquipmentLoadoutItem(MAGIC_LONGBOW)),
//            new EmoteClue("Do a jig at the barrows chest. Beware of double agents! Equip any full barrows set.", "Barrows chest", BARROWS_CHEST, new Tile(3551, 9694, 0), DOUBLE_AGENT_141, JIG, any("Any full barrows set",
//                    all(any("Ahrim's hood", new EquipmentLoadoutItem(AHRIMS_HOOD), range(AHRIMS_HOOD_100, AHRIMS_HOOD_0), new EquipmentLoadoutItem(ECHO_AHRIMS_HOOD), new EquipmentLoadoutItem(ECHO_AHRIMS_HOOD_100), new EquipmentLoadoutItem(ECHO_AHRIMS_HOOD_75), new EquipmentLoadoutItem(ECHO_AHRIMS_HOOD_50), new EquipmentLoadoutItem(ECHO_AHRIMS_HOOD_25), new EquipmentLoadoutItem(ECHO_AHRIMS_HOOD_0)), any("Ahrim's staff", new EquipmentLoadoutItem(AHRIMS_STAFF), range(AHRIMS_STAFF_100, AHRIMS_STAFF_0), new EquipmentLoadoutItem(ECHO_AHRIMS_STAFF), range(ECHO_AHRIMS_STAFF_100, ECHO_AHRIMS_STAFF_0)), any("Ahrim's robetop", new EquipmentLoadoutItem(AHRIMS_ROBETOP), range(AHRIMS_ROBETOP_100, AHRIMS_ROBETOP_0), new EquipmentLoadoutItem(ECHO_AHRIMS_ROBETOP), new EquipmentLoadoutItem(ECHO_AHRIMS_ROBETOP_100), new EquipmentLoadoutItem(ECHO_AHRIMS_ROBETOP_75), new EquipmentLoadoutItem(ECHO_AHRIMS_ROBETOP_50), new EquipmentLoadoutItem(ECHO_AHRIMS_ROBETOP_25), new EquipmentLoadoutItem(ECHO_AHRIMS_ROBETOP_0)), any("Ahrim's robeskirt", new EquipmentLoadoutItem(AHRIMS_ROBESKIRT), range(AHRIMS_ROBESKIRT_100, AHRIMS_ROBESKIRT_0), new EquipmentLoadoutItem(ECHO_AHRIMS_ROBESKIRT), new EquipmentLoadoutItem(ECHO_AHRIMS_ROBESKIRT_100), new EquipmentLoadoutItem(ECHO_AHRIMS_ROBESKIRT_75), new EquipmentLoadoutItem(ECHO_AHRIMS_ROBESKIRT_50), new EquipmentLoadoutItem(ECHO_AHRIMS_ROBESKIRT_25), new EquipmentLoadoutItem(ECHO_AHRIMS_ROBESKIRT_0))),
//                    all(any("Dharok's helm", new EquipmentLoadoutItem(DHAROKS_HELM), range(DHAROKS_HELM_100, DHAROKS_HELM_0)), any("Dharok's greataxe", new EquipmentLoadoutItem(DHAROKS_GREATAXE), range(DHAROKS_GREATAXE_100, DHAROKS_GREATAXE_0)), any("Dharok's platebody", new EquipmentLoadoutItem(DHAROKS_PLATEBODY), range(DHAROKS_PLATEBODY_100, DHAROKS_PLATEBODY_0)), any("Dharok's platelegs", new EquipmentLoadoutItem(DHAROKS_PLATELEGS), range(DHAROKS_PLATELEGS_100, DHAROKS_PLATELEGS_0))),
//                    all(any("Guthan's helm", new EquipmentLoadoutItem(GUTHANS_HELM), range(GUTHANS_HELM_100, GUTHANS_HELM_0)), any("Guthan's warspear", new EquipmentLoadoutItem(GUTHANS_WARSPEAR), range(GUTHANS_WARSPEAR_100, GUTHANS_WARSPEAR_0)), any("Guthan's platebody", new EquipmentLoadoutItem(GUTHANS_PLATEBODY), range(GUTHANS_PLATEBODY_100, GUTHANS_PLATEBODY_0)), any("Guthan's chainskirt", new EquipmentLoadoutItem(GUTHANS_CHAINSKIRT), range(GUTHANS_CHAINSKIRT_100, GUTHANS_CHAINSKIRT_0))),
//                    all(any("Karil's coif", new EquipmentLoadoutItem(KARILS_COIF), range(KARILS_COIF_100, KARILS_COIF_0)), any("Karil's crossbow", new EquipmentLoadoutItem(KARILS_CROSSBOW), range(KARILS_CROSSBOW_100, KARILS_CROSSBOW_0)), any("Karil's leathertop", new EquipmentLoadoutItem(KARILS_LEATHERTOP), range(KARILS_LEATHERTOP_100, KARILS_LEATHERTOP_0)), any("Karil's leatherskirt", new EquipmentLoadoutItem(KARILS_LEATHERSKIRT), range(KARILS_LEATHERSKIRT_100, KARILS_LEATHERSKIRT_0))),
//                    all(any("Torag's helm", new EquipmentLoadoutItem(TORAGS_HELM), range(TORAGS_HELM_100, TORAGS_HELM_0)), any("Torag's hammers", new EquipmentLoadoutItem(TORAGS_HAMMERS), range(TORAGS_HAMMERS_100, TORAGS_HAMMERS_0)), any("Torag's platebody", new EquipmentLoadoutItem(TORAGS_PLATEBODY), range(TORAGS_PLATEBODY_100, TORAGS_PLATEBODY_0)), any("Torag's platelegs", new EquipmentLoadoutItem(TORAGS_PLATELEGS), range(TORAGS_PLATELEGS_100, TORAGS_PLATELEGS_0))),
//                    all(any("Verac's helm", new EquipmentLoadoutItem(VERACS_HELM), range(VERACS_HELM_100, VERACS_HELM_0)), any("Verac's flail", new EquipmentLoadoutItem(VERACS_FLAIL), range(VERACS_FLAIL_100, VERACS_FLAIL_0)), any("Verac's brassard", new EquipmentLoadoutItem(VERACS_BRASSARD), range(VERACS_BRASSARD_100, VERACS_BRASSARD_0)), any("Verac's plateskirt", new EquipmentLoadoutItem(VERACS_PLATESKIRT), range(VERACS_PLATESKIRT_100, VERACS_PLATESKIRT_0))))),
//            new EmoteClue("Jig at Jiggig. Beware of double agents! Equip a Rune spear, rune platelegs and any rune heraldic helm.", "Jiggig", IN_THE_MIDDLE_OF_JIGGIG, new Tile(2477, 3047, 0), DOUBLE_AGENT_108, JIG, range("Any rune heraldic helm", RUNE_HELM_H1, RUNE_HELM_H5), new EquipmentLoadoutItem(RUNE_SPEAR), new EquipmentLoadoutItem(RUNE_PLATELEGS)),
//            new EmoteClue("Cheer at the games room. Have nothing equipped at all when you do.", "Burthorpe Games Room", null, new Tile(2207, 4952, 0), CHEER, emptySlot("Nothing at all", EquipmentInventorySlot.values())),
//            new EmoteClue("Panic on the pier where you catch the Fishing trawler. Have nothing equipped at all when you do.", "Fishing trawler", null, new Tile(2676, 3169, 0), PANIC, emptySlot("Nothing at all", EquipmentInventorySlot.values())),
//            new EmoteClue("Panic in the heart of the Haunted Woods. Beware of double agents! Have no items equipped when you do.", "Haunted Woods (ALQ)", null, new Tile(3611, 3492, 0), DOUBLE_AGENT_108, PANIC, emptySlot("Nothing at all", EquipmentInventorySlot.values())),
//            new EmoteClue("Show your anger towards the Statue of Saradomin in Ellamaria's garden. Beware of double agents! Equip a zamorak godsword.", "Varrock Castle", BY_THE_BEAR_CAGE_IN_VARROCK_PALACE_GARDENS, new Tile(3230, 3478, 0), DOUBLE_AGENT_141, ANGRY, any("Zamorak godsword", new EquipmentLoadoutItem(ZAMORAK_GODSWORD), new EquipmentLoadoutItem(ZAMORAK_GODSWORD_OR))),
//            new EmoteClue("Show your anger at the Wise old man. Beware of double agents! Equip an abyssal whip, a legend's cape and some spined chaps.", "Draynor Village", BEHIND_MISS_SCHISM_IN_DRAYNOR_VILLAGE, new Tile(3088, 3254, 0), DOUBLE_AGENT_141, ANGRY, any("Abyssal whip", new EquipmentLoadoutItem(ABYSSAL_WHIP), new EquipmentLoadoutItem(VOLCANIC_ABYSSAL_WHIP), new EquipmentLoadoutItem(FROZEN_ABYSSAL_WHIP), new EquipmentLoadoutItem(ABYSSAL_WHIP_OR), new EquipmentLoadoutItem(ABYSSAL_TENTACLE), new EquipmentLoadoutItem(ABYSSAL_TENTACLE_OR)), new EquipmentLoadoutItem(CAPE_OF_LEGENDS), new EquipmentLoadoutItem(SPINED_CHAPS)),
//            new EmoteClue("Beckon by a collection of crystalline maple trees. Beware of double agents! Equip Bryophyta's staff and a nature tiara.", "North of Prifddinas", CRYSTALLINE_MAPLE_TREES, new Tile(2211, 3427, 0), DOUBLE_AGENT_141, BECKON, range("Bryophyta's staff", BRYOPHYTAS_STAFF_UNCHARGED, BRYOPHYTAS_STAFF), new EquipmentLoadoutItem(NATURE_TIARA)),
//            new EmoteClue("Bow near Lord Iorwerth. Beware of double agents! Equip a charged crystal bow.", "Lord Iorwerth's camp", TENT_IN_LORD_IORWERTHS_ENCAMPMENT, new Tile(2205, 3252, 0), DOUBLE_AGENT_141, BOW, ACTIVE_CRYSTAL_BOW_OR_BOW_OF_FAERDHINEN),
//            new EmoteClue("Bow in the Iorwerth Camp. Beware of double agents! Equip a charged crystal bow.", "Lord Iorwerth's camp", TENT_IN_LORD_IORWERTHS_ENCAMPMENT, new Tile(2205, 3252, 0), DOUBLE_AGENT_141, BOW, ACTIVE_CRYSTAL_BOW_OR_BOW_OF_FAERDHINEN),
//            new EmoteClue("Bow outside the entrance to the Legends' Guild. Equip iron platelegs, an emerald amulet and an oak longbow.", "Legend's Guild", OUTSIDE_THE_LEGENDS_GUILD_GATES, new Tile(2729, 3349, 0), BOW, new EquipmentLoadoutItem(IRON_PLATELEGS), new EquipmentLoadoutItem(OAK_LONGBOW), new EquipmentLoadoutItem(EMERALD_AMULET)),
//            new EmoteClue("Bow on the ground floor of the Legends' Guild. Equip a Cape of Legends, a dragon battleaxe and an amulet of glory.", "Legend's Guild", OUTSIDE_THE_LEGENDS_GUILD_DOOR, new Tile(2728, 3377, 0), BOW, new EquipmentLoadoutItem(CAPE_OF_LEGENDS), new EquipmentLoadoutItem(DRAGON_BATTLEAXE), any("Any amulet of glory", new EquipmentLoadoutItem(AMULET_OF_GLORY), new EquipmentLoadoutItem(AMULET_OF_GLORY1), new EquipmentLoadoutItem(AMULET_OF_GLORY2), new EquipmentLoadoutItem(AMULET_OF_GLORY3), new EquipmentLoadoutItem(AMULET_OF_GLORY4), new EquipmentLoadoutItem(AMULET_OF_GLORY5), new EquipmentLoadoutItem(AMULET_OF_GLORY6))),
//            new EmoteClue("Bow in the office of the Emir's Arena. Equip an iron chain body, leather chaps and coif.", "PvP Arena", PVP_ARENA_TICKET_OFFICE, new Tile(3314, 3241, 0), BOW, new EquipmentLoadoutItem(IRON_CHAINBODY), new EquipmentLoadoutItem(LEATHER_CHAPS), new EquipmentLoadoutItem(COIF)),
//            new EmoteClue("Bow at the top of the lighthouse. Beware of double agents! Equip a blue dragonhide body, blue dragonhide vambraces and no jewelry.", "Lighthouse", TOP_FLOOR_OF_THE_LIGHTHOUSE, new Tile(2511, 3641, 2), DOUBLE_AGENT_108, BOW, new EquipmentLoadoutItem(BLUE_DHIDE_BODY), new EquipmentLoadoutItem(BLUE_DHIDE_VAMBRACES), emptySlot("No jewellery", AMULET, RING)),
//            new EmoteClue("Bow within the temple in Civitas illa Fortis. Equip any piece of sunfire fanatic armour.", "Civitas illa Fortis", TEMPLE_SOUTHEAST_OF_THE_BAZAAR, new Tile(1699, 3087, 0), BOW, any("Any piece of Sunfire Fanatic armour", new EquipmentLoadoutItem(SUNFIRE_FANATIC_HELM), new EquipmentLoadoutItem(SUNFIRE_FANATIC_CUIRASS), new EquipmentLoadoutItem(SUNFIRE_FANATIC_CHAUSSES))),
//            new EmoteClue("Blow a kiss between the tables in Shilo Village bank. Beware of double agents! Equip a blue mystic hat, bone spear and rune platebody.", "Shilo Village", SHILO_VILLAGE_BANK, new Tile(2851, 2954, 0), DOUBLE_AGENT_108, BLOW_KISS, new EquipmentLoadoutItem(MYSTIC_HAT), new EquipmentLoadoutItem(BONE_SPEAR), new EquipmentLoadoutItem(RUNE_PLATEBODY)),
//            new EmoteClue("Blow a kiss in the heart of the lava maze. Equip black dragonhide chaps, a spotted cape and a rolling pin.", "Lava maze", NEAR_A_LADDER_IN_THE_WILDERNESS_LAVA_MAZE, new Tile(3069, 3861, 0), BLOW_KISS, new EquipmentLoadoutItem(BLACK_DHIDE_CHAPS), any("Spotted cape", new EquipmentLoadoutItem(SPOTTED_CAPE), new EquipmentLoadoutItem(SPOTTED_CAPE_10073)), new EquipmentLoadoutItem(ROLLING_PIN)),
//            new EmoteClue("Blow a kiss outside K'ril Tsutsaroth's chamber. Beware of double agents! Equip a zamorak full helm and the shadow sword.", "K'ril's chamber", OUTSIDE_KRIL_TSUTSAROTHS_ROOM, new Tile(2925, 5333, 2), DOUBLE_AGENT_141, BLOW_KISS, new EquipmentLoadoutItem(ZAMORAK_FULL_HELM), new EquipmentLoadoutItem(SHADOW_SWORD)),
//            new EmoteClue("Cheer at the Druids' Circle. Equip a blue wizard hat, a bronze two-handed sword and HAM boots.", "Taverley stone circle", TAVERLEY_STONE_CIRCLE, new Tile(2924, 3478, 0), CHEER, new EquipmentLoadoutItem(BLUE_WIZARD_HAT), new EquipmentLoadoutItem(BRONZE_2H_SWORD), new EquipmentLoadoutItem(HAM_BOOTS)),
//            new EmoteClue("Cheer in the Entrana church. Beware of double agents! Equip a full set of black dragonhide armour.", "Entrana church", ENTRANA_CHAPEL, new Tile(2852, 3349, 0), DOUBLE_AGENT_141, CHEER, new EquipmentLoadoutItem(BLACK_DHIDE_VAMBRACES), new EquipmentLoadoutItem(BLACK_DHIDE_CHAPS), new EquipmentLoadoutItem(BLACK_DHIDE_BODY)),
//            new EmoteClue("Cheer for the monks at Port Sarim. Equip a coif, steel plateskirt and a sapphire necklace.", "Port Sarim", NEAR_THE_ENTRANA_FERRY_IN_PORT_SARIM, new Tile(3047, 3237, 0), CHEER, new EquipmentLoadoutItem(COIF), new EquipmentLoadoutItem(STEEL_PLATESKIRT), new EquipmentLoadoutItem(SAPPHIRE_NECKLACE)),
//            new EmoteClue("Clap in the main exam room in the Exam Centre. Equip a white apron, green gnome boots and leather gloves.", "Exam Centre", OUTSIDE_THE_DIGSITE_EXAM_CENTRE, new Tile(3361, 3339, 0), CLAP, new EquipmentLoadoutItem(WHITE_APRON), new EquipmentLoadoutItem(GREEN_BOOTS), new EquipmentLoadoutItem(LEATHER_GLOVES)),
//            new EmoteClue("Clap on the causeway to the Wizards' Tower. Equip an iron medium helmet, emerald ring and a white apron.", "Wizards' Tower", ON_THE_BRIDGE_TO_THE_MISTHALIN_WIZARDS_TOWER, new Tile(3113, 3196, 0), CLAP, new EquipmentLoadoutItem(IRON_MED_HELM), new EquipmentLoadoutItem(EMERALD_RING), new EquipmentLoadoutItem(WHITE_APRON)),
//            new EmoteClue("Clap on the top level of the mill, north of East Ardougne. Equip a blue gnome robe top, HAM robe bottom and an unenchanted tiara.", "East Ardougne", UPSTAIRS_IN_THE_ARDOUGNE_WINDMILL, new Tile(2635, 3385, 3), CLAP, new EquipmentLoadoutItem(BLUE_ROBE_TOP), new EquipmentLoadoutItem(HAM_ROBE), new EquipmentLoadoutItem(TIARA)),
//            new EmoteClue("Clap in the magic axe hut. Beware of double agents! Equip only some flared trousers.", "Magic axe hut", OUTSIDE_THE_WILDERNESS_AXE_HUT, new Tile(3191, 3960, 0), DOUBLE_AGENT_141, CLAP, new EquipmentLoadoutItem(FLARED_TROUSERS), new EquipmentLoadoutItem(LOCKPICK), emptySlot("Nothing else", HEAD, CAPE, AMULET, WEAPON, BODY, SHIELD, GLOVES, BOOTS, RING, AMMO)),
//            new EmoteClue("Cry in the TzHaar gem store. Beware of double agents! Equip a fire cape and TokTz-Xil-Ul.", "Tzhaar gem store", TZHAAR_GEM_STORE, new Tile(2463, 5149, 0), DOUBLE_AGENT_141, CRY, any("Fire cape", new EquipmentLoadoutItem(FIRE_CAPE), new EquipmentLoadoutItem(FIRE_CAPE_L), new EquipmentLoadoutItem(FIRE_MAX_CAPE), new EquipmentLoadoutItem(FIRE_MAX_CAPE_L), new EquipmentLoadoutItem(INFERNAL_CAPE), new EquipmentLoadoutItem(INFERNAL_CAPE_L), new EquipmentLoadoutItem(INFERNAL_MAX_CAPE_21285), new EquipmentLoadoutItem(INFERNAL_MAX_CAPE_L)), new EquipmentLoadoutItem(TOKTZXILUL)),

            // --- MED CLUE EMOTES ---
            new EmoteClue("Beckon in the Digsite, near the eastern winch. Bow before you talk to me. Equip a green gnome hat, snakeskin boots and an iron pickaxe.", "Digsite",
                    DIGSITE, new Tile(3370, 3425, 0),
                    BECKON, BOW,
                    new EquipmentLoadout()
                            .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
                            .addItem(EquipmentSlot.WEAPON, ItemID.IRON_PICKAXE)
                            .addItem(EquipmentSlot.HAT, ItemID.GREEN_HAT)),
            new EmoteClue("Beckon in Tai Bwo Wannai. Clap before you talk to me. Equip green dragonhide chaps, a ring of dueling and a mithril medium helmet.", "Tai Bwo Wannai",
                    SOUTH_OF_THE_SHRINE_IN_TAI_BWO_WANNAI_VILLAGE, new Tile(2803, 3073, 0), BECKON, CLAP,
                    new EquipmentLoadout()
                            .addItem(EquipmentSlot.HAT, ItemID.MITHRIL_MED_HELM)
                            .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                            .addItem(EquipmentSlot.LEGS, ItemID.GREEN_DHIDE_CHAPS)),
            new EmoteClue("Beckon in the Shayzien Combat Ring. Show your anger before you talk to me. Equip an adamant platebody, adamant full helm and adamant platelegs.",
                    "Shayzien Combat Ring", WEST_OF_THE_SHAYZIEN_COMBAT_RING, new Tile(1543, 3623, 0),
                    BECKON, ANGRY,
                    new EquipmentLoadout()
                            .addItem(EquipmentSlot.HAT, ItemID.ADAMANT_FULL_HELM)
                            .addItem(EquipmentSlot.CHEST, ItemID.ADAMANT_PLATEBODY)
                            .addItem(EquipmentSlot.LEGS, ItemID.ADAMANT_PLATELEGS)),
            new EmoteClue("Cheer in the Edgeville general store. Dance before you talk to me. Equip a brown apron, leather boots and leather gloves.", "Edgeville",
                    NORTH_OF_EVIL_DAVES_HOUSE_IN_EDGEVILLE,
                    new Tile(3080, 3509, 0), CHEER, DANCE,
                    new EquipmentLoadout()
                            .addItem(EquipmentSlot.FEET, ItemID.LEATHER_BOOTS)
                            .addItem(EquipmentSlot.HANDS, ItemID.LEATHER_GLOVES)
                            .addItem(EquipmentSlot.CHEST, ItemID.BROWN_APRON)),
            new EmoteClue("Cheer in the Ogre Pen in the Training Camp. Show you are angry before you talk to me. Equip a green dragonhide body and chaps and a steel square shield.", "King Lathas' camp",
                    OGRE_CAGE_IN_KING_LATHAS_TRAINING_CAMP, new Tile(2527, 3375, 0), CHEER, ANGRY,
                    new EquipmentLoadout()
                            .addItem(EquipmentSlot.SHIELD, ItemID.STEEL_SQ_SHIELD)
                            .addItem(EquipmentSlot.LEGS, ItemID.GREEN_DHIDE_CHAPS)
                            .addItem(EquipmentSlot.CHEST, ItemID.GREEN_DHIDE_BODY)),
            new EmoteClue("Clap in Seers court house. Spin before you talk to me. Equip an adamant halberd, blue mystic robe bottom and a diamond ring.", "Seers Village",
                    OUTSIDE_THE_SEERS_VILLAGE_COURTHOUSE, new Tile(2735, 3469, 0), CLAP, SPIN,
                    new EquipmentLoadout()
                            .addItem(EquipmentSlot.LEGS, ItemID.MYSTIC_ROBE_BOTTOM)
                            .addItem(EquipmentSlot.RING, ItemID.DIAMOND_RING)
                            .addItem(EquipmentSlot.WEAPON, ItemID.ADAMANT_HALBERD)),
            new EmoteClue("Clap your hands north of Mount Karuulm Spin before you talk to me. Equip an adamant warhammer, a ring of life and a pair of mithril boots.", "Mount Karuulm",
                    NORTH_OF_MOUNT_KARUULM, new Tile(1306, 3839, 0), CLAP, SPIN,
                    new EquipmentLoadout()
                            .addItem(EquipmentSlot.FEET, ItemID.MITHRIL_BOOTS)
                            .addItem(EquipmentSlot.RING, ItemID.RING_OF_LIFE)
                            .addItem(EquipmentSlot.WEAPON, ItemID.ADAMANT_WARHAMMER)),
            new EmoteClue("Cry in the Catherby Ranging shop. Bow before you talk to me. Equip blue gnome boots, a hard leather body and an unblessed silver sickle.", "Catherby",
                    HICKTONS_ARCHERY_EMPORIUM, new Tile(2823, 3443, 0), CRY, BOW,
                    new EquipmentLoadout()
                            .addItem(EquipmentSlot.WEAPON, ItemID.SILVER_SICKLE)
                            .addItem(EquipmentSlot.CHEST, ItemID.HARDLEATHER_BODY)
                            .addItem(EquipmentSlot.FEET, ItemID.BLUE_BOOTS)),
//                    new EquipmentLoadoutItem(BLUE_BOOTS), new EquipmentLoadoutItem(HARDLEATHER_BODY), new EquipmentLoadoutItem(SILVER_SICKLE)),
            new EmoteClue("Cry on the shore of Catherby beach. Laugh before you talk to me, equip an adamant sq shield, a bone dagger and mithril platebody.", "Catherby",
                    OUTSIDE_HARRYS_FISHING_SHOP_IN_CATHERBY, new Tile(2852, 3429, 0), CRY, LAUGH,
                    new EquipmentLoadout()
                            .addItem(EquipmentSlot.WEAPON, ItemID.BONE_DAGGER)
                            .addItem(EquipmentSlot.CHEST, ItemID.MITHRIL_PLATEBODY)
                            .addItem(EquipmentSlot.SHIELD, ItemID.ADAMANT_SQ_SHIELD)),
//                    new EquipmentLoadoutItem(ADAMANT_SQ_SHIELD), new EquipmentLoadoutItem(BONE_DAGGER), new EquipmentLoadoutItem(MITHRIL_PLATEBODY)),
            new EmoteClue("Cry on top of the western tree in the Gnome Agility Arena. Indicate 'no' before you talk to me. Equip a steel kiteshield, ring of forging and green dragonhide chaps.", "Gnome Stronghold",
                    GNOME_STRONGHOLD_BALANCING_ROPE, new Tile(2473, 3420, 2), CRY, NO,
                    new EquipmentLoadout()
                            .addItem(EquipmentSlot.WEAPON, ItemID.BONE_DAGGER)
                            .addItem(EquipmentSlot.CHEST, ItemID.MITHRIL_PLATEBODY)
                            .addItem(EquipmentSlot.SHIELD, ItemID.ADAMANT_SQ_SHIELD)),
//                    new EquipmentLoadoutItem(STEEL_KITESHIELD), new EquipmentLoadoutItem(RING_OF_FORGING), new EquipmentLoadoutItem(GREEN_DHIDE_CHAPS)),
            new EmoteClue("Cry in the Draynor Village jail. Jump for joy before you talk to me. Equip an adamant sword, a sapphire amulet and an adamant plateskirt.", "Draynor Village jail",
                    OUTSIDE_DRAYNOR_VILLAGE_JAIL, new Tile(3128, 3245, 0), CRY, JUMP_FOR_JOY,
                    new EquipmentLoadout()
                            .addItem(EquipmentSlot.WEAPON, ItemID.ADAMANT_SWORD)
                            .addItem(EquipmentSlot.AMULET, ItemID.SAPPHIRE_AMULET)
                            .addItem(EquipmentSlot.LEGS, ItemID.ADAMANT_PLATESKIRT)),
//                    new EquipmentLoadoutItem(ADAMANT_SWORD), new EquipmentLoadoutItem(SAPPHIRE_AMULET), new EquipmentLoadoutItem(ADAMANT_PLATESKIRT)),
            new EmoteClue("Dance in the dark caves beneath Lumbridge Swamp. Blow a kiss before you talk to me. Equip an air staff, Bronze full helm and an amulet of power.", "Lumbridge swamp caves",
                    LUMBRIDGE_SWAMP_CAVES, new Tile(3168, 9571, 0), DANCE, BLOW_KISS,
//                    Varbits.FIRE_PIT_LUMBRIDGE_SWAMP, todo something for lamp
                    new EquipmentLoadout()
                            .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_AIR)
                            .addItem(EquipmentSlot.AMULET, ItemID.AMULET_OF_POWER)
                            .addItem(EquipmentSlot.HAT, ItemID.BRONZE_FULL_HELM)),
//                    new EquipmentLoadoutItem(STAFF_OF_AIR), new EquipmentLoadoutItem(BRONZE_FULL_HELM), new EquipmentLoadoutItem(AMULET_OF_POWER)),
            new EmoteClue("Dance in the centre of Canifis. Bow before you talk to me. Equip a green gnome robe top, mithril plate legs and an iron two-handed sword.", "Canifis",
                    CENTRE_OF_CANIFIS, new Tile(3492, 3488, 0), DANCE, BOW,
                    new EquipmentLoadout()
                            .addItem(EquipmentSlot.WEAPON, ItemID.IRON_2H_SWORD)
                            .addItem(EquipmentSlot.LEGS, ItemID.MITHRIL_PLATELEGS)
                            .addItem(EquipmentSlot.CHEST, ItemID.GREEN_ROBE_TOP)),
//                    new EquipmentLoadoutItem(GREEN_ROBE_TOP), new EquipmentLoadoutItem(MITHRIL_PLATELEGS), new EquipmentLoadoutItem(IRON_2H_SWORD)),
            new EmoteClue("Dance a jig under Shantay's Awning. Bow before you talk to me. Equip a pointed blue snail helmet, an air staff and a bronze square shield.", "Shantay Pass",
                    SHANTAY_PASS, new Tile(3304, 3124, 0), JIG, BOW,
                    new EquipmentLoadout()
                            .addItem(EquipmentSlot.HAT, ItemID.POINTED_BRUISE_BLUE_SNELM)
                            .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_AIR)
                            .addItem(EquipmentSlot.SHIELD, ItemID.BRONZE_SQ_SHIELD)),
//                    new EquipmentLoadoutItem(STAFF_OF_AIR), new EquipmentLoadoutItem(BRONZE_SQ_SHIELD)),
            new EmoteClue("Jump for joy in Yanille bank. Dance a jig before you talk to me. Equip a brown apron, adamantite medium helmet and snakeskin chaps.", "Yanille",
                    OUTSIDE_YANILLE_BANK, new Tile(2610, 3092, 0), JUMP_FOR_JOY, JIG,
                    new EquipmentLoadout()
                            .addItem(EquipmentSlot.WEAPON, ItemID.IRON_2H_SWORD)
                            .addItem(EquipmentSlot.LEGS, ItemID.ADAMANT_MED_HELM)
                            .addItem(EquipmentSlot.CHEST, ItemID.BROWN_APRON)),
//                    new EquipmentLoadoutItem(BROWN_APRON), new EquipmentLoadoutItem(ADAMANT_MED_HELM), new EquipmentLoadoutItem(SNAKESKIN_CHAPS)),
            new EmoteClue("Jump for joy in the TzHaar sword shop. Shrug before you talk to me. Equip a Steel longsword, Blue D'hide body and blue mystic gloves.", "Tzhaar weapon store",
                    TZHAAR_WEAPONS_STORE, new Tile(2477, 5146, 0), JUMP_FOR_JOY, SHRUG,
                    new EquipmentLoadout()
                            .addItem(EquipmentSlot.WEAPON, ItemID.STEEL_LONGSWORD)
                            .addItem(EquipmentSlot.HANDS, ItemID.MYSTIC_GLOVES)
                            .addItem(EquipmentSlot.CHEST, ItemID.BLUE_DHIDE_BODY)),
//                    new EquipmentLoadoutItem(STEEL_LONGSWORD), new EquipmentLoadoutItem(BLUE_DHIDE_BODY), new EquipmentLoadoutItem(MYSTIC_GLOVES)),
            new EmoteClue("Nod your head where the River Ortus meets the Proudspire. Indicate 'no' before you talk to me. Equip a blue wizard hat, a blue wizard robe and wear nothing on your legs.", "East of The Proudspire",
                    ORTUS_MEETS_PROUDSPIRE, new Tile(1626, 3241, 0), YES, NO,
                    new EquipmentLoadout()
                            .addItem(EquipmentSlot.WEAPON, ItemID.STEEL_LONGSWORD)
                            .addItem(EquipmentSlot.CHEST, ItemID.BLUE_WIZARD_ROBE)
                            .setStrict(true)), // need empty legs
//                    new EquipmentLoadoutItem(BLUE_WIZARD_HAT), new EquipmentLoadoutItem(BLUE_WIZARD_ROBE), emptySlot("Nothing on legs", LEGS)),
            new EmoteClue("Jump for Joy in the mine near the Twilight Temple. Bow before you talk to me. Equip a maple longbow, a ruby amulet and some steel platelegs.", "Twilight Temple",
                    TWILIGHT_TEMPLE_MINE, new Tile(1672, 3284, 0), JUMP_FOR_JOY, BOW,
                    new EquipmentLoadout()
                            .addItem(EquipmentSlot.WEAPON, ItemID.MAPLE_LONGBOW)
                            .addItem(EquipmentSlot.AMULET, ItemID.RUBY_AMULET)
                            .addItem(EquipmentSlot.LEGS, ItemID.STEEL_PLATELEGS)),
//                    new EquipmentLoadoutItem(MAPLE_LONGBOW), new EquipmentLoadoutItem(RUBY_AMULET), new EquipmentLoadoutItem(STEEL_PLATELEGS)),
            new EmoteClue("Panic by the mausoleum in Morytania. Wave before you speak to me. Equip a mithril plate skirt, a maple longbow and no boots.", "Morytania mausoleum, access via the experiments cave",
                    MAUSOLEUM_OFF_THE_MORYTANIA_COAST, new Tile(3504, 3576, 0), PANIC, WAVE,
                    new EquipmentLoadout()
                            .addItem(EquipmentSlot.WEAPON, ItemID.MAPLE_LONGBOW)
                            .setStrict(true)
                            .addItem(EquipmentSlot.LEGS, ItemID.MITHRIL_PLATESKIRT)),
//                    new EquipmentLoadoutItem(MITHRIL_PLATESKIRT), new EquipmentLoadoutItem(MAPLE_LONGBOW), emptySlot("No boots", BOOTS)),
            new EmoteClue("Shrug in Catherby bank. Yawn before you talk to me. Equip a maple longbow, green d'hide chaps and an iron med helm.", "Catherby",
                    OUTSIDE_CATHERBY_BANK, new Tile(2808, 3440, 0), SHRUG, YAWN,
                    new EquipmentLoadout()
                            .addItem(EquipmentSlot.WEAPON, ItemID.MAPLE_LONGBOW)
                            .addItem(EquipmentSlot.HAT, ItemID.IRON_MED_HELM)
                            .addItem(EquipmentSlot.LEGS, ItemID.GREEN_DHIDE_CHAPS)),
//                    new EquipmentLoadoutItem(MAPLE_LONGBOW), new EquipmentLoadoutItem(GREEN_DHIDE_CHAPS), new EquipmentLoadoutItem(IRON_MED_HELM)),
            new EmoteClue("Spin on the bridge by the Barbarian Village. Salute before you talk to me. Equip purple gloves, a steel kiteshield and a mithril full helmet.", "Barbarian Village",
                    EAST_OF_THE_BARBARIAN_VILLAGE_BRIDGE, new Tile(3105, 3420, 0), SPIN, SALUTE,
                    new EquipmentLoadout()
                            .addItem(EquipmentSlot.SHIELD, ItemID.STEEL_KITESHIELD)
                            .addItem(EquipmentSlot.HAT, ItemID.MITHRIL_FULL_HELM)
                            .addItem(EquipmentSlot.HANDS, ItemID.PURPLE_GLOVES)),
//                    new EquipmentLoadoutItem(PURPLE_GLOVES), new EquipmentLoadoutItem(STEEL_KITESHIELD), new EquipmentLoadoutItem(MITHRIL_FULL_HELM)),
            new EmoteClue("Think in the centre of the Observatory. Spin before you talk to me. Equip a mithril chain body, green dragonhide chaps and a ruby amulet.", "Observatory",
                    OBSERVATORY, new Tile(2439, 3161, 0), THINK, SPIN,
                    new EquipmentLoadout()
                            .addItem(EquipmentSlot.LEGS, ItemID.GREEN_DHIDE_CHAPS)
                            .addItem(EquipmentSlot.CHEST, ItemID.MITHRIL_CHAINBODY)
                            .addItem(EquipmentSlot.AMULET, ItemID.RUBY_AMULET)),
//                    new EquipmentLoadoutItem(MITHRIL_CHAINBODY), new EquipmentLoadoutItem(GREEN_DHIDE_CHAPS), new EquipmentLoadoutItem(RUBY_AMULET)),
            new EmoteClue("Yawn in the Castle Wars lobby. Shrug before you talk to me. Equip a ruby amulet, a mithril scimitar and a Wilderness cape.", "Castle Wars",
                    CASTLE_WARS_BANK, new Tile(2440, 3092, 0), YAWN, SHRUG,
                    new EquipmentLoadout()
                            .addItem(EquipmentSlot.WEAPON, ItemID.MITHRIL_SCIMITAR)
                            .addItem(EquipmentSlot.CAPE, ItemID.TEAM1_CAPE)
                            .addItem(EquipmentSlot.AMULET, ItemID.RUBY_AMULET)),
//                    new EquipmentLoadoutItem(RUBY_AMULET), new EquipmentLoadoutItem(MITHRIL_SCIMITAR), ANY_TEAM_CAPE),
            new EmoteClue("Yawn in the centre of the Arceuus Library. Nod your head before you talk to me. Equip blue dragonhide vambraces, adamant boots and an adamant dagger.", "Arceuus library",
                    ENTRANCE_OF_THE_ARCEUUS_LIBRARY, new Tile(1632, 3807, 0), YAWN, YES,
                    new EquipmentLoadout()
                            .addItem(EquipmentSlot.WEAPON, ItemID.ADAMANT_DAGGER)
                            .addItem(EquipmentSlot.FEET, ItemID.ADAMANT_BOOTS)
                            .addItem(EquipmentSlot.HANDS, ItemID.BLUE_DHIDE_VAMBRACES))
//                    new EquipmentLoadoutItem(BLUE_DHIDE_VAMBRACES), new EquipmentLoadoutItem(ADAMANT_BOOTS), new EquipmentLoadoutItem(ADAMANT_DAGGER))

//            new EmoteClue("Dance at the crossroads north of Draynor. Equip an iron chain body, a sapphire ring and a longbow.", "Draynor Village", CROSSROADS_NORTH_OF_DRAYNOR_VILLAGE, new Tile(3109, 3294, 0), DANCE, new EquipmentLoadoutItem(IRON_CHAINBODY), new EquipmentLoadoutItem(SAPPHIRE_RING), new EquipmentLoadoutItem(LONGBOW)),
//            new EmoteClue("Dance in the Party Room. Equip a steel full helmet, steel platebody and an iron plateskirt.", "Falador Party Room", OUTSIDE_THE_FALADOR_PARTY_ROOM, new Tile(3045, 3376, 0), DANCE, new EquipmentLoadoutItem(STEEL_FULL_HELM), new EquipmentLoadoutItem(STEEL_PLATEBODY), new EquipmentLoadoutItem(IRON_PLATESKIRT)),
//            new EmoteClue("Dance in the shack in Lumbridge Swamp. Equip a bronze dagger, iron full helmet and a gold ring.", "Lumbridge swamp", NEAR_A_SHED_IN_LUMBRIDGE_SWAMP, new Tile(3203, 3169, 0), DANCE, new EquipmentLoadoutItem(BRONZE_DAGGER), new EquipmentLoadoutItem(IRON_FULL_HELM), new EquipmentLoadoutItem(GOLD_RING)),
//            new EmoteClue("Dance at the cat-doored pyramid in Sophanem. Beware of double agents! Equip a ring of life, an uncharged amulet of glory and an adamant two-handed sword.", "Pyramid Of Sophanem", OUTSIDE_THE_GREAT_PYRAMID_OF_SOPHANEM, new Tile(3294, 2781, 0), DOUBLE_AGENT_108, DANCE, new EquipmentLoadoutItem(RING_OF_LIFE), new EquipmentLoadoutItem(AMULET_OF_GLORY), new EquipmentLoadoutItem(ADAMANT_2H_SWORD)),
//            new EmoteClue("Dance in the King Black Dragon's lair. Beware of double agents! Equip a black dragonhide body, black dragonhide vambraces and a black dragon mask.", "King black dragon's lair", KING_BLACK_DRAGONS_LAIR, new Tile(2271, 4680, 0), DOUBLE_AGENT_141, DANCE, new EquipmentLoadoutItem(BLACK_DHIDE_BODY), new EquipmentLoadoutItem(BLACK_DHIDE_VAMBRACES), new EquipmentLoadoutItem(BLACK_DRAGON_MASK)),
//            new EmoteClue("Dance at the entrance to the Grand Exchange. Equip a pink skirt, pink robe top and a body tiara.", "Grand Exchange", SOUTH_OF_THE_GRAND_EXCHANGE, new Tile(3165, 3467, 0), DANCE, new EquipmentLoadoutItem(PINK_SKIRT), new EquipmentLoadoutItem(PINK_ROBE_TOP), new EquipmentLoadoutItem(BODY_TIARA)),
//            new EmoteClue("Goblin Salute in the Goblin Village. Beware of double agents! Equip a bandos godsword, a bandos cloak and a bandos platebody.", "Goblin Village", OUTSIDE_MUDKNUCKLES_HUT, new Tile(2956, 3505, 0), DOUBLE_AGENT_141, GOBLIN_SALUTE, new EquipmentLoadoutItem(BANDOS_PLATEBODY), new EquipmentLoadoutItem(BANDOS_CLOAK), any("Bandos godsword", new EquipmentLoadoutItem(BANDOS_GODSWORD), new EquipmentLoadoutItem(BANDOS_GODSWORD_OR))),
//            new EmoteClue("Headbang in the mine north of Al Kharid. Equip a desert shirt, leather gloves and leather boots.", "Al Kharid mine", AL_KHARID_SCORPION_MINE, new Tile(3299, 3289, 0), HEADBANG, new EquipmentLoadoutItem(DESERT_SHIRT), new EquipmentLoadoutItem(LEATHER_GLOVES), new EquipmentLoadoutItem(LEATHER_BOOTS)),
//            new EmoteClue("Headbang at the exam centre. Beware of double agents! Equip a mystic fire staff, a diamond bracelet and rune boots.", "Exam Centre", INSIDE_THE_DIGSITE_EXAM_CENTRE, new Tile(3362, 3340, 0), DOUBLE_AGENT_108, HEADBANG, new EquipmentLoadoutItem(MYSTIC_FIRE_STAFF), new EquipmentLoadoutItem(DIAMOND_BRACELET), new EquipmentLoadoutItem(RUNE_BOOTS)),
//            new EmoteClue("Headbang at the top of Slayer Tower. Equip a seercull, a combat bracelet and helm of Neitiznot.", "Slayer Tower", OUTSIDE_THE_SLAYER_TOWER_GARGOYLE_ROOM, new Tile(3421, 3537, 2), HEADBANG, new EquipmentLoadoutItem(SEERCULL), any("Combat bracelet", range(COMBAT_BRACELET4, COMBAT_BRACELET), new EquipmentLoadoutItem(COMBAT_BRACELET5), new EquipmentLoadoutItem(COMBAT_BRACELET6)), new EquipmentLoadoutItem(HELM_OF_NEITIZNOT)),
//            new EmoteClue("Dance a jig by the entrance to the Fishing Guild. Equip an emerald ring, a sapphire amulet, and a bronze chain body.", "Fishing Guild", OUTSIDE_THE_FISHING_GUILD, new Tile(2610, 3391, 0), JIG, new EquipmentLoadoutItem(EMERALD_RING), new EquipmentLoadoutItem(SAPPHIRE_AMULET), new EquipmentLoadoutItem(BRONZE_CHAINBODY)),
//            new EmoteClue("Do a jig in Varrock's rune store. Equip an air tiara and a staff of water.", "Varrock rune store", AUBURYS_SHOP_IN_VARROCK, new Tile(3253, 3401, 0), JIG, new EquipmentLoadoutItem(AIR_TIARA), new EquipmentLoadoutItem(STAFF_OF_WATER)),
//            new EmoteClue("Jump for joy at the beehives. Equip a desert shirt, green gnome robe bottoms and a steel axe.", "Catherby", CATHERBY_BEEHIVE_FIELD, new Tile(2759, 3445, 0), JUMP_FOR_JOY, new EquipmentLoadoutItem(DESERT_SHIRT), new EquipmentLoadoutItem(GREEN_ROBE_BOTTOMS), new EquipmentLoadoutItem(STEEL_AXE)),
//            new EmoteClue("Jump for joy in the Ancient Cavern. Equip a granite shield, splitbark body and any rune heraldic helm.", "Ancient cavern", ENTRANCE_OF_THE_CAVERN_UNDER_THE_WHIRLPOOL, new Tile(1768, 5366, 1), JUMP_FOR_JOY, new EquipmentLoadoutItem(GRANITE_SHIELD), new EquipmentLoadoutItem(SPLITBARK_BODY), range("Any rune heraldic helm", RUNE_HELM_H1, RUNE_HELM_H5)),
//            new EmoteClue("Jump for joy at the Neitiznot rune rock. Equip Rune boots, a proselyte hauberk and a dragonstone ring.", "Fremennik Isles", NEAR_A_RUNITE_ROCK_IN_THE_FREMENNIK_ISLES, new Tile(2375, 3850, 0), JUMP_FOR_JOY, new EquipmentLoadoutItem(RUNE_BOOTS), new EquipmentLoadoutItem(PROSELYTE_HAUBERK), new EquipmentLoadoutItem(DRAGONSTONE_RING)),
//            new EmoteClue("Jump for joy in the centre of Zul-Andra. Beware of double agents! Equip a dragon 2h sword, bandos boots and an obsidian cape.", "Zul-Andra", NEAR_THE_PIER_IN_ZULANDRA, new Tile(2199, 3056, 0), DOUBLE_AGENT_141, JUMP_FOR_JOY, new EquipmentLoadoutItem(DRAGON_2H_SWORD), any("Bandos boots", new EquipmentLoadoutItem(BANDOS_BOOTS), new EquipmentLoadoutItem(GUARDIAN_BOOTS), new EquipmentLoadoutItem(ECHO_BOOTS)), new EquipmentLoadoutItem(OBSIDIAN_CAPE)),
//            new EmoteClue("Laugh by the fountain of heroes. Equip splitbark legs, dragon boots and a Rune longsword.", "Fountain of heroes", FOUNTAIN_OF_HEROES, new Tile(2920, 9893, 0), LAUGH, new EquipmentLoadoutItem(SPLITBARK_LEGS), any("Dragon boots", new EquipmentLoadoutItem(DRAGON_BOOTS), new EquipmentLoadoutItem(DRAGON_BOOTS_G), new EquipmentLoadoutItem(PRIMORDIAL_BOOTS)), new EquipmentLoadoutItem(RUNE_LONGSWORD)),
//            new EmoteClue("Laugh in Jokul's tent in the Mountain Camp. Beware of double agents! Equip a rune full helmet, blue dragonhide chaps and a fire battlestaff.", "Mountain Camp", MOUNTAIN_CAMP_GOAT_ENCLOSURE, new Tile(2812, 3681, 0), DOUBLE_AGENT_108, LAUGH, new EquipmentLoadoutItem(RUNE_FULL_HELM), new EquipmentLoadoutItem(BLUE_DHIDE_CHAPS), new EquipmentLoadoutItem(FIRE_BATTLESTAFF)),
//            new EmoteClue("Laugh at the crossroads south of the Sinclair Mansion. Equip a cowl, a blue wizard robe top and an iron scimitar.", "Sinclair Mansion", ROAD_JUNCTION_SOUTH_OF_SINCLAIR_MANSION, new Tile(2741, 3536, 0), LAUGH, new EquipmentLoadoutItem(LEATHER_COWL), new EquipmentLoadoutItem(BLUE_WIZARD_ROBE), new EquipmentLoadoutItem(IRON_SCIMITAR)),
//            new EmoteClue("Laugh in front of the gem store in Ardougne market. Equip a Castlewars bracelet, a dragonstone amulet and a ring of forging.", "Ardougne", NEAR_THE_GEM_STALL_IN_ARDOUGNE_MARKET, new Tile(2666, 3304, 0), LAUGH, any("Castle wars bracelet", range(CASTLE_WARS_BRACELET3, CASTLE_WARS_BRACELET1)), new EquipmentLoadoutItem(DRAGONSTONE_AMULET), new EquipmentLoadoutItem(RING_OF_FORGING)),
//            new EmoteClue("Panic in the Limestone Mine. Equip bronze platelegs, a steel pickaxe and a steel medium helmet.", "Limestone Mine", LIMESTONE_MINE, new Tile(3372, 3498, 0), PANIC, new EquipmentLoadoutItem(BRONZE_PLATELEGS), new EquipmentLoadoutItem(STEEL_PICKAXE), new EquipmentLoadoutItem(STEEL_MED_HELM)),
//            new EmoteClue("Panic on the Wilderness volcano bridge. Beware of double agents! Equip any headband and crozier.", "Wilderness volcano", VOLCANO_IN_THE_NORTHEASTERN_WILDERNESS, new Tile(3368, 3935, 0), DOUBLE_AGENT_65, PANIC, any("Any headband", range(RED_HEADBAND, BROWN_HEADBAND), range(WHITE_HEADBAND, GREEN_HEADBAND)), any("Any crozier", new EquipmentLoadoutItem(ANCIENT_CROZIER), new EquipmentLoadoutItem(ARMADYL_CROZIER), new EquipmentLoadoutItem(BANDOS_CROZIER), range(SARADOMIN_CROZIER, ZAMORAK_CROZIER))),
//            new EmoteClue("Panic by the pilot on White Wolf Mountain. Beware of double agents! Equip mithril platelegs, a ring of life and a rune axe.", "White Wolf Mountain", GNOME_GLIDER_ON_WHITE_WOLF_MOUNTAIN, new Tile(2847, 3499, 0), DOUBLE_AGENT_108, PANIC, new EquipmentLoadoutItem(MITHRIL_PLATELEGS), new EquipmentLoadoutItem(RING_OF_LIFE), new EquipmentLoadoutItem(RUNE_AXE)),
//            new EmoteClue("Panic by the big egg where no one dare goes and the ground is burnt. Beware of double agents! Equip a dragon med helm, a TokTz-Ket-Xil, a brine sabre, rune platebody and an uncharged amulet of glory.", "Lava dragon isle", SOUTHEAST_CORNER_OF_LAVA_DRAGON_ISLE, new Tile(3227, 3831, 0), DOUBLE_AGENT_141, PANIC, new EquipmentLoadoutItem(DRAGON_MED_HELM), new EquipmentLoadoutItem(TOKTZKETXIL), new EquipmentLoadoutItem(BRINE_SABRE), new EquipmentLoadoutItem(RUNE_PLATEBODY), any("Uncharged Amulet of glory", new EquipmentLoadoutItem(AMULET_OF_GLORY))),
//            new EmoteClue("Panic at the area flowers meet snow. Equip Blue D'hide vambraces, a dragon spear and a rune plateskirt.", "Trollweiss mountain", HALFWAY_DOWN_TROLLWEISS_MOUNTAIN, new Tile(2776, 3781, 0), PANIC, new EquipmentLoadoutItem(BLUE_DHIDE_VAMBRACES), new EquipmentLoadoutItem(DRAGON_SPEAR), new EquipmentLoadoutItem(RUNE_PLATESKIRT), new EquipmentLoadoutItem(SLED_4084)),
//            new EmoteClue("Panic outside the Twilight Temple. Beware of double agents! Equip a rune longsword, rune platebody and a rune plateskirt.", "Twilight Temple", OUTSIDE_TWILIGHT_TEMPLE, new Tile(1694, 3247, 0), DOUBLE_AGENT_108, PANIC, new EquipmentLoadoutItem(RUNE_LONGSWORD), new EquipmentLoadoutItem(RUNE_PLATEBODY), new EquipmentLoadoutItem(RUNE_PLATESKIRT)),
//            new EmoteClue("Blow a raspberry in the bank of the Warriors' Guild. Beware of double agents! Equip a dragon battleaxe, a slayer helm of any kind and a dragon defender or avernic defender.", "Warriors' guild", WARRIORS_GUILD_BANK_29047, new Tile(2843, 3543, 0), DOUBLE_AGENT_141, RASPBERRY, new EquipmentLoadoutItem(DRAGON_BATTLEAXE), any("Dragon defender or Avernic defender", new EquipmentLoadoutItem(DRAGON_DEFENDER), new EquipmentLoadoutItem(DRAGON_DEFENDER_T), new EquipmentLoadoutItem(DRAGON_DEFENDER_L), new EquipmentLoadoutItem(AVERNIC_DEFENDER), new EquipmentLoadoutItem(AVERNIC_DEFENDER_L), new EquipmentLoadoutItem(GHOMMALS_AVERNIC_DEFENDER_5), new EquipmentLoadoutItem(GHOMMALS_AVERNIC_DEFENDER_5_L), new EquipmentLoadoutItem(GHOMMALS_AVERNIC_DEFENDER_6), new EquipmentLoadoutItem(GHOMMALS_AVERNIC_DEFENDER_6_L)), ANY_SLAYER_HELMET),
//            new EmoteClue("Blow a raspberry at the monkey cage in Ardougne Zoo. Equip a studded leather body, bronze platelegs and a normal staff with no orb.", "Ardougne Zoo", NEAR_THE_PARROTS_IN_ARDOUGNE_ZOO, new Tile(2607, 3282, 0), RASPBERRY, new EquipmentLoadoutItem(STUDDED_BODY), new EquipmentLoadoutItem(BRONZE_PLATELEGS), new EquipmentLoadoutItem(STAFF)),
//            new EmoteClue("Blow raspberries outside the entrance to Keep Le Faye. Equip a coif, an iron platebody and leather gloves.", "Keep Le Faye", OUTSIDE_KEEP_LE_FAYE, new Tile(2757, 3401, 0), RASPBERRY, new EquipmentLoadoutItem(COIF), new EquipmentLoadoutItem(IRON_PLATEBODY), new EquipmentLoadoutItem(LEATHER_GLOVES)),
//            new EmoteClue("Blow a raspberry in the Fishing Guild bank. Beware of double agents! Equip an elemental shield, blue dragonhide chaps and a rune warhammer.", "Fishing Guild", FISHING_GUILD_BANK, new Tile(2588, 3419, 0), DOUBLE_AGENT_108, RASPBERRY, new EquipmentLoadoutItem(ELEMENTAL_SHIELD), new EquipmentLoadoutItem(BLUE_DHIDE_CHAPS), new EquipmentLoadoutItem(RUNE_WARHAMMER)),
//            new EmoteClue("Salute in the banana plantation. Beware of double agents! Equip a diamond ring, amulet of power, and nothing on your chest and legs.", "Karamja", WEST_SIDE_OF_THE_KARAMJA_BANANA_PLANTATION, new Tile(2914, 3168, 0), DOUBLE_AGENT_108, SALUTE, new EquipmentLoadoutItem(DIAMOND_RING), new EquipmentLoadoutItem(AMULET_OF_POWER), emptySlot("Nothing on chest & legs", BODY, LEGS)),
//            new EmoteClue("Salute in the Warriors' Guild bank. Equip only a black salamander.", "Warriors' guild", WARRIORS_GUILD_BANK, new Tile(2844, 3542, 0), SALUTE, new EquipmentLoadoutItem(BLACK_SALAMANDER), emptySlot("Nothing else", HEAD, CAPE, AMULET, BODY, SHIELD, LEGS, GLOVES, BOOTS, RING, AMMO)),
//            new EmoteClue("Salute in the centre of the mess hall. Beware of double agents! Equip a rune halberd rune platebody and an amulet of strength.", "Hosidius mess hall", HOSIDIUS_MESS, new Tile(1646, 3631, 0), DOUBLE_AGENT_108, SALUTE, new EquipmentLoadoutItem(RUNE_HALBERD), new EquipmentLoadoutItem(RUNE_PLATEBODY), new EquipmentLoadoutItem(AMULET_OF_STRENGTH)),
//            new EmoteClue("Salute outside the gates of Cam Torum. Beware of double agents! Equip a full set of blue moon equipment.", "Cam Torum", CAM_TORUM_ENTRANCE, new Tile(1436, 3115, 0), DOUBLE_AGENT_141, SALUTE, any("Blue moon helm", new EquipmentLoadoutItem(BLUE_MOON_HELM), new EquipmentLoadoutItem(BLUE_MOON_HELM_29041)), any("Blue moon chestplate", new EquipmentLoadoutItem(BLUE_MOON_CHESTPLATE), new EquipmentLoadoutItem(BLUE_MOON_CHESTPLATE_29037)), any("Blue moon tassets", new EquipmentLoadoutItem(BLUE_MOON_TASSETS), new EquipmentLoadoutItem(BLUE_MOON_TASSETS_29039)), new EquipmentLoadoutItem(BLUE_MOON_SPEAR)),
//            new EmoteClue("Shrug in the mine near Rimmington. Equip a gold necklace, a gold ring and a bronze spear.", "Rimmington mine", RIMMINGTON_MINE, new Tile(2976, 3238, 0), SHRUG, new EquipmentLoadoutItem(GOLD_NECKLACE), new EquipmentLoadoutItem(GOLD_RING), new EquipmentLoadoutItem(BRONZE_SPEAR)),
//            new EmoteClue("Shrug in the woods east of the Level 19 Wilderness Obelisk. Beware of double agents! Equip rune platelegs, an iron platebody and blue dragonhide vambraces.", "East of the Level 19 Wilderness Obelisk", EAST_OF_THE_LEVEL_19_WILDERNESS_OBELISK, new Tile(3241, 3672, 0), DOUBLE_AGENT_65, SHRUG, new EquipmentLoadoutItem(RUNE_PLATELEGS), new EquipmentLoadoutItem(IRON_PLATEBODY), new EquipmentLoadoutItem(BLUE_DHIDE_VAMBRACES)),
//            new EmoteClue("Shrug in the Shayzien war tent. Equip a blue mystic robe bottom, a rune kiteshield and any bob shirt.", "Shayzien war tent", SHAYZIEN_WAR_TENT, new Tile(1487, 3635, 0), SHRUG, new EquipmentLoadoutItem(MYSTIC_ROBE_BOTTOM), new EquipmentLoadoutItem(RUNE_KITESHIELD), range("Any bob shirt", BOBS_RED_SHIRT, BOBS_PURPLE_SHIRT)),
//            new EmoteClue("Slap your head in the centre of the Kourend catacombs. Beware of double agents! Equip arclight or emberlight along with the amulet of the damned.", "Kourend catacombs", CENTRE_OF_THE_CATACOMBS_OF_KOUREND, new Tile(1663, 10045, 0), DOUBLE_AGENT_141, SLAP_HEAD, any("Arclight or Emberlight", new EquipmentLoadoutItem(ARCLIGHT), new EquipmentLoadoutItem(EMBERLIGHT)), any("Amulet of the damned", new EquipmentLoadoutItem(AMULET_OF_THE_DAMNED), new EquipmentLoadoutItem(AMULET_OF_THE_DAMNED_FULL))),
//            new EmoteClue("Spin at the crossroads north of Rimmington. Equip a green gnome hat, cream gnome top and leather chaps.", "Rimmington", ROAD_JUNCTION_NORTH_OF_RIMMINGTON, new Tile(2981, 3276, 0), SPIN, new EquipmentLoadoutItem(GREEN_HAT), new EquipmentLoadoutItem(CREAM_ROBE_TOP), new EquipmentLoadoutItem(LEATHER_CHAPS)),
//            new EmoteClue("Spin in Draynor Manor by the fountain. Equip an iron platebody, studded leather chaps and a bronze full helmet.", "Draynor Manor", DRAYNOR_MANOR_BY_THE_FOUNTAIN, new Tile(3088, 3336, 0), SPIN, new EquipmentLoadoutItem(IRON_PLATEBODY), new EquipmentLoadoutItem(STUDDED_CHAPS), new EquipmentLoadoutItem(BRONZE_FULL_HELM)),
//            new EmoteClue("Spin in front of the Soul altar. Beware of double agents! Equip a dragon pickaxe, helm of neitiznot and a pair of rune boots.", "Soul altar", SOUL_ALTAR, new Tile(1815, 3856, 0), DOUBLE_AGENT_141, SPIN, any("Dragon or Crystal pickaxe", new EquipmentLoadoutItem(DRAGON_PICKAXE), new EquipmentLoadoutItem(DRAGON_PICKAXE_12797), new EquipmentLoadoutItem(INFERNAL_PICKAXE), new EquipmentLoadoutItem(INFERNAL_PICKAXE_UNCHARGED), new EquipmentLoadoutItem(DRAGON_PICKAXE_OR), new EquipmentLoadoutItem(DRAGON_PICKAXE_OR_25376), new EquipmentLoadoutItem(CRYSTAL_PICKAXE), new EquipmentLoadoutItem(CRYSTAL_PICKAXE_INACTIVE), new EquipmentLoadoutItem(INFERNAL_PICKAXE_OR), new EquipmentLoadoutItem(INFERNAL_PICKAXE_UNCHARGED_25369)), new EquipmentLoadoutItem(HELM_OF_NEITIZNOT), new EquipmentLoadoutItem(RUNE_BOOTS)),
//            new EmoteClue("Spin in the Varrock Castle courtyard. Equip a black axe, a coif and a ruby ring.", "Varrock Castle", OUTSIDE_VARROCK_PALACE_COURTYARD, new Tile(3213, 3463, 0), SPIN, new EquipmentLoadoutItem(BLACK_AXE), new EquipmentLoadoutItem(COIF), new EquipmentLoadoutItem(RUBY_RING)),
//            new EmoteClue("Spin in West Ardougne Church. Equip a dragon spear and red dragonhide chaps.", "West Ardougne Church", CHAPEL_IN_WEST_ARDOUGNE, new Tile(2530, 3290, 0), SPIN, new EquipmentLoadoutItem(DRAGON_SPEAR), new EquipmentLoadoutItem(RED_DHIDE_CHAPS)),
//            new EmoteClue("Stamp in the Enchanted valley west of the waterfall. Beware of double agents! Equip a dragon axe.", "Enchanted Valley (BKQ)", NORTHWESTERN_CORNER_OF_THE_ENCHANTED_VALLEY, new Tile(3030, 4522, 0), DOUBLE_AGENT_141, STAMP, any("Dragon or Crystal axe", new EquipmentLoadoutItem(DRAGON_AXE), new EquipmentLoadoutItem(DRAGON_AXE_OR), new EquipmentLoadoutItem(DRAGON_FELLING_AXE), new EquipmentLoadoutItem(CRYSTAL_AXE), new EquipmentLoadoutItem(CRYSTAL_AXE_INACTIVE), new EquipmentLoadoutItem(CRYSTAL_FELLING_AXE), new EquipmentLoadoutItem(CRYSTAL_FELLING_AXE_INACTIVE), new EquipmentLoadoutItem(INFERNAL_AXE), new EquipmentLoadoutItem(INFERNAL_AXE_UNCHARGED), new EquipmentLoadoutItem(INFERNAL_AXE_OR), new EquipmentLoadoutItem(INFERNAL_AXE_UNCHARGED_25371))),
//            new EmoteClue("Think in middle of the wheat field by the Lumbridge mill. Equip a blue gnome robetop, a turquoise gnome robe bottom and an oak shortbow.", "Lumbridge mill", WHEAT_FIELD_NEAR_THE_LUMBRIDGE_WINDMILL, new Tile(3159, 3298, 0), THINK, new EquipmentLoadoutItem(BLUE_ROBE_TOP), new EquipmentLoadoutItem(TURQUOISE_ROBE_BOTTOMS), new EquipmentLoadoutItem(OAK_SHORTBOW)),
//            new EmoteClue("Think on the western coast of Salvager Overlook. Beware of double agents! Equip a Hueycoatl hide coif and some Hueycoatl hide vambraces.", "Salvager Overlook", WESTERN_SALVAGER_OVERLOOK, new Tile(1610, 3302, 0), DOUBLE_AGENT_141, THINK, new EquipmentLoadoutItem(HUEYCOATL_HIDE_COIF), new EquipmentLoadoutItem(HUEYCOATL_HIDE_VAMBRACES)),
//            new EmoteClue("Wave along the south fence of the Lumber Yard. Equip a hard leather body, leather chaps and a bronze axe.", "Lumber Yard", NEAR_THE_SAWMILL_OPERATORS_BOOTH, new Tile(3307, 3491, 0), WAVE, new EquipmentLoadoutItem(HARDLEATHER_BODY), new EquipmentLoadoutItem(LEATHER_CHAPS), new EquipmentLoadoutItem(BRONZE_AXE)),
//            new EmoteClue("Wave in the Falador gem store. Equip a Mithril pickaxe, Black platebody and an Iron Kiteshield.", "Falador", NEAR_HERQUINS_SHOP_IN_FALADOR, new Tile(2945, 3335, 0), WAVE, new EquipmentLoadoutItem(MITHRIL_PICKAXE), new EquipmentLoadoutItem(BLACK_PLATEBODY), new EquipmentLoadoutItem(IRON_KITESHIELD)),
//            new EmoteClue("Wave on Mudskipper Point. Equip a black cape, leather chaps and a steel mace.", "Mudskipper Point (AIQ)", MUDSKIPPER_POINT, new Tile(2989, 3110, 0), WAVE, new EquipmentLoadoutItem(BLACK_CAPE), new EquipmentLoadoutItem(LEATHER_CHAPS), new EquipmentLoadoutItem(STEEL_MACE)),
//            new EmoteClue("Wave on the northern wall of Castle Drakan. Beware of double agents! Wear a dragon sq shield, splitbark body and any boater.", "Castle Drakan", NORTHERN_WALL_OF_CASTLE_DRAKAN, new Tile(3562, 3379, 0), DOUBLE_AGENT_141, WAVE, any("Dragon sq shield", new EquipmentLoadoutItem(DRAGON_SQ_SHIELD), new EquipmentLoadoutItem(DRAGON_SQ_SHIELD_G)), new EquipmentLoadoutItem(SPLITBARK_BODY), any("Any boater", new EquipmentLoadoutItem(RED_BOATER), new EquipmentLoadoutItem(ORANGE_BOATER), new EquipmentLoadoutItem(GREEN_BOATER), new EquipmentLoadoutItem(BLUE_BOATER), new EquipmentLoadoutItem(BLACK_BOATER), new EquipmentLoadoutItem(PINK_BOATER), new EquipmentLoadoutItem(PURPLE_BOATER), new EquipmentLoadoutItem(WHITE_BOATER))),
//            new EmoteClue("Yawn in the 7th room of Pyramid Plunder. Beware of double agents! Equip a pharaoh sceptre and a full set of menaphite robes.", "Pyramid Plunder", _7TH_CHAMBER_OF_JALSAVRAH, new Tile(1944, 4427, 0), DOUBLE_AGENT_141, YAWN, ANY_PHARAOHS_SCEPTRE, any("Full set of menaphite robes", all(new EquipmentLoadoutItem(MENAPHITE_PURPLE_HAT), new EquipmentLoadoutItem(MENAPHITE_PURPLE_TOP), range(MENAPHITE_PURPLE_ROBE, MENAPHITE_PURPLE_KILT)), all(new EquipmentLoadoutItem(MENAPHITE_RED_HAT), new EquipmentLoadoutItem(MENAPHITE_RED_TOP), range(MENAPHITE_RED_ROBE, MENAPHITE_RED_KILT)))),
//            new EmoteClue("Yawn in the Varrock library. Equip a green gnome robe top, HAM robe bottom and an iron warhammer.", "Varrock Castle", VARROCK_PALACE_LIBRARY, new Tile(3209, 3492, 0), YAWN, new EquipmentLoadoutItem(GREEN_ROBE_TOP), new EquipmentLoadoutItem(HAM_ROBE), new EquipmentLoadoutItem(IRON_WARHAMMER)),
//            new EmoteClue("Yawn in Draynor marketplace. Equip studded leather chaps, an iron kiteshield and a steel longsword.", "Draynor", DRAYNOR_VILLAGE_MARKET, new Tile(3083, 3253, 0), YAWN, new EquipmentLoadoutItem(STUDDED_CHAPS), new EquipmentLoadoutItem(IRON_KITESHIELD), new EquipmentLoadoutItem(STEEL_LONGSWORD)),
//            new EmoteClue("Yawn in the rogues' general store. Beware of double agents! Equip an adamant square shield, blue dragon vambraces and a rune pickaxe.", "Rogues general store", NOTERAZZOS_SHOP_IN_THE_WILDERNESS, new Tile(3026, 3701, 0), DOUBLE_AGENT_65, YAWN, new EquipmentLoadoutItem(ADAMANT_SQ_SHIELD), new EquipmentLoadoutItem(BLUE_DHIDE_VAMBRACES), new EquipmentLoadoutItem(RUNE_PICKAXE)),
//            new EmoteClue("Yawn at the top of Trollheim. Equip a lava battlestaff, black dragonhide vambraces and a mind shield.", "Trollheim Mountain", ON_TOP_OF_TROLLHEIM_MOUNTAIN, new Tile(2887, 3676, 0), YAWN, any("Lava battlestaff", new EquipmentLoadoutItem(LAVA_BATTLESTAFF), new EquipmentLoadoutItem(LAVA_BATTLESTAFF_21198)), new EquipmentLoadoutItem(BLACK_DHIDE_VAMBRACES), new EquipmentLoadoutItem(MIND_SHIELD)),
//            new EmoteClue("Yawn in the Fortis Grand Museum. Equip an emerald necklace, blue skirt, and turqoise gnome robe top.", "Fortis Grand Museum", FORTIS_GRAND_MUSEUM, new Tile(1712, 3163, 0), YAWN, new EquipmentLoadoutItem(EMERALD_NECKLACE), new EquipmentLoadoutItem(BLUE_SKIRT), new EquipmentLoadoutItem(TURQUOISE_ROBE_TOP)),
//            new EmoteClue("Swing a bullroarer at the top of the Watchtower. Beware of double agents! Equip a dragon plateskirt, climbing boots and a dragon chainbody.", "Yanille Watchtower", TOP_FLOOR_OF_THE_YANILLE_WATCHTOWER, new Tile(2930, 4717, 2), DOUBLE_AGENT_141, BULL_ROARER, any("Dragon plateskirt", new EquipmentLoadoutItem(DRAGON_PLATESKIRT), new EquipmentLoadoutItem(DRAGON_PLATESKIRT_G)), any("Climbing boots", new EquipmentLoadoutItem(CLIMBING_BOOTS), new EquipmentLoadoutItem(CLIMBING_BOOTS_G)), any("Dragon chainbody", new EquipmentLoadoutItem(DRAGON_CHAINBODY_3140), new EquipmentLoadoutItem(DRAGON_CHAINBODY_G)), new EquipmentLoadoutItem(BULLROARER)),
//            new EmoteClue("Blow a raspberry at Aris in her tent. Equip a gold ring and a gold necklace.", "Varrock", GYPSY_TENT_ENTRANCE, new Tile(3203, 3424, 0), RASPBERRY, new EquipmentLoadoutItem(GOLD_RING), new EquipmentLoadoutItem(GOLD_NECKLACE)),
//            new EmoteClue("Bow to Brugsen Bursen at the Grand Exchange.", "Grand Exchange", null, new Tile(3164, 3477, 0), BOW),
//            new EmoteClue("Cheer at Iffie Nitter. Equip a chef hat and a red cape.", "Varrock", FINE_CLOTHES_ENTRANCE, new Tile(3205, 3416, 0), CHEER, new EquipmentLoadoutItem(CHEFS_HAT), new EquipmentLoadoutItem(RED_CAPE)),
//            new EmoteClue("Clap at Bob's Brilliant Axes. Equip a bronze axe and leather boots.", "Lumbridge", BOB_AXES_ENTRANCE, new Tile(3231, 3203, 0), CLAP, new EquipmentLoadoutItem(BRONZE_AXE), new EquipmentLoadoutItem(LEATHER_BOOTS)),
//            new EmoteClue("Panic at Al Kharid mine.", "Al Kharid mine", null, new Tile(3303, 3271, 0), PANIC),
//            new EmoteClue("Spin at Flynn's Mace Shop.", "Falador", null, new Tile(2950, 3387, 0), SPIN),
//            new EmoteClue("Salute by the Charcoal Burners. Equip a Farmer's strawhat, Shayzien platebody (5) and Pyromancer robes.", "Charcoal Burners", CHARCOAL_BURNERS, new Tile(1714, 3467, 0), SALUTE, any("Farmer's strawhat", new EquipmentLoadoutItem(FARMERS_STRAWHAT), new EquipmentLoadoutItem(FARMERS_STRAWHAT_13647)), new EquipmentLoadoutItem(SHAYZIEN_BODY_5), new EquipmentLoadoutItem(PYROMANCER_ROBE))
    );

    private static final String UNICODE_CHECK_MARK = "\u2713";
    private static final String UNICODE_BALLOT_X = "\u2717";

    private final String text;
    private final String locationName;
    private final STASHUnit stashUnit;
    private final Tile location;
    private final Emote firstEmote;
    private final Emote secondEmote;
    private final EquipmentLoadout itemRequirements;

    private EmoteClue(String text, String locationName, STASHUnit stashUnit, Tile location, Emote firstEmote, EquipmentLoadout loadout) {
        this(text, locationName, stashUnit, location, firstEmote, null, loadout);
    }

    private EmoteClue(String text, String locationName, STASHUnit stashUnit, Tile location, Supplier<NPC> enemy, Emote firstEmote, EquipmentLoadout loadout) {
        this(text, locationName, stashUnit, location, firstEmote, null, loadout);
//        setEnemy(enemy);
    }

    private EmoteClue(String text, String locationName, STASHUnit stashUnit, Tile location, Emote firstEmote, Emote secondEmote, EquipmentLoadout loadout) {
        this.text = text;
        this.locationName = locationName;
        this.stashUnit = stashUnit;
        this.location = location;
        this.firstEmote = firstEmote;
        this.secondEmote = secondEmote;
        this.itemRequirements = loadout;
    }

    private EmoteClue(String text, String locationName, @Nullable STASHUnit stashUnit, Tile location, Emote firstEmote, Emote secondEmote, int firePitVarbitId, EquipmentLoadout loadout) {
        this(text, locationName, stashUnit, location, firstEmote, secondEmote, loadout);
//        setRequiresLight(true);
//        setFirePitVarbitId(firePitVarbitId);
    }


    public static EmoteClue forText(String text) {
        for (EmoteClue clue : CLUES) {
            if (clue.getText().equalsIgnoreCase(text)) {
                return clue;
            }
        }

        return null;
    }

    @Override
    public int solve() {
        if (!getItemRequirements().isFulfilled()) {
            Logger.info("Need to get item reqs");
            new WithdrawLoadoutEvent(null, getItemRequirements())
                    .executed();
            return ReactionGenerator.getNormal();
        }

        NPC uri = NPCs.closest("Uri");
        if (uri != null) {
            Logger.info("Talk to uri");
            uri.interact();
            return ReactionGenerator.getNormal();
        }

        // you now have the equipment, go and emote
        Tile loc = getLocation();
        if (!Players.getLocal().getTile().equals(loc)) {
            Logger.info("Go to loc");
            if (Walking.shouldWalk()) Walking.walk(loc);
            return ReactionGenerator.getNormal();
        }

        // emote
        Logger.info("Do first emote " + getFirstEmote());
        Emotes.doEmote(getFirstEmote());
        Sleep.sleep(1200);
        Logger.info("Do 2nd emote " + getSecondEmote());
        Emotes.doEmote(getSecondEmote());
        return ReactionGenerator.getNormal();
    }

    @Override
    public ClueScrollType getType() {
        return ClueScrollType.EMOTE;
    }
}
