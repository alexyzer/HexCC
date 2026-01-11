package me.alexyzer.casting;

import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.MishapEnvironment;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import me.alexyzer.computer.ComputerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TurtleCastingEnv extends CastingEnvironment{
    public final ITurtleAccess turtleAccess;
    public final TurtleSide side;
    public final MishapEnvironment mishapEnvironment = new TurtleMishapEnvironment(this);
    public FrozenPigment pigment = FrozenPigment.DEFAULT.get();

    public TurtleCastingEnv(ITurtleAccess turtleAccess, TurtleSide side) {
        super((ServerLevel) turtleAccess.getLevel());
        this.turtleAccess = turtleAccess; this.side = side;
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
    public Vec3 getPosition(){
        return turtleAccess.getPosition().getCenter();
    }
    public ItemStack extractItem(int slot){
        ItemStack extracted = turtleAccess.getInventory().getItem(slot);
        turtleAccess.getInventory().setItem(slot, ItemStack.EMPTY);
        return extracted;
    }
    public ItemStack extractSelectedItem(){
        return extractItem(turtleAccess.getSelectedSlot());
    }
    @Override public @Nullable LivingEntity getCastingEntity() {return null;}
    @Override public MishapEnvironment getMishapEnvironment() {return mishapEnvironment;}
    @Override public void printMessage(Component message) {ComputerUtil.getOwnerPlayer(turtleAccess).displayClientMessage(message, false);}
    @Override public Vec3 mishapSprayPos() {return turtleAccess.getPosition().getCenter();}
    @Override protected boolean isVecInRangeEnvironment(Vec3 vec) {
        return turtleAccess.getPosition().getCenter().subtract(vec).lengthSqr()<256; //16 block ambit
    }
    /*Debug*/ @Override protected boolean hasEditPermissionsAtEnvironment(BlockPos pos) {return true;}
    @Override public InteractionHand getCastingHand() {return InteractionHand.MAIN_HAND;} //Need to figure out what hand is actually used
    @Override protected List<ItemStack> getUsableStacks(StackDiscoveryMode mode) {
        List<ItemStack> stacks = new ArrayList<>(16);
        Container inventory = turtleAccess.getInventory();
        int size = inventory.getContainerSize();
        for (int n = 0; n<size; n++){stacks.add(inventory.getItem(n));}
        return stacks;
    }
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
    @Override public void produceParticles(ParticleSpray particles, FrozenPigment colorizer) {}
}

