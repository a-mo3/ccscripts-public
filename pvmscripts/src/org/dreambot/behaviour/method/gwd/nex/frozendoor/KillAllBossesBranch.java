package org.dreambot.behaviour.method.gwd.nex.frozendoor;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.behaviour.method.gwd.GWDRechargeAtFerox;
import org.dreambot.behaviour.method.gwd.PlaceRopes;
import org.dreambot.behaviour.method.gwd.bandos.BandosConsts;
import org.dreambot.behaviour.method.gwd.bandos.BandosLoadout;
import org.dreambot.behaviour.method.gwd.bandos.GetBandosKC;
import org.dreambot.behaviour.method.gwd.bandos.KillBandos;
import org.dreambot.behaviour.method.gwd.bandos.tickbandosfight.TickKillBandosBranch;
import org.dreambot.behaviour.method.gwd.kree.GetKreeKC;
import org.dreambot.behaviour.method.gwd.kree.KillKreearra;
import org.dreambot.behaviour.method.gwd.zammy.GetZammyKC;
import org.dreambot.behaviour.method.gwd.zammy.KillZammy;
import org.dreambot.behaviour.method.gwd.zammy.range.TickRangeZammyBranch;
import org.dreambot.behaviour.method.gwd.zilyana.GetZilyanaKC;
import org.dreambot.behaviour.method.gwd.zilyana.KillZilyana;
import org.dreambot.behaviour.method.gwd.zilyana.ZilyanaConsts;
import org.dreambot.behaviour.method.gwd.zilyana.ZilyanaLoadout;
import org.dreambot.behaviour.method.gwd.zilyana.tickkillcount.ZilyanaKCTickBranch;
import org.dreambot.behaviour.method.gwd.zilyana.tickzilyanafight.TickKillZilyanaBranch;
import org.dreambot.behaviour.misc.GetBankCache;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.fractals.util.PVMUtil;
import org.dreambot.fractals.util.RatConfigureQuickPrayers;
import org.dreambot.scriptdata.BandosSettings;
import org.dreambot.scriptdata.KreearraSettings;
import org.dreambot.scriptdata.ZammySettings;
import org.dreambot.scriptdata.ZilyanaSettings;

import java.util.function.Supplier;

public class KillAllBossesBranch extends Fractal {
    public KillAllBossesBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("GWD Bosses");

        ZilyanaSettings zilyanaSettings = new ZilyanaSettings();
        ZammySettings zammySettings = new ZammySettings();
        KreearraSettings kreearraSettings = new KreearraSettings();
        BandosSettings bandosSettings = new BandosSettings();

        bandosSettings.sweatPrayer = true;
//        bandosSettings.tileSkipping = true;
        bandosSettings.loadout = BandosLoadout.KARILS_DCB;


        addChildren(
                new GetBankCache().setSimpleName("Get cache"),
                new DepositKeyPiece().setSimpleName("Deposit key"),
                new GWDRechargeAtFerox().setSimpleName("Use Ferox pool"),
                // Bandos
                new Fractal(() -> !OwnedItems.contains(ItemID.FROZEN_KEY_PIECE_BANDOS))
                        .setSimpleName("Bandos")
                        .addChildren(
                                new RatConfigureQuickPrayers(() -> new Prayer[]{PVMUtil.getBestRangePray(), Prayer.PROTECT_FROM_MISSILES}).setSimpleName("Configure qp"),
                                new TickKillBandosBranch(() -> KillBandos.BANDOS_ROOM.contains(Players.getLocal())
                                        || (Players.getLocal().getY() > 4000 && GetBandosKC.getBandosKillcount() >= 40)
                                        || (Inventory.contains(ItemID.ECUMENICAL_KEY) && Players.getLocal().getY() > 4000 && Players.getLocal().getY() < 8000),
                                        bandosSettings)
                                        .setSimpleName("Kill bandos"),
                                new GetBandosKC(() -> true)
                                        .setInventoryLoadout(BandosConsts.specialNexBrewInv)
                                        .setSimpleName("Get KC")
                        ),

                // kree
                new Fractal(() -> !OwnedItems.contains(ItemID.FROZEN_KEY_PIECE_ARMADYL)).setSimpleName("Kree")
                        .addChildren(
                                new KillKreearra(() -> KillKreearra.KREE_BOSS_ROOM.contains(Players.getLocal())
                                        || (GetKreeKC.getArmadylKC() >= 40 && GetKreeKC.ARMADYL_EYRiE.contains(Players.getLocal()))
                                        // < 8000 y is a check for not in the wilderness gwd
                                        || (Inventory.contains(ItemID.ECUMENICAL_KEY) && Players.getLocal().getY() > 4000 && Players.getLocal().getY() < 8000), kreearraSettings)
                                        .setSimpleName("Kill Kree"),

                                new GetKreeKC(() -> true, kreearraSettings)
                                        .setSimpleName("Get KC")
                        ),

                // zam
                new Fractal(() -> !OwnedItems.contains(ItemID.FROZEN_KEY_PIECE_ZAMORAK)).setSimpleName("zamy")
                        .addChildren(
                                new TickRangeZammyBranch(() -> KillZammy.ZAMMY_ARENA.contains(Players.getLocal())
                                        || (GetZammyKC.getZammyKC() >= 40 && Players.getLocal().getY() > 4000)
                                        || (Inventory.contains(ItemID.ECUMENICAL_KEY) && Players.getLocal().getY() > 4000 && Players.getLocal().getY() < 8000),
                                        zammySettings),

                                new GetZammyKC(() -> true, zammySettings)
                                        .setSimpleName("Get KC")
                        ),


                // zil
                new Fractal(() -> !OwnedItems.contains(ItemID.FROZEN_KEY_PIECE_SARADOMIN)).setSimpleName("Zil")
                        .addChildren(
                                new RatConfigureQuickPrayers(() -> new Prayer[]{Prayer.PROTECT_FROM_MAGIC, PVMUtil.getBestRangePray()})
                                        .setSimpleName("Configure quick prayers"),
                                new PlaceRopes(true).setSimpleName("Place GWD rope"),
                                new TickKillZilyanaBranch(() -> KillZilyana.ZILYANA_BOSS_ROOM.contains(Players.getLocal())
                                        || (GetZilyanaKC.getSaradominKC() >= 40 && Players.getLocal().getY() > 4000)
                                        // < 8000 y is a check for not in the wilderness gwd
                                        || (Inventory.contains(ItemID.ECUMENICAL_KEY) && Players.getLocal().getY() > 4000 && Players.getLocal().getY() < 8000), zilyanaSettings)
                                        .setSimpleName("Kill zil"),
                                new ZilyanaKCTickBranch(() -> true, zilyanaSettings)
                                        .setInventoryLoadout(ZilyanaConsts.nexBrewInv)
                                        .setSimpleName("Get KC")
                        )
        );
    }
}
