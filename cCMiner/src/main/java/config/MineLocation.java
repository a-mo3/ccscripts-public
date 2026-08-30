package config;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;

public enum MineLocation {
    RIMMINGTON(new Area(
            new Tile(2970, 3248 ),
            new Tile(2981, 3250 ),
            new Tile(2990, 3242 ),
            new Tile(2988, 3233 ),
            new Tile(2978, 3229 ),
            new Tile(2965, 3238 )
    )),
    LUMBRIDGE_SOUTH(new Area(
            new Tile ( 3226, 3151 ),
            new Tile ( 3233, 3149 ),
            new Tile ( 3232, 3142 ),
            new Tile ( 3224, 3143 ),
            new Tile ( 3219, 3148 ),
            new Tile ( 3221, 3152 )
    )),
    LUMMY_SOUTH_WEST(new Area(
            new Tile ( 3146, 3155 ),
            new Tile ( 3152, 3149 ),
            new Tile ( 3150, 3142 ),
            new Tile ( 3141, 3142 ),
            new Tile ( 3141, 3153 )
    )),
    CHAMPIONS_GUILD(new Area(
            new Tile ( 3182, 3380 ),
            new Tile ( 3186, 3376 ),
            new Tile ( 3182, 3367 ),
            new Tile ( 3173, 3362 ),
            new Tile ( 3170, 3367 )
    )),
    VARROCK_SOUTH_EAST(new Area(
            new Tile ( 3281, 3371 ),
            new Tile ( 3290, 3371 ),
            new Tile ( 3293, 3359 ),
            new Tile ( 3288, 3359 ),
            new Tile ( 3280, 3361 )
    )),
    FALADOR_WEST(new Area(
            new Tile ( 2911, 3369 ),
            new Tile ( 2913, 3366 ),
            new Tile ( 2905, 3351 ),
            new Tile ( 2900, 3353 )
    )), AL_KHARID(new Area(
            new Tile(3293, 3281, 0),
            new Tile(3294, 3314, 0),
            new Tile(3300, 3320, 0),
            new Tile(3306, 3314, 0),
            new Tile(3306, 3279, 0),
            new Tile(3300, 3276, 0)
    )), BANDIT_CAMP(new Area(
            new Tile(3073, 3775, 0),
            new Tile(3102, 3776, 0),
            new Tile(3106, 3757, 0),
            new Tile(3091, 3741, 0),
            new Tile(3072, 3756, 0)
    )), BARBARIAN_VILLAGE(new Area(
            new Tile(3080, 3423, 0),
            new Tile(3085, 3423, 0),
            new Tile(3085, 3417, 0),
            new Tile(3078, 3416, 0),
            new Tile(3076, 3422, 0)
    )), CRAFTING_GUILD(new Area(
            new Tile(2939, 3291, 0),
            new Tile(2943, 3291, 0),
            new Tile(2943, 3275, 0),
            new Tile(2937, 3277, 0),
            new Tile(2936, 3283, 0)
    )), LAVA_MAZE_RUNITE(new Area(
            new Tile(3057, 3886, 0),
            new Tile(3062, 3887, 0),
            new Tile(3061, 3880, 0)
    )), PORT_PISCARILIUS(new Area(
            new Tile(1755, 3724, 0),
            new Tile(1749, 3713, 0),
            new Tile(1769, 3709, 0),
            new Tile(1780, 3710, 0),
            new Tile(1778, 3727, 0)
    )), PISCARTORIS_COLONY(new Area(
            new Tile(2321, 3647, 0),
            new Tile(2343, 3648, 0),
            new Tile(2350, 3640, 0),
            new Tile(2339, 3627, 0),
            new Tile(2325, 3632, 0)
    )), MONASTERY(new Area(
            new Tile(2599, 3236, 0),
            new Tile(2607, 3239, 0),
            new Tile(2613, 3222, 0),
            new Tile(2601, 3218, 0)
    )), ZMI_ALTER(new Area(
            new Tile(2467, 3258, 0),
            new Tile(2478, 3258, 0),
            new Tile(2479, 3247, 0),
            new Tile(2471, 3248, 0)
    )), SHILO_GEMS_UPPER(new Area(
            new Tile(2819, 2997, 0),
            new Tile(2826, 3003, 0)
    )), SHILO_GEMS_LOWER(new Area(
            new Tile(2830, 9399, 0),
            new Tile(2833, 9400, 0),
            new Tile(2842, 9398, 0),
            new Tile(2850, 9391, 0),
            new Tile(2854, 9391, 0),
            new Tile(2859, 9385, 0),
            new Tile(2852, 9376, 0),
            new Tile(2829, 9381, 0),
            new Tile(2825, 9388, 0)));




    public final Area LOCATION;

    MineLocation(org.dreambot.api.methods.map.Area LOCATION) {
        this.LOCATION = LOCATION;
    }

//    public static void main(String[] args) {
//        for (MineLocation m : MineLocation.values()) {
//            System.out.println(m.toString());
//        }
//    }

}
