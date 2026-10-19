package alexyzer.hexcc;

import alexyzer.hexcc.util.IotaPermit;
import com.mojang.logging.LogUtils;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

public final class HexCC {
    public static final String MOD_ID = "hexcc";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void onCommonSetup() {
        AutoConfig.register(HexCCConfig.class, Toml4jConfigSerializer::new);
        IotaPermit.BuiltInValidators.register();
    }

    public static void onReloadAsync(ResourceManager resourceManager) {
        IotaPermit.reload(resourceManager);
    }

    public static void onServerStopping(MinecraftServer server) {
        IotaPermit.close();
    }
}
