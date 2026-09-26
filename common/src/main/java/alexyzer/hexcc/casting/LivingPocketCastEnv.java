package alexyzer.hexcc.casting;

import alexyzer.hexcc.HexCCUtil;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.MishapEnvironment;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import dan200.computercraft.api.pocket.IPocketAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class LivingPocketCastEnv extends CastingEnvironment implements IComputerBasedCastEnv {

    public final IPocketAccess pocket;

    public LivingPocketCastEnv(IPocketAccess pocket) {
        super(pocket.getLevel());
        this.pocket = pocket;
    }


    //IComputerBasedCastEnv

    public final HexCCUtil.DumpableList<String> revealBuffer = new HexCCUtil.DumpableList<>();

    @Override
    public HexCCUtil.DumpableList<String> getRevealBuffer() {
        return revealBuffer;
    }

    @Override
    public void printMessage(Component component) {
        revealBuffer.add(component.getString());
    }


    //CastingEnvironment

    @Override
    public @NotNull LivingEntity getCastingEntity() {

        //Expected to only be handled by LivingEntity at the moment.
        var entity = pocket.getEntity();
        if (!(entity instanceof LivingEntity livingEntity)) throw new IllegalStateException("Expected to be handled by LivingEntity.");

        return livingEntity;
    }

    @Override
    public MishapEnvironment getMishapEnvironment() {
        return new NoOpMishapEnv(world, null);
    }

    @Override
    public Vec3 mishapSprayPos() {
        return pocket.getPosition();
    }

    @Override
    protected long extractMediaEnvironment(long cost, boolean simulate) {
        var livingEntity = getCastingEntity();
        return HexCCUtil.extractMediaFromInventory(livingEntity.getAllSlots(), cost, true, simulate, livingEntity, -1);
    }

    @Override
    protected boolean isVecInRangeEnvironment(Vec3 vec) {
        return vec.distanceToSqr(pocket.getPosition()) <= DEFAULT_AMBIT_RADIUS * DEFAULT_AMBIT_RADIUS + 1.0E-11;
    }

    @Override
    protected boolean hasEditPermissionsAtEnvironment(BlockPos blockPos) {
        return true;
    }

    @Override
    public InteractionHand getCastingHand() {
        return InteractionHand.MAIN_HAND;
    }

    @Override
    public List<ItemStack> getUsableStacks(StackDiscoveryMode mode) {
        return List.of();
    }

    @Override
    public List<HeldItemInfo> getPrimaryStacks() {
        return List.of();
    }

    @Override
    public boolean replaceItem(Predicate<ItemStack> predicate, ItemStack itemStack, @Nullable InteractionHand interactionHand) {
        return false;
    }

    @Override
    public FrozenPigment getPigment() {
        return FrozenPigment.DEFAULT.get();
    }

    @Override
    public @Nullable FrozenPigment setPigment(@Nullable FrozenPigment pigment) {
        return null;
    }

    @Override
    public void produceParticles(ParticleSpray particles, FrozenPigment colorizer) {
        particles.sprayParticles(world, colorizer);
    }
}
