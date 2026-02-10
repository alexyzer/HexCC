package me.alexyzer.hexcc.computer.peripheral;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import me.alexyzer.hexcc.casting.TurtleCastingEnv;

public class StaffTurtlePeripheral extends StaffPeripheral{
    public StaffTurtlePeripheral(ITurtleAccess turtleAccess, TurtleSide side) {
        super(new TurtleCastingEnv(turtleAccess, side),turtleAccess.getUpgradeNBTData(side));
    }
}
