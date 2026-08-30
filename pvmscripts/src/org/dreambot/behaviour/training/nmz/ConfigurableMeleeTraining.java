package org.dreambot.behaviour.training.nmz;

import com.google.common.collect.ImmutableMap;
import org.dreambot.LocalSDNOwnershipCache;
import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.GetOff330;
import org.dreambot.behaviour.method.gemstone.GemstoneCrab;
import org.dreambot.behaviour.method.gemstone.GemstoneCrabMeleeLoadout;
import org.dreambot.behaviour.method.scurrius.GoToScurrius;
import org.dreambot.behaviour.method.scurrius.MakeBoneWeapon;
import org.dreambot.behaviour.method.scurrius.ScurriusBranch;
import org.dreambot.behaviour.method.scurrius.ScurriusMode;
import org.dreambot.behaviour.misc.MoonlightPotionReup;
import org.dreambot.behaviour.misc.SandCrabs;
import org.dreambot.behaviour.quests.LostCity;
import org.dreambot.behaviour.quests.VampyreSlayer;
import org.dreambot.behaviour.quests.ascentofarceuus.AscentOfArceuus;
import org.dreambot.behaviour.quests.childrenofthesun.ChildrenOfTheSun;
import org.dreambot.behaviour.quests.fightarena.FightArena;
import org.dreambot.behaviour.quests.fishingcontest.FishingFractal;
import org.dreambot.behaviour.quests.impcatcher.ImpCatcher;
import org.dreambot.behaviour.quests.perilousmoon.PerilousMoon;
import org.dreambot.behaviour.quests.rfd.GetRockCake;
import org.dreambot.behaviour.quests.twilightpromise.TwilightPromise;
import org.dreambot.behaviour.quests.witchshouse.WitchsHouse;
import org.dreambot.behaviour.training.agility.AgilityBranch;
import org.dreambot.behaviour.training.construction.ConstructionBranch;
import org.dreambot.behaviour.training.firemaking.BurnLogs;
import org.dreambot.behaviour.training.herblore.HerbloreBranch;
import org.dreambot.behaviour.training.hunter.HunterBranch;
import org.dreambot.behaviour.training.magic.EnchantRecoils;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.behaviour.training.runecraft.RuneCraftingBranch;
import org.dreambot.behaviour.training.slayer.Helper;
import org.dreambot.behaviour.training.slayer.SlayerBranch;
import org.dreambot.behaviour.training.slayer.behaviour.StandardCombat;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.fractals.util.PVMUtil;
import org.dreambot.fractals.util.RatConfigureQuickPrayers;
import org.dreambot.settings.fractalsettings.ConfigurableFractal;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.webintegration.WebLoadoutLoader;

import java.util.Arrays;
import java.util.function.Supplier;

public class ConfigurableMeleeTraining extends Fractal implements ConfigurableFractal<ConfigurableCombatSettings> {
    public ConfigurableMeleeTraining setStyleSupplier(Supplier<CombatStyle> styleSupplier) {
        // todo make this fully traverse the tree
        this.children.stream().filter(x -> x instanceof SandCrabs)
                .map(x -> (SandCrabs) x)
                .forEach(x -> x.setStyleSupplier(styleSupplier));
        return this;
    }

    final Area SHRIMP_AREA = new Area(3240, 3159, 3246, 3141);
    final Area NAGUA_AREA = new Area(1370, 9570, 1382, 9553, 0);

    final EquipmentLoadout naguaEquipment = new EquipmentLoadout()

            .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) < 60)
            .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) < 60)

            .addItem(EquipmentSlot.CHEST, ItemID.OBSIDIAN_PLATEBODY)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) >= 60)
            .addItem(EquipmentSlot.LEGS, ItemID.OBSIDIAN_PLATELEGS)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) >= 60)
            .addItem(EquipmentSlot.SHIELD, ItemID.TOKTZKETXIL_OBSIDIAN_SHIELD)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) >= 60 && !(Skills.getRealLevel(Skill.ATTACK) >= 55))
            // todo 60 def req for these

            // dragon sword or rune sword or mithril
            .addItem(EquipmentSlot.WEAPON, ItemID.IRON_SCIMITAR)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.ATTACK) < 20)
            .setRefill(5)
            .addItem(EquipmentSlot.WEAPON, ItemID.MITHRIL_SCIMITAR)
            .setEnabledCondition(() -> Helper.skillBetween(Skill.ATTACK, 30, 20))
            .setRefill(5)
            .addItem(EquipmentSlot.WEAPON, ItemID.ADAMANT_SCIMITAR)
            .setEnabledCondition(() -> Helper.skillBetween(Skill.ATTACK, 40, 30))
            .setRefill(5)
            .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_SCIMITAR)
            .setEnabledCondition(() -> Helper.skillBetween(Skill.ATTACK, 55, 40))
            .addItem(EquipmentSlot.WEAPON, ItemID.SULPHUR_BLADES)
            .setEnabledCondition(() -> Helper.skillBetween(Skill.ATTACK, 100, 55))
            .addItem(EquipmentSlot.WEAPON, ItemID.ABYSSAL_WHIP)
            .setEnabledCondition(() -> Helper.skillBetween(Skill.ATTACK, 100, 100))
            // glory
            .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
            .setRefill(5)
            .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
            .setRefill(5)
            .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
            .setEnabledCondition(() -> Combat.getCombatLevel() >= 85)
            .setRefill(5)

            .addItem(EquipmentSlot.HAT, ItemID.OBSIDIAN_HELMET)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) >= 60)
            .addItem(EquipmentSlot.FEET, ItemID.DRAGON_BOOTS)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) >= 60);

    public ConfigurableMeleeTraining(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Melee training");

        EquipmentLoadout parsedCustomLoadout = null;
        if (getSettings().gemstoneCustomLoadout != null && !getSettings().gemstoneCustomLoadout.isEmpty()) {
            try {
                parsedCustomLoadout = WebLoadoutLoader.parseEquipment(getSettings().gemstoneCustomLoadout);
            } catch (Exception e) {
                log("Failed to parse gemstone custom");
            }
        }
        addChildren(
                // PRE REQ QUESTS FOR EARLY EXP
                new Fractal(() -> !PaidQuest.WITCHS_HOUSE.isFinished() && getSettings().witchsHouse).setSimpleName("Quest")
                        .addChildren(
                                new WitchsHouse().setSimpleName("Witch's house")
                        ),
                new Fractal(() -> !FreeQuest.VAMPIRE_SLAYER.isFinished() && getSettings().vampyreSlayer).setSimpleName("Quest")
                        .addChildren(
                                new VampyreSlayer().setSimpleName("Vampyre slayer")
                        ),

                new Fractal(() -> !PaidQuest.FIGHT_ARENA.isFinished() && getSettings().fightArena).setSimpleName("Quest")
                        .addChildren(
                                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < 43)
                                        .setSimpleName("Prayer 43"),
                                new GetOff330(GetOff330.MEMBERS_WORLD_FILTER).setSimpleName("Hop off 330"),
                                new FightArena().setSimpleName("Fight arena")
                        ),
                // todo add waterfall quest

                new GetOff330(GetOff330.MEMBERS_WORLD_FILTER).setSimpleName("Hop off 330"),
                // GEMSTONE
                new Fractal(() -> getSettings().trainingMode == ConfigurableCombatMode.GEMSTONE_CRAB && ConfigurableCombatMode.GEMSTONE_CRAB.isOwned())
                        .setSimpleName("Gemstone")
                        .addChildren(
                                new ChildrenOfTheSun().setSimpleName("COS"),
                                // kill gemstone
                                new GetRockCake(() -> getSettings().gemstoneCrabMeleeLoadout == GemstoneCrabMeleeLoadout.DHAROKS && GemstoneCrabMeleeLoadout.unlockedDharoks()),
                                GemstoneCrab.getMelee(new ImmutableMap.Builder<Skill, Integer>()
                                                .put(Skill.ATTACK, getSettings().attackTarget)
                                                .put(Skill.DEFENCE, getSettings().defenceTarget)
                                                .put(Skill.STRENGTH, getSettings().strengthTarget)
                                                .build()
                                        )
                                        .setEquipmentLoadout(parsedCustomLoadout != null ? parsedCustomLoadout : getSettings().gemstoneCrabMeleeLoadout.equipmentLoadout)
                                        .setInventoryLoadout(getSettings().gemstoneCrabMeleeLoadout.inventoryLoadout)
                                        .setSimpleName("Gemstone crab")
                        ),

                // SCURRIUS MODE
                new Fractal(() -> getSettings().trainingMode == ConfigurableCombatMode.SCURRIUS && ConfigurableCombatMode.SCURRIUS.isOwned())
                        .addChildren(

                                SandCrabs.getMelee(() -> !reachedBase(50, Skill.DEFENCE, Skill.ATTACK, Skill.STRENGTH))
                                        .setSimpleName("Base 50s @ Crabs"),
                                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < 43)
                                        .setSimpleName("Prayer training"),
                                new RatConfigureQuickPrayers(() -> new Prayer[]{Prayer.PROTECT_FROM_MELEE, PVMUtil.getBestMeleePray()})
                                        .setSimpleName("Melee q p"),

                                new MakeBoneWeapon(() -> OwnedItems.contains(ItemID.SCURRIUS_SPINE) && !OwnedItems.contains(ItemID.BONE_MACE),
                                        MakeBoneWeapon.BONE_MACE_LOADOUT)
                                        .setSimpleName("Make bone staff"),

                                new GoToScurrius(() -> !Client.isDynamicRegion(), ScurriusMode.MELEE),
                                new ScurriusBranch(() -> true, ScurriusMode.MELEE, false)
                                        .setFlick(getSettings().flicking)
                                        .setStyleSupplier(() -> {
                                            int atk = Skills.getRealLevel(Skill.ATTACK);
                                            int str = Skills.getRealLevel(Skill.STRENGTH);
                                            int def = Skills.getRealLevel(Skill.DEFENCE);
                                            if (str < getSettings().strengthTarget && str <= Math.max(atk, def))
                                                return CombatStyle.STRENGTH;
                                            if (atk < getSettings().attackTarget && atk <= def)
                                                return CombatStyle.ATTACK;
                                            return def < getSettings().defenceTarget ? CombatStyle.DEFENCE : (atk < getSettings().attackTarget ? CombatStyle.ATTACK : CombatStyle.STRENGTH);
                                        })
                                        .setSimpleName("Scurrius")
                        ).setSimpleName("Scurrius"),

                // SANDCRAB MODE
                SandCrabs.getMelee(() -> getSettings().trainingMode == ConfigurableCombatMode.SANDCRABS),

                // SULPHUR NAGUAS
                new Fractal(() -> getSettings().trainingMode == ConfigurableCombatMode.SULPHUR_NAGUAS && ConfigurableCombatMode.SULPHUR_NAGUAS.isOwned())
                        .addChildren(
                                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < 43)
                                        .setSimpleName("Prayer 43 req"),
                                new GetOff330(GetOff330.MEMBERS_WORLD_FILTER).setSimpleName("Get off 330"),
                                new Fractal(() -> !PaidQuest.TWILIGHTS_PROMISE.isFinished() && Skills.getRealLevel(Skill.MAGIC) < 27).addChildren(
                                        new ImpCatcher().setSimpleName("Impcatcher"),
                                        new EnchantRecoils().setSimpleName("Enchant Recoils until 27")
                                ).setSimpleName("Training some mage for twilight promise"),
                                new HerbloreBranch(() -> Skills.getRealLevel(Skill.HERBLORE) < 38, false)
                                        .setPrependLogic(() -> {
                                            if (Client.isDynamicRegion()) {
                                                Magic.castSpell(Normal.HOME_TELEPORT);
                                                Sleep.sleepUntil(() -> !Client.isDynamicRegion(), 34_000);
                                                return true;
                                            }
                                            return false;
                                        })
                                        .setSimpleName("Herblore training (min 38)"),

                                new BurnLogs(() -> Skills.getRealLevel(Skill.FIREMAKING) < 4, 7, ItemID.LOGS)
                                        .setSimpleName("Burn logs need it for slayer"),
                                new SlayerBranch(() -> Skills.getRealLevel(Skill.SLAYER) < 48)
                                        .setPrependLogic(() -> {
                                            if (!Combat.isAutoRetaliateOn()) {
                                                if (Widgets.isOpen()) Widgets.closeAll();
                                                Combat.toggleAutoRetaliate(true);
                                            }

                                            return false;
                                        })
                                        .setSimpleName("Slayer until naguas are unlocked"),

                                new HunterBranch(() -> Skills.getRealLevel(Skill.HUNTER) < 20)
                                        .setSimpleName("Hunter training to 20"),

                                new RuneCraftingBranch(() -> Skills.getRealLevel(Skill.RUNECRAFTING) < 20)
                                        .setSimpleName("Rune crafting"),

                                new FishingFractal(() -> Skills.getRealLevel(Skill.FISHING) < 20,
                                        SHRIMP_AREA, () -> NPCs.closest(n -> n.hasAction("Net") && SHRIMP_AREA.contains(n)))
                                        .setShouldBank(false)
                                        .setInteraction("Net")
                                        .setSimpleName("Shrimp until lvl 20")
                                        .setInventoryLoadout(
                                                new InventoryLoadout()
                                                        .strictIgnore(ItemID.RAW_SHRIMPS, ItemID.RAW_ANCHOVIES)
                                                        .addItem(FishingFractal.SMALL_FISHING_NET, 1)
                                                        .setStrict(true)
                                        ),

                                new ConstructionBranch(() -> Skills.getRealLevel(Skill.CONSTRUCTION) < 10)
                                        .setSimpleName("Construction"),

                                new ChildrenOfTheSun().setSimpleName("Children of the sun"),
                                new TwilightPromise().setSimpleName("Twilight promise"),
                                new Fractal(() -> PaidQuest.PERILOUS_MOONS.getConfigValue() < 17).setSimpleName("Unlock moonlight pots")
                                        .addChildren(
                                                new PerilousMoon().setSimpleName("Perilous moon")
                                        ),

                                new MoonlightPotionReup().setSimpleName("Reup pots"),

                                new StandardCombat(NAGUA_AREA, "Sulphur Nagua", ItemID.SHARK)
                                        .setEatPercentThreshold(1)
                                        .setStyleSupplier(() -> {
                                            if (Equipment.contains(ItemID.ABYSSAL_WHIP)) return CombatStyle.SHARED;
                                            int atk = Skills.getRealLevel(Skill.ATTACK);
                                            int str = Skills.getRealLevel(Skill.STRENGTH);
                                            int def = Skills.getRealLevel(Skill.DEFENCE);
                                            if (str <= Math.min(atk, def)) return CombatStyle.STRENGTH;
                                            if (atk <= def) return CombatStyle.ATTACK;
                                            return CombatStyle.DEFENCE;
                                        })
                                        .setLootFilter(x -> !x.getName().contains("uncut")
                                                && LivePrices.get(x.getId()) > 1200
                                                || x.getItem().isNoted()
                                                || x.getItem().isStackable()
                                        )
                                        .setOverhead(Prayer.PROTECT_FROM_MELEE)
                                        .setEquipmentLoadout(naguaEquipment)
                                        .setInventoryLoadout(new InventoryLoadout()
                                                .addItem(ItemID.PESTLE_AND_MORTAR)
                                                .addItem(ItemID.VIAL, 10)
                                                .setRefill(100)
                                                .setEnabledCondition(() -> !Inventory.contains(ItemID.VIAL_OF_WATER) && ItemVariants.MOONLIGHT_POTION.getItem() == null)
                                                .setStrictSupplier(() -> BankLocation.GRAND_EXCHANGE.distance(Players.getLocal().getTile()) < 50 || Inventory.isFull())
                                        )
                                        .setAcceptCondition(() -> true)
                                        .setPrependLogic(() -> {
                                            // todo 11000 animation = prayer drain attack
                                            if (Inventory.contains(ItemID.MOONLIGHT_GRUB, ItemID.MOONLIGHT_GRUB_PASTE)) {
                                                Inventory.dropAll(ItemID.MOONLIGHT_GRUB, ItemID.MOONLIGHT_GRUB_PASTE);
                                            }

                                            if (Skills.getBoostedLevel(Skill.PRAYER) < 10 && NAGUA_AREA.contains(Players.getLocal())) {
                                                Item moonlightPot = ItemVariants.MOONLIGHT_POTION.getItem();
                                                if (moonlightPot == null) {
                                                    if (MoonlightPotionReup.WHOLE_MOONLIGHT_DUNGEON.contains(Players.getLocal()))
                                                        Logger.warn("No moonlight pot???");
                                                    // todo this shouldnt happen so probably log all state here.
                                                    return false;
                                                }

                                                moonlightPot.interact("Drink");
                                                return true;
                                            }

                                            return false;
                                        }).setSimpleName("Kill Naguas")


                        ).setSimpleName("Sulphur naguas"),

                // get base 60 combats
                new Fractal(() -> getSettings().trainingMode == ConfigurableCombatMode.NMZ).addChildren(
                        new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < 43)
                                .setSimpleName("Prayer 43"),
                        SandCrabs.getMelee(() -> !reachedBase(60, Skill.DEFENCE, Skill.ATTACK, Skill.STRENGTH)).setSimpleName("Base 60s @ Crabs"),
                        new WitchsHouse().setSimpleName("Witchs house"),
                        new VampyreSlayer().setSimpleName("NMZ vampire slayer"),
                        new FightArena().setSimpleName("NMZ fight arena"),
                        new LostCity().setSimpleName("Lost city"),
                        new AgilityBranch(() -> Skills.getRealLevel(Skill.AGILITY) < 15).setSimpleName("15 agil req"),
                        new AscentOfArceuus(),
                        new GetRockCake().setSimpleName("Rock cake"),
                        new NightmareZone(() -> true, getSettings().nmzCustom.getLoadout())
                                .setAtkMax(getSettings().attackTarget)
                                .setDefMax(getSettings().defenceTarget)
                                .setStrMax(getSettings().strengthTarget)
                                .setSimpleName("Nightmare zone")

                ).setSimpleName("NMZ")
        );
    }

    public static boolean reachedBase(int base, Skill... skills) {
        return Arrays.stream(skills).allMatch(x -> x.getLevel() >= base);
    }

    @Override
    public ConfigurableCombatSettings getSettings() {
        return SettingsRepository.getSetting(settingName(), new ConfigurableCombatSettings());
    }

    @Override
    public String settingName() {
        return "MeleeTraining";
    }
}
