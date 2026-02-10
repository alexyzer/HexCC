package me.alexyzer;

import at.petrak.hexcasting.api.casting.iota.BooleanIota;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.GarbageIota;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import dan200.computercraft.api.lua.IComputerSystem;
import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import org.jetbrains.annotations.NotNull;

public class HexCCLuaAPI implements ILuaAPI {
    public HexCCLuaAPI(IComputerSystem computerSystem) {}
    @LuaFunction public final MethodResult NullIota(){return MethodResult.of(new NativeLuaIota(new NullIota()));}
    @LuaFunction public final MethodResult GarbageIota(){return MethodResult.of(new NativeLuaIota(new GarbageIota()));}
    @LuaFunction public final MethodResult BooleanIota(boolean val){return MethodResult.of(new NativeLuaIota(new BooleanIota(val)));}
    @LuaFunction public final MethodResult DoubleIota(double val){return MethodResult.of(new NativeLuaIota(new DoubleIota(val)));}
    @Override public String @NotNull [] getNames() {return new String[]{"iota"};}
}
