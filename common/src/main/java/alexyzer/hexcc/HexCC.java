package alexyzer.hexcc;

import alexyzer.hexcc.util.IotaPermit;
import com.mojang.logging.LogUtils;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;

public final class HexCC {
    public static final String MOD_ID = "hexcc";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        // Write common init code here.
    }

    public static void onServerStarting(MinecraftServer server) {
        IotaPermit.init(server);
    }

    public static void onServerStopping(MinecraftServer server) {
        IotaPermit.close();
    }
}
