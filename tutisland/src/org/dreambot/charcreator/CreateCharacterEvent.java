package org.dreambot.charcreator;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.ReactionGenerator;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.charcreator.colour.*;
import org.dreambot.charcreator.design.*;
import org.dreambot.util.AbstractEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Setter
@Accessors(chain = true)
public class CreateCharacterEvent extends AbstractEvent {
    HeadDesign headDesign;
    JawDesign jawDesign;
    TorsoDesign torsoDesign;
    ArmsDesign armsDesign;
    HandDesign handDesign;
    LegDesign legDesign;
    FeetDesign feetDesign;

    HairColour hairColour;
    TorsoColour torsoColour;
    LegColour legColour;
    FeetColour feetColour;
    SkinColour skinColour;

    @Override
    public void onStart() {
        features = new HashMap<CharacterFeature, CharacterFeature[]>() {{
            put(headDesign, HeadDesign.values());
            put(jawDesign, JawDesign.values());
            put(torsoDesign, TorsoDesign.values());
            put(armsDesign, ArmsDesign.values());
            put(handDesign, HandDesign.values());
            put(legDesign, LegDesign.values());
            put(feetDesign, FeetDesign.values());
            put(hairColour, HairColour.values());
            put(torsoColour, TorsoDesign.values());
            put(legColour, LegColour.values());
            put(feetColour, FeetColour.values());
            put(skinColour, SkinColour.values());
        }};
    }

    Map<CharacterFeature, CharacterFeature[]> features = new HashMap<CharacterFeature, CharacterFeature[]>() {{
        put(headDesign, HeadDesign.values());
//                jawDesign,
//                torsoDesign,
//                armsDesign,
//                handDesign,
//                legDesign,
//                feetDesign,
//                hairColour, torsoColour, legColour, feetColour, skinColour
    }};

    Timer timeout = new Timer(60 * 2 * 1000);

    @Override
    public int onLoop() {
        if (timeout.finished()) {
            Logger.info("Appearance time out");
            setFailed(true);
            return ReactionGenerator.getNormal();
        }

        // todo check for widget being opened
        if (!Widgets.getWidget(679).isVisible()) {
            Logger.info("setting failed - char creator widget not open");
            setFailed(true);
            return ReactionGenerator.getNormal();
        }

        for (Map.Entry<CharacterFeature, CharacterFeature[]> entry : features.entrySet()) {
            if (timeout.finished()) {
                Logger.info("Appearance time out");
                setFailed(true);
                return ReactionGenerator.getNormal();
            }
            CharacterFeature feature = entry.getKey();
            CharacterFeature[] values = entry.getValue();
            Logger.info(feature + " " + values.length);
            if (feature != null && !feature.isComplete()) {
                Logger.info("not complete " + feature);
                CharacterFeature currentSelected = Arrays.stream(values)
                        .filter(x -> x.getTarget() == feature.currentlySelected())
                        .findFirst()
                        .orElse(null);
                if (currentSelected != null && currentSelected.getOrdinal() < feature.getOrdinal()) {
                    Logger.info("Select right " + feature.getOrdinal());
                    feature.selectRight();
                } else {
                    Logger.info("Select left " + currentSelected);
                    feature.selectLeft();
                }
                return ReactionGenerator.getNormal() + 1390;
            }
        }

        Logger.info("Complete");
//        setEventCompleted(true);
        setComplete(true);
        return ReactionGenerator.getNormal();
    }
}
