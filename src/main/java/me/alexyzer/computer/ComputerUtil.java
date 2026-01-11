package me.alexyzer.computer;

import dan200.computercraft.api.turtle.ITurtleAccess;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class ComputerUtil {
    @Nullable
    public static ServerPlayer getOwnerPlayer(ITurtleAccess turtleAccess){try {
        return turtleAccess.getLevel().getServer().getPlayerList().getPlayer(turtleAccess.getOwningPlayer().getId());
        }catch (Exception ignored){
        return null;
    }}
}
