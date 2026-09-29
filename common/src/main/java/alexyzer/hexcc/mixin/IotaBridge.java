package alexyzer.hexcc.mixin;

import at.petrak.hexcasting.api.casting.iota.Iota;
import dan200.computercraft.api.lua.*;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Deprecated
public class IotaBridge {

    public static @Nullable Iota JUNCTION;

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Mixin(Iota.class)
    public abstract static class LuaIota implements IDynamicLuaObject {

        @Shadow
        public abstract Component display();

        @Override
        public String @NotNull [] getMethodNames() {
            return new String[0];
        }

        @Override
        public @NotNull MethodResult callMethod(@NotNull ILuaContext context, int method, @NotNull IArguments arguments) {
            return MethodResult.of();
        }

        @Unique
        @LuaFunction
        public final void getInternal() {
            JUNCTION = ((Iota) (Object) this);
        }

        @Unique
        @LuaFunction
        public final String __tostring() {
            return display().getString();
        }
    }
}