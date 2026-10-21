package alexyzer.hexcc.mixin;

import alexyzer.hexcc.casting.IComputerBasedCastEnv;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import at.petrak.hexcasting.common.casting.actions.circles.OpImpetusDir;
import at.petrak.hexcasting.common.casting.actions.circles.OpImpetusPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

public class CircleOpMixins {

    //Inject just before throwing mishap

    @Mixin(OpImpetusPos.class)
    public static class OpImpetusPosMixin {
        @Inject(
                method = "execute",
                at = @At(value = "NEW", target = "at/petrak/hexcasting/api/casting/mishaps/circle/MishapNoSpellCircle"),
                cancellable = true
        )
        public void execute(List<? extends Iota> args, CastingEnvironment env, CallbackInfoReturnable<List<Iota>> cir) {
            if (env instanceof IComputerBasedCastEnv computerCastEnv) {
                cir.setReturnValue(List.of(new Vec3Iota(
                        computerCastEnv.getPos()
                )));
                cir.cancel();
            }
        }
    }

    @Mixin(OpImpetusDir.class)
    public static class OpImpetusDirMixin {
        @Inject(
                method = "execute",
                at = @At(value = "NEW", target = "at/petrak/hexcasting/api/casting/mishaps/circle/MishapNoSpellCircle"),
                cancellable = true
        )
        public void execute(List<? extends Iota> args, CastingEnvironment env, CallbackInfoReturnable<List<Iota>> cir) {
            if (env instanceof IComputerBasedCastEnv computerCastEnv) {
                cir.setReturnValue(List.of(new Vec3Iota(
                        computerCastEnv.getDir()
                )));
                cir.cancel();
            }
        }
    }
}
