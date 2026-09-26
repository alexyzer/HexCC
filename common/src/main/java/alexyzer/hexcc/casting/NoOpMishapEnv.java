package alexyzer.hexcc.casting;

import at.petrak.hexcasting.api.casting.eval.MishapEnvironment;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class NoOpMishapEnv extends MishapEnvironment {
    protected NoOpMishapEnv(ServerLevel world, @Nullable ServerPlayer caster) {
        super(world, caster);
    }

    @Override
    public void yeetHeldItemsTowards(Vec3 vec3) {

    }

    @Override
    public void dropHeldItems() {

    }

    @Override
    public void drown() {

    }

    @Override
    public void damage(float healthProportion) {

    }

    @Override
    public void removeXp(int amount) {

    }

    @Override
    public void blind(int ticks) {

    }

    @Override
    public void nauseate(int ticks) {

    }
}
