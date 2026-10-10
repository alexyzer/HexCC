package alexyzer.hexcc;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import net.minecraft.server.level.ServerLevel;

import java.util.function.BiFunction;

import static alexyzer.hexcc.util.IotaPermit.validators;

public class HexCCAPI {
    public static void registerIotaDeserializationValidator(IotaType<?> iotaType, BiFunction<Iota, ServerLevel, Iota> validator) {
        validators.put(iotaType, validator);
    }
}
