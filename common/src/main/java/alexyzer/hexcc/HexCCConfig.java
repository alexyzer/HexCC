package alexyzer.hexcc;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = HexCC.MOD_ID)
public class HexCCConfig implements ConfigData {
    public static boolean ALLOW_UNKNOWN_IOTAS = false;
    public static boolean ALLOW_PLAYER_DESERIALIZATION = false;
    public static boolean ALLOW_INVALID_PATTERNS = false;
    public static int REPLENISH_TICKS = 200;
}
