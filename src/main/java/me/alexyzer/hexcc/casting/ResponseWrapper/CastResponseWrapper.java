package me.alexyzer.hexcc.casting.ResponseWrapper;

import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

///Holds the methods for capturing reveal and mishaps
public class CastResponseWrapper {
    public final List<String> revealBuffer = new ArrayList<>();
    public @Nullable String lastMishap;

    public List<String> dumpReveal() {
        List<String> output = List.copyOf(revealBuffer);
        revealBuffer.clear();
        return output;
    }
    @Nullable public String dumpMishap(){
        String output = lastMishap;
        lastMishap = null;
        return output;
    }
    public void wrapPrintMessage(Component message) {revealBuffer.add(message.getString());}
    public void wrapPostExecution(CastResult result, CastingEnvironment env, Vec3 soundPos) {
        var sound = result.getSound().sound();
        if (sound != null) {
            env.getWorld().playSound(null,soundPos.x,soundPos.y,soundPos.z,sound, SoundSource.BLOCKS,1,1);
        }
        for (var sideEffect : result.getSideEffects()) {
            if (sideEffect instanceof OperatorSideEffect.DoMishap doMishap) {
                var msg = doMishap.getMishap().errorMessageWithName(env, doMishap.getErrorCtx());
                if (msg != null) {
                    this.lastMishap = doMishap.getMishap().errorMessageWithName(env,doMishap.getErrorCtx()).getString();
                }
            }
        }
    }
}
