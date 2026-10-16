package alexyzer.hexcc.casting;

import alexyzer.hexcc.util.Misc;
import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect;
import dan200.computercraft.api.lua.LuaException;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IComputerBasedCastEnv {

    double DEFAULT_AMBIT_RADIUS = 16;

    Misc.DumpableList<String> getRevealBuffer();

    Vec3 getPos();
    Vec3 getDir();

    @Nullable OperatorSideEffect.DoMishap getLastMishap();

    default @Nullable OperatorSideEffect.DoMishap checkMishap(CastResult result) {
        for (Object effect : result.getSideEffects().toArray()) {
            if (effect instanceof OperatorSideEffect.DoMishap mishapEffect) return mishapEffect;
        }
        return null;
    }

    void clearMishap();

    default void throwIfMishapped(CastingEnvironment env) throws LuaException {
        var mishap = getLastMishap();
        if (mishap != null) {
            var mishapComponent = mishap.getMishap().errorMessageWithName(env, mishap.getErrorCtx());
            var mishapStr = mishapComponent != null ? mishapComponent.getString() : mishap.getMishap().getMessage();
            clearMishap();
            getRevealBuffer().clear();
            throw new LuaException(mishapStr);
        }
    }

    default List<String> dumpRevealBuffer() {
        return getRevealBuffer().dump();
    }
}
