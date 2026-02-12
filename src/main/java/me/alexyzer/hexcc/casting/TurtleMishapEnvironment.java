package me.alexyzer.hexcc.casting;

import at.petrak.hexcasting.api.casting.eval.MishapEnvironment;
import net.minecraft.util.math.Vec3d;

public class TurtleMishapEnvironment extends MishapEnvironment {
    public final me.alexyzer.hexcc.casting.TurtleCastingEnv env;
    protected TurtleMishapEnvironment(TurtleCastingEnv env) {
        super(env.getWorld(), null);
        this.env = env;
    }

    @Override public void yeetHeldItemsTowards(Vec3d targetPos) {
        yeetItem(
            env.extractSelectedItem(),
            env.getFacePosition(),
            targetPos.subtract(env.getPosition()).normalize().multiply(.2)
        );
    }
    @Override public void dropHeldItems() {
        yeetItem(
            env.extractSelectedItem(),
            env.getFacePosition(),
            env.getDirection().multiply(.2)
        );
    }
    @Override public void drown() {/*Immune*/} //Todo Should add shutdown
    @Override public void damage(float healthProportion) {/*Immune*/} //Todo Should add shutdown
    @Override public void removeXp(int amount) {/*Immune*/} //Todo Should add media reduction
    @Override public void blind(int ticks) {/*Immune*/}
}
