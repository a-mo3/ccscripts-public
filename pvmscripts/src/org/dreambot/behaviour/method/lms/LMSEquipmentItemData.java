package org.dreambot.behaviour.method.lms;

import lombok.Getter;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 *  all(ish) the lms items
 */
@Getter
public enum LMSEquipmentItemData {
    HELM_OF_NEITIZNOT(23591, 0, 0, 0, 0, 0, 31, 29, 34, 3, 30, 3, 0, 0, 3),
    OCCULT_NECKLACE(12002, 0, 0, 0, 12, 0, 0, 0, 0, 0, 0, 0, 0, 5, 2),
    BARROWS_GLOVES(23593, 12, 12, 12, 6, 12, 12, 12, 12, 6, 12, 12, 0, 0, 0),
    BERSERKER_RING(23595, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 4, 0, 0, 0),
    DRAGON_DEFENDER(23597, 25, 24, 23, -3, -2, 25, 24, 23, -3, -2, 6, 0, 0, 0),
    SPIRIT_SHIELD(23599, 0, 0, 0, 0, 0, 39, 41, 50, 1, 45, 0, 0, 0, 1),
    RUNE_CROSSBOW(23601, 0, 0, 0, 0, 90, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    IMBUED_GUTHIX_CAPE(23603, 0, 0, 0, 15, 0, 3, 3, 3, 15, 0, 0, 0, 2, 0),
    IMBUED_ZAMORAK_CAPE(23605, 0, 0, 0, 15, 0, 3, 3, 3, 15, 0, 0, 0, 2, 0),
    IMBUED_SARADOMIN_CAPE(23607, 0, 0, 0, 15, 0, 3, 3, 3, 15, 0, 0, 0, 2, 0),
    AVAS_ACCUMULATOR(23609, 0, 0, 0, 0, 4, 0, 1, 0, 4, 0, 0, 0, 0, 0),
    ARMADYL_CROSSBOW(23611, 0, 0, 0, 0, 100, 0, 0, 0, 0, 0, 0, 0, 0, 1),
    STAFF_OF_THE_DEAD(23613, 55, 70, 0, 17, 0, 0, 3, 3, 17, 0, 72, 0, 15, 0),
    VESTAS_LONGSWORD(23615, 106, 121, -2, 0, 0, 1, 4, 3, 0, 0, 118, 0, 0, 0),
    ZURIELS_STAFF(23617, 13, -1, 65, 18, 0, 5, 7, 4, 18, 0, 72, 0, 10, 0),
    MORRIGANS_JAVELIN(23619, 0, 0, 0, 0, 105, 0, 0, 0, 0, 0, 0, 145, 0, 0),
    STATIUSS_WARHAMMER(23620, -4, -4, 123, 0, 0, 0, 0, 0, 0, 0, 114, 0, 0, 0),
    INFERNAL_CAPE(23622, 4, 4, 4, 1, 1, 12, 12, 12, 12, 12, 8, 0, 0, 2),
    SEERS_RING_I(23624, 0, 0, 0, 12, 0, 0, 0, 0, 12, 0, 0, 0, 0, 0),
    KODAI_WAND(23626, 0, 0, 0, 28, 0, 0, 3, 3, 20, 0, 0, 0, 15, 0),
    GHRAZI_RAPIER(23628, 94, 55, 0, 0, 0, 0, 0, 0, 0, 0, 89, 0, 0, 0),
    HEAVY_BALLISTA(23630, 0, 0, 0, 0, 125, 0, 0, 0, 0, 0, 0, 15, 0, 0),
    KARILS_LEATHERTOP(23632, 0, 0, 0, -15, 30, 47, 42, 50, 65, 57, 0, 0, 0, 0),
    DHAROKS_PLATELEGS(23633, 0, 0, 0, -21, -11, 85, 82, 83, -4, 92, 0, 0, 0, 0),
    TORAGS_PLATELEGS(23634, 0, 0, 0, -21, -11, 85, 82, 83, -4, 92, 0, 0, 0, 0),
    VERACS_PLATESKIRT(23635, 0, 0, 0, -21, -11, 85, 82, 83, 0, 84, 0, 0, 0, 4),
    VERACS_HELM(23636, 0, 0, 0, -6, -2, 55, 58, 54, 0, 56, 0, 0, 0, 3),
    TORAGS_HELM(23637, 0, 0, 0, -6, -2, 55, 58, 54, -1, 62, 0, 0, 0, 0),
    GUTHANS_HELM(23638, 0, 0, 0, -6, -2, 55, 58, 54, -1, 62, 0, 0, 0, 0),
    DHAROKS_HELM(23639, 0, 0, 0, -3, -1, 45, 48, 44, -1, 51, 0, 0, 0, 0),
    AMULET_OF_FURY(23640, 10, 10, 10, 10, 10, 15, 15, 15, 15, 15, 8, 0, 0, 5),
    BLESSED_SPIRIT_SHIELD(23642, 0, 0, 0, 0, 0, 53, 55, 73, 2, 52, 0, 0, 0, 3),
    ETERNAL_BOOTS(23644, 0, 0, 0, 8, 0, 5, 5, 5, 8, 5, 0, 0, 0, 0),
    BANDOS_TASSETS(23646, 0, 0, 0, -21, -7, 71, 63, 66, -4, 93, 2, 0, 0, 1),
    DRAGON_JAVELIN(23648, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 150, 0, 0),
    DIAMOND_BOLTS_E(23649, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 105, 0, 0),
    GRANITE_MAUL(20557, 0, 0, 81, 0, 0, 0, 0, 0, 0, 0, 79, 0, 0, 0),
    MAGIC_SHORTBOW(20558, 0, 0, 0, 0, 69, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    DRAGON_2H_SWORD(20559, -4, 92, 80, -4, 0, 0, 0, 0, 0, -1, 93, 0, 0, 0),
    MASTER_WAND(20560, 0, 0, 0, 20, 0, 0, 0, 0, 20, 0, 0, 0, 0, 0),
    ADAMANT_FULL_HELM(20561, 0, 0, 0, -6, -3, 19, 21, 16, -1, 19, 0, 0, 0, 0),
    MYSTIC_HAT(20562, 0, 0, 0, 4, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0),
    PROSELYTE_SALLET(20563, 0, 0, 0, -6, -3, 19, 21, 16, -1, 19, 0, 0, 0, 4),
    PROSELYTE_HAUBERK(20564, 0, 0, 0, -30, -15, 65, 63, 55, -6, 63, 0, 0, 0, 8),
    PROSELYTE_CUISSE(20565, 0, 0, 0, -21, -11, 33, 31, 29, -4, 31, 0, 0, 0, 6),
    RED_DHIDE_BODY(20566, 0, 0, 0, -15, 25, 26, 34, 36, 36, 45, 0, 0, 0, 0),
    RED_DHIDE_CHAPS(20567, 0, 0, 0, -10, 14, 15, 18, 22, 18, 20, 0, 0, 0, 0),
    SPLITBARK_HELM(20568, 0, 0, 0, 3, -2, 10, 9, 11, 3, 0, 0, 0, 0, 0),
    WARRIOR_HELM(20571, 0, 5, 0, -5, -5, 31, 33, 29, 0, 30, 0, 0, 0, 0),
    ARCHER_HELM(20572, -5, -5, -5, -5, 6, 6, 8, 10, 6, 6, 0, 0, 0, 0),
    FARSEER_HELM(20573, -5, -5, -5, 6, -5, 8, 10, 12, 6, 0, 0, 0, 0, 0),
    INFINITY_TOP(20574, 0, 0, 0, 22, 0, 0, 0, 0, 22, 0, 0, 0, 1, 0),
    INFINITY_BOTTOMS(20575, 0, 0, 0, 17, 0, 0, 0, 0, 17, 0, 0, 0, 1, 0),
    THIRD_AGE_ROBE_TOP(20576, 0, 0, 0, 24, 0, 0, 0, 0, 24, 0, 0, 0, 1, 0),
    THIRD_AGE_ROBE(20577, 0, 0, 0, 19, 0, 0, 0, 0, 19, 0, 0, 0, 1, 0),
    CLIMBING_BOOTS(20578, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 2, 0, 0, 0),
    MYSTIC_BOOTS(20579, 0, 0, 0, 3, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0),
    SNAKESKIN_BOOTS(20580, 0, 0, 0, -10, 3, 1, 1, 2, 1, 0, 0, 0, 0, 0),
    MITHRIL_GLOVES(20581, 6, 6, 6, 3, 6, 6, 6, 6, 3, 6, 6, 0, 0, 0),
    ADAMANT_GLOVES(20582, 7, 7, 7, 4, 7, 7, 7, 7, 4, 7, 7, 0, 0, 0),
    RUNE_GLOVES(20583, 8, 8, 8, 4, 8, 8, 8, 8, 4, 8, 8, 0, 0, 0),
    AMULET_OF_ACCURACY(20584, 4, 4, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    AMULET_OF_POWER(20585, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 0, 0, 1),
    AMULET_OF_GLORY(20586, 10, 10, 10, 10, 10, 3, 3, 3, 3, 3, 6, 0, 0, 3),
    STALE_BAGUETTE(20590, -100, -100, -50, 0, 0, 0, 0, 0, 0, 0, -10, 0, 0, 0),
    ARMADYL_GODSWORD(20593, 0, 132, 80, 0, 0, 0, 0, 0, 0, 0, 132, 0, 0, 8),
    ELDER_CHAOS_HOOD(20595, 0, 0, 0, 5, 0, 0, 0, 0, 4, 0, 0, 0, 1, 0),
    AHRIMS_ROBETOP(20598, 0, 0, 0, 30, -10, 52, 37, 63, 30, 0, 0, 0, 0, 0),
    AHRIMS_ROBESKIRT(20599, 0, 0, 0, 22, -7, 33, 30, 36, 22, 0, 0, 0, 0, 0),
    RUNE_ARROW(20600, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 49, 0, 0),
    RING_OF_SUFFERING_R(20655, 0, 0, 0, 0, 0, 10, 10, 10, 10, 10, 0, 0, 0, 2),
    RING_OF_SUFFERING_RI(20657, 0, 0, 0, 0, 0, 20, 20, 20, 20, 20, 0, 0, 0, 4),
    PYROMANCER_GARB(20704, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    PYROMANCER_ROBE(20706, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    PYROMANCER_HOOD(20708, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    PYROMANCER_BOOTS(20710, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    WARM_GLOVES(20712, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    TOME_OF_FIRE(20714, 0, 0, 0, 8, 0, 0, 0, 0, 8, 0, 0, 0, 0, 0),
    TOME_OF_FIRE_EMPTY(20716, 0, 0, 0, 8, 0, 0, 0, 0, 8, 0, 0, 0, 0, 0),
    BRUMA_TORCH(20720, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    LEAF_BLADED_BATTLEAXE(20727, -2, 72, 72, 0, 0, 0, 0, 0, 0, -1, 92, 0, 0, 0),
    MIST_BATTLESTAFF(20730, 7, -1, 28, 12, 0, 2, 3, 1, 12, 0, 35, 0, 0, 0),
    MYSTIC_MIST_STAFF(20733, 10, -1, 40, 14, 0, 2, 3, 1, 14, 0, 50, 0, 0, 0),
    DUST_BATTLESTAFF(20736, 7, -1, 28, 12, 0, 2, 3, 1, 12, 0, 35, 0, 0, 0),
    MYSTIC_DUST_STAFF(20739, 10, -1, 40, 14, 0, 2, 3, 1, 14, 0, 50, 0, 0, 0),
    HILL_GIANT_CLUB(20756, -4, 50, 65, -4, 0, 0, 0, 0, 0, -1, 70, 0, 0, 0),
    ARDOUGNE_MAX_CAPE(20760, 6, 0, 0, 6, 0, 6, 0, 0, 6, 0, 0, 0, 0, 6),
    ARDOUGNE_MAX_HOOD(20764, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    BANSHEE_MASK(20773, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    BANSHEE_TOP(20775, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    BANSHEE_ROBE(20777, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    HUNTING_KNIFE(20779, -100, -100, -100, 0, 0, 0, 0, 0, 0, 0, -100, 0, 0, 0),
    KILLERS_KNIFE(20781, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    DRAGON_CLAWS(20784, 41, 57, -4, 0, 0, 13, 26, 7, 0, 0, 56, 0, 0, 0),
    YEW_SHORTBOW(20401, 0, 0, 0, 0, 47, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    RUNE_SCIMITAR(20402, 7, 45, -2, 0, 0, 0, 1, 0, 0, 0, 44, 0, 0, 0),
    MAPLE_SHORTBOW(20403, 0, 0, 0, 0, 29, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    ABYSSAL_WHIP(20405, 0, 82, 0, 0, 0, 0, 0, 0, 0, 0, 82, 0, 0, 0),
    DRAGON_SCIMITAR(20406, 8, 67, -2, 0, 0, 0, 1, 0, 0, 0, 66, 0, 0, 0),
    DRAGON_DAGGER(20407, 40, 25, -4, 1, 0, 0, 0, 0, 1, 0, 40, 0, 0, 0),
    DARK_BOW(20408, 0, 0, 0, 0, 95, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    ADAMANT_PLATEBODY(20415, 0, 0, 0, -30, -15, 65, 63, 55, -6, 63, 0, 0, 0, 0),
    ADAMANT_PLATELEGS(20416, 0, 0, 0, -21, -11, 33, 31, 29, -4, 31, 0, 0, 0, 0),
    BLUE_DHIDE_BODY(20417, 0, 0, 0, -15, 20, 23, 30, 30, 26, 40, 0, 0, 0, 0),
    BLUE_DHIDE_CHAPS(20418, 0, 0, 0, -10, 11, 13, 16, 20, 14, 20, 0, 0, 0, 0),
    RUNE_PLATEBODY(20421, 0, 0, 0, -30, -15, 82, 80, 72, -6, 80, 0, 0, 0, 0),
    RUNE_PLATELEGS(20422, 0, 0, 0, -21, -11, 51, 49, 47, -4, 49, 0, 0, 0, 0),
    BLACK_DHIDE_BODY(20423, 0, 0, 0, -15, 30, 30, 38, 45, 45, 50, 0, 0, 0, 0),
    BLACK_DHIDE_CHAPS(20424, 0, 0, 0, -10, 17, 18, 20, 26, 23, 26, 0, 0, 0, 0),
    MYSTIC_ROBE_TOP(20425, 0, 0, 0, 20, 0, 0, 0, 0, 20, 0, 0, 0, 0, 0),
    MYSTIC_ROBE_BOTTOM(20426, 0, 0, 0, 15, 0, 0, 0, 0, 15, 0, 0, 0, 0, 0),
    DRAGON_CHAINBODY(20428, 0, 0, 0, -15, 0, 81, 93, 98, -3, 82, 0, 0, 0, 0),
    DRAGON_PLATELEGS(20429, 0, 0, 0, -21, -11, 68, 66, 63, -4, 65, 0, 0, 0, 0),
    ANCIENT_STAFF(20431, 10, -1, 40, 15, 0, 2, 3, 1, 15, 0, 50, 0, 0, -1),
    EVIL_CHICKEN_FEET(20433, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    EVIL_CHICKEN_WINGS(20436, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    EVIL_CHICKEN_HEAD(20439, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    EVIL_CHICKEN_LEGS(20442, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    ELDER_CHAOS_TOP(20517, 0, 0, 0, 10, 0, 0, 0, 0, 8, 0, 0, 0, 1, 0),
    ELDER_CHAOS_ROBE(20520, 0, 0, 0, 6, 0, 0, 0, 0, 6, 0, 0, 0, 1, 0),
    RUNE_BATTLEAXE(20552, -2, 48, 43, 0, 0, 0, 0, 0, 0, -1, 64, 0, 0, 0),
    BEGINNER_WAND(20553, 0, 0, 0, 5, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0),
    TOKTZ_XIL_AK(20554, 47, 38, -2, 0, 0, 2, 3, 0, 0, 0, 49, 0, 0, 0),
    RUNE_2H_SWORD(20555, -4, 69, 50, -4, 0, 0, 0, 0, 0, -1, 70, 0, 0, 0),
    APPRENTICE_WAND(20556, 0, 0, 0, 10, 0, 0, 0, 0, 10, 0, 0, 0, 0, 0),
    GHOSTLY_HOOD(27166, 0, 0, 0, 3, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0),
    GHOSTLY_ROBE(27167, 0, 0, 0, 5, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0),
    UNHOLY_BOOK(27191, 8, 8, 8, 8, 8, 0, 0, 0, 0, 0, 0, 0, 0, 5),
    MAGES_BOOK(23652, 0, 0, 0, 15, 0, 0, 0, 0, 15, 0, 0, 0, 2, 0),
    AHRIMS_STAFF(23653, 12, -1, 65, 15, 0, 3, 5, 2, 15, 0, 68, 0, 5, 0),
    DRAGON_KNIFE(27157, 0, 0, 0, 0, 28, 0, 0, 0, 0, 0, 0, 30, 0, 0),
    MYSTIC_ROBE_TOP_DARK(27158, 0, 0, 0, 20, 0, 0, 0, 0, 20, 0, 0, 0, 0, 0),
    MYSTIC_ROBE_BOTTOM_DARK(27159, 0, 0, 0, 15, 0, 0, 0, 0, 15, 0, 0, 0, 0, 0),
    MYSTIC_ROBE_TOP_LIGHT(27160, 0, 0, 0, 20, 0, 0, 0, 0, 20, 0, 0, 0, 0, 0),
    MYSTIC_ROBE_BOTTOM_LIGHT(27161, 0, 0, 0, 15, 0, 0, 0, 0, 15, 0, 0, 0, 0, 0),
    WIZARD_BOOTS(27162, 0, 0, 0, 4, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0),
    GUTHIX_HALO(27163, 0, 0, 0, 0, 0, 11, 12, 10, 11, -1, 0, 0, 0, 3),
    ZAMORAK_HALO(27164, 0, 0, 0, 0, 0, 11, 12, 10, 11, -1, 0, 0, 0, 3),
    SARADOMIN_HALO(27165, 0, 0, 0, 0, 0, 11, 12, 10, 11, -1, 0, 0, 0, 3),
    BERSERKER_HELM(27169, 0, 0, 0, -5, -5, 31, 29, 33, 0, 30, 3, 0, 0, 0),
    INFINITY_BOOTS(27170, 0, 0, 0, 5, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0),
    TORMENTED_BRACELET(27171, 0, 0, 0, 10, 0, 0, 0, 0, 0, 0, 0, 0, 5, 2),
    NECKLACE_OF_ANGUISH(27172, 0, 0, 0, 0, 15, 0, 0, 0, 0, 0, 0, 5, 0, 2),
    AMULET_OF_TORTURE(27173, 15, 15, 15, 0, 0, 0, 0, 0, 0, 0, 10, 0, 0, 2),
    FREMENNIK_KILT(27177, 0, 0, 0, -21, -7, 11, 10, 10, -4, 10, 1, 0, 0, 0),
    SPIKED_MANACLES(27178, 0, 0, 0, -3, -1, 0, 0, 0, -4, 0, 4, 0, 0, 0),
    RANGERS_TUNIC(27179, 0, 0, 0, -15, 15, 6, 9, 12, 6, 6, 0, 0, 0, 0),
    GUTHIX_CHAPS(27180, 0, 0, 0, -10, 17, 31, 25, 33, 28, 31, 0, 0, 0, 1),
    ZAMORAK_CHAPS(27181, 0, 0, 0, -10, 17, 31, 25, 33, 28, 31, 0, 0, 0, 1),
    SARADOMIN_CHAPS(27182, 0, 0, 0, -10, 17, 31, 25, 33, 28, 31, 0, 0, 0, 1),
    THIRD_AGE_MAGE_HAT(27183, 0, 0, 0, 8, 0, 0, 0, 0, 8, 0, 0, 0, 1, 0),
    ANCIENT_GODSWORD(27184, 0, 132, 80, 0, 0, 0, 0, 0, 0, 0, 132, 0, 0, 8),
    RUNE_DEFENDER(27185, 20, 19, 18, -3, -2, 20, 19, 18, -3, -2, 5, 0, 0, 0),
    ZARYTE_CROSSBOW(27186, 0, 0, 0, 0, 110, 14, 14, 12, 15, 16, 0, 0, 0, 1),
    BOW_OF_FAERDHINEN(27187, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    LIGHT_BALLISTA(27188, 0, 0, 0, 0, 110, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    VERACS_FLAIL(27189, 68, -2, 82, 0, 0, 0, 0, 0, 0, 0, 72, 0, 0, 6),
    OPAL_DRAGON_BOLTS_E(27192, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 122, 0, 0),
    ANCESTRAL_ROBE_TOP(27193, 0, 0, 0, 35, -8, 42, 31, 51, 28, 0, 0, 0, 3, 0),
    ANCESTRAL_ROBE_BOTTOM(27194, 0, 0, 0, 26, -7, 27, 24, 30, 20, 0, 0, 0, 3, 0),
    INQUISITORS_GREAT_HELM(27195, -2, -2, 8, -5, -5, 19, 10, 21, 0, 12, 4, 0, 0, 1),
    INQUISITORS_HAUBERK(27196, -3, -3, 12, -11, -10, 67, 55, 71, 0, 35, 4, 0, 0, 2),
    INQUISITORS_PLATESKIRT(27197, -3, -3, 12, -9, -5, 42, 30, 49, 0, 22, 2, 0, 0, 2),
    INQUISITORS_MACE(27198, 52, -4, 95, 0, 0, 0, 0, 0, 0, 0, 89, 0, 0, 2),
    THIRD_AGE_RANGE_TOP(27199, 0, 0, 0, -15, 30, 55, 47, 60, 60, 55, 0, 0, 0, 0),
    THIRD_AGE_RANGE_LEGS(27200, 0, 0, 0, -10, 17, 31, 25, 33, 30, 31, 0, 0, 0, 0),
    THIRD_AGE_RANGE_COIF(27201, 0, 0, 0, -2, 9, 4, 7, 10, 5, 8, 0, 0, 0, 0),
    ;

    final int itemId;
    final int stabBonus;
    final int slashBonus;
    final int crushBonus;
    final int magicBonus;
    final int rangeBonus;

    final int stabDef;
    final int slashDef;
    final int crushDef;
    final int magicDef;
    final int rangeDef;

    final int strBonus;
    final int rangeStrBonus;
    final int magicStrBonus;
    final int prayerBonusk;

    LMSEquipmentItemData(int itemId, int stabBonus, int slashBonus, int crushBonus, int magicBonus, int rangeBonus, int stabDef, int slashDef, int crushDef, int magicDef, int rangeDef, int strBonus, int rangeStrBonus, int magicStrBonus, int prayerBonusk) {
        this.itemId = itemId;
        this.stabBonus = stabBonus;
        this.slashBonus = slashBonus;
        this.crushBonus = crushBonus;
        this.magicBonus = magicBonus;
        this.rangeBonus = rangeBonus;
        this.stabDef = stabDef;
        this.slashDef = slashDef;
        this.crushDef = crushDef;
        this.magicDef = magicDef;
        this.rangeDef = rangeDef;
        this.strBonus = strBonus;
        this.rangeStrBonus = rangeStrBonus;
        this.magicStrBonus = magicStrBonus;
        this.prayerBonusk = prayerBonusk;
    }

    private static Map<Integer, LMSEquipmentItemData> idMap;

    static {
        idMap = new HashMap<>();
        for (LMSEquipmentItemData value : values()) {
            idMap.put(value.itemId, value);
        }
    }

    public static int[] getPlayerStats(Player p) {
        if (p == null) return new int[0];
        int[] s = new int[14];
        for (Item i : p.getEquipment()) {
            if (i == null) continue;
            LMSEquipmentItemData data = idMap.get(i.getId());
            if (data == null) {
                Logger.log(Color.red, "Failed to find data on item " + i.getId());
                continue;
            }

            s[0] += data.stabBonus;
            s[1] += data.slashBonus;
            s[2] += data.crushBonus;
            s[3] += data.magicBonus;
            s[4] += data.rangeBonus;

            s[5] += data.stabDef;
            s[6] += data.slashDef;
            s[7] += data.crushDef;
            s[8] += data.magicDef;
            s[9] += data.rangeDef;

            s[10] += data.strBonus;
            s[11] += data.rangeStrBonus;
            s[12] += data.magicStrBonus;
            s[13] += data.prayerBonusk;
        }
        return s;
    }
}
