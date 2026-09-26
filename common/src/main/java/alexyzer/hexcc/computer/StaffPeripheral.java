package alexyzer.hexcc.computer;

import alexyzer.hexcc.casting.IComputerBasedCastEnv;
import alexyzer.hexcc.casting.LivingPocketCastEnv;
import alexyzer.hexcc.casting.PlayerPocketCastEnv;
import alexyzer.hexcc.casting.TurtleCastEnv;
import alexyzer.hexcc.mixin.CastingImageAccessor;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.iota.BooleanIota;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.common.lib.hex.HexActions;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

import static alexyzer.hexcc.computer.StaffUpgrade.TYPE_STR;

public class StaffPeripheral implements IPeripheral {

    public CastingEnvironment env;
    public CastingVM vm;
    protected final Supplier<CastingEnvironment> envInitializer;

    public StaffPeripheral(ITurtleAccess turtle, TurtleSide side) {
        envInitializer = () -> new TurtleCastEnv(turtle);
    }

    public StaffPeripheral(IPocketAccess access) {
        envInitializer = () -> {
            var entity = access.getEntity();
            if (entity instanceof ServerPlayer) return new PlayerPocketCastEnv(access);
            if (entity instanceof LivingEntity) return new LivingPocketCastEnv(access);
            throw new IllegalStateException("Expected to be initialized held by a LivingEntity.");
        };
    }

    @Override
    public @NotNull String getType() {
        return TYPE_STR;
    }

    @Override
    public void attach(@NotNull IComputerAccess computer) {
        //Init here
        env = envInitializer.get();
        vm = CastingVM.empty(env);
    }

    @Override
    public void detach(@NotNull IComputerAccess computer) {
        //Unload here
        env = null;
        vm = null;
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        //Stub
        return other != null && other.getClass() == StaffPeripheral.class;
    }

    @LuaFunction(mainThread = true)
    public List<String> cast(boolean b) {
        vm = CastingVM.empty(env);
        ((CastingImageAccessor) (Object) vm.getImage()).setStack(vm.getImage().getStack().appended(new BooleanIota(b)));
        vm.queueExecuteAndWrapIota(new PatternIota(HexActions.PRINT.value().prototype()), env.getWorld());
        return ((IComputerBasedCastEnv) env).dumpRevealBuffer();
    }
}
