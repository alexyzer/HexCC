package alexyzer.hexcc.casting;

import alexyzer.hexcc.util.Misc;
import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv;
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import dan200.computercraft.api.pocket.IPocketAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PlayerPocketCastEnv extends PlayerBasedCastEnv implements IComputerBasedCastEnv {

    public final IPocketAccess pocket;

    public PlayerPocketCastEnv(IPocketAccess pocket) {
        super(((ServerPlayer) pocket.getEntity()), InteractionHand.OFF_HAND);
        this.pocket = pocket;
    }


    //IComputerBasedCastEnv

    public final Misc.DumpableList<String> revealBuffer = new Misc.DumpableList<>();
    public @Nullable OperatorSideEffect.DoMishap lastMishap;

    @Override
    public Misc.DumpableList<String> getRevealBuffer() {
        return revealBuffer;
    }

    @Override
    public Vec3 getPos() {
        return pocket.getPosition();
    }

    @Override
    public Vec3 getDir() {
        var entity = pocket.getEntity();
        return entity != null ? entity.getLookAngle() : new Vec3(0, 0, 1); //South
    }

    public @Nullable OperatorSideEffect.DoMishap getLastMishap() {
        return lastMishap;
    }

    public void clearMishap() {
        lastMishap = null;
    }

    @Override
    public void printMessage(Component message) {
        super.printMessage(message);
        revealBuffer.add(message.getString());
    }

    @Override
    public void postExecution(CastResult result) {
        lastMishap = checkMishap(result);
        super.postExecution(result);
    }

    //PlayerBasedCastEnv

    @Override
    public long extractMediaEnvironment(long cost, boolean simulate) {
        if (this.caster.isCreative()) {
            return 0L;
        } else {
            boolean canOvercast = this.canOvercast();
            return this.extractMediaFromInventory(cost, canOvercast, simulate);
        }
    }

    @Override
    public InteractionHand getCastingHand() {
        return castingHand;
    }

    @Override
    public FrozenPigment getPigment() {
        return HexAPI.instance().getColorizer(this.caster);
    }
}
