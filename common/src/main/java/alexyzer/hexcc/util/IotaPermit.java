package alexyzer.hexcc.util;

import alexyzer.hexcc.HexCC;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.common.lib.HexRegistries;
import com.google.gson.JsonParser;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;

public class IotaPermit {

    public static final ResourceLocation PERMIT_LOCATION = ResourceLocation.fromNamespaceAndPath(HexCC.MOD_ID, "iota_permit.json");
    public static Object2BooleanOpenHashMap<IotaType<?>> permitMap = new Object2BooleanOpenHashMap<>();

    public static void init(MinecraftServer server) {
        @SuppressWarnings("unchecked")
        var IOTA_TYPE_REGISTRY = (Registry<IotaType<?>>) BuiltInRegistries.REGISTRY.get(HexRegistries.IOTA_TYPE.location());
        assert IOTA_TYPE_REGISTRY != null;

        for (Resource resource : server.getResourceManager().getResourceStack(PERMIT_LOCATION)) {
            try {
                var contents = JsonParser.parseReader(resource.openAsReader()).getAsJsonObject();
                contents.asMap().forEach((iota_type_str, json_bool) -> {
                    var iota_type = IOTA_TYPE_REGISTRY.get(Misc.tryParseWithHexDefault(iota_type_str));
                    var allowed = json_bool.getAsBoolean();
                    permitMap.put(iota_type, allowed);
                });
            } catch (Exception e) {
                HexCC.LOGGER.error("Failed to load iota deserialization permit for {}. This provider will be skipped.", resource.sourcePackId(), e);
            }
        }
    }

    public static void close() {
        permitMap.clear();
    }

    //TODO add config
    public static boolean allowByDefault = true;

    public static boolean isDeserializationAllowed(IotaType<?> type) {
        return permitMap.getOrDefault(type, allowByDefault);
    }
}
