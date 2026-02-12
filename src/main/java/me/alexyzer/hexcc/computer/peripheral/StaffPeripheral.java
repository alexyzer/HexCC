package me.alexyzer.hexcc.computer.peripheral;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IPeripheral;
import me.alexyzer.NativeLuaIota;
import me.alexyzer.hexcc.casting.ResponseWrapper.WithResponseWrapper;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class StaffPeripheral implements IPeripheral {
    public NbtCompound upgradeData;
    public CastingVM castingVM;
    public boolean needsUpdate = true;

    protected StaffPeripheral(CastingEnvironment env, NbtCompound upgradeData){
        this.upgradeData = upgradeData;
        castingVM = new CastingVM(CastingImage.loadFromNbt(upgradeData, env.getWorld()), env);
    };

    public void tick(){
        if (needsUpdate){
            needsUpdate = false;
            update();
        }
    }
    public void update(){
        CastingImage oldImage = castingVM.getImage();
        castingVM.setImage(oldImage.withOverriddenUsedOps(Math.max(0, oldImage.getOpsConsumed() - 500))); //10 second to replenish
        needsUpdate |= oldImage.getOpsConsumed()>0;
        upgradeData.put("data",castingVM.getImage().serializeToNbt());
    }

    //Lua section
    @LuaFunction(mainThread = true) public final MethodResult cast(IArguments args) throws LuaException {
        needsUpdate = true;
        List<Iota> castList = new ArrayList<>(args.count());
        for (Object arg : args.getAll()) {
            if (arg instanceof String) castList.add(new PatternIota(HexPattern.fromAngles((String) arg, HexDir.NORTH_EAST)));
            else return MethodResult.of(false);
        }
        castingVM.queueExecuteAndWrapIotas(castList, castingVM.getEnv().getWorld());
        //Handle response
        if (castingVM.getEnv() instanceof WithResponseWrapper responseEnv){
            var mishap = responseEnv.dumpMishap();
            if (mishap == null) return MethodResult.of(responseEnv.dumpReveal().toArray());
            else throw new LuaException(mishap);
        }
        return MethodResult.of();
    }
    @LuaFunction() public MethodResult clear(){
        //Clear the state, but not opCount
        CastingImage old = castingVM.getImage();
        castingVM.setImage(new CastingImage().withUsedOps(old.getOpsConsumed()));
        return MethodResult.of(old.getStack().size());
    }

    @LuaFunction() public final NativeLuaIota peek(){
        List<Iota> stack = castingVM.getImage().getStack();
        return new NativeLuaIota(stack.get(stack.size()-1));
    }
    @LuaFunction() public final NativeLuaIota pop(){
        var stack = castingVM.getImage().getStack();
        return new NativeLuaIota(stack.remove(stack.size()-1));
    }
    @LuaFunction() public final void push(){
        var iota = NativeLuaIota.bridge.iota();
        var image = castingVM.getImage();

        if(image.getStack().isEmpty())
            castingVM.setImage(image.copy(List.of(iota),image.getParenCount(),image.getParenthesized(),image.getEscapeNext(),image.getOpsConsumed(),image.getUserData()));
        else
            image.getStack().add(iota);
    }

    //Obligatory definitions
    @Override public @NotNull String getType() {return "staff";}
    @Override public boolean equals(@Nullable IPeripheral other) {return this == other;}
}
