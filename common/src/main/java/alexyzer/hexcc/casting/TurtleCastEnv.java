package alexyzer.hexcc.casting;

import alexyzer.hexcc.util.Misc;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.MishapEnvironment;
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import dan200.computercraft.api.turtle.ITurtleAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class TurtleCastEnv extends CastingEnvironment implements IComputerBasedCastEnv {

    public final ITurtleAccess turtle;
    public FrozenPigment pigment = FrozenPigment.DEFAULT.get();

    public TurtleCastEnv(ITurtleAccess turtle) {
        super(((ServerLevel) turtle.getLevel()));
        this.turtle = turtle;
    }


    //IComputerBasedCastEnv

    public final Misc.DumpableList<String> revealBuffer = new Misc.DumpableList<>();
    public @Nullable OperatorSideEffect.DoMishap lastMishap;

    @Override
    public Misc.DumpableList<String> getRevealBuffer() {
        return revealBuffer;
    }

    public @Nullable OperatorSideEffect.DoMishap getLastMishap() {
        return lastMishap;
    }

    public void clearMishap() {
        lastMishap = null;
    }

    @Override
    public void printMessage(Component component) {
        revealBuffer.add(component.getString());
    }

    @Override
    public void postExecution(CastResult result) {
        lastMishap = checkMishap(result);
        super.postExecution(result);
    }

    //CastingEnvironment

    @Override
    public @Nullable LivingEntity getCastingEntity() {
        return null;
    }

    @Override
    public MishapEnvironment getMishapEnvironment() {
        return new NoOpMishapEnv(world, getCastingEntity() instanceof ServerPlayer player ? player : null);
    }

    @Override
    public Vec3 mishapSprayPos() {
        return turtle.getPosition().getCenter();
    }

    @Override
    protected long extractMediaEnvironment(long cost, boolean simulate) {
        return 0;
    }

    @Override
    protected boolean isVecInRangeEnvironment(Vec3 vec) {
        return vec.distanceToSqr(turtle.getPosition().getCenter()) <= DEFAULT_AMBIT_RADIUS * DEFAULT_AMBIT_RADIUS + 1.0E-11;
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
        return Misc.getItems(turtle.getInventory());
    }

    @Override
    public List<HeldItemInfo> getPrimaryStacks() {
        return List.of(new HeldItemInfo(turtle.getInventory().getItem(turtle.getSelectedSlot()), InteractionHand.OFF_HAND));
    }

    @Override
    public boolean replaceItem(Predicate<ItemStack> predicate, ItemStack itemStack, @Nullable InteractionHand interactionHand) {
        //TODO
        return false;
    }

    @Override
    public FrozenPigment getPigment() {
        return pigment;
    }

    @Override
    public @Nullable FrozenPigment setPigment(@Nullable FrozenPigment pigment) {
        var old = this.pigment;
        this.pigment = pigment;
        return old;
    }

    @Override
    public void produceParticles(ParticleSpray particles, FrozenPigment colorizer) {
        particles.sprayParticles(world, colorizer);
    }
}