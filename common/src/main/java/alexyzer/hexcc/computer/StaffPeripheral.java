package alexyzer.hexcc.computer;

import alexyzer.hexcc.HexCC;
import alexyzer.hexcc.casting.IComputerBasedCastEnv;
import alexyzer.hexcc.casting.LivingPocketCastEnv;
import alexyzer.hexcc.casting.PlayerPocketCastEnv;
import alexyzer.hexcc.casting.TurtleCastEnv;
import alexyzer.hexcc.util.LuaIota;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.ListIota;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.casting.mishaps.MishapStackSize;
import at.petrak.hexcasting.api.mod.HexConfig;
import at.petrak.hexcasting.api.utils.TreeList;
import at.petrak.hexcasting.common.lib.hex.HexActions;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static alexyzer.hexcc.HexCCConfig.REPLENISH_TICKS;
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
        return this == other;
    }

    public boolean needsUpdate = false;

    public void update() {
        if (vm != null && needsUpdate) {
            needsUpdate = false;
            var image = vm.getImage();

            var replenish_rate = HexConfig.ServerConfigAccess.DEFAULT_MAX_OP_COUNT / REPLENISH_TICKS;
            var ops_consumed = Math.max(image.getOpsConsumed() - replenish_rate, 0);
            if (ops_consumed > 0) needsUpdate = true; //Update until 0

            image = image.copy(image.getStack(), image.getParenCount(), image.getParenthesized(), image.getEscapeNext(), image.getSimulateNext(), ops_consumed, image.getUserData());
            vm.setImage(image);
        }
    }


    //Lua

    public MethodResult cast(Iota iota) throws LuaException {
        needsUpdate = true;
        var level = env.getWorld();
        if (iota instanceof ListIota listIota)
            vm.queueExecuteAndWrapIotas(listIota.getList(), level);
        else
            vm.queueExecuteAndWrapIota(iota, level);

        if (env instanceof IComputerBasedCastEnv attachment) {
            attachment.throwIfMishapped(env);
            return MethodResult.of(attachment.dumpRevealBuffer().toArray());
        }

        HexCC.LOGGER.warn("Used cast() in environment without IComputerBasedCastEnv attachment.");
        return MethodResult.of("Used cast() in environment without IComputerBasedCastEnv attachment.");
    }

    @LuaFunction(value = "cast", mainThread = true)
    public final MethodResult lua$cast(IArguments args) throws LuaException {
        return cast(switch (args.count()) {
            case 0 -> new PatternIota(HexActions.EVAL.value().prototype());
            case 1 -> {
                if (args.getType(0).equals("string"))
                    yield new PatternIota(HexPattern.fromAngleString(args.getString(0), HexDir.NORTH_EAST));
                else
                    yield LuaIota.fromLua(args.get(0), env.getWorld());
            }
            case 2 -> new PatternIota(HexPattern.fromAngleString(
                    args.getString(0),
                    HexDir.fromString(args.getString(1))
            ));
            default -> throw new LuaException("Too many arguments");
        });
    }

    @LuaFunction("pushStack")
    public final void lua$pushStack(IArguments args) throws LuaException {
        setStack(getStack().appendedAll(LuaIota.parseArgs(env.getWorld(), args.getAll())));
    }

    @LuaFunction("popStack")
    public final Map<String, Object> lua$popStack() throws LuaException {
        var oldStack = getStack();
        if (oldStack.isEmpty()) return null; //To be on par with table.remove actually
        setStack(oldStack.tail());
        return LuaIota.toLua(oldStack.last());
    }

    @LuaFunction("getStack")
    public final List<Map<String, Object>> lua$getStack() {
        var stack = getStack();
        var luaIotas = new ArrayList<Map<String, Object>>(stack.size());
        stack.foreach(iota -> luaIotas.add(LuaIota.toLua(iota)));
        return luaIotas;
    }

    @LuaFunction("setStack")
    public final void lua$setStack(Map<?, ?> luaIotasSeq) throws LuaException {
        var stack = new TreeList.TreeListBuilder<Iota>();
        for (double i = 1.0; i <= luaIotasSeq.size(); i++) {
            var luaIota = luaIotasSeq.get(i);
            if (luaIota == null) break;
            stack.addOne(LuaIota.fromLua(luaIota, env.getWorld()));
        }
        setStack(stack.result());
    }

    @LuaFunction("clear")
    public final void lua$clear() {
        needsUpdate = true;
        var image = vm.getImage();
        image = image.copy(
                TreeList.empty(), //clear stack
                0, //clear parenthesized count
                TreeList.empty(), //clear parenthesized iotas
                false, //cancel escape
                false, //cancel simulate
                image.getOpsConsumed(), //Keep used ops
                new CompoundTag() //Clear userdata
        );
        vm.setImage(image);
    }

    public TreeList<Iota> getStack() {
        return vm.getImage().getStack();
    }

    public void setStack(TreeList<Iota> newStack) throws LuaException {
        needsUpdate = true;
        var image = vm.getImage();
        if (IotaType.isTooLargeToSerialize(newStack)) doMishap(new MishapStackSize());
        image = image.copy(newStack, image.getParenCount(), image.getParenthesized(), image.getEscapeNext(), image.getSimulateNext(), image.getOpsConsumed(), image.getUserData());
        vm.setImage(image);
    }

    public void doMishap(Mishap mishap) throws LuaException {
        needsUpdate = true;
        var ctx = new Mishap.Context(null, null);
        vm.performSideEffects(List.of(new OperatorSideEffect.DoMishap(mishap, ctx)));
        throw new LuaException(mishap.errorMessageWithName(env, ctx).getString());
    }
}
