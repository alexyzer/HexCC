package me.alexyzer.computer;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.env.StaffCastEnv;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import me.alexyzer.casting.TurtleCastingEnv;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StaffPeripheral implements IPeripheral {
    public final CastingEnvironment castingEnv;

    public StaffPeripheral(ITurtleAccess turtleAccess, TurtleSide side) {
        castingEnv = new TurtleCastingEnv(turtleAccess, side);
    }
    public StaffPeripheral(IPocketAccess pocketAccess) {
        castingEnv = new StaffCastEnv((ServerPlayer) pocketAccess.getEntity(), InteractionHand.MAIN_HAND);
    }

    @LuaFunction
    public final MethodResult cast(IArguments args) {
        try {
            CastingVM castingVM = CastingVM.empty(castingEnv);
            List<Iota> castList = new ArrayList<>();
            for (Object obj: args.getAll()){
                castList.add(new PatternIota(HexPattern.fromAngles((String)obj, HexDir.NORTH_EAST)));
            }
            castingVM.queueExecuteAndWrapIotas(castList, castingEnv.getWorld());
        } catch (Exception e) {
            System.out.println("Exception caught");
            System.out.println(e);
        }
        return MethodResult.of();
    }

    @Override public @NotNull String getType() {return "staff";}
    @Override public boolean equals(@Nullable IPeripheral other) {return this == other;}
}
