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
            try {
                throw new LuaException(
                    Misc.getEnglishMishapMessage.apply(env, mishap.getMishap(), mishap.getErrorCtx())
                );
            } finally {
                clearMishap();
                getRevealBuffer().clear();
            }
        }
    }

    default List<String> dumpRevealBuffer() {
        return getRevealBuffer().dump();
    }
}
