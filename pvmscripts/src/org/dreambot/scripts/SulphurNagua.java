package org.dreambot.scripts;

import org.dreambot.PseudoScript;
import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.misc.MoonlightPotionReup;
import org.dreambot.behaviour.misc.MuleOff;
import org.dreambot.behaviour.quests.childrenofthesun.ChildrenOfTheSun;
import org.dreambot.behaviour.quests.fishingcontest.FishingFractal;
import org.dreambot.behaviour.quests.impcatcher.ImpCatcher;
import org.dreambot.behaviour.quests.perilousmoon.PerilousMoon;
import org.dreambot.behaviour.quests.twilightpromise.TwilightPromise;
import org.dreambot.behaviour.training.construction.ConstructionBranch;
import org.dreambot.behaviour.training.firemaking.BurnLogs;
import org.dreambot.behaviour.training.herblore.HerbloreBranch;
import org.dreambot.behaviour.training.hunter.HunterBranch;
import org.dreambot.behaviour.training.magic.EnchantRecoils;
import org.dreambot.behaviour.training.nmz.ConfigurableMeleeTraining;
import org.dreambot.behaviour.training.prayer.PrayerBranch;
import org.dreambot.behaviour.training.runecraft.RuneCraftingBranch;
import org.dreambot.behaviour.training.slayer.Helper;
import org.dreambot.behaviour.training.slayer.SlayerBranch;
import org.dreambot.behaviour.training.slayer.SlayerTaskMap;
import org.dreambot.behaviour.training.slayer.behaviour.StandardCombat;
import org.dreambot.behaviour.tutorial.MyVarps;
import org.dreambot.behaviour.tutorial.TutorialTree;
import org.dreambot.antiban.AntibanFractal;
import org.dreambot.discordwebhook.AutoProggy;

import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.LampHandler;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.AbstractEvent;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.generic.GetMembershipBranch;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.PutPetAway;
import org.dreambot.scriptdata.SulphurSettings;
import org.dreambot.settings.fractalsettings.FractalRoot;
import org.dreambot.settings.timing.ReactionSettingsFractal;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

public class SulphurNagua extends PseudoScript implements ItemContainerListener {
    FractalRoot<SulphurSettings> tree = new FractalRoot<>(new SulphurSettings(), getScriptName());
    AtomicBoolean isBreaking = new AtomicBoolean(false);
    BooleanSupplier breakingSupplier = () -> isBreaking.get();

    @Override
    public void init() {
        Client.getInstance().addEventListener(this);


        Logger.info("Init");
        SlayerTaskMap.minLootValue = tree.getSettings().minLootValue;
        final Area SHRIMP_AREA = new Area(3240, 3159, 3246, 3141);
        final Area NAGUA_AREA = new Area(1370, 9570, 1382, 9553, 0);
        tree.setSimpleName("cCSulphurNagua");
        AbstractEvent.globalInterruptCondition = () -> Inventory.contains("Coin pouch");

        MuleOff.LOOT = new int[]{
                ItemID.FIRE_RUNE,
                ItemID.COPPER_ORE,
                ItemID.MITHRIL_ORE,
                ItemID.SILVER_ORE,
                ItemID.COAL,
                ItemID.IRON_ORE,
                ItemID.COSMIC_RUNE,
                ItemID.SULPHUR_BLADES,
                ItemID.DEATH_RUNE,
                ItemID.NATURE_RUNE,
                ItemID.TIN_ORE,
                ItemID.LOOP_HALF_OF_KEY,
                ItemID.TOOTH_HALF_OF_KEY,
                ItemID.SHIELD_LEFT_HALF,
                ItemID.BLACK_MASK,
                ItemID.CHAOS_RUNE,
        };

        tree.addChildren(
                new GetMembershipBranch()
                        .setSimpleName("Get Membership"),
                new EmptyDeathsCoffer().setSimpleName("Death coffer"),

                new ConfigurableMeleeTraining(() ->  Skills.getRealLevel(Skill.STRENGTH) < tree.getSettings().preSlayerCombatTarget)
                        .setSimpleName("Post Slayer Sandcrabs"),

                new Fractal(() -> !PaidQuest.TWILIGHTS_PROMISE.isFinished() && Skills.getRealLevel(Skill.MAGIC) < 27).addChildren(
                        new ImpCatcher().setSimpleName("Impcatcher"),
                        new EnchantRecoils().setSimpleName("Enchant Recoils until 27")
                ).setSimpleName("Training some mage for twilight promise"),

                new PrayerBranch(() -> Skills.getRealLevel(Skill.PRAYER) < tree.getSettings().prayerTarget)
                        .setSimpleName("Prayer Training"),

                new HerbloreBranch(() -> Skills.getRealLevel(Skill.HERBLORE) < Math.max(38, tree.getSettings().herbloreTarget), false)
                        .setPrependLogic(() -> {
                            if (Client.isDynamicRegion()) {
                                Magic.castSpell(Normal.HOME_TELEPORT);
                                Antiban.sleepUntil(() -> !Client.isDynamicRegion(), 34_000);
                                return true;
                            }

                            if (Worlds.getCurrentWorld() == 330) {
                                WorldHopper.hopWorld(Worlds.getRandomWorld(x -> x.isNormal() && x.getWorld() != 401
                                        && x.isMembers() && x.getMinimumLevel() < Skills.getTotalLevel()
                                ));
                                return true;
                            }
                            return false;
                        })
                        // todo hop off 330
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

                new ConfigurableMeleeTraining(() -> Skills.getRealLevel(Skill.STRENGTH) < tree.getSettings().postSlayerCombatTarget)
                        .setSimpleName("Post Slayer Sandcrabs"),

                new MuleOff().setSimpleName("Mule off")
                        .setPrependLogic(() -> {
                            if (Dialogues.inDialogue()) {
                                Dialog.solve("Yes");
                                return true;
                            }
                            return false;
                        }),
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
                                && LivePrices.get(x.getId()) > tree.getSettings().minLootValue
                                || x.getItem().isNoted()
                                || x.getItem().isStackable()
                        )
                        .setOverhead(Prayer.PROTECT_FROM_MELEE)
                        .setEquipmentLoadout(new EquipmentLoadout()

                                .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                                .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) < 60)
                                .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
                                .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) < 60)

                                .addItem(EquipmentSlot.CHEST, ItemID.OBSIDIAN_PLATEBODY)
                                .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) >= 60)
                                .addItem(EquipmentSlot.LEGS, ItemID.OBSIDIAN_PLATELEGS)
                                .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) >= 60)
                                .addItem(EquipmentSlot.SHIELD, ItemID.TOKTZKETXIL_OBSIDIAN_SHIELD)
                                .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) >= 60
                                        && !(Skills.getRealLevel(Skill.ATTACK) >= 55))
                                // todo 60 def req for these

                                // dragon sword or rune sword or mithril
                                .addItem(EquipmentSlot.WEAPON, ItemID.IRON_SCIMITAR)
                                .setEnabledCondition(() -> Skills.getRealLevel(Skill.ATTACK) < 20)

                                .addItem(EquipmentSlot.WEAPON, ItemID.MITHRIL_SCIMITAR)
                                .setEnabledCondition(() -> Helper.skillBetween(Skill.ATTACK, 30, 20))

                                .addItem(EquipmentSlot.WEAPON, ItemID.ADAMANT_SCIMITAR)
                                .setEnabledCondition(() -> Helper.skillBetween(Skill.ATTACK, 40, 30))

                                .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_SCIMITAR)
                                .setEnabledCondition(() -> Helper.skillBetween(Skill.ATTACK, 55, 40))
                                .addItem(EquipmentSlot.WEAPON, ItemID.SULPHUR_BLADES)
                                .setEnabledCondition(() -> Skills.getRealLevel(Skill.ATTACK) >= 50)
                                .addItem(EquipmentSlot.WEAPON, ItemID.GLACIAL_TEMOTLI)
                                .setEnabledCondition(() -> Skills.getRealLevel(Skill.ATTACK) >= 55)
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
                                .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) >= 60)
                        )
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

        );
//        new AIAntiban();
    }

    @Override
    public int onLoop() {
        if (MyVarps.getTutVarp() < 1000) return tree.run();
        return tree.run();
    }

    Timer runtime = new Timer();
    int grossGp = 0;
    DecimalFormat df = new DecimalFormat("###,###,###");

    @Override
    public String[] getPaintInfo() {

        String muleOff = "-";
        if (MuleOff.timer != null) muleOff = formatTime(MuleOff.timer.remaining());
        Player local = Players.getLocal();
        String target = "";
        if (local != null) {
            Character tgt = local.getInteractingCharacter();
            if (tgt != null) target = tgt.getName();
        }

        return new String[]{
                FractalAPI.hierarchy + " " + FractalAPI.status,
                "target: " + target,
        };
    }

    @Override
    public String getScriptName() {
        return "cCSulphurNagua";
    }

    @Override
    public int getMoneyMade() {
        return grossGp;
    }

    @Override
    public Timer getRuntime() {
        return runtime;
    }

    @Override
    public long getMuleOffTime() {
        return MuleOff.timer == null ? 0 : MuleOff.timer.remaining();
    }

    @Override
    public Fractal getFractal() {
        return tree;
    }

    private String formatTime(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
        DecimalFormat format = new DecimalFormat("00");
        return String.format("%s:%s:%s",
                format.format(hours),
                format.format(minutes),
                format.format(seconds));
    }


    public void onInventoryItemAdded(Item item) {
        if (!MoonlightPotionReup.WHOLE_MOONLIGHT_DUNGEON.contains(Players.getLocal())) return;
        grossGp += item.getLivePrice() * item.getAmount();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        if (!MoonlightPotionReup.WHOLE_MOONLIGHT_DUNGEON.contains(Players.getLocal())) return;
        int quantity = incoming.getAmount() - existing.getAmount();
        if (quantity <= 0) return;

        grossGp += incoming.getLivePrice() * quantity;
    }
}
