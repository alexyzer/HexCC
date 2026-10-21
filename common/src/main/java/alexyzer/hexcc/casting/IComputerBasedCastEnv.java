package alexyzer.hexcc.casting;

import alexyzer.hexcc.util.Misc;
import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect;
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

    default List<String> dumpRevealBuffer() {
        return getRevealBuffer().dump();
    }
}
