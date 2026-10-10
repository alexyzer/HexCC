package alexyzer.hexcc;

import alexyzer.hexcc.util.IotaPermit;
import com.mojang.logging.LogUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

public final class HexCC {
    public static final String MOD_ID = "hexcc";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        // Write common init code here.
    }

    public static void onReloadAsync(ResourceManager resourceManager) {
        IotaPermit.reload(resourceManager);
    }

    public static void onServerStopping(MinecraftServer server) {
        IotaPermit.close();
    }
}
