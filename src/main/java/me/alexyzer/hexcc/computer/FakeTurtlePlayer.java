package me.alexyzer.hexcc.computer;

import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.shared.turtle.blocks.TurtleBlockEntity;
import net.fabricmc.fabric.impl.event.interaction.FakePlayerNetworkHandler;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stat;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.UUID;

public class FakeTurtlePlayer extends ServerPlayerEntity {
    public final ITurtleAccess turtleAccess;
    public FakeTurtlePlayer(ITurtleAccess turtleAccess) {
        super(((ServerWorld)turtleAccess.getLevel()).getServer(),(ServerWorld) turtleAccess.getLevel(),makeProfile(getTurtleId(turtleAccess)));
        this.turtleAccess = turtleAccess;
        this.networkHandler = new FakePlayerNetworkHandler(this);
        this.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET,Vec3d.of(turtleAccess.getDirection().getVector()));
        this.setPosition(turtleAccess.getPosition().toCenterPos());
        this.setWorld(turtleAccess.getLevel());
    }

    public static int getTurtleId(ITurtleAccess turtleAccess){
        var turtlePos = turtleAccess.getPosition();
        return ((TurtleBlockEntity) Objects.requireNonNull(turtleAccess.getLevel().getBlockEntity(turtlePos),"Could not find turtle at position "+turtlePos.toString())).getComputerID();
    }
    public static GameProfile makeProfile(String name){return new GameProfile(UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8)),name);}
    public static GameProfile makeProfile(int turtleId){
        String turtleName = "[Turtle "+Integer.toString(turtleId)+"]";
        return makeProfile(turtleName);
    }


    @Override public void setPosition(double x, double y, double z) {
        //Stupid Java 17, cannot set turtleAccess before super call this.
        if (turtleAccess != null) {turtleAccess.teleportTo(getServerWorld(),BlockPos.ofFloored(x,y,z));}
        super.setPosition(x,y,z);
    }
    @Override protected void setRotation(float yRot, float xRot) {
        turtleAccess.setDirection(Direction.fromRotation(yRot));
        super.setRotation(yRot,xRot);
    }

    @Override public @NotNull Vec3d getPos() {return turtleAccess.getPosition().toCenterPos();}
    @Override public float getEyeHeight(EntityPose pose) {return 0;}

    //From Fabric's FakePlayer
    @Override public void tick() { }
    @Override public void setClientSettings(ClientSettingsC2SPacket packet) { }
    @Override public void increaseStat(Stat<?> stat, int amount) { }
    @Override public void resetStat(Stat<?> stat) { }
    @Override public boolean isInvulnerableTo(DamageSource damageSource) {
        return true;
    }
    @Nullable @Override public AbstractTeam getScoreboardTeam() {return null;}
    @Override public void sleep(BlockPos pos) {}
    @Override public boolean startRiding(Entity entity, boolean force) {
        return false;
    }
    @Override public void openEditSignScreen(SignBlockEntity sign, boolean front) { }
    @Override public @NotNull OptionalInt openHandledScreen(@Nullable NamedScreenHandlerFactory factory) {
        return OptionalInt.empty();
    }
    @Override public void openHorseInventory(AbstractHorseEntity horse, Inventory inventory) { }
}
