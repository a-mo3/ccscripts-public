package org.dreambot.behaviour.method.mta;

import lombok.Getter;
import org.dreambot.api.Client;
import org.dreambot.api.methods.widget.Widget;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.WidgetEventListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.muling.Log;

@Getter
public class MTAPointManager implements WidgetEventListener, ChatListener {
    private static MTAPointManager instance;

    // when the widgets spawn their text are all 999, when they spawn we will set this true and then check widgets later
    static boolean shouldCheck = true;
    static final Timer cacheTime = new Timer(60_000);

    public static MTAPointManager get() {
        if (instance == null) instance = new MTAPointManager();
        if (shouldCheck) {
            Widget mtaMainParent = Widgets.getWidget(MTA_PARENT);
            cacheTime.reset();
            if (mtaMainParent != null && mtaMainParent.isVisible()) {
                Logger.info("Updating");
                shouldCheck = false;
                WidgetChild tele = mtaMainParent.getChild(MTA_MAIN_TELEKINETIC);
                if (tele != null) {
                    Logger.info("Updating telekinetic");
                    instance.telekineticPoints =
                            Integer.parseInt(tele.getText().replaceAll(",", ""));
                }

                WidgetChild enchant = mtaMainParent.getChild(MTA_MAIN_ENCHANTMENT);
                if (enchant != null) {
                    Logger.info("Updating enchant");
                    instance.enchantPoints = Integer.parseInt(enchant.getText().replaceAll(",", ""));
                }

                WidgetChild grave = mtaMainParent.getChild(MTA_MAIN_GRAVEYARD);
                if (grave != null) {
                    Logger.info("Updating grave");
                    instance.graveyardPoints = Integer.parseInt(grave.getText().replaceAll(",", ""));
                }

                WidgetChild alch = mtaMainParent.getChild(MTA_MAIN_ALCHEMIST);
                if (alch != null) {
                    Logger.info("Updating alch");
                    instance.alchemyPoints = Integer.parseInt(alch.getText().replaceAll(",", ""));
                }
            }

            // alchemy room widget
            WidgetChild wc = Widgets.get(194, 6);
            if (wc != null && wc.isVisible())
                instance.alchemyPoints = Integer.parseInt(wc.getText().replaceAll(",", ""));
            wc = Widgets.get(196, 6);
            if (wc != null) instance.graveyardPoints = Integer.parseInt(wc.getText().replaceAll(",", ""));

            // enchantment room widget
            WidgetChild enchantPoint = Widgets.get(195, 6);
            if (enchantPoint != null) {
                Logger.info("Enchant points " + enchantPoint.getText());
                try {
                    instance.enchantPoints = Integer.parseInt(enchantPoint.getText().replaceAll(",", ""));
                } catch (Exception e) {
                    Logger.info("Failed to format points");
                }
                return instance;
            }

            // graveyard points
            WidgetChild graveyardWidget = Widgets.get(196, 6);
            if (graveyardWidget != null) {
                Logger.info("graveyard points " + graveyardWidget.getText());
                try {
                    instance.graveyardPoints = Integer.parseInt(graveyardWidget.getText().replaceAll(",", ""));
                } catch (Exception e) {
                    Logger.info("Failed to format points");
                }
                return instance;
            }
            // telekinetic
            WidgetChild telekeneticWidget = Widgets.get(196, 6);
            if (telekeneticWidget != null) {
                Logger.info("telekentic points " + telekeneticWidget.getText());
                try {
                    instance.telekineticPoints = Integer.parseInt(telekeneticWidget.getText().replaceAll(",", ""));
                } catch (Exception e) {
                    Logger.info("Failed to format points");
                }
                return instance;
            }


        } else {
            if (cacheTime.finished()) shouldCheck = true;
        }

        return instance;
    }

    private MTAPointManager() {
        Client.getInstance().addEventListener(this);
    }

    static final int MTA_PARENT = 553;
    static final int MTA_MAIN_TELEKINETIC = 10;
    static final int MTA_MAIN_ALCHEMIST = 11;
    static final int MTA_MAIN_ENCHANTMENT = 12;
    static final int MTA_MAIN_GRAVEYARD = 13;

    private int alchemyPoints = 0;
    private int enchantPoints = 0;
    private int graveyardPoints = 0;
    private int telekineticPoints = 0;

    public boolean hasAny() {
        return alchemyPoints + enchantPoints + graveyardPoints + telekineticPoints > 0;
    }

    @Override
    public void onWidgetLoaded(WidgetChild event) {
        if (event == null) return;
        if (event.getParentID() == MTA_PARENT) {
            Logger.info("MTA parent loaded");
            shouldCheck = true;
            return;
        }
//        if (event.getParentID() == 196 && event.getId() == 6) {
//            graveyardPoints = Integer.parseInt(event.getText());
//            return;
//        }
//
//        if (event.getParentID() == 195 && event.getId() == 6) {
//            enchantPoints = Integer.parseInt(event.getText());
//            return;
//        }
//
//        if (event.getParentID() == 194 && event.getId() == 6) {
//            alchemyPoints = Integer.parseInt(event.getText());
//            return;
//        }
//
//        if (event.getParentID() == 198 && event.getId() == 6) {
//            telekineticPoints = Integer.parseInt(event.getText());
//            return;
//        }

    }
    // mta points dont seem to have a varbit until you open the store and select sometime so we track them
    // by checking the widget whenever appropriate


    @Override
    public void onMessage(Message message) {
        if (message.getType() == MessageType.PLAYER) {
            return;
        }

        if (message.getMessage().contains("You do not have enough") // then it says the type of points
                && message.getMessage().contains("points to buy one of this item.")) {
            Logger.info("Point manager not enough points");
            // set all points back to 0, points will be captured again when widgets load on bottom floor
            alchemyPoints = 0;
            enchantPoints = 0;
            telekineticPoints = 0;
            graveyardPoints = 0;
        }
    }
}
