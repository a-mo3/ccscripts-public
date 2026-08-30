package org.dreambot.behaviour.training.magic;

import lombok.experimental.Accessors;
import org.dreambot.LocalSDNOwnershipCache;
import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.magic.Spell;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.behaviour.GetOff330;
import org.dreambot.behaviour.method.gemstone.GemstoneCrab;
import org.dreambot.behaviour.method.mta.UnlockMTA;
import org.dreambot.behaviour.method.mta.enchant.EnchantRoomMTA;
import org.dreambot.behaviour.method.scurrius.GoToScurrius;
import org.dreambot.behaviour.method.scurrius.MakeBoneWeapon;
import org.dreambot.behaviour.method.scurrius.ScurriusBranch;
import org.dreambot.behaviour.method.scurrius.ScurriusMode;
import org.dreambot.behaviour.misc.RechargeBoneStaff;
import org.dreambot.behaviour.quests.childrenofthesun.ChildrenOfTheSun;
import org.dreambot.behaviour.quests.impcatcher.ImpCatcher;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.fractals.util.RatConfigureQuickPrayers;
import org.dreambot.settings.fractalsettings.ConfigurableFractal;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.webintegration.WebLoadoutLoader;

import java.util.Arrays;
import java.util.function.Supplier;

@Accessors(chain = true)
public class ConfigurableMagicBranch extends Fractal implements ConfigurableFractal<MagicBranchSettings> {
    public enum MagicTrainingMode {
        MTA_ENCHANTMENTS_ONLY(2029),
        GEMSTONE_CRAB(2103),
        SCURRIUS(2078),
        STANDARD(),
        COMBAT();

        // IDs for scripts that must have any of to use this mode
        final int[] scriptIDs;

        MagicTrainingMode(int... scriptIDs) {
            this.scriptIDs = scriptIDs;
        }

        public boolean isOwned() {
            ScriptManager sm = Client.getInstance().getScriptManager();
            return LocalSDNOwnershipCache.ownsAny(scriptIDs);
//            return scriptIDs.length == 0 || Arrays.stream(scriptIDs).anyMatch(x -> sm.hasSDNScript(x) || sm.hasPurchasedScript(x) || sm.hasPremiumScript(x));
        }
    }


    public ConfigurableMagicBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        MagicBranchSettings settings = getSettings();
        if (settings.trainingMode == MagicTrainingMode.COMBAT) {
            addChildren(
                    new GetOff330(GetOff330.MEMBERS_WORLD_FILTER).setSimpleName("Hop off 330"),
                    new ImpCatcher().setSimpleName("Impcatcher")
                            .setPrependLogic(() -> {
                                if (Client.isDynamicRegion()) {
                                    Magic.castSpell(Normal.HOME_TELEPORT);
                                    Sleep.sleepUntil(() -> !Client.isDynamicRegion(), 32_000);
                                }
                                return false;
                            }),
                    new EnchantRecoils().setSimpleName("Enchant Recoils "),
                    new MagicCombat(25, 15).setSimpleName("Getting some hp levels"),
                    new MageIceGiants(() -> true).setSimpleName("Magic giants")
            );
            return;
        }

        // parse custom for gemstone
        EquipmentLoadout parsedCustomLoadout = null;
        if (getSettings().gemstoneCustomLoadout != null && !getSettings().gemstoneCustomLoadout.isEmpty()) {
            try {
                parsedCustomLoadout = WebLoadoutLoader.parseEquipment(getSettings().gemstoneCustomLoadout);
            } catch (Exception e) {
                log("Failed to parse gemstone custom");
            }
        }

        if (settings.trainingMode == MagicTrainingMode.GEMSTONE_CRAB) {
            addChildren(
                    new ChildrenOfTheSun().setSimpleName("COS"),
                    new GetOff330(GetOff330.MEMBERS_WORLD_FILTER).setSimpleName("Hop off 330"),
                    GemstoneCrab.getMagic(100, settings.defTarget)
                            .setInventoryLoadout(settings.gemstoneLoadout.inventoryLoadout)
                            .setEquipmentLoadout(parsedCustomLoadout != null ? parsedCustomLoadout : getSettings().gemstoneLoadout.equipmentLoadout)
            );
            return;
        }

        // whenever an event is running it will manage the nodes in mta, this handle edge cases when muling and other events
        if (settings.trainingMode == MagicTrainingMode.MTA_ENCHANTMENTS_ONLY) {
            // insantiating these objects activates the mta node mngr which can sometimes cause a problem.
            // only make them if you are doing mta.
            addChildren(

                    new Fractal(() -> settings.trainingMode == MagicTrainingMode.MTA_ENCHANTMENTS_ONLY)
                            .setSimpleName("MTA enchantment")
                            .addChildren(
                                    new GetOff330(GetOff330.MEMBERS_WORLD_FILTER).setSimpleName("Hop off 330"),
                                    new MagicBranch(() -> Skills.getRealLevel(Skill.MAGIC) < 57).setSimpleName("Get 50s"),
                                    new UnlockMTA().setSimpleName("Unlock MTA"),
                                    new EnchantRoomMTA(() -> true)
                            )
            );
            return;
        }


        addChildren(
                new Fractal(() -> settings.trainingMode == MagicTrainingMode.SCURRIUS && MagicTrainingMode.SCURRIUS.isOwned())
                        .addChildren(
                                new GetOff330(GetOff330.MEMBERS_WORLD_FILTER).setSimpleName("Hop off 330"),
                                new MagicBranch(() -> Skills.getRealLevel(Skill.MAGIC) < 50).setSimpleName("Get 50s"),
                                new MagicCombat(30, 10).setSimpleName("Get 30 HP"),
                                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < 43)
                                        .setSimpleName("Prayer training"),
                                new GetOff330().setSimpleName("Get off 330"),
                                new RatConfigureQuickPrayers(() -> new Prayer[]{Prayer.PROTECT_FROM_MELEE, ScurriusMode.getBestMagePray()})
                                        .setSimpleName("Magic q p"),

                                new RechargeBoneStaff().setSimpleName("Recharge bone staff"),
                                new MakeBoneWeapon(() -> OwnedItems.contains(ItemID.SCURRIUS_SPINE) && !OwnedItems.contains(ItemID.BONE_STAFF),
                                        MakeBoneWeapon.BONE_STAFF_LOADOUT)
                                        .setSimpleName("Make bone staff"),

                                new GoToScurrius(() -> !Client.isDynamicRegion(), ScurriusMode.MAGIC)
                                        .setSimpleName("Magic"),
                                new ScurriusBranch(() -> true, ScurriusMode.MAGIC, false)
                                        .setFlick(getSettings().flicking)
                                        .setPrependLogic(() -> {
                                            // todo def target
                                            boolean shouldDefCase = false;
                                            // bone staff unique handle
                                            if (Equipment.contains(ItemID.BONE_STAFF)) {
                                                if (shouldDefCase) {
                                                    if (Combat.getCombatStyle() != CombatStyle.MAGIC_DEFENCE) {
                                                        log("Handle bone staff auto casting " + Combat.getCombatStyle());
                                                        log("Set Magic def style");
                                                        Combat.setCombatStyle(CombatStyle.MAGIC_DEFENCE);
                                                    }
                                                } else {
                                                    log("Handle bone staff auto casting " + Combat.getCombatStyle());
                                                    log("Set Magic style");
                                                    if (Combat.getCombatStyle() != CombatStyle.MAGIC)
                                                        Combat.setCombatStyle(CombatStyle.MAGIC);
                                                }
                                            } else {
                                                if ((shouldDefCase && !Magic.isAutocastDefensive())
                                                        || (Magic.getAutocastSpell() == null || !Magic.canCast(Magic.getAutocastSpell()))) {
                                                    log("Scurrius needs to set magic autocast");
                                                    if (getSpell() == null) {
                                                        log("Gotta leave scurrius no runes left.");
                                                        if (Inventory.contains(ItemID.TELEPORT_TO_HOUSE))
                                                            Inventory.interact(ItemID.TELEPORT_TO_HOUSE, "Outside");
                                                        Bank.open();
                                                        return true;
                                                    }
                                                    log("Change def cast state");

                                                    if (shouldDefCase) {
                                                        Magic.setDefensiveAutocastSpell(getSpell());
                                                    } else {
                                                        Magic.setAutocastSpell(getSpell());
                                                    }
                                                    return true;
                                                }
                                            }
                                            return false;
                                        })
                                        .setSimpleName("Scurrius")
                        )
                        .setSimpleName("Scurrius"),


                new ImpCatcher().setSimpleName("Impcatcher")
                        .setPrependLogic(() -> {
                            if (Client.isDynamicRegion()) {
                                Magic.castSpell(Normal.HOME_TELEPORT);
                                Sleep.sleepUntil(() -> !Client.isDynamicRegion(), 32_000);
                            }
                            return false;
                        }),
                new EnchantRecoils().setSimpleName("Enchant Recoils "),
                new EnchantDueling().setSimpleName("Enchant Duelings "),
                new AlchSomething(() -> true)
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.RUNE_ARROW, 1, 2000)
                                .addItem(ItemID.NATURE_RUNE, 1, 2000)
                        )
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_FIRE)
                        )
                        .setSimpleName("Alch rune arrows")
        );
    }

    @Override
    public MagicBranchSettings getSettings() {
        return SettingsRepository.getSetting(settingName(), new MagicBranchSettings());
    }

    @Override
    public String settingName() {
        return "Magic";
    }

    private Spell getSpell() {
        Spell[] allowed = new Spell[]{
                Normal.WIND_STRIKE,
                Normal.WIND_BOLT,
                Normal.WIND_BLAST,
                Normal.WIND_WAVE,
                Normal.WIND_SURGE
        };

        return Arrays.stream(allowed).filter(Magic::canCast).reduce((f, s) -> s).orElse(null);
    }
}
