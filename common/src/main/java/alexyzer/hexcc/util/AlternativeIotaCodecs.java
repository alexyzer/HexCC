package alexyzer.hexcc.util;

import alexyzer.hexcc.HexCCConfig;
import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.casting.math.HexSignature;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

public class AlternativeIotaCodecs {

    public static final Object2ObjectOpenHashMap<ResourceLocation, MapCodec<? extends Iota>> ALTERNATIVE_CODECS = new Object2ObjectOpenHashMap<>();

    public static <I extends Iota> MapCodec<I> register(ResourceLocation iotaResLoc, MapCodec<I> codec) {
        ALTERNATIVE_CODECS.put(iotaResLoc, codec);
        return codec;
    }

    @SuppressWarnings("unchecked")
    public static <T, I extends Iota> DataResult<T> encode(I input, DynamicOps<T> ops) {
        var prefix = ops.mapBuilder();

        var iotaType = (IotaType<I>) input.getType();
        var iotaResLoc = IXplatAbstractions.INSTANCE.getIotaTypeRegistry().getKey(iotaType);
        if (iotaResLoc == null) return DataResult.error(() -> "Unknown Iota type key:" + iotaType);

        var iotaCodec = (MapCodec<I>) ALTERNATIVE_CODECS.get(iotaResLoc);
        if (iotaCodec == null) iotaCodec = iotaType.codec();

        Codec.STRING
                .fieldOf("type")
                .encode(Misc.writeResLocWithHexDefault(iotaResLoc), ops, prefix);
        iotaCodec
                .encode(input, ops, prefix);

        return prefix.build(ops.empty());
    }

    public static <T> DataResult<? extends Iota> decode(ResourceLocation iotaResLoc, DynamicOps<T> ops, T input) {
        return ops.getMap(input).flatMap(mapLike ->
                (switch (ALTERNATIVE_CODECS.get(iotaResLoc)) {
                    case MapCodec<? extends Iota> alternative -> DataResult.success(alternative);
                    case null -> {
                        var iotaType = IXplatAbstractions.INSTANCE.getIotaTypeRegistry().get(iotaResLoc);
                        yield iotaType != null ? DataResult.success(iotaType.codec()) :
                                DataResult.<MapCodec<? extends Iota>>error(() -> "Unknown Iota type:" + iotaResLoc);
                    }
                }).flatMap(codec -> codec.decode(ops, mapLike))
        );
    }

    public static final MapCodec<PatternIota> PATTERN_IOTA_CODEC = register(
            HexAPI.modLoc("pattern"),
            new MapCodec<>() {
                @Override
                public <T> RecordBuilder<T> encode(PatternIota input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                    var pattern = input.getPattern();
                    prefix.add("start_dir", ops.createString(pattern.getOrientation().name()));
                    prefix.add("angles", ops.createString(pattern.getSignature().toAnglesString()));
                    return prefix;
                }

                @Override
                public <T> DataResult<PatternIota> decode(DynamicOps<T> ops, MapLike<T> input) {
                    return ops.getStringValue(input.get("angles")).flatMap(angles ->
                            (switch (input.get("start_dir")) {
                                case T present -> ops.getStringValue(present).flatMap(start_dir -> {
                                    var simplified = start_dir.toLowerCase(Locale.ROOT).strip().replace("_", "");
                                    var dir = switch (simplified) {
                                        case "northwest", "nw" -> HexDir.NORTH_WEST;
                                        case "west", "w" -> HexDir.WEST;
                                        case "southwest", "sw" -> HexDir.SOUTH_WEST;
                                        case "southeast", "se" -> HexDir.SOUTH_EAST;
                                        case "east", "e" -> HexDir.EAST;
                                        case "northeast", "ne" -> HexDir.NORTH_EAST;
                                        default -> null;
                                    };
                                    if (dir != null) return DataResult.success(dir);
                                    return DataResult.error(() -> "Unknown orientation: " + start_dir);
                                });
                                case /*absent*/ null -> DataResult.success(HexDir.EAST);
                            }).flatMap(start_dir -> {
                                if (HexCCConfig.ALLOW_INVALID_PATTERNS)
                                    return DataResult.success(new HexPattern(start_dir, HexSignature.fromAnglesStringUnchecked(angles)));

                                try {
                                    return DataResult.success(HexPattern.fromAngleString(angles, start_dir, true));
                                } catch (Exception e) {
                                    return DataResult.error(e::getMessage);
                                }
                            }).map(PatternIota::new)
                    );
                }

                @Override
                public <T> Stream<T> keys(DynamicOps<T> ops) {
                    return Stream.of(
                            ops.createString("start_dir"),
                            ops.createString("angles")
                    );
                }
            }
    );

    public static final MapCodec<EntityIota> ENTITY_IOTA_CODEC = register(
            HexAPI.modLoc("entity"),
            RecordCodecBuilder.mapCodec(
                    inst -> inst.group(
                            UUIDUtil.STRING_CODEC.fieldOf("entityId").forGetter(EntityIota::getEntityId),
                            Codec.STRING.optionalFieldOf("entityName").forGetter(iota -> {
                                if (iota.getEntityName() == null) return Optional.empty();
                                try {
                                    LanguageWorkaround.useClientEnglish.set(true);
                                    //noinspection DataFlowIssue
                                    return Optional.of(iota.getRawName());
                                } finally {
                                    LanguageWorkaround.useClientEnglish.set(false);
                                }
                            })
                    ).apply(inst, (entityId, entityName) -> new EntityIota(entityId, null))
            ));
}
