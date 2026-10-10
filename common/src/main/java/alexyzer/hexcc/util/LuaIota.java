package alexyzer.hexcc.util;

import at.petrak.hexcasting.api.casting.iota.*;
import at.petrak.hexcasting.api.utils.TreeList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LuaIota {

    public static Map<String, Object> toLua(Iota iota) {
        //noinspection unchecked
        return (Map<String, Object>) IotaType.TYPED_CODEC.encode(iota, LuaOps.INSTANCE, new HashMap<>()).getOrThrow();
    }

    public static Iota parseIota(Map<String, Object> luaIota, @Nullable ServerLevel level) {
        return IotaPermit.parseValidateLuaIota(luaIota, level);
    }

    public static Iota fromLua(Object lua, @Nullable ServerLevel level) {
        return switch (lua) {
            case String str -> new GarbageIota(); //TODO
            case Number num -> new DoubleIota(num.doubleValue());
            case Boolean bool -> new BooleanIota(bool);
            case Map<?, ?> table -> {
                var seq = Misc.getSequence(table);

                if (seq == -1) {
                    if (
                        table.size() == 3
                        && table.get("x") instanceof Double x
                        && table.get("y") instanceof Double y
                        && table.get("z") instanceof Double z
                    ) {
                        yield new Vec3Iota(new Vec3(x, y, z));
                    } else {
                        yield parseIota((Map<String, Object>) table, level);
                    }
                }

                if (seq == 0) yield new ListIota(TreeList.empty());
                var iotas = new TreeList.TreeListBuilder<Iota>();
                for (double i = 1; i <= seq; i++) {
                    iotas.addOne(fromLua(table.get(i), level));
                }
                yield new ListIota(iotas.result());
            }
            case null -> new NullIota();
            default -> new GarbageIota(); //TODO unreachable
        };
    }

    public static List<Iota> parseArgs(@Nullable ServerLevel level, Object... args) {
        var iotas = new Iota[args.length];
        for (int i = 0; i < args.length; i++) iotas[i] = fromLua(args[i], level);
        return Arrays.asList(iotas);
    }
}