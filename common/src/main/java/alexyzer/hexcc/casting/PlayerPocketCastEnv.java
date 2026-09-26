package alexyzer.hexcc.casting;

import alexyzer.hexcc.HexCCUtil;
import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import dan200.computercraft.api.pocket.IPocketAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;

public class PlayerPocketCastEnv extends PlayerBasedCastEnv implements IComputerBasedCastEnv {

    public final IPocketAccess pocket;

    public PlayerPocketCastEnv(IPocketAccess pocket) {
        super(((ServerPlayer) pocket.getEntity()), InteractionHand.OFF_HAND);
        this.pocket = pocket;
    }


    //IComputerBasedCastEnv

    public final HexCCUtil.DumpableList<String> revealBuffer = new HexCCUtil.DumpableList<>();

    @Override
    public HexCCUtil.DumpableList<String> getRevealBuffer() {
        return revealBuffer;
    }

    @Override
    public void printMessage(Component message) {
        super.printMessage(message);
        revealBuffer.add(message.getString());
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
