package alexyzer.hexcc.computer;

import alexyzer.hexcc.HexCC;
import alexyzer.hexcc.HexCCConfig;
import alexyzer.hexcc.casting.IComputerBasedCastEnv;
import alexyzer.hexcc.casting.LivingPocketCastEnv;
import alexyzer.hexcc.casting.PlayerPocketCastEnv;
import alexyzer.hexcc.casting.TurtleCastEnv;
import alexyzer.hexcc.util.LuaIota;
import alexyzer.hexcc.util.Misc;
import alexyzer.hexcc.util.StaffSaveComponent;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
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
import dan200.computercraft.api.lua.*;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static alexyzer.hexcc.HexCCConfig.PERSIST_STATE;
import static alexyzer.hexcc.HexCCConfig.REPLENISH_TICKS;
import static alexyzer.hexcc.computer.StaffUpgrade.TYPE_STR;

public class StaffPeripheral implements IPeripheral {

    public CastingEnvironment env;
    public CastingVM vm;
    public IComputerAccess computer;

    protected final Supplier<CastingEnvironment> envInitializer;
    public final Supplier<DataComponentPatch> dataGetter;
    public final Consumer<DataComponentPatch> dataSetter;

    public StaffPeripheral(ITurtleAccess turtle, TurtleSide side) {
        dataGetter = () -> turtle.getUpgradeData(side);
        dataSetter = data -> turtle.setUpgradeData(side, data);
        envInitializer = () -> new TurtleCastEnv(turtle);
    }

    public StaffPeripheral(IPocketAccess access) {
        dataGetter = access::getUpgradeData;
        dataSetter = access::setUpgradeData;
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
        this.computer = computer;

        env = envInitializer.get();
        if (PERSIST_STATE) {
            var image = dataGetter.get().get(StaffSaveComponent.INSTANCE);
            //noinspection DataFlowIssue
            vm = new CastingVM(
                    image.isPresent() ? image.get() : new CastingImage(),
                    env
            );
        } else {
            vm = CastingVM.empty(env);
        }

        //In case it got stuck after sudden unload
        needsUpdate = true;
    }

    @Override
    public void detach(@NotNull IComputerAccess computer) {
        //Unload here

        if (HexCCConfig.PERSIST_STATE)
            dataSetter.accept(
                DataComponentPatch
                    .builder()
                    .set(StaffSaveComponent.INSTANCE, vm.getImage())
                    .build()
            );

        env = null;
        vm = null;
        computer = null;
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return this == other;
    }

    //This only ever lives on main thread
    public boolean needsUpdate = false;

    public void update() {
        if (vm != null && needsUpdate) {
            needsUpdate = false;
            var image = vm.getImage();

            var replenish_rate = HexConfig.ServerConfigAccess.DEFAULT_MAX_OP_COUNT / REPLENISH_TICKS;
            var ops_consumed = Math.max(image.getOpsConsumed() - replenish_rate, 0);
            if (ops_consumed > 0) needsUpdate = true; //Update until 0

            //Reset cast() call limit
            cast$remaining = HexCCConfig.MAX_CASTS_PER_TICK;

            image = image.copy(image.getStack(), image.getParenCount(), image.getParenthesized(), image.getEscapeNext(), image.getSimulateNext(), ops_consumed, image.getUserData());
            vm.setImage(image);
        }
    }


    //Lua

    @LuaFunction(value = "cast", mainThread = true)
    public final Object lua$cast(IArguments args) throws LuaException {
        //This doesn't need additional blocking
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
    public final void lua$pushStack(IArguments args, ILuaContext lua) throws LuaException {
        doMainThreadTask(() -> {
            setStack(
                getStack().appendedAll(
                    LuaIota.parseArgs(env.getWorld(), args.getAll())
            ), lua);

            return null;
        }, lua);
    }

    @LuaFunction("popStack")
    public final MethodResult lua$popStack(ILuaContext lua) throws LuaException {
        return waitChanges(() -> {
            var oldStack = getStack();
            if (oldStack.isEmpty()) return MethodResult.of((Object) null); //To be on par with table.remove actually

            setStack(oldStack.tail(), lua);

            return MethodResult.of(
                LuaIota.toLua(oldStack.last())
            );
        });
    }

    @LuaFunction("getStack")
    public final MethodResult lua$getStack() throws LuaException {
        return waitChanges(() -> {
            var stack = getStack();
            var luaIotas = new ArrayList<Map<String, Object>>(stack.size());
            stack.foreach(iota -> luaIotas.add(LuaIota.toLua(iota)));
            return MethodResult.of(luaIotas);
        });
    }

    @LuaFunction("setStack")
    public final void lua$setStack(Map<?, ?> luaIotasSeq, ILuaContext lua) throws LuaException {
        doMainThreadTask(() -> {
            var stack = new TreeList.TreeListBuilder<Iota>();
            for (double i = 1.0; i <= luaIotasSeq.size(); i++) {
                var luaIota = luaIotasSeq.get(i);
                if (luaIota == null) break;
                stack.addOne(LuaIota.fromLua(luaIota, env.getWorld()));
            }
            setStack(stack.result(), lua);

            return null;
        }, lua);
    }

    @LuaFunction("clear")
    public final void lua$clear(ILuaContext lua) throws LuaException {
        doMainThreadTask(() -> {
            var image = vm.getImage();
            image = image.copy(
                    TreeList.empty(), //clear stack
                    0, //clear parenthesized count
                    TreeList.empty(), //clear parenthesized iotas
                    false, //cancel escape
                    false, //cancel simulate
                    image.getOpsConsumed(), //keep used ops
                    new CompoundTag() //clear userdata
            );
            vm.setImage(image);

            return null;
        }, lua);
    }


    //Helpers

    public int cast$remaining = HexCCConfig.MAX_CASTS_PER_TICK;
    public MethodResult cast(Iota iota) throws LuaException {

        if (cast$remaining <= 0)
            return MethodResult.yield(null, (ignored) -> cast(iota));
        cast$remaining--;

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

    public TreeList<Iota> getStack() {
        return vm.getImage().getStack();
    }

    public void setStack(TreeList<Iota> newStack, ILuaContext lua) throws LuaException {
        var image = vm.getImage();
        if (IotaType.isTooLargeToSerialize(newStack)) doMishap(new MishapStackSize(), lua);
        image = image.copy(newStack, image.getParenCount(), image.getParenthesized(), image.getEscapeNext(), image.getSimulateNext(), image.getOpsConsumed(), image.getUserData());
        vm.setImage(image);
    }

    static Mishap.Context NULL_MISHAP_CTX = new Mishap.Context(null, null);
    public void doMishap(Mishap mishap, ILuaContext lua) throws LuaException {
        //Send out a task to perform side effects
        doMainThreadTask(() -> {
            vm.performSideEffects(List.of(new OperatorSideEffect.DoMishap(mishap, NULL_MISHAP_CTX)));
            return null;
        }, lua);

        //Throw error meanwhile
        throw new LuaException(
            //Getting a message is expected to be thread safe
            Misc.getEnglishMishapMessage.apply(env, mishap, NULL_MISHAP_CTX)
        );
    }


    //Lua partial execution blocking implementation

    protected final AtomicBoolean needsWait = new AtomicBoolean(false);

    /**
     * Yields if some method has unresolved main thread changes, else runs {@code then}.<p>
     * Used to make method usage more responsive while also addressing race conditions.
     * @param then code to run after changes.
     * @return result of {@code then} or {@linkplain MethodResult#yield}.
     */
    public MethodResult waitChanges(Callable<MethodResult> then) throws LuaException {
        synchronized (needsWait) {
            if (needsWait.get())
                return MethodResult.yield(null, ignored -> this.waitChanges(then));

            try {
                return then.call();
            } catch (LuaException luaException) {
                throw luaException;
            } catch (Exception unreachable) {
                throw new RuntimeException(unreachable);
            }
        }
    }

    /**
     * Helper wrapper method that adds {@code needsWait} handling and enqueues {@code task} on main thread.<p>
     * Useful for methods that don't need the result of {@code task}.
     * @param task task to execute on main thread.
     */
    public void doMainThreadTask(Callable<Void> task, ILuaContext lua) throws LuaException {
        synchronized (needsWait) {
            lua.issueMainThreadTask(() -> {
                try {
                    task.call();
                } catch (LuaException e) {
                    throw e;
                } catch (Exception unreachable) {
                    throw new RuntimeException(unreachable);
                } finally {
                    needsWait.set(false);
                }
                return new Object[0];
            });
            needsWait.set(true);
        }
    }
}
