package me.alexyzer.hexcc.casting;

import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.MishapEnvironment;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.shared.turtle.core.TurtlePlayer;
import me.alexyzer.hexcc.casting.ResponseWrapper.CastResponseWrapper;
import me.alexyzer.hexcc.casting.ResponseWrapper.WithResponseWrapper;
import me.alexyzer.hexcc.computer.FakeTurtlePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TurtleCastingEnv extends CastingEnvironment implements WithResponseWrapper {
    public final CastResponseWrapper wrapper = new CastResponseWrapper();
    @Override public CastResponseWrapper getResponseWrapper() {return wrapper;}
    @Override public void postExecution(CastResult result) {
        super.postExecution(result);
        wrapper.wrapPostExecution(result,this,this.mishapSprayPos());
    }
    @Override public void printMessage(Component message) {wrapper.wrapPrintMessage(message);}
    public final ServerPlayer fakePlayer;

    public final ITurtleAccess turtleAccess;
    public final TurtleSide side;
    public final TurtleMishapEnvironment mishapEnvironment = new TurtleMishapEnvironment(this);
    public FrozenPigment pigment = FrozenPigment.DEFAULT.get();

    public TurtleCastingEnv(ITurtleAccess turtleAccess, TurtleSide side) {
        super((ServerLevel) turtleAccess.getLevel());
        this.turtleAccess = turtleAccess; this.side = side;
        fakePlayer = new FakeTurtlePlayer(turtleAccess);
    }

    @Override protected long extractMediaEnvironment(long cost, boolean simulate) {
        long negativeMedia = cost;
        List<ADMediaHolder> mediaHolders = HexcastingUtil.scanContainerForMediaStuff(turtleAccess.getInventory());
        for (ADMediaHolder mediaHolder: mediaHolders){
            if (!mediaHolder.canProvide()) continue;
            negativeMedia -= mediaHolder.withdrawMedia(negativeMedia, simulate);
        }
        return negativeMedia;
    }
    public Vec3 getDirection(){
        return Vec3.atLowerCornerOf(turtleAccess.getDirection().getNormal());
    }
    public Vec3 getFacePosition(){
        return getPosition().add(getDirection().scale(.5));
    }
    public Vec3 getPosition(){return turtleAccess.getPosition().getCenter();}
    public ItemStack extractItem(int slot){
        ItemStack extracted = turtleAccess.getInventory().getItem(slot);
        turtleAccess.getInventory().setItem(slot, ItemStack.EMPTY);
        return extracted;
    }
    public ItemStack extractSelectedItem(){
        return extractItem(turtleAccess.getSelectedSlot());
    }

    @Override protected boolean isVecInRangeEnvironment(Vec3 vec) {
        return turtleAccess.getPosition().getCenter().subtract(vec).lengthSqr()<256; //16 block ambit
    }
    @Override protected boolean hasEditPermissionsAtEnvironment(BlockPos pos) {return this.world.mayInteract(fakePlayer,pos);}
    @Override public InteractionHand getCastingHand() {
        return switch (side) {
            case LEFT -> InteractionHand.OFF_HAND;
            case RIGHT -> InteractionHand.MAIN_HAND;
        };
    }

    @Override protected List<ItemStack> getUsableStacks(StackDiscoveryMode mode) {
        List<ItemStack> stacks = new ArrayList<>(16);
        Container inventory = turtleAccess.getInventory();
        int size = inventory.getContainerSize();
        for (int n = 0; n<size; n++){stacks.add(inventory.getItem(n));}
        return stacks;
    }
    @Override public boolean isEnlightened() {return true;}
    @Override protected List<HeldItemInfo> getPrimaryStacks() {return List.of(new HeldItemInfo(turtleAccess.getInventory().getItem(turtleAccess.getSelectedSlot()), InteractionHand.MAIN_HAND));}
    @Override public boolean replaceItem(Predicate<ItemStack> stackOk, ItemStack replaceWith, @Nullable InteractionHand hand) {
        Container inventory = turtleAccess.getInventory();
        if (stackOk.test(inventory.getItem(turtleAccess.getSelectedSlot()))) {
            inventory.setItem(turtleAccess.getSelectedSlot(), replaceWith);
            return true;
        }
        for (int i = inventory.getContainerSize() - 1; i >= 0; i--) {
            if (i != turtleAccess.getSelectedSlot()) {
                if (stackOk.test(inventory.getItem(i))) {
                    inventory.setItem(i, replaceWith);
                    return true;
                }
            }
        }
        return false;
    }
    @Override public FrozenPigment getPigment() {return pigment;}
    @Override public @Nullable FrozenPigment setPigment(@Nullable FrozenPigment pigment) {
        FrozenPigment old = this.pigment;
        this.pigment = pigment;
        return old;
    }
    @Override public Vec3 mishapSprayPos() {return getPosition();}
    @Override public void produceParticles(ParticleSpray particles, FrozenPigment colorizer) {particles.sprayParticles(this.world, colorizer);}
    @Override public @Nullable LivingEntity getCastingEntity() {return fakePlayer;}
    @Override public MishapEnvironment getMishapEnvironment() {return mishapEnvironment;}
}

