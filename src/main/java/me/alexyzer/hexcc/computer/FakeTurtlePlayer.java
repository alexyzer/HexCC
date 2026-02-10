package me.alexyzer.hexcc.computer;

import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.shared.turtle.blocks.TurtleBlockEntity;
import me.alexyzer.HexCC;
import net.fabricmc.fabric.impl.event.interaction.FakePlayerNetworkHandler;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.UUID;

public class FakeTurtlePlayer extends ServerPlayer {
    public final ITurtleAccess turtleAccess;
    public FakeTurtlePlayer(ITurtleAccess turtleAccess) {
        super(((ServerLevel)turtleAccess.getLevel()).getServer(),(ServerLevel) turtleAccess.getLevel(),makeProfile(getTurtleId(turtleAccess)));
        this.turtleAccess = turtleAccess;
        this.connection = new FakePlayerNetworkHandler(this);
        this.lookAt(EntityAnchorArgument.Anchor.FEET,Vec3.atLowerCornerOf(turtleAccess.getDirection().getNormal()));
        this.setPos(turtleAccess.getPosition().getCenter());
        this.setLevel(turtleAccess.getLevel());
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


    @Override public void setPos(double x, double y, double z) {
        //Stupid Java 17, cannot set turtleAccess before super call this.
        if (turtleAccess != null) {turtleAccess.teleportTo(serverLevel(),BlockPos.containing(x,y,z));}
        super.setPos(x,y,z);
    }
    @Override protected void setRot(float yRot, float xRot) {
        turtleAccess.setDirection(Direction.fromYRot(yRot));
        super.setRot(yRot,xRot);
    }

    @Override public @NotNull Vec3 position() {return turtleAccess.getPosition().getCenter();}
    @Override public float getEyeHeight(Pose pose) {return 0;}

    //From Fabric's FakePlayer
    @Override public void tick() { }
    @Override public void updateOptions(ServerboundClientInformationPacket packet) { }
    @Override public void awardStat(Stat<?> stat, int amount) { }
    @Override public void resetStat(Stat<?> stat) { }
    @Override public boolean isInvulnerableTo(DamageSource damageSource) {
        return true;
    }
    @Nullable @Override public Team getTeam() {return null;}
    @Override public void startSleeping(BlockPos pos) {}
    @Override public boolean startRiding(Entity entity, boolean force) {
        return false;
    }
    @Override public void openTextEdit(SignBlockEntity sign, boolean front) { }
    @Override public @NotNull OptionalInt openMenu(@Nullable MenuProvider factory) {
        return OptionalInt.empty();
    }
    @Override public void openHorseInventory(AbstractHorse horse, Container inventory) { }
}
