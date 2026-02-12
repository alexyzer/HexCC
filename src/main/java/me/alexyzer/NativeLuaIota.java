package me.alexyzer;

import at.petrak.hexcasting.api.casting.iota.GarbageIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import dan200.computercraft.api.lua.*;
import me.alexyzer.hexcc.computer.ComputerUtil;
import net.minecraft.registry.Registry;
import java.util.*;

public record NativeLuaIota(Iota iota) implements IDynamicLuaObject {
    public final static Registry<IotaType<?>> IOTA_TYPES = IXplatAbstractions.INSTANCE.getIotaTypeRegistry();

    @LuaFunction public String type() {return iota.getType().typeName().getString();}
    @LuaFunction public String id() {
        return Objects.requireNonNull(
                IOTA_TYPES.getId(iota.getType()), "Failed to get registry name for " + iota.getType()).toString();
    }
    @LuaFunction public Object data() {return ComputerUtil.tagToLua(iota.serialize());}

    //This is set by lua methods for peripherals to take from.
    public static NativeLuaIota bridge = new NativeLuaIota(new GarbageIota());
    @LuaFunction public void retrieve() {bridge = this;}

    @Override public String[] getMethodNames() {return new String[0];}
    @Override public MethodResult callMethod(ILuaContext context, int method, IArguments arguments) throws LuaException {return null;}
}
