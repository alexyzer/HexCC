package me.alexyzer.computer;

import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;

import java.nio.ByteBuffer;
import java.util.UUID;

public class TurtlePlayer extends net.fabricmc.fabric.api.entity.FakePlayer {
    public final Container container;

    public TurtlePlayer(ITurtleAccess turtleAccess, TurtleSide side) {
        super((ServerLevel) turtleAccess.getLevel(), makeProfile(turtleAccess,"FakePlayer"));
        container = turtleAccess.getInventory();
    }

    public static GameProfile makeProfile(Object any, String name){
        return new GameProfile(
                UUID.nameUUIDFromBytes(intToByteArray(any.hashCode())),
                name
        );
    }
    private static byte[] intToByteArray(int value){
        ByteBuffer buffer = ByteBuffer.allocate(4); //4 byte int
        buffer.putInt(value);
        return buffer.array();
    }

    @Override
    public Inventory getInventory() {
        return (Inventory) container;
    }
}
