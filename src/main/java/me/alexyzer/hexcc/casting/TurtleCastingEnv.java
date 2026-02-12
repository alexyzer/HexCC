package me.alexyzer.hexcc.casting;

import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.MishapEnvironment;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import me.alexyzer.hexcc.casting.ResponseWrapper.CastResponseWrapper;
import me.alexyzer.hexcc.casting.ResponseWrapper.WithResponseWrapper;
import me.alexyzer.hexcc.computer.FakeTurtlePlayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
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
    @Override public void printMessage(Text message) {wrapper.wrapPrintMessage(message);}
    public final ServerPlayerEntity fakePlayer;

    public final ITurtleAccess turtleAccess;
    public final TurtleSide side;
    public final me.alexyzer.hexcc.casting.TurtleMishapEnvironment mishapEnvironment = new TurtleMishapEnvironment(this);
    public FrozenPigment pigment = FrozenPigment.DEFAULT.get();

    public TurtleCastingEnv(ITurtleAccess turtleAccess, TurtleSide side) {
        super((ServerWorld) turtleAccess.getLevel());
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
    public Vec3d getDirection(){
        return Vec3d.of(turtleAccess.getDirection().getVector());
    }
    public Vec3d getFacePosition(){
        return getPosition().add(getDirection().multiply(.5));
    }
    public Vec3d getPosition(){return turtleAccess.getPosition().toCenterPos();}
    public ItemStack extractItem(int slot){
        ItemStack extracted = turtleAccess.getInventory().getStack(slot);
        turtleAccess.getInventory().setStack(slot, ItemStack.EMPTY);
        return extracted;
    }
    public ItemStack extractSelectedItem(){
        return extractItem(turtleAccess.getSelectedSlot());
    }

    @Override protected boolean isVecInRangeEnvironment(Vec3d vec) {
        return turtleAccess.getPosition().toCenterPos().subtract(vec).lengthSquared()<256; //16 block ambit
    }
    @Override protected boolean hasEditPermissionsAtEnvironment(BlockPos pos) {return this.world.canPlayerModifyAt(fakePlayer,pos);}
    @Override public Hand getCastingHand() {
        return switch (side) {
            case LEFT -> Hand.OFF_HAND;
            case RIGHT -> Hand.MAIN_HAND;
        };
    }

    @Override protected List<ItemStack> getUsableStacks(StackDiscoveryMode mode) {
        List<ItemStack> stacks = new ArrayList<>(16);
        Inventory inventory = turtleAccess.getInventory();
        int size = inventory.size();
        for (int n = 0; n<size; n++){stacks.add(inventory.getStack(n));}
        return stacks;
    }
    @Override public boolean isEnlightened() {return true;}
    @Override protected List<HeldItemInfo> getPrimaryStacks() {return List.of(new HeldItemInfo(turtleAccess.getInventory().getStack(turtleAccess.getSelectedSlot()), Hand.MAIN_HAND));}
    @Override public boolean replaceItem(Predicate<ItemStack> stackOk, ItemStack replaceWith, @Nullable Hand hand) {
        Inventory inventory = turtleAccess.getInventory();
        if (stackOk.test(inventory.getStack(turtleAccess.getSelectedSlot()))) {
            inventory.setStack(turtleAccess.getSelectedSlot(), replaceWith);
            return true;
        }
        for (int i = inventory.size() - 1; i >= 0; i--) {
            if (i != turtleAccess.getSelectedSlot()) {
                if (stackOk.test(inventory.getStack(i))) {
                    inventory.setStack(i, replaceWith);
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
    @Override public Vec3d mishapSprayPos() {return getPosition();}
    @Override public void produceParticles(ParticleSpray particles, FrozenPigment colorizer) {particles.sprayParticles(this.world, colorizer);}
    @Override public @Nullable LivingEntity getCastingEntity() {return fakePlayer;}
    @Override public MishapEnvironment getMishapEnvironment() {return mishapEnvironment;}
}

