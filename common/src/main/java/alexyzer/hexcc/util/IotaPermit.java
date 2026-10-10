package alexyzer.hexcc.util;

import alexyzer.hexcc.HexCC;
import at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame;
import at.petrak.hexcasting.api.casting.eval.vm.FrameEvaluate;
import at.petrak.hexcasting.api.casting.eval.vm.FrameForEach;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.*;
import at.petrak.hexcasting.api.casting.math.HexAngle;
import at.petrak.hexcasting.api.utils.TreeList;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import com.google.gson.JsonParser;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.BiFunction;

import static alexyzer.hexcc.HexCCAPI.registerIotaDeserializationValidator;

public class IotaPermit {

    public static final ResourceLocation PERMIT_LOCATION = ResourceLocation.fromNamespaceAndPath(HexCC.MOD_ID, "allow_iota_deserialization.json");

    public static final Object2BooleanOpenHashMap<ResourceLocation> typePermits = new Object2BooleanOpenHashMap<>();
    public static final Map<IotaType<?>, BiFunction<Iota, ServerLevel, Iota>> validators = new Object2ObjectOpenHashMap<>();

    public static void reload(ResourceManager resourceManager) {
        typePermits.clear();
        for (Resource resource : resourceManager.getResourceStack(PERMIT_LOCATION)) {
            try {
                var contents = JsonParser.parseReader(resource.openAsReader()).getAsJsonObject();
                contents.asMap().forEach((iota_type_str, json_bool) -> {
                    var type = Misc.parseWithHexDefault(iota_type_str);
                    var allowed = json_bool.getAsBoolean();
                    typePermits.put(type, allowed);
                });
            } catch (Exception e) {
                HexCC.LOGGER.error("Failed to load iota deserialization permit for {}. This provider will be skipped.", resource.sourcePackId(), e);
            }
        }
    }

    public static void close() {
        typePermits.clear();
    }

    public static Iota validateIota(Iota iota, @Nullable ServerLevel level) {
        try {
            var validator = validators.get(iota.getType());
            if (validator != null) return validator.apply(iota, level);

            return iota;
        } catch (Exception ignored) {
            return new GarbageIota();
        }
    }

    //TODO add config
    public static boolean ALLOW_BY_DEFAULT = false;

    public static Iota parseValidateLuaIota(Map<String, Object> luaIota, @Nullable ServerLevel level) {
        try {
            var resLoc = Misc.parseWithHexDefault((String) luaIota.get("type"));

            var allowed = typePermits.getOrDefault(resLoc, ALLOW_BY_DEFAULT);
            if (!allowed) return new GarbageIota();

            var iota = IotaType.TYPED_CODEC.decode(LuaOps.INSTANCE, luaIota).getOrThrow().getFirst();
            return validateIota(iota, level);
        } catch (Exception ignored) {
            return new GarbageIota();
        }
    }

    public static TreeList<Iota> validateIotas(TreeList<Iota> iotas, @Nullable ServerLevel level) {
        var newList = new TreeList.TreeListBuilder<Iota>();
        for (Iota elem : iotas)
            newList.addOne(validateIota(elem, level));
        return newList.result();
    }

    public static class BuiltInValidators {

        //TODO add config
        public static boolean ALLOW_PLAYER_DESERIALIZATION = false;
        public static boolean ALLOW_INVALID_PATTERNS = false;

        public static void init() {
            registerIotaDeserializationValidator(HexIotaTypes.ENTITY.get(), BuiltInValidators::validateEntity);
            registerIotaDeserializationValidator(HexIotaTypes.LIST.get(), BuiltInValidators::validateList);
            registerIotaDeserializationValidator(HexIotaTypes.PATTERN.get(), BuiltInValidators::validatePattern);
            registerIotaDeserializationValidator(HexIotaTypes.CONTINUATION.get(), BuiltInValidators::validateContinuation);
        }

        public static Iota validateEntity(Iota entityIota, ServerLevel level) {
            var valid = EntityIota.TYPE.validate(((EntityIota) entityIota), level);
            if (!valid) return new NullIota();

            if (!ALLOW_PLAYER_DESERIALIZATION && ((EntityIota) entityIota).isPlayer()) return new GarbageIota();

            return entityIota;
        }

        public static Iota validateList(Iota listIota, ServerLevel level) {
            return new ListIota(validateIotas(((ListIota) listIota).getList(), level));
        }

        public static Iota validatePattern(Iota patternIota, ServerLevel level) {
            if (ALLOW_INVALID_PATTERNS) return patternIota;

            for (HexAngle angle : ((PatternIota) patternIota).getPattern().getSignature())
                if (angle == HexAngle.BACK) return new GarbageIota();

            return patternIota;
        }

        public static Iota validateContinuation(Iota continuationIota, ServerLevel level) {
            var continuation = ((ContinuationIota) continuationIota).getContinuation();

            var newFrames = new ArrayList<ContinuationFrame>();
            while (continuation instanceof SpellContinuation.NotDone notDone) {
                ContinuationFrame frame = notDone.getFrame();
                switch (frame) {
                    case FrameEvaluate frameEvaluate ->
                            frame = frameEvaluate.copy(
                                    validateIotas(frameEvaluate.getList(), level),
                                    frameEvaluate.isMetacasting()
                            );
                    case FrameForEach frameForEach ->
                            frame = frameForEach.copy(
                                    validateIotas(frameForEach.getData(), level),
                                    validateIotas(frameForEach.getCode(), level),
                                    validateIotas(frameForEach.getContextStack(), level),
                                    validateIotas(frameForEach.getStashedStack(), level),
                                    validateIotas(frameForEach.getAcc(), level)
                            );
                    default -> {
                        //As in, not supported / unable to check
                        //TODO add API for registering frame validators
                        return new GarbageIota();
                    }
                }
                newFrames.add(frame);
                continuation = notDone.getNext();
            }

            SpellContinuation newContinuation = SpellContinuation.Done.INSTANCE;
            for (int i = newFrames.size() - 1; i >= 0; i--) {
                newContinuation = newContinuation.pushFrame(newFrames.get(i));
            }

            return new ContinuationIota(newContinuation);
        }
    }
}
