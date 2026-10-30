package alexyzer.hexcc;

import alexyzer.hexcc.computer.HexLib;
import alexyzer.hexcc.util.IotaPermit;
import com.mojang.logging.LogUtils;
import dan200.computercraft.api.ComputerCraftAPI;
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
        ComputerCraftAPI.registerAPIFactory(computer -> HexLib.INSTANCE);
    }

    public static void onReloadAsync(ResourceManager resourceManager) {
        IotaPermit.reload(resourceManager);
        HexLib.generatePatternLookup();
    }

    public static void onServerStopping(MinecraftServer server) {
        IotaPermit.close();
    }
}
