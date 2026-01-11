package me.alexyzer.casting;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import net.minecraft.server.level.ServerLevel;

public abstract class ComputerBasedCastingEnv extends CastingEnvironment {
    protected ComputerBasedCastingEnv(ServerLevel world) {
        super(world);
    }
}
