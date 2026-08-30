package org.dreambot.behaviour.wilddiary;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.MiniQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.behaviour.magearenaone.MageArenaOneBranch;
import org.dreambot.behaviour.method.chaoselemental.ChaosElemental;
import org.dreambot.behaviour.method.chaosfanatic.ChaosFanatic;
import org.dreambot.behaviour.method.crazyarch.FightCrazyArch;
import org.dreambot.behaviour.method.lavadragons.*;
import org.dreambot.behaviour.method.orbers.AirOrb;
import org.dreambot.behaviour.method.scorpia.FightScorpia;
import org.dreambot.behaviour.quests.deathplateau.DeathPlateau;
import org.dreambot.behaviour.quests.doricsquest.DoricsQuest;
import org.dreambot.behaviour.quests.fishingcontest.FishingBranch;
import org.dreambot.behaviour.quests.fishingcontest.FishingFractal;
import org.dreambot.behaviour.quests.theknightssword.TheKnightsSword;
import org.dreambot.behaviour.quests.trollstronghold.TrollStronghold;
import org.dreambot.behaviour.training.agility.AgilityBranch;
import org.dreambot.behaviour.training.herblore.HerbloreBranch;
import org.dreambot.behaviour.training.hunter.HunterBranch;
import org.dreambot.behaviour.training.hunter.sallys.GenericSalamander;
import org.dreambot.behaviour.training.magic.MagicBranch;
import org.dreambot.behaviour.training.mining.MixedMining;
import org.dreambot.behaviour.training.slayer.SlayerBranch;
import org.dreambot.behaviour.training.smithing.SmithingBranch;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.BuyFromShopFractal;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.generic.UseOnFractal;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.fractals.util.VarplayerRequirement;
import org.dreambot.scriptdata.ChaosElementalSettings;
import org.dreambot.scriptdata.ChaosFanaticSettings;
import org.dreambot.scriptdata.CrazySettings;
import org.dreambot.scriptdata.LavaDragonSettings;

import java.util.function.Supplier;

public class HardWildernessDiary extends Fractal {
    static VarplayerRequirement hasntDoneHardDiary = new VarplayerRequirement(1190, false, 10);

    VarplayerRequirement notGodSpells = new VarplayerRequirement(1192, false, 25);
    VarplayerRequirement notAirOrb = new VarplayerRequirement(1192, false, 26);
    VarplayerRequirement notBlackSally = new VarplayerRequirement(1192, false, 27);
    VarplayerRequirement notAddyScim = new VarplayerRequirement(1192, false, 28);
    VarplayerRequirement notLavaDrag = new VarplayerRequirement(1192, false, 29);
    VarplayerRequirement notChaosEle = new VarplayerRequirement(1192, false, 30);
    VarplayerRequirement notThreeBosses = new VarplayerRequirement(1192, false, 31);
    VarplayerRequirement notTrollWildy = new VarplayerRequirement(1193, false, 0);
    VarplayerRequirement notSprirtualWarrior = new VarplayerRequirement(1193, false, 1);
    VarplayerRequirement notRawLavaEel = new VarplayerRequirement(1193, false, 2);

    Area blackSal = new Area(
            new Tile(3291, 3678, 0),
            new Tile(3299, 3680, 0),
            new Tile(3302, 3672, 0),
            new Tile(3300, 3661, 0),
            new Tile(3291, 3668, 0));
    Area wildernessResourceArea = new Area(3187, 3941, 3191, 3937);

    public static final Area TROLLHEIM_SHORTCUT = new Area(2912, 3674, 2916, 3671);

    public static final Area RASOLO_AREA = new Area(2529, 3436, 2541, 3416);

    public static final Area LAVA_EEL_SPOT = new Area(3066, 3842, 3073, 3836);

    public HardWildernessDiary() {
        super(() -> hasntDoneHardDiary.check());
        init();
    }

    public HardWildernessDiary(Supplier<Boolean> acceptCondition) {
        super(() -> acceptCondition.get() && hasntDoneHardDiary.check());
        init();
    }

    void init() {
        setSimpleName("Hard diary");
        addChildren(
                new EasyWildernessDiary(),
                new MediumWildernessDiary(),
                new MagicBranch(() -> Skills.getRealLevel(Skill.MAGIC) < 66).setSimpleName("Magic till 66"),
                new HunterBranch(() -> Skills.getRealLevel(Skill.HUNTER) < 67).setSimpleName("Hunter till 67"),
                new DoricsQuest().setSimpleName("Dorics quesst"),
                new MixedMining(() -> Skills.getRealLevel(Skill.MINING) < 10).setSimpleName("10 mining"),
                new TheKnightsSword().setSimpleName("Knights sword"),
                new SmithingBranch(() -> Skills.getRealLevel(Skill.SMITHING) < 75).setSimpleName("Smtihing till 75"),
                new AgilityBranch(() -> Skills.getRealLevel(Skill.AGILITY) < 64).setSimpleName("Agility till 64"),
                new SlayerBranch(() -> Skills.getRealLevel(Skill.SLAYER) < 68).setSimpleName("Slayer till 68"),
                new FishingBranch(() -> Skills.getRealLevel(Skill.FISHING) < 53).setSimpleName("Fishing till 53"),
                new HerbloreBranch(() -> Skills.getRealLevel(Skill.HERBLORE) < 25, false).setSimpleName("Herblore to 25"),
                new TrollStronghold().setSimpleName("Troll stronghold"),
                new DeathPlateau().setSimpleName("Death plateau"),

                new MageArenaOneBranch(() -> !MiniQuest.MAGE_ARENA_I.isFinished()),

                new Fractal(notAirOrb::check).addChildren(
                        new AirOrb().setSimpleName("Charge air orb")
                ),

                new GenericSalamander(() -> notBlackSally.check(), blackSal)
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.ROPE, 1, 12)
                                .addItem(ItemID.SMALL_FISHING_NET, 1, 12)
                                .addItem(ItemID.KNIFE, 1)
                        )

                        .setSimpleName("Blacks"),

                new KillLavaDragons(() -> notLavaDrag.check(),
                        new EquipmentLoadout()
                                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                                .setRefill(10)
                                .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_WATER),
                        new InventoryLoadout()
                                .addItem(ItemID.AIR_RUNE, 2500)
                                .addItem(ItemID.CHAOS_RUNE, 300)

                                .addItem(ItemID.KNIFE)

                                .addItem(ItemID.JUG_OF_WINE, 12, 12)
                                .setRefill(200),
                        LavaDragonAntiPKStrategy.SKULLED_IN_COMBAT_RANGE,
                        false,
                        1000,
                        1,
                        true
                )
                        .setSimpleName("Kill Lava dragon"),

                new TalkToFractal(() -> notAddyScim.check(), wildernessResourceArea, () -> GameObjects.closest("Anvil"))
                        .setInteraction("Smith")
                        .setDialogueOptions("scim", "Yes", "Okay")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.HAMMER)
                                .addItem(ItemID.ADAMANTITE_BAR, 2)
                                .addItem(ItemID.COINS_995, 7500)
                                .setStrict(true)
                                .setEnabledCondition(() -> !Combat.isInWild())
                        )
                        .setSimpleName("make adamant scimitar"),

                new TalkToFractal(() -> notTrollWildy.check(), TROLLHEIM_SHORTCUT,
                        () -> GameObjects.closest(x -> x.getName().equals("Rocks") && x.hasAction("Climb")))
                        .setInteraction("Climb")
                        .setSimpleName("Shortcut"),

                // todo spiritual warrior

                // Get oily rod for fishing lava eel
                new Fractal(() -> !OwnedItems.contains(ItemID.OILY_FISHING_ROD))
                        .addChildren(
                                // mix blamish oil with fishing rod
                                new UseOnFractal(() -> OwnedItems.contains(ItemID.BLAMISH_OIL),
                                        () -> Inventory.get(ItemID.FISHING_ROD),
                                        () -> Inventory.get(ItemID.BLAMISH_OIL))
                                        .setInventoryLoadout(new InventoryLoadout()
                                                .addItem(ItemID.FISHING_ROD)
                                                .addItem(ItemID.BLAMISH_OIL)
                                        )
                                        .setSimpleName("Oil up your rod"),

                                // mix snail slime with harralander unf
                                new UseOnFractal(() -> OwnedItems.contains(ItemID.BLAMISH_SNAIL_SLIME),
                                        () -> Inventory.get(ItemID.HARRALANDER_POTION_UNF),
                                        () -> Inventory.get(ItemID.BLAMISH_SNAIL_SLIME))
                                        .setInventoryLoadout(new InventoryLoadout()
                                                .addItem(ItemID.HARRALANDER_POTION_UNF)
                                                .addItem(ItemID.BLAMISH_SNAIL_SLIME)
                                        )
                                        .setSimpleName("Get blamish oil"),

                                // mix sample bottle and fat snail
                                new UseOnFractal(() -> OwnedItems.contains(ItemID.SAMPLE_BOTTLE),
                                        () -> Inventory.get(ItemID.SAMPLE_BOTTLE),
                                        () -> Inventory.get(ItemID.FAT_SNAIL))
                                        .setInventoryLoadout(new InventoryLoadout()
                                                .addItem(ItemID.FAT_SNAIL)
                                                .addItem(ItemID.SAMPLE_BOTTLE)
                                        )
                                        .setSimpleName("Get snail slime"),

                                new BuyFromShopFractal(() -> !OwnedItems.contains(ItemID.SAMPLE_BOTTLE),
                                        () -> NPCs.closest("Rasolo"),
                                        RASOLO_AREA, ItemID.SAMPLE_BOTTLE)
                                        .setSimpleName("Get sample bottles")
                                        .setInventoryLoadout(new InventoryLoadout()
                                                .addItem(ItemID.COINS_995, 2000)
                                                .addItem(ItemVariants.SKILLS_NECKLACE)
                                                .setStrict(true)
                                        )
                        ),
                new FishingFractal(() -> !notRawLavaEel.check(), LAVA_EEL_SPOT, () -> NPCs.closest("Fishing spot"))
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.OILY_FISHING_ROD)
                                .addItem(ItemID.KNIFE)
                                .addItem(ItemVariants.BURNING_AMULET).setEnabledCondition(Combat::isInWild)
                                .setStrict(true)
                        )
                        .setSimpleName("Lava eel"),

                new ChaosElemental(notChaosEle::check, new ChaosElementalSettings()),

                new Fractal(notThreeBosses::check)
                        .addChildren(
                                // todo fix conditions
                                new ChaosFanatic(() -> true, new ChaosFanaticSettings()),
                                new FightScorpia(() -> true).setSimpleName("Fight scorpia"),
                                new FightCrazyArch(() -> true, new CrazySettings())
                        )

                // todo use a god spell on another person
        );
    }
}
