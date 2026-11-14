package alexyzer.hexcc.util;

import alexyzer.hexcc.HexCCConfig;
import at.petrak.hexcasting.api.casting.iota.*;
import at.petrak.hexcasting.api.utils.TreeList;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;

public class IotaRepresentable {
    public static @Nullable IotaType<?> getIotaType(Object iotaRepresentable) {
        try {
            return switch (iotaRepresentable) {
                case Iota iota -> iota.getType();
                case Map<?, ?> table -> {
                    //LuaIota
                    var typeField = table.get("type");
                    if (typeField instanceof String typeStr) {
                        var typeResLoc = Misc.parseWithHexDefault(typeStr);
                        yield IXplatAbstractions.INSTANCE.getIotaTypeRegistry().get(typeResLoc);
                    }

                    //List
                    var seq = LuaOps.getSequence(table);
                    if (seq >= 0) yield HexIotaTypes.LIST.get();

                    //Vector
                    if (table.size() == 3)
                        if (table.get("x") instanceof Number)
                            if (table.get("y") instanceof Number)
                                if (table.get("z") instanceof Number)
                                    yield HexIotaTypes.VEC3.get();

                    yield null;
                }
                case Number ignored -> HexIotaTypes.DOUBLE.get();
                case Boolean ignored -> HexIotaTypes.BOOLEAN.get();
                case null -> NullIota.TYPE;
                default -> null;
            };
        } catch (Exception ignored) {
            return null;
        }
    }

    public static Iota getIotaRepresentation(Object iotaRepresentable, boolean moderate) {
        try {
            return switch (iotaRepresentable) {
                case Iota iota -> iota;
                case Map<?, ?> table -> {
                    //LuaIota
                    if (table.get("type") instanceof String typeString) {
                        ResourceLocation typeResLoc = Misc.parseWithHexDefault(typeString);
                        var allowed = !moderate || IotaPermit.typePermits.getOrDefault(typeResLoc, HexCCConfig.ALLOW_UNKNOWN_IOTAS);
                        if (!allowed) yield new GarbageIota();

                        var iotaResult = AlternativeIotaCodecs.decode(typeResLoc, LuaOps.INSTANCE, table);
                        yield iotaResult.isSuccess() ? iotaResult.getOrThrow() : new GarbageIota();
                    }

                    //List
                    var seq = LuaOps.getSequence(table);
                    if (seq >= 0) {
                        var listBuilder = new TreeList.TreeListBuilder<Iota>();
                        table.forEach((k, v) -> {
                            var iota = getIotaRepresentation(v, moderate);
                            listBuilder.addOne(iota != null ? iota : new GarbageIota());
                        });
                        yield new ListIota(listBuilder.result());
                    }

                    //Vector
                    if (table.size() == 3)
                        if (table.get("x") instanceof Number x)
                            if (table.get("y") instanceof Number y)
                                if (table.get("z") instanceof Number z)
                                    yield new Vec3Iota(new Vec3(x.doubleValue(), y.doubleValue(), z.doubleValue()));

                    yield new GarbageIota();
                }
                case Number num -> new DoubleIota(num.doubleValue());
                case Boolean bool -> new BooleanIota(bool);
                case null -> new NullIota();
                default -> new GarbageIota();
            };
        } catch (Exception ignored) {
            return new GarbageIota();
        }
    }

    public static Object getLuaRepresentation(Object iotaRepresentable) {
        return switch (iotaRepresentable) {
            case NullIota nullIota -> null;
            case DoubleIota doubleIota -> doubleIota.getDouble();
            case BooleanIota booleanIota -> booleanIota.getBool();
            case ListIota listIota -> {
                var list = listIota.getList();
                var result = new ArrayList<>(list.size());
                list.foreach(iota -> result.add(getLuaRepresentation(iota)));
                yield result;
            }
            case Vec3Iota vec3Iota -> {
                var vec = vec3Iota.getVec3();
                yield Map.of("x", vec.x, "y", vec.y, "z", vec.z);
            }
            case Iota iota -> AlternativeIotaCodecs.encode(iota, LuaOps.INSTANCE).getOrThrow();
            default -> throw new IllegalStateException("Unexpected value: " + iotaRepresentable);
        };
    }
}
