package alexyzer.hexcc.computer;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import static alexyzer.hexcc.computer.StaffUpgrade.TYPE_STR;

public class StaffPeripheral implements IPeripheral {
    @Override
    public @NotNull String getType() {
        return TYPE_STR;
    }

    @Override
    public void attach(IComputerAccess computer) {
        //Init here
    }

    @Override
    public void detach(IComputerAccess computer) {
        //Unload here
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        //Stub
        return other != null && other.getClass() == StaffPeripheral.class;
    }
}
