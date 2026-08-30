package config;

import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.List;

public enum Rock {
//    RUNE_ESSENCE,
    CLAY(6705),
    COPPER(4645),
    TIN(53),
//    BLURITE,
//    LIMESTONE,
//    BARRONITE,
    IRON(2576),
    SILVER(74),
//    VOLCANIC_ASH,
    COAL(10508),
//    PURE_ESSENCE,
//    MOTHERLODE,
//    SANDSTONE,
    GOLD(8885),
    GEM(-10335),
//    SULPHUR,
//    BLAST_MINE,
//    GRANITE,
//    VOLCANIC_MINE,
    MITHRIL(-22239),
    ADAMANTITE(21662);
//    RUNITE(-31437);

    public short COLOR;

    Rock(int COLOUR) {
        this.COLOR = (short) COLOUR;
    }

    Rock() {}


    public List<GameObject> getRocksWithOre(Rock rock){
        return GameObjects.all(obj -> {
            short[] color = obj.getModelColors();
            if(color != null){
                for(short c : color){

                    if(c == rock.COLOR) return true;
                }
            }
            return false;
        });
    }

    public GameObject getRockWithOres(Rock rock){
        return  GameObjects.closest(gameObject -> {
            short[] colours = gameObject.getModelColors();
            if(colours != null){
                for(short c : colours){

                    if(c == rock.COLOR) return true;
                }
            }
            return false;
        });
    }
}
