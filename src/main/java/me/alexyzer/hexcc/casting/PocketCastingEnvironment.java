package me.alexyzer.hexcc.casting;

import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.env.StaffCastEnv;
import dan200.computercraft.api.pocket.IPocketAccess;
import me.alexyzer.hexcc.casting.ResponseWrapper.CastResponseWrapper;
import me.alexyzer.hexcc.casting.ResponseWrapper.WithResponseWrapper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;

public class PocketCastingEnvironment extends StaffCastEnv implements WithResponseWrapper {
    public final CastResponseWrapper wrapper = new CastResponseWrapper();
    @Override public CastResponseWrapper getResponseWrapper() {return wrapper;}
    @Override public void postExecution(CastResult result) {
        super.postExecution(result);
        wrapper.wrapPostExecution(result,this,this.mishapSprayPos());
    }
    @Override public void printMessage(Component message) {
        super.printMessage(message);
        wrapper.wrapPrintMessage(message);
    }

    public PocketCastingEnvironment(IPocketAccess pocketAccess) {super((ServerPlayer) pocketAccess.getEntity(), InteractionHand.MAIN_HAND);}
}
