package alexyzer.hexcc.computer;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import dan200.computercraft.api.upgrades.UpgradeType;
import org.jspecify.annotations.Nullable;

public class StaffTurtleUpgrade extends StaffUpgrade implements ITurtleUpgrade {

    public static final StaffTurtleUpgrade INSTANCE = new StaffTurtleUpgrade();
    public static final UpgradeType<ITurtleUpgrade> TYPE = UpgradeType.simple(INSTANCE);

    @Override
    public UpgradeType<? extends ITurtleUpgrade> getType() {
        return TYPE;
    }

    @Override
    public TurtleUpgradeType getUpgradeType() {
        return TurtleUpgradeType.PERIPHERAL;
    }

    @Override
    public @Nullable IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
        return new StaffPeripheral(turtle, side);
    }
}
